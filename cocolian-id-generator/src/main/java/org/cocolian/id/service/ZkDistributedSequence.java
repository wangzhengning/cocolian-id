package org.cocolian.id.service;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CreateBuilder;
import org.apache.curator.framework.api.GetACLBuilder;
import org.apache.curator.framework.api.GetChildrenBuilder;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.ACL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import sun.tools.tree.SynchronizedStatement;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * @description:zk(基于apache-curator)实现的信号器
 * @author: zn.wang , Created in 12:25 2018/6/24.
 */
public class ZkDistributedSequence {

    private static final Logger logger = LoggerFactory.getLogger(ZkDistributedSequence.class);

    private static final String DEFAULT_STR = "";

    private volatile CuratorFramework client;

    private Object lock = new Object();

    /**
     * Curator：最多重试次数
     */
    private int maxRetries=3;
    /**
     * Curator：初始的重试等待时间
     */
    private final int baseSleepTimeMs=1000;

    public ZkDistributedSequence(String zookeeperAddress){
        try{
            //重试一定次数，每次重试时间依次递增
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(baseSleepTimeMs, maxRetries);
            client = CuratorFrameworkFactory.newClient(zookeeperAddress, retryPolicy);
            client.start();
        }
        catch (Throwable ex){
            logger.error(ex.getMessage() , ex);
        }
    }

    /**
     * Curator默认提供了以下几种：
     * ExponentialBackoffRetry：重试一定次数，每次重试时间依次递增
     * RetryNTimes：重试N次
     * RetryOneTime：重试一次
     * RetryUntilElapsed：重试一定时间
     *
     * @param zookeeperAddress
     * @param retryPolicy
     */
    public ZkDistributedSequence(String zookeeperAddress , RetryPolicy retryPolicy){
        try{
            if(null == retryPolicy){
                throw new IllegalArgumentException("retryPolicy is not null.");
            }
            client = CuratorFrameworkFactory.newClient(zookeeperAddress, retryPolicy);
            client.start();
        }
        catch (Throwable ex){
            logger.error(ex.getMessage() , ex);
        }
    }

    public Long sequence(String sequenceName) {
        try{
            return InnerNodeBuilder.defaultGetNodeVersion(client , sequenceName);
        }
        catch (KeeperException.NoNodeException e){
            //only execute one time
            logger.error( "has no zNode , attempt create a new with sequenceName:{}", sequenceName , e );
            try {
                InnerNodeBuilder.defaultCreateNode(client , sequenceName);
                return InnerNodeBuilder.defaultGetNodeVersionNoThrowException(client , sequenceName);
            }
            catch (Exception e1) {
                logger.error( "create zNode error , nodePath:{}", sequenceName , e1);
            }
        }
        catch (Throwable ex){
            logger.error( "get sequence error, sequenceName:{}", sequenceName , ex);
        }
        return null;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public int getBaseSleepTimeMs() {
        return baseSleepTimeMs;
    }


    /**
     * 内部类：构建zNode节点
     */
    private static final class InnerNodeBuilder{

        /**
         * 默认方式：创建一个zNode
         * @param client
         * @param nodePath
         * @return
         */
        public static String defaultCreateNode(CuratorFramework client , String nodePath) throws Exception {
            return client.create().creatingParentsIfNeeded().forPath(nodePath);
        }

        /**
         * 默认方式：获取当前zNode的版本值
         * @param client
         * @param currentNodePath
         * @return
         */
        public static Long defaultGetNodeVersion(CuratorFramework client , String currentNodePath) throws Exception {
            int value = client.setData()
                    .withVersion(-1)
                    .forPath(currentNodePath , DEFAULT_STR.getBytes())
                    .getVersion();
            return new Long(value);
        }

        /**
         * 默认方式：获取当前zNode的版本值
         * @param client
         * @param currentNodePath
         * @return
         */
        public static Long defaultGetNodeVersionNoThrowException(CuratorFramework client , String currentNodePath) {
            int value = 0;
            try {
                value = client.setData()
                        .withVersion(-1)
                        .forPath(currentNodePath , DEFAULT_STR.getBytes())
                        .getVersion();
            } catch (Exception e) {
                logger.error( "has no zNode and no throw exception,  sequenceName:{}", currentNodePath , e );
            }
            return new Long(value);
        }

    }

}
