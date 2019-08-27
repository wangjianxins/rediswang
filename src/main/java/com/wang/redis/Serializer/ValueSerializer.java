package com.wang.redis.Serializer;

import java.io.UnsupportedEncodingException;

public class ValueSerializer implements Serializer {
    @Override
    public byte[] serialize(Object o) throws UnsupportedEncodingException {
        return new byte[0];
    }

    @Override
    public Object deserialize(byte[] bytes) {
        return null;
    }
}
