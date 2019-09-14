package com.wang.redis.connection.impl;

import com.wang.redis.Exception.RedisWangException;
import com.wang.redis.client.host.RedisWangClient;
import com.wang.redis.connection.Connection;
import com.wang.redis.connection.ConnectionPool;

import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description 哨兵连接池
 * @author Jianxin Wang
 * @date 2019-09-14
 */
public class SentinelPoolImpl implements ConnectionPool {

    private String mastername;

    private Set<String> sentinels;

    private ReentrantLock reentrantLock = new ReentrantLock();

    private SentinelPoolImpl(String mastername,Set<String> sentinels){
        this.mastername = mastername;
        this.sentinels = sentinels;

        //获取哨兵中的有效的master节点
        String masterinfo = getEffectiveMaster(mastername,sentinels);
    }

    //获得哨兵监控的redis节点中主节点的信息，这里的参数masterName不一定是正确的主节点，有可能是目前的主节点down了，哨兵正在做新的选举中
    public String getEffectiveMaster(String masterName,Set<String> sentinels){
        RedisWangClient redisWangClient;
        String master = null;
        Boolean sentineleislive = false;
        try {
            reentrantLock.lock();
            for(String sentinel : sentinels){
                sentineleislive = true;
                String address = sentinel.trim().split(":")[0];
                String port = sentinel.trim().split(":")[1];
                redisWangClient = new RedisWangClient(address,Integer.valueOf(port));
                List<String> masterAddr = redisWangClient.getSentinelMasterByName(masterName);
                if (masterAddr == null || masterAddr.size() != 2) {
                    //warm
                    System.out.println("(哨兵没有down)通过哨兵没有获得到master地址");
                    continue;
                }
                String masterhost =  masterAddr.get(0);
                int masterport = Integer.parseInt(masterAddr.get(1));
                System.out.println(masterhost);
                System.out.println(masterport);
                master = masterhost+":"+ port;
            }

            if(master == null){
                if(sentineleislive){
                    throw new RedisWangException("在哨兵中没有获取的master地址");
                }else{
                    throw new RedisWangException("哨兵都down了，无法获取master地址");
                }
            }



        }catch (Exception e){
            throw new RedisWangException("初始化哨兵模式连接池失败："+e.getMessage());
        }finally {
            //这里还要释放哨兵的连接

            reentrantLock.unlock();
        }

        return master;
    }


    @Override
    public Connection getConnection() {
        return null;
    }

    @Override
    public Connection getConnection(long second) {
        return null;
    }

    @Override
    public void releaseConnection(Connection connection) {

    }
}
