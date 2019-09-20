package com.wang.redis.client.cluster;

import com.wang.redis.connection.impl.ClusterPoolImpl;
import com.wang.redis.transmission.HostInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RedisClusterCache {

    private final Map<String, ClusterPoolImpl> nodes = new HashMap<>();
    private final Map<Integer, ClusterPoolImpl> slots = new HashMap<>();

    public void getAllNodeInfo(List<Object> slotInfo){
        List<Integer> slotNums = getAssignedSlotArray(slotInfo);
        int len = slotInfo.size();
        for (int i = 2; i < len; i++) {
            List<Object> hostInfos = (List<Object>) slotInfo.get(i);
            if (hostInfos.size() <= 0) {
                continue;
            }

            String nodeAddress = (String) hostInfos.get(0);
            int nodePort = ((Long) hostInfos.get(1)).intValue();
            String nodeHost = nodeAddress+":"+nodePort;
            //保存节点连接池缓存
            setupNodeIfNotExist(nodeHost);
            if (i == 2) {
                assignSlotsToNode(slotNums, nodeHost);
            }
        }
    }

    public ClusterPoolImpl setupNodeIfNotExist(String nodeHost) {
        ClusterPoolImpl nodePool = null;

        try {
            String nodeKey = nodeHost;
            ClusterPoolImpl existingPool = nodes.get(nodeKey);
            if (existingPool != null) {
                return existingPool;
            }
            nodePool = new ClusterPoolImpl(nodeHost);
            nodes.put(nodeKey, nodePool);
        }catch (Exception e){
            e.printStackTrace();
        }

        return nodePool;
    }

    public void assignSlotsToNode(List<Integer> targetSlots, String nodeHost) {
        try {
            ClusterPoolImpl targetPool = setupNodeIfNotExist(nodeHost);
            for (Integer slot : targetSlots) {
                slots.put(slot, targetPool);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private List<Integer> getAssignedSlotArray(List<Object> slotInfo) {
        List<Integer> slotNums = new ArrayList<>();
        int slot = ((Long) slotInfo.get(0)).intValue();
        int c = ((Long) slotInfo.get(1)).intValue();
        for (;slot <= c; slot++) {
            slotNums.add(slot);
        }
        return slotNums;
    }

    public HostInfo getConnectionByKey(int slot){
        ClusterPoolImpl connectionPool = slots.get(slot);
        if (connectionPool != null) {
            // It can't guaranteed to get valid connection because of node
            // assignment
            HostInfo hostInfo = new HostInfo();
            hostInfo.setAdress(connectionPool.getAddress());
            hostInfo.setPort(connectionPool.getPort());
            return hostInfo;
        } else {
            //初始化从新，根据node的缓存map
//            connectionPool = cache.getSlotPool(slot);
//            if (connectionPool != null) {
//                return connectionPool.getResource();
//            } else {
//                //no choice, fallback to new connection to random node
//                return getConnection();
//            }
            return null;
        }
    }


}
