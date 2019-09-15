package com.wang.redis.Serializer;


import com.wang.redis.Exception.RedisWangException;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * @Description string序列化实现类
 * @author Jianxin Wang
 * @date 2019-08-27
 */
public class StringRedisSerializer {

    private static final Charset charset = Charset.forName("UTF-8");

    public static byte[] serialize(String s) {
        try {
            return s.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
           throw new RedisWangException("字符串getbyte错误");
        }
    }

    public static String deserialize(byte[] bytes) {
        return (bytes == null ? null : new String(bytes, charset));
    }
}
