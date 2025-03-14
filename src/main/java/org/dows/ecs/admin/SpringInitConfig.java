package org.dows.ecs.admin;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("spring.init")
@Data
public class SpringInitConfig {
    private MongoInitConfig mongodb;

}
