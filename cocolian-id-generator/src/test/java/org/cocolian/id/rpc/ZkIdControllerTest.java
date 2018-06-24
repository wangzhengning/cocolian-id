package org.cocolian.id.rpc;

import org.cocolian.id.IdRpcService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * @description:
 * @author: zn.wang , Created in 14:36 2018/6/24.
 */
@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
@SpringBootTest(classes = TestConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ZkIdControllerTest {

    @Autowired
    @Qualifier("zkIdController")
    private ZkIdController zkIdController;

    @Test
    public void process() throws Exception {
        IdRpcService.GenerateIdRequest.Builder request = IdRpcService.GenerateIdRequest
        .newBuilder();
        request.setUserName("cocolian");
        request.setPassword("123456");
        request.setEntityType(1);
        request.setCount(5);
        IdRpcService.GenerateIdResponse response = zkIdController.process(request
                .build());
    }

}