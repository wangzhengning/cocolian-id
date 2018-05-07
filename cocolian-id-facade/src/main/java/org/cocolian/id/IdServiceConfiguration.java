/**
 * 
 */
package org.cocolian.id;

import java.util.ArrayList;
import java.util.List;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.AuthInfo;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.retry.BoundedExponentialBackoffRetry;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.cocolian.rpc.register.DigestAuthInfo;
import org.cocolian.rpc.sharder.BasicTransportPool;
import org.cocolian.rpc.sharder.RpcServiceClient;
import org.cocolian.rpc.sharder.TransportManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * IdService的Spring配置
 * @author shamphone@gmail.com
 * @version 1.0.0
 *
 */
@Configuration
public class IdServiceConfiguration {
	@Autowired
	private Environment env;

	@Bean
	public IdService dataService() {
		String username = env.getProperty("rpc.data.user.name");
		String password = env.getProperty("rpc.data.password");
		return new IdService(client(), username, password);
	}

	@Bean
	public RpcServiceClient client() {
		RpcServiceClient.Builder client = RpcServiceClient.newBuilder();
		client.transportManager(this.transportManager());
		return client.build();
	}

	@Bean(initMethod = "start", destroyMethod = "close")
	public TransportManager transportManager() {
		BasicTransportPool pool = new BasicTransportPool();
		pool.setPath(env.getProperty("rpc.client.service.path"));
		pool.setClient(this.curatorFramework());
		return pool;
	}

	@Bean(initMethod = "start", destroyMethod = "close")
	public CuratorFramework curatorFramework() {
		CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();
		int sessionTimeoutMs = Integer.parseInt(env.getProperty("rpc.client.zookeeper.session.timeout.ms", "5000"));
		int connectionTimeoutMs = Integer
				.parseInt(env.getProperty("rpc.client.zookeeper.connection.timeout.ms", "5000"));
		builder.connectString(env.getProperty("rpc.client.zookeeper.connect.string")).sessionTimeoutMs(sessionTimeoutMs)
				.connectionTimeoutMs(connectionTimeoutMs).retryPolicy(this.retryPolicy())
				.aclProvider(this.aclProvider()).authorization(this.authInfo());
		return builder.build();
	}

	@Bean
	public List<AuthInfo> authInfo() {
		String username = env.getProperty("rpc.client.zookeeper.username");
		String password = env.getProperty("rpc.client.zookeeper.password");
		List<AuthInfo> info = new ArrayList<AuthInfo>();
		info.add(new DigestAuthInfo(username, password));
		return info;
	}

	@Bean
	public RetryPolicy retryPolicy() {
		int baseSleepTimeMs = Integer.parseInt(env.getProperty("rpc.client.zookeeper.base.sleep.time.ms", "1000"));
		int maxSleepTimeMs = Integer.parseInt(env.getProperty("rpc.client.zookeeper.max.sleep.time.ms", "5000"));
		int maxRetries = Integer.parseInt(env.getProperty("rpc.client.zookeeper.max.retries", "29"));
		return new BoundedExponentialBackoffRetry(baseSleepTimeMs, maxSleepTimeMs, maxRetries);
	}

	@Bean
	public ACLProvider aclProvider() {
		return new ACLProvider() {
			@Override
			public List<ACL> getDefaultAcl() {
				return ZooDefs.Ids.CREATOR_ALL_ACL;
			}

			@Override
			public List<ACL> getAclForPath(String path) {
				return ZooDefs.Ids.CREATOR_ALL_ACL;
			}
		};
	}
}
