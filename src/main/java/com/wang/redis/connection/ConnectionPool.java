package com.wang.redis.connection;

import java.util.List;

public interface ConnectionPool {

    Connection getConnection(Object key);

    List<Connection> getAllConection();

    Connection getConnection(long second,Object key);

    void releaseConnection(Connection connection);
}
