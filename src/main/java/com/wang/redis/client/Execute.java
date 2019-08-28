package com.wang.redis.client;

import com.wang.redis.Command.Command;
import com.wang.redis.connection.Connection;

public interface Execute<T> {

    T doExecute(Connection connection, Command command, Object ...params);
}
