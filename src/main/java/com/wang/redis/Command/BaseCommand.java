package com.wang.redis.Command;

import com.wang.redis.Serializer.Serializer;
import com.wang.redis.Serializer.StringRedisSerializer;

import java.io.UnsupportedEncodingException;

public class BaseCommand {

    public Serializer stringSerializer = new StringRedisSerializer();

    public byte[] rawString(String key) throws UnsupportedEncodingException {
        return stringSerializer.serialize(key);
    }
}
