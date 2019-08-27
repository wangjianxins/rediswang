package com.wang.redis.connection.impl;

import com.wang.redis.connection.Connection;
import com.wang.redis.connection.ConnectionPool;
import com.wang.redis.io.RedisInputStream;
import com.wang.redis.io.RedisOutputStream;

import java.io.InputStream;
import java.io.OutputStream;

public class ConnectionProxy implements Connection {

    private Connection connection;

    private ConnectionPool connectionPool;

    public ConnectionProxy(Connection connection, ConnectionPool connectionPool) {
        this.connection = connection;
        this.connectionPool = connectionPool;
    }

    @Override
    public RedisInputStream getInputStream() {
        return connection.getInputStream();
    }

    @Override
    public RedisOutputStream getOutputStream() {
        return connection.getOutputStream();
    }

    /**
     * @Description 伪代理，主要是这里
     * @author Jianxin Wang
     * @date 2019-08-27
     */
    @Override
    public void close() {
        connectionPool.releaseConnection(this);
    }

    @Override
    public Boolean isClosed() {
        return connection.isClosed();
    }
}
