package com.wang.redis.connection;

public interface ConnectionPool {

    Connection getConnection();

    Connection getConnection(long second);

    void releaseConnection(Connection connection);
}
