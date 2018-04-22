## 一、功能

cocolian-id模块是一个基础功能模块，为其他业务提供id发生器支持。包括如下子模块：
1. cocolian-id-generator：id发生器，这是一个RPC服务； 
2. cocolian-id-facade： id发生器SDK， 提供给需要ID服务的模块使用，对cocolian-id-generator的客户端进行封装，提供实现无关的接口。 
3. cocolian-id-docker-redis： 支持cocolian-id-generator的redis镜像。 

## 二、cocolian-id-facade

提供id发生器接口SDK给调用方使用。 

这里是**调用方** 的使用方式：

1. 初始化ID Service 

```java

IdService service= IdService.newBuilder()
					.userName("RpC用户名")
					.password("Rpc密码")
					.stage("配置的stage")
					.build();

```

2. 调用service来生成id

```java 

long id = service.nextId(ACCOUNT_ID_TYPE);
... //使用id 

```

按照如上方式，cocolian-id-facade提供class IdService, 
- 初始化： 通过builder来构建。 
- 方法 nextId() : 封装对IdRpcService::generateId() 调用来实现ID的分配。 

### 三、cocolian-id-generator

id发生器的实现的Server。使用Redis来确保生成的Id是不重复的。 

技术栈： 
- cocolian-rpc/Apache Thrift, Google Protobuf ： 本项目使用Thrift作为RPC服务器，但通讯使用Protobuf。 
- Redis: 最新的ID保存在Redis中。 

### 四、cocolian-id-docker-redis

用来构建支持这个服务的redis镜像
