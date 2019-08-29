package com.wang.redis.client;

import com.wang.redis.Command.Command;
import com.wang.redis.config.RedisWangProperties;
import com.wang.redis.connection.ConnectionPool;
import com.wang.redis.connection.impl.ConnectionPoolImpl;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;

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

    public static byte[] stringToBytes(String s){
        try {
            return s.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int del(String... key) {
        return doExecute(Command.del,IntResult.class,key);
    }

    @Override
    public boolean set(String key, java.lang.Object value) {
        return doExecute(Command.set,BooleanResult.class,key,value);
    }

    @Override
    public boolean mset(String[] keys, java.lang.Object... values) {
        return doExecute(Command.mset,BooleanResult.class,keys,values);

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
}
