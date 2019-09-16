package com.wang.redis.connection;

import com.wang.redis.io.RedisInputStream;
import com.wang.redis.io.RedisOutputStream;


public interface Connection {

    RedisInputStream getInputStream();

    RedisOutputStream getOutputStream();

    void setTimeoutInfinite();

    void close();

    Boolean isClosed();
}
