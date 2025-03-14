package org.dows.ecs.admin;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class Version {
    @Value("${spring.profiles.active}")
    private String env;
    @Autowired
    private AppVersion appVersion;

    @Autowired
    private List<DataSourceProperties> dataSourceProperties;

    @GetMapping("/version")
    public String version() {
        log.info("version......");
        appVersion.setEnv(env);
        return JSONUtil.toJsonStr(appVersion);
    }

    @GetMapping("/env")
    public String getDataSourceProperties() {
        return JSONUtil.toJsonStr(dataSourceProperties);
    }
}
