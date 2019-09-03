package com.wang.redis.client;

import com.wang.redis.Command.Command;
import com.wang.redis.Exception.RedisWangException;
import com.wang.redis.config.RedisWangProperties;
import com.wang.redis.connection.ConnectionPool;
import com.wang.redis.connection.impl.ConnectionPoolImpl;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @Description 具体的一个执行client
 * @author Jianxin Wang
 * @date 2019-08-28
 */
public class RedisWangClient implements WangClient  {

    protected ConnectionPool connectionPool;

    public RedisWangClient(RedisWangProperties redisWangProperties){
        connectionPool = new ConnectionPoolImpl(redisWangProperties);
    }

    public <T>T doExecute(Command command,Class<? extends Execute<T>> execute ,Object ...params){
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
        return doExecute(Command.del,IntResult.class,key);
    }

    /**
     * @Description expires暂时只支持EX，秒级别
     * @author Jianxin Wang
     * @date 2019-09-03
     */
    @Override
    public boolean setString(String key, String value,long expires) {
        if (expires != 0) {
            return doExecute(Command.setex,BooleanResult.class,key,expires,value);
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
    public int incr(String key) {
        return doExecute(Command.incr,IntResult.class,key);
    }

    @Override
    public String get(String key) {
        return doExecute(Command.get,StringResult.class,key);

    }

    @Override
    public boolean expire(String key, int seconds) {
        return doExecute(Command.expire,BooleanResult.class,key);
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
    @Override
    public Boolean setList(String key, List list, long expires) {
        return null;
    }

    @Override
    public Boolean setIndex(String key, int index, Object value) {
        return null;
    }

    @Override
    public Boolean leftPush(String key, Object value) {
        return null;
    }

    @Override
    public Boolean rightPush(String key, Object value) {
        return null;
    }

    @Override
    public Boolean leftPop(String key, Boolean blocking) {
        return null;
    }

    @Override
    public Boolean rightPop(String key, Boolean blocking) {
        return null;
    }

    //===========================set操作


    //===========================hash操作

}
