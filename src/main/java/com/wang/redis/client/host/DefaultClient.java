package com.wang.redis.client.host;

import com.wang.redis.Command.Command;
import com.wang.redis.connection.ConnectionPool;

import java.lang.reflect.InvocationTargetException;

/**
 * @Description 默认实现类
 * @author Jianxin Wang
 * @date 2019-09-09
 */
public abstract class DefaultClient implements BaseClient {

    protected ConnectionPool connectionPool;

    public DefaultClient(ConnectionPool connectionPool){
        this.connectionPool = connectionPool;
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

    public <T> T doSentinelExecute(String commandName,Class<? extends Execute<T>> execute ,Object ...param){
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

        return (T) commandInstance.doSentinelExecute(connectionPool.getConnection(),commandName,param);
    }

}
