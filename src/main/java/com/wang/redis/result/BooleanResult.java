package com.wang.redis.result;

import com.wang.redis.Command.Command;
import com.wang.redis.client.AbstractExecute;
import com.wang.redis.io.RedisInputStream;

/**
 * @Description boolean类型的返回
 * @author Jianxin Wang
 * @date 2019-08-29
 */
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
