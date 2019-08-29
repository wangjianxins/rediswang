package com.wang.redis.connection.impl;

import com.wang.redis.Exception.RedisWangException;
import com.wang.redis.config.RedisWangProperties;
import com.wang.redis.connection.Connection;
import com.wang.redis.connection.ConnectionPool;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description redis连接池
 * @author Jianxin Wang
 * @date 2019-08-27
 */
@Service
public class ConnectionPoolImpl implements ConnectionPool {

    private String address;
    private int port;

    //当前连接池
    private volatile LinkedList<Connection> connectionPool = new LinkedList<>();

    private ReentrantLock lock = new ReentrantLock();

    //最大连接数
    private final int maxSize = 20;

    //最小空闲数，用于初始化使用
    private final int minIdleSize = 3;

    //最大空闲数，用于释放连接使用
    private final int maxIdleSize = 5;

    //当前连接数
    private volatile int totalSize;

    public ConnectionPoolImpl(RedisWangProperties redisWangProperties){
        this.address = redisWangProperties.getAddress();
        this.port = redisWangProperties.getPort();
        if(this.port <= 0){
            throw new RedisWangException("[redis-wang]redis的端口设置错误");
        }
    }

    @Override
    public Connection getConnection() {
        return this.getConnection(0);
    }

    /**
     * @Description 获得连接
     * @author Jianxin Wang
     * @date 2019-08-27
     */
    @Override
    public Connection getConnection(long second) {
        initPoole();
        Connection connection = null;
        //首先判断连接池的大小还有多少
        //大于0说明还有连接可以拿取
        if (connectionPool.size() > 0){
            lock.lock();
            try {
                connection = connectionPool.removeLast();
            }catch (Exception e){

            }finally {
                lock.unlock();
            }
        }
        //当前为最小空闲数为空，依照策略进行获得连接
        if(connectionPool.size() == 0){
            long thisTime = System.currentTimeMillis();
            long oTime = second * 1000;
            thisTime += thisTime + oTime;
            while (connection == null){
                long currentTime = System.currentTimeMillis();
                lock.lock();
                try {
                    if (connectionPool.size() > 0) {
                        connection = connectionPool.removeLast();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }finally {
                    lock.unlock();
                }
                //时间到了
                if(currentTime > thisTime){
                    break;
                }
            }
        }

        if(null == connection){
            throw new RedisWangException("[redis-wang]当前获得redis实例为空");

            //激活重试等待机制
        }

        return connection;
    }

    /**
     * @Description 释放连接
     * @author Jianxin Wang
     * @date 2019-08-27
     */
    @Override
    public void releaseConnection(Connection connection) {
        lock.lock();
        try {
            connectionPool.add(connection);
        } finally {
            lock.unlock();
        }
        if (connectionPool.size() > maxIdleSize) {
            lock.lock();
            try {
                if (connectionPool.size() > maxIdleSize) {
                    decrementPool(connectionPool.size() - maxIdleSize);
                }
            } finally {
                lock.unlock();
            }
        }

    }

    /**
     * @Description 初始化pool,初始化到最小空闲数
     * @author Jianxin Wang
     * @date 2019-08-27
     */
    public void initPoole(){
        if (connectionPool.size() < minIdleSize) {
            lock.lock();
            try {
                incrementPool();
            } catch (Exception e) {
                throw new RedisWangException("[redis-wang]获得连接池错误");
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * @Description 连接池子减少
     * @author Jianxin Wang
     * @date 2019-08-27
     */
    public void decrementPool(int len){
        lock.lock();
        try {
            for (int i = 0; i < len; i++) {
                try {
                    (connectionPool.removeLast()).close();
                } catch (NoSuchElementException e) {
                    break;
                }
                totalSize--;
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * @Description 增加连接池
     * @author Jianxin Wang
     * @date 2019-08-27
     */
    public void incrementPool() throws IOException {
        if (totalSize >= maxSize) {
            return;
        }
        int len = minIdleSize - connectionPool.size();
        for(int i = 0; i < len; i++){
//            Connection connection = new ConnectionImpl("127.0.0.1",6379);
            Connection connection = new ConnectionImpl(address,port);
            connectionPool.add(new ConnectionProxy(connection,this));
            totalSize++;
        }
    }
}
