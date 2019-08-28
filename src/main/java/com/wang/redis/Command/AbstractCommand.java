package com.wang.redis.Command;

import com.wang.redis.connection.Connection;
import com.wang.redis.io.RedisInputStream;
import java.io.UnsupportedEncodingException;

public abstract class AbstractCommand<T> extends BaseCommand{


    protected Connection connection;

    public static final byte[] PART = new byte[] { ':' };

    public AbstractCommand(Connection connection){
        this.connection = connection;
    }

    /**
     * @Description 接收信息
     * @author Jianxin Wang
     * @date 2019-08-27
     */
    protected abstract Object receive(RedisInputStream inputStream, Object... arguments) throws Exception;


    public static byte[] stringToBytes(String s){
        try {
            return s.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
