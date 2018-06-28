package org.cocolian.id.rpc;

import org.apache.commons.lang3.RandomUtils;
import org.cocolian.id.IdRpcService.GenerateIdRequest;
import org.cocolian.id.IdRpcService.GenerateIdResponse;
import org.cocolian.id.exception.InvalidBatchSizeException;
import org.cocolian.id.redis.CocolianIdGenerator;
import org.cocolian.id.redis.Id;
import org.cocolian.metric.Timer;
import org.cocolian.rpc.NotFoundException;
import org.cocolian.rpc.SystemException;
import org.cocolian.rpc.UserException;
import org.cocolian.rpc.server.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;;import java.util.List;
import java.util.Optional;

/**
 * 生成创建订单的Id和Key。 Id可以支持2~128个数据库（2为模）X 10张表的分表分库策略，
 * 并且在增加分库（比如从2增加到4增加到8等）时，已有的订单id仍然可以支持按照用户uid进行分片的需求。 确保每次产生的Key可以在跨表跨库中递增。
 * 
 * @author shamphone@gmail.com
 * @version 1.0.0
 * @date 2017年8月22日
 */
@Component("generateId")
public class GenerateIdController implements Controller<GenerateIdRequest, GenerateIdResponse> {
	private static final Logger logger = LoggerFactory.getLogger(GenerateIdController.class);

	@Autowired
	CocolianIdGenerator cocolianIdGenerator;

	@Override
	@Timer("generateId")
	public GenerateIdResponse process(GenerateIdRequest request)
			throws NotFoundException, SystemException, UserException {
		logger.info("generate {} id for {}", request.getCount(), request.getUserName());
		GenerateIdResponse.Builder response = GenerateIdResponse.newBuilder();
		try{
			Optional<List<Id>> ids = cocolianIdGenerator.generateIdBatch(request.getCount());
			if(ids.isPresent()){
				ids.get().forEach(id->{
					response.addId(id.getId());
				});
			}
		}catch (InvalidBatchSizeException e){
			logger.error(e.getMessage());
			throw new SystemException().setMessage(e.getMessage());
		}
		return response.build();
	}

}
