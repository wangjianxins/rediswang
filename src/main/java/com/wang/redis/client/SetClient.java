package com.wang.redis.client;


import java.util.Set;

/**
 * @Description 有关set  、 zset数据结构的命令
 * @author Jianxin Wang
 * @date 2019-09-09
 */
public interface SetClient {

    //添加set
    int sadd(Object... value);

    //删除，返回删除元素的个数
    int srem(Object... value);

    //计算set集合的个数
    int scount();

    //itme是否在set集合中，返回1存在，0不存在
    int scontains(Object item);

    //随机中set集合中返回count个元素
    Object srandmerber(int count);

    //随机弹出一个元素，删除操作
    Object spop();

    //获得所有元素
    Set getall();

    Set sunion(String key2);

    Set sdiff(String key2);

}
