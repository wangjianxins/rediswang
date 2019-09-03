package com.wang.redis.client;


import com.sun.org.apache.xpath.internal.operations.Bool;

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

    String get(String key);

    boolean expire(String key, int seconds);

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


    //list,这里没次的value最好都统一在list中，list<T>

    Boolean setList(String key, List list,long expires);

    Boolean setIndex(String key,int index,Object value);

    Boolean leftPush(String key,Object value);

    Boolean rightPush(String key,Object value);

    /**
     * @param blocking 是否阻塞等赛
     */
    Boolean leftPop(String key, Boolean blocking);

    /**
     * @param blocking 是否阻塞等赛
     */
    Boolean rightPop(String key,Boolean blocking);



}
