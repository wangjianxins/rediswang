package com.wang.redis.client;

import com.wang.redis.Command.Command;
import com.wang.redis.Exception.RedisWangException;
import com.wang.redis.Serializer.Serializer;
import com.wang.redis.config.RedisWangProperties;
import com.wang.redis.connection.ConnectionPool;
import com.wang.redis.connection.impl.ConnectionPoolImpl;
import com.wang.redis.result.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @Description 具体的一个执行client
 * @author Jianxin Wang
 * @date 2019-08-28
 */
public class RedisWangClient extends DefaultClient implements SetClient,BitMapsClient{

    protected ConnectionPool connectionPool;

    protected Serializer serializer;

    public RedisWangClient(RedisWangProperties redisWangProperties){
        connectionPool = new ConnectionPoolImpl(redisWangProperties);
    }

    public Serializer getSerializer() {
        return serializer;
    }

    public void setSerializer(Serializer serializer) {
        this.serializer = serializer;
    }

    public <T>T doExecute(Command command, Class<? extends Execute<T>> execute , Object ...params){
        Execute commandInstance = null;
        try {
            commandInstance = execute.getConstructor(new Class<?>[]{}).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return (T) commandInstance.doExecute(connectionPool.getConnection(),command,params);
    }


    @Override
    public int del(String... key) {
        return doExecute(Command.del, IntResult.class,key);
    }

    /**
     * @Description expires暂时只支持EX，秒级别
     * @author Jianxin Wang
     * @date 2019-09-03
     */
    @Override
    public boolean set(String key, String value,long expires) {
        if (expires != 0) {
            return doExecute(Command.setex, BooleanResult.class,key,expires,value);
        } else {
            return doExecute(Command.set,BooleanResult.class,key,value);
        }
    }

    /**
     * @Description 后续添加过期时间
     * @author Jianxin Wang
     * @date 2019-09-03
     */
    @Override
    public boolean mset(String[] keys, java.lang.Object... values) {
        int klen = keys.length;
        int vlen = keys.length;
        int len = klen +vlen;
        if(klen != vlen){
            throw new RedisWangException("mset格式不正确,保证key,value个数对应");
        }
        Object[] mkv = new Object[len];
        int kt = 0;
        int vt = 0;
        for (int i = 0; i< len; i++) {
            if ((i+1) % 2 != 0) {
                mkv[i] = keys[kt];
                ++kt;
            } else {
                mkv[i] = values[vt];
                ++vt;
            }

        }

        return doExecute(Command.mset,BooleanResult.class,mkv);
    }

    @Override
    public List<String> mget(String... keys) {
        return doExecute(Command.mget, ObjectResult.class,keys);
    }

    @Override
    public int incr(String key) {
        return doExecute(Command.incr,IntResult.class,key);
    }

    @Override
    public String get(String key) {
        return doExecute(Command.get, StringResult.class,key);

    }

    @Override
    public boolean expire(String key, long seconds) {
        return doExecute(Command.expire,BooleanResult.class,key,seconds);
    }

    @Override
    public boolean tryLock(String key, long expires) {
        //nx不存在才set,xx存在才set
        //EX代表秒，PX代表毫秒
        //解分布式锁，可以用到value值判断是否是自己的，如果说涉及到锁释放不了，高并发的，就用zk吧，redis还得考虑续命啥的，太麻烦了
        return doExecute(Command.set,BooleanResult.class,key,1,"nx","ex",expires);
    }

    //===========================hyperloglog操作
    @Override
    public int pfadd(String key, Object... param) {
        return doExecute(Command.pfadd,IntResult.class,key,param);

    }

    @Override
    public int pfcount(String key) {
        return doExecute(Command.pfcount,IntResult.class,key);
    }


    //===========================list操作
    //针对list中某个item修改
    @Override
    public Boolean setListIndex(String key, int index, Object value) {
        return doExecute(Command.lset, BooleanResult.class,key,value);
    }

    @Override
    public Object getListIndex(String key, int index) {
        return doExecute(Command.lindex, ListResult.class,key);
    }

    @Override
    public List getRangeList(String key, int start, int end) {
        return doExecute(Command.lrange, ObjectResult.class,key,start,end);
    }

    @Override
    public int leftPush(String key, Object value) {
        return doExecute(Command.lpush, IntResult.class,key,value);
    }

    @Override
    public int rightPush(String key, Object value) {
        return doExecute(Command.rpush, IntResult.class,key,value);
    }

    @Override
    public Object leftPop(String key, Boolean blocking) {
        return doExecute(Command.lpop, ObjectResult.class,key);
    }

    @Override
    public Object rightPop(String key, Boolean blocking) {
        return doExecute(Command.rpop, ObjectResult.class,key);
    }


    //===========================hash操作
    @Override
    public int hset(String key, String filed,Object o) {
        return doExecute(Command.hset, IntResult.class,key,filed,o);
    }

    /**
     * @param o 可以是Map<String,Object>，或者是我们的实体类
     */
    @Override
    public Boolean hsetObject(String key, Object o) {
        if (o instanceof Map) {
            Object[] r = new Object[((Map) o).size() * 2];
            int i = 0;
            for(Map.Entry m :  ((Map<Object,Object>) o).entrySet()){
                Object k = m.getKey();
                Object v = m.getValue();
                r[i] = k;
                ++i;
                r[i] = v;
                ++i;
            }
            return doExecute(Command.hmset, BooleanResult.class,key,r);
        } else  {
            Field[] fields = o.getClass().getDeclaredFields();
            //这里传递给redis的值是一个大数组，重点一个。最后面算len是算这个大数组的length的。
            Object[] v = new Object[fields.length * 2];
            for(int i = 0 ; i < fields.length ; i++) {
                fields[i].setAccessible(true);
                int j = 0;
                if(i != 0){
                    j = i+1;
                }
                v[j] = fields[i].getName();
                try {
                    v[j+1] = fields[i].get(o);
                } catch (IllegalAccessException e) {
                    throw new RedisWangException("[redis-wang] hmset 无法获取参数 o 的属性值");
                }
            }
            return doExecute(Command.hmset, BooleanResult.class,key,v);
        }
    }

    @Override
    public Object hget(String key,String filed) {
        return doExecute(Command.hget, ObjectResult.class,key,filed);
    }

    @Override
    public int hdel(String key, String filed) {
        return doExecute(Command.hdel, IntResult.class,key,filed);
    }

    @Override
    public List<Object> hkeys(String key) {
        return doExecute(Command.hkeys, ObjectResult.class,key);

    }

    @Override
    public List<Object> hvals(String key) {
        return doExecute(Command.hvals, ObjectResult.class,key);
    }

    @Override
    public Map<String, Object> hgetall(String key) {
        throw new RedisWangException("暂未实现");
    }

    //===========================set操作


    //===========================hash操作

}
