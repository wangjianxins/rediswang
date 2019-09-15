package com.wang.redis.connection;

import java.util.List;

public interface ConnectionPool {

    Connection getConnection();

    List<Connection> getAllConection();

    Connection getConnection(long second);

    void releaseConnection(Connection connection);
}
