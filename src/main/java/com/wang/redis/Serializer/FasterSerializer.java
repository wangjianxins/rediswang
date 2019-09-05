package com.wang.redis.Serializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.util.IOUtils;
import com.wang.redis.Exception.RedisWangException;

public class FasterSerializer implements Serializer<Object> {

    private FastJsonConfig fastJsonConfig = new FastJsonConfig();

    public FastJsonConfig getFastJsonConfig() {
        return this.fastJsonConfig;
    }
    private static final ParserConfig defaultRedisConfig = new ParserConfig();

    public byte[] serialize(Object object) {
        if (object == null) {
            return new byte[0];
        } else {
            try {
                return JSON.toJSONBytes(object, new SerializerFeature[]{SerializerFeature.WriteClassName});
            } catch (Exception var3) {
                throw new RedisWangException("序列化失败: " + var3.getMessage()+var3);
            }
        }
    }

    public Object deserialize(byte[] bytes){
        if (bytes != null && bytes.length != 0) {
            try {
                return JSON.parseObject(new String(bytes, IOUtils.UTF8), Object.class, defaultRedisConfig, new Feature[0]);
            } catch (Exception var3) {
                throw new RedisWangException("反序列化失败: " + var3.getMessage()+ var3);
            }
        } else {
            return null;
        }
    }

    //开启自动类型匹配
    static {
        defaultRedisConfig.setAutoTypeSupport(true);
    }

}
