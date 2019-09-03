package com.wang.redis.result;

import com.wang.redis.Command.Command;
import com.wang.redis.client.AbstractExecute;
import com.wang.redis.io.RedisInputStream;

/**
 * @Description String类型的返回，这里只返回字符串类型的，最好是set的时候就是字符串，不需要其他乱七八糟的object类型，方便开发，没有歧义
 * @author Jianxin Wang
 * @date 2019-09-02
 */
public class StringResult extends AbstractExecute<String> {

    @Override
    protected String receive(RedisInputStream inputStream, Command command, Object... arguments) throws Exception {
        String result = inputStream.readLine();
        if(result.contains("$")){
            if(String.valueOf(result.charAt(1)).equals("1")){
                Object o = arguments[0];
                return (String)o;
            }
        }

        return "";
    }
}
