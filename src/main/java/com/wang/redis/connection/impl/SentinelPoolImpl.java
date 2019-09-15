package com.wang.redis.connection.impl;

import com.wang.redis.Exception.RedisWangException;
import com.wang.redis.client.sentinel.SimpleSentinelClient;
import com.wang.redis.connection.Connection;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description 哨兵连接池
 * @author Jianxin Wang
 * @date 2019-09-14
 */
public class SentinelPoolImpl extends DefaultAbstractPoolImpl {

    private static final Logger logger = Logger.getLogger(ConnectionPoolImpl.class);

    private ReentrantLock reentrantLock = new ReentrantLock();

    private volatile String currentHostMaster;

    public SentinelPoolImpl(String address,int port){
        currentHostMaster = address+":"+port;
    }

    public SentinelPoolImpl(String master,String sentinels){
        String[] sentinalArray = sentinels.split(",");
        Set<String> set = new HashSet<>();
        for(String s : sentinalArray){
            set.add(s);
        }
        this.createSentinelPool(master, set);
    }


    public void createSentinelPool(String masterName,Set<String> sentinels){
        //获取哨兵中的有效的master节点
        String masterInfo = getEffectiveMaster(masterName,sentinels);
        //init
        initSentinelPool(masterInfo);
    }

    //获得哨兵监控的redis节点中主节点的信息，这里的参数masterName不一定是正确的主节点，有可能是目前的主节点down了，哨兵正在做新的选举中
    public String getEffectiveMaster(String masterName,Set<String> sentinels){
        //初始化连接哨兵的池,用完需要关闭连接
        SimpleSentinelClient simpleSentinelClient = null;
        String master = null;
        Boolean sentineleislive = false;
        try {
            reentrantLock.lock();
            for(String sentinel : sentinels){
                sentineleislive = true;
                logger.info("sentinel:"+sentinel);
                String address = sentinel.trim().split(":")[0];
                String port = sentinel.trim().split(":")[1];
                if(simpleSentinelClient == null){
                    simpleSentinelClient = new SimpleSentinelClient(address,Integer.valueOf(port));
                }
                List<Object> masterAddr = simpleSentinelClient.getSentinelMasterByName(masterName);
                logger.info("masterAddr:"+masterAddr);
                if (masterAddr == null || masterAddr.size() != 2) {
                    //warn
                    logger.warn("(哨兵没有down)通过哨兵没有获得到master地址");
                    continue;
                }
                String masterhost = (String) masterAddr.get(0);
                Integer masterport = (Integer) masterAddr.get(1);
                master = masterhost+":"+ masterport;
            }

            if(master == null){
                if(sentineleislive){
                    throw new RedisWangException("在哨兵中没有获取的master地址");
                }else{
                    throw new RedisWangException("哨兵都down了，无法获取master地址");
                }
            }
            currentHostMaster = master;
            //这里需要订阅哨兵的频道，随时的得知哨兵选举的最新的master



        }catch (Exception e){
            e.printStackTrace();
            throw new RedisWangException("初始化哨兵模式连接池失败："+e.getMessage());
        }finally {
            //这里还要释放哨兵的连接
            simpleSentinelClient.close();
            reentrantLock.unlock();
        }

        return master;
    }

    public void initSentinelPool(String masterInfo){
        //和当前哨兵的master不一样才要开始初始化的
        if(!masterInfo.equals(currentHostMaster)){

        }
    }

    @Override
    public Connection connection() {
        String address = currentHostMaster.split(":")[0];
        String port = currentHostMaster.split(":")[1];
        Connection connection = null;
        try {
            connection = new ConnectionImpl(address,Integer.valueOf(port));
        } catch (IOException e) {
            logger.error("[wang-redis]连接错误");
        }
        return connection;
    }


}
