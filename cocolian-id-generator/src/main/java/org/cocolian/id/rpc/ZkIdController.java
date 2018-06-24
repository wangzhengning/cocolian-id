package org.cocolian.id.rpc;

import jdk.nashorn.internal.objects.annotations.Getter;
import org.apache.commons.lang3.RandomUtils;
import org.cocolian.id.IdRpcService;
import org.cocolian.id.RedisTemplate;
import org.cocolian.id.service.DistributedSequence;
import org.cocolian.id.service.ZkDistributedSequence;
import org.cocolian.metric.Timer;
import org.cocolian.rpc.NotFoundException;
import org.cocolian.rpc.SystemException;
import org.cocolian.rpc.UserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import redis.clients.jedis.ShardedJedisPool;

import javax.annotation.Resource;
import java.util.Iterator;

/**
 * @description:
 * @author: zn.wang , Created in 14:09 2018/6/24.
 */
@Component("zkIdController")
public class ZkIdController extends AbstractGenerateIdController{

    private static final Logger logger = LoggerFactory.getLogger(ZkIdController.class);

    @Value("${rpc.server.zookeeper.connect.string}")
    private String zookeeperAddress;

    @Value("${zk.id.generator.path.string}")
    private String zkIdGeneratorPath;

    @Autowired
    private DistributedSequence distributedSequence;

    @Bean
    public DistributedSequence instanceBean(){
        return new ZkDistributedSequence(zookeeperAddress);
    }

    @Timer("zkIdController")
    @Override
    public IdRpcService.GenerateIdResponse process(IdRpcService.GenerateIdRequest request) throws NotFoundException, SystemException, UserException {
        logger.info("zkIdController {} id for {}", request.getCount(), request.getUserName());
        IdRpcService.GenerateIdResponse.Builder response = IdRpcService.GenerateIdResponse.newBuilder();
        for(int i = 0; i < request.getCount(); i++){
           Long sequence = distributedSequence.sequence(zkIdGeneratorPath);
           response.addId(sequence);
        }
        return response.build();
    }

}
