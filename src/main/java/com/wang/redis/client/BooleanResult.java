package com.wang.redis.client;

import com.wang.redis.Command.Command;
import com.wang.redis.io.RedisInputStream;
import org.springframework.util.StringUtils;

public class BooleanResult extends AbstractExecute<Boolean> {

    @Override
    protected Object receive(RedisInputStream inputStream, Command command, Object... arguments) throws Exception {
        String result = inputStream.readLine();
        if(result.contains("OK")){
            return true;
        }
        return false;
    }
}
