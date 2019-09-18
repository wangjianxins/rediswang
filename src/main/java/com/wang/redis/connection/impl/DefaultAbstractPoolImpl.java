package com.wang.redis.connection.impl;

import com.wang.redis.Exception.RedisWangException;
import com.wang.redis.connection.Connection;
import com.wang.redis.connection.ConnectionPool;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description 默认实现类
 * @author Jianxin Wang
 * @date 2019-09-15
 */
public abstract class DefaultAbstractPoolImpl implements ConnectionPool {

    private static final Logger logger = Logger.getLogger(ConnectionPoolImpl.class);

    private ReentrantLock lock = new ReentrantLock();

    //当前连接池
    protected volatile LinkedList<Connection> connectionPool = new LinkedList();

    //最大连接数
    private final int maxSize = 20;

    //最小空闲数，用于初始化使用
    private final int minIdleSize = 3;

    //最大空闲数，用于释放连接使用
    private final int maxIdleSize = 10;

    //当前连接数
    protected volatile int totalSize;

    @Override
    public Connection getConnection(Object key) {
        return this.getConnection(0,key);
    }

    public List<Connection> getAllConection(){
        return connectionPool;
    }

    /**
     * @Description 获得连接
     * @author Jianxin Wang
     * @date 2019-08-27
     */
    @Override
    public Connection getConnection(long second,Object key) {
        initPoole(key);
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
     * @Description 初始化pool,初始化到最小空闲数
     * @author Jianxin Wang
     * @date 2019-08-27
     */
    public void initPoole(Object key){
        logger.debug("[当前redis连接池大小为;]"+connectionPool.size());
        if (connectionPool.size() < minIdleSize) {
            lock.lock();
            try {
                incrementPool(key);
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
    public void incrementPool(Object key) throws IOException {
        if (totalSize >= maxSize) {
            return;
        }
        int len = minIdleSize - connectionPool.size();
        for(int i = 0; i < len; i++){
            connectionPool.add(new ConnectionProxy(connection(key),this));
            totalSize++;
        }
    }


    /**
     * @Description 获得连接
     * @author Jianxin Wang
     * @date 2019-09-02
     */
    public abstract Connection connection(Object key);

    /**
     * @Description 释放连接
     * @author Jianxin Wang
     * @date 2019-08-27
     */
    @Override
    public void releaseConnection(Connection connection) {
        lock.lock();
        try {
            if (connectionPool.size() < maxIdleSize) {
                connectionPool.add(connection);
            } else {
                connection.close();
                totalSize--;
            }
        } finally {
            lock.unlock();
        }
    }
}
