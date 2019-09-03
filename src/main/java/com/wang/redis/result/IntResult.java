package com.wang.redis.result;

import com.wang.redis.Command.Command;
import com.wang.redis.client.AbstractExecute;
import com.wang.redis.io.RedisInputStream;

/**
 * @Description int类型的返回
 * @author Jianxin Wang
 * @date 2019-08-29
 */
public class IntResult extends AbstractExecute<Integer> {

    @Override
    protected Object receive(RedisInputStream inputStream, Command command, Object... arguments) throws Exception {
        String result = inputStream.readLine();
        if(result.contains(":")){
            return Integer.valueOf(result.replace(":",""));
        }
        return 0;
    }
}
