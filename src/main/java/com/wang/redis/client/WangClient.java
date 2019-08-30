package com.wang.redis.client;
import java.util.concurrent.TimeUnit;

/**
 * @Description 
 * @author Jianxin Wang
 * @date 2019-08-28
 */
public interface WangClient {
    
    int del(String ...key);

    boolean set(String key, Object value);

    boolean mset(String[] keys,Object... values);

    int incr(String key);

    String get(String key);

    boolean expire(String key, int seconds);

    /**
     * 实现分布式锁
     */
    boolean tryLock(String key,long expires);


}
