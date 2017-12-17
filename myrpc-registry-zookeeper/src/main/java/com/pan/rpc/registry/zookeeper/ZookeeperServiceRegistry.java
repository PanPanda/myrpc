package com.pan.rpc.registry.zookeeper;

import com.pan.rpc.registry.ServiceRegistry;
import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author panzheng
 * @ClassName:
 * @Description:
 * @date 2017/12/17
 */
public class ZookeeperServiceRegistry implements ServiceRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZookeeperServiceRegistry.class);

    private final ZkClient zkClient;

    public ZookeeperServiceRegistry(String zkAddress) {
        zkClient = new ZkClient(zkAddress,Constant.ZK_CONNECTION_TIMEOUT,Constant.ZK_CONNECTION_TIMEOUT);
        LOGGER.debug("connect zookeeper");
    }

    @Override
    public void register(String serviceName, String serviceAddress) {

        //创建registry 节点（持久）
        String registryPath = Constant.ZK_REGISTRY_PATH;
        if(!zkClient.exists(registryPath)) {
            zkClient.createPersistent(registryPath);
            LOGGER.debug("create registry node :{}",registryPath);
        }

        //创建service节点（持久）
        String servicePath = registryPath + "/" + serviceName;
        if(!zkClient.exists(servicePath)) {
            zkClient.createPersistent(servicePath);
            LOGGER.debug("create service node :{}",servicePath);
        }

        //创建address节点(临时)
        String addressPath = servicePath + "address-";
        String addressNode = zkClient.createEphemeralSequential(addressPath,serviceAddress);
        LOGGER.debug("create address node: {}",addressNode);
    }
}
