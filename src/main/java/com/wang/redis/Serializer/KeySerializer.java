package com.wang.redis.Serializer;

import com.sun.xml.internal.ws.encoding.soap.SerializationException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;

public class KeySerializer implements Serializer {

    @Override
    public byte[] serialize(Object o) throws UnsupportedEncodingException {
        byte[] bytes;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(o);
            oos.flush();
            bytes = bos.toByteArray ();
            oos.close();
            bos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new SerializationException("序列化key失败了:"+ ex.getMessage());
        }
        return bytes;
    }

    @Override
    public Object deserialize(byte[] bytes) {
        return null;
    }
}
