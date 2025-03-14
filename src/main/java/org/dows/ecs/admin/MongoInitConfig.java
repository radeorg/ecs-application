package org.dows.ecs.admin;

import lombok.Data;

@Data
public class MongoInitConfig {
    private Boolean enabled;
    private String username;
    private String password;
}
