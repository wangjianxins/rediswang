package com.wang.redis.connection.impl;

import com.wang.redis.connection.Connection;
import com.wang.redis.connection.ConnectionPool;

public class ConnectionProxy implements Connection {

    private Connection connection;

    private ConnectionPool connectionPool;

    public ConnectionProxy(Connection connection, ConnectionPool connectionPool) {
        this.connection = connection;
        this.connectionPool = connectionPool;
    }

    @Override
    public void close() {
        connectionPool.releaseConnection(this);
    }
}
