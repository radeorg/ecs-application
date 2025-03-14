package org.dows.ecs.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Hello world!
 */
//@EnableFeignClients(basePackages = "org.dows.vac.rpc")
@EnableConfigurationProperties(AppVersion.class)
@SpringBootApplication(scanBasePackages = {"org.dows.framework", "com.shdy.admin"})
public class EcsApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcsApplication.class, args);
    }


}
