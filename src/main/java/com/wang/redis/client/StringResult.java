package com.wang.redis.client;

import com.wang.redis.Command.Command;
import com.wang.redis.io.RedisInputStream;


public class StringResult extends AbstractExecute<String> {

    @Override
    protected Object receive(RedisInputStream inputStream, Command command, Object... arguments) throws Exception {
        return null;
    }
}
