package org.cocolian.id.rpc;

import org.cocolian.id.IdRpcService;
import org.cocolian.id.service.UUIDDistributedSequence;
import org.cocolian.rpc.NotFoundException;
import org.cocolian.rpc.SystemException;
import org.cocolian.rpc.UserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: zn.wang , Created in 23:18 2018/6/24.
 */
@Component("uUIDIdController")
public class UUIDIdController extends AbstractGenerateIdController{

    private static final Logger logger = LoggerFactory.getLogger(UUIDIdController.class);

    @Autowired
    private UUIDDistributedSequence uUIDDistributedSequence;

    @Bean
    public UUIDDistributedSequence instanceUUIDSequenceBean(){
        return new UUIDDistributedSequence();
    }

    @Override
    public IdRpcService.GenerateIdResponse process(IdRpcService.GenerateIdRequest request) throws NotFoundException, SystemException, UserException {

        IdRpcService.GenerateIdResponse.Builder response = IdRpcService.GenerateIdResponse.newBuilder();
        for(int i = 0; i < request.getCount(); i++){
            Long sequence = uUIDDistributedSequence.sequence("");
            response.addId(sequence);
        }
        return response.build();
    }
}
