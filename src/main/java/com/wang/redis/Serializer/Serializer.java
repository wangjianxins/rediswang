package com.wang.redis.Serializer;

import java.io.UnsupportedEncodingException;

/**
 * @Description 序列化接口
 * @author Jianxin Wang
 * @date 2019-08-27
 */
public interface Serializer<T> {


    byte[] serialize(T t) throws UnsupportedEncodingException;


    T deserialize(byte[] bytes);
}
