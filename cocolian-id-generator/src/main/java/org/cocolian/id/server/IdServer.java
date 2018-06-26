
package org.cocolian.id.server;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * 启动服务器端
 * @author shamphone@gmail.com
 *
 */
@SpringBootApplication
public class IdServer {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(IdServer.class);
        app.setWebEnvironment(false);
        app.run(args);
    }
}
