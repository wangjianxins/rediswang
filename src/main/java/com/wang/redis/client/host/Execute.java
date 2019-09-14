package com.wang.redis.client.host;

import com.wang.redis.Command.Command;
import com.wang.redis.connection.Connection;

public interface Execute<T> {

    T doExecute(Connection connection, Command command, Object ...params);

    T doSentinelExecute(Connection connection, String commandName, Object ...params);
}
