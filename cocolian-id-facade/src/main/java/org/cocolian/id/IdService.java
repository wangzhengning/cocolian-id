/**
 * 
 */
package org.cocolian.id;

import org.cocolian.id.IdRpcService.GenerateIdRequest;
import org.cocolian.id.IdRpcService.GenerateIdResponse;
import org.cocolian.rpc.sharder.RpcServiceClient;

/**
 * IdService，客户端API，提供访问IdRpcService的客户端封装。 
 * 注意，IdService设置为自动配置，可以直接注入到引用类中。 
 * @author shamphone@gmail.com
 * @version 1.0.0
 *
 */
public class IdService {

	private RpcServiceClient client;
	private String rpcUserName;
	private String rpcPassword;
	
	public IdService(RpcServiceClient client, String rpcUserName, String rpcPassword){
		this.client = client;
		this.rpcPassword = rpcPassword;
		this.rpcUserName = rpcUserName;
	}
	
	/**
	 * 产生一个新的Id
	 * 
	 * @return
	 */
	public long nextId(){
		GenerateIdRequest.Builder request = GenerateIdRequest.newBuilder();
		request.setUserName(rpcUserName);
		request.setPassword(rpcPassword);
		request.setCount(1);
		GenerateIdResponse response = client.execute("generateId", request.build(), GenerateIdResponse.class);
		return response.getIdList().get(0);
	}
}
