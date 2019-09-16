package com.wang.redis.result;

import com.wang.redis.Command.Command;
import com.wang.redis.client.host.AbstractExecute;
import com.wang.redis.io.RedisInputStream;

public class VoidResult extends AbstractExecute<Void> {
    @Override
    protected Object receive(RedisInputStream inputStream, Command command, Object... arguments) throws Exception {
        return null;
    }
}
