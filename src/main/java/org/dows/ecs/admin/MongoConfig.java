package org.dows.ecs.admin;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties("spring.data.mongodb")
@Data
public class MongoConfig {
    private String database;
    private String host;
    private int port;
    private String username;
    private String password;
}
