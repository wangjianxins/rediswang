package com.wang.redis.Serializer;


import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * @Description string序列化实现类
 * @author Jianxin Wang
 * @date 2019-08-27
 */
public class StringRedisSerializer implements Serializer<String>{

    private static final Charset charset = Charset.forName("UTF-8");

    @Override
    public byte[] serialize(String s) throws UnsupportedEncodingException {
        return s.getBytes("UTF-8");
    }

    @Override
    public String deserialize(byte[] bytes) {
        return (bytes == null ? null : new String(bytes, charset));
    }
}
