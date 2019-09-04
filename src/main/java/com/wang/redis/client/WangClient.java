package com.wang.redis.client;


import com.wang.redis.result.IntResult;

import java.util.List;

/**
 * @Description 
 * @author Jianxin Wang
 * @date 2019-08-28
 */
public interface WangClient {
    
    int del(String ...key);

    boolean setString(String key, String value,long expires);

    boolean mset(String[] keys,Object... values);

    int incr(String key);

    String getString(String key);

    boolean expire(String key, long seconds);

    /**
     * 实现分布式锁
     */
    boolean tryLock(String key,long expires);

    /**
     * hyperLogLog数据结构，一般用于很大数量的add和统计，适用于，统计一个网站访问量啥的，对于精确度要求不是很高的可以
     * hyperLogLog 0.81%的错误率
     */
    int pfadd(String key,Object ...param);

    /**
     * hyperLogLog数据结构，获得count,精确度不准确
     */
    int pfcount(String key);


    //list

    Boolean setListIndex(String key,int index,Object value);

    Object getListIndex(String key,int index);

    List getRangeList(String key,int start,int end);

    int leftPush(String key, Object value);

    int rightPush(String key,Object value);

    /**
     * @param blocking 是否阻塞等待
     */
    Object leftPop(String key, Boolean blocking);

    /**
     * @param blocking 是否阻塞等待
     */
    Object rightPop(String key,Boolean blocking);



}
