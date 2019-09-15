package com.wang.redis.client.host;

import com.wang.redis.Command.Command;
import com.wang.redis.connection.Connection;
import com.wang.redis.connection.ConnectionPool;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @Description 默认实现类
 * @author Jianxin Wang
 * @date 2019-09-09
 */
public abstract class DefaultExecute {

    protected ConnectionPool connectionPool;

    public DefaultExecute(ConnectionPool connectionPool){
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

    public ConnectionPool getConnectionPool() {
        return connectionPool;
    }

    public void setConnectionPool(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    //关闭client所有连接
    public void close(){
        List<Connection> connectionList = connectionPool.getAllConection();
        connectionList.forEach(con -> con.close());
    }

}