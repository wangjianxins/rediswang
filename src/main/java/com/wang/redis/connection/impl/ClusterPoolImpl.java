package com.wang.redis.connection.impl;

import com.wang.redis.Command.Command;
import com.wang.redis.Serializer.StringRedisSerializer;
import com.wang.redis.client.cluster.RedisClusterCache;
import com.wang.redis.client.cluster.RedisClusterClient;
import com.wang.redis.connection.Connection;
import com.wang.redis.result.ObjectResult;
import com.wang.redis.util.RedisClusterCRC16;
import org.apache.log4j.Logger;
import java.util.List;
import java.util.Set;

/**
 * @Description 集群模式连接池
 * @author Jianxin Wang
 * @date 2019-09-17
 */
public class ClusterPoolImpl extends DefaultAbstractPoolImpl {

    //是否开启集群模式的虚拟槽算法
    protected Boolean clusterFlag;

    public static final String CLUSTER_SLOTS = "slots";

    private static final Logger logger = Logger.getLogger(DefaultAbstractPoolImpl.class);

    private Set<String> clusterHost;
    private int maxAttempts;
    private String address;
    private int port;

    private RedisClusterCache redisClusterCache;

    public ClusterPoolImpl(Set<String> clusterHost,int maxAttempts){
        redisClusterCache = new RedisClusterCache();
        this.clusterHost = clusterHost;
        this.maxAttempts = maxAttempts;
        initcache();
    }

    public ClusterPoolImpl(String nodeHost){
        this.address = nodeHost.split(":")[0];
        this.port = Integer.valueOf(nodeHost.split(":")[1]);
    }

    private void initcache(){
        for(String host : clusterHost){
            String address = host.split(":")[0];
            String port = host.split(":")[1];
            this.address = address;
            this.port = Integer.valueOf(port);
            RedisClusterClient clusterClient = new RedisClusterClient(this,address,Integer.valueOf(port));
//            if (password != null) {
//                clusterClient.auth(password);
//            }
            clusterFlag = false;
            List<Object> slots = clusterClient.doExecute(Command.cluster, ObjectResult.class,CLUSTER_SLOTS);
            for(Object slot : slots){
                List<Object> slotInfo = (List<Object>) slot;
                if (slotInfo.size() <= 2) {
                    continue;
                }
                //根据配置的集群host,发现其他的节点
                redisClusterCache.getAllNodeInfo(slotInfo);
            }

            clusterClient.close(connectionPool);

        }
    }


    @Override
    public Connection connection(Object key) {
        byte[] k = new byte[0];
        if(!(key instanceof byte[])){
            k = StringRedisSerializer.serialize(key.toString());
        }
        return redisClusterCache.getConnectionByKey(RedisClusterCRC16.getSlot(k));
    }


}
