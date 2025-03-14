//package com.shdy.admin;
//
//import jakarta.annotation.PostConstruct;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Component
//@Slf4j
//public class MongoConnect {
//
//
//    @Autowired
//    private MongoConfig mongoConfig;
//
//    @Autowired
//    private SpringInitConfig springInitConfig;
//
//    private final static String PERMISSION = "readWrite";
//
//
//    @PostConstruct
//    public void init() {
//        if (springInitConfig.getMongodb().getEnabled()) {
//            log.info("mongo初始化数据库");
//            try {
//                MongoUtils.initMongoClient(springInitConfig.getMongodb().getUsername(), springInitConfig.getMongodb().getPassword(), mongoConfig.getHost(), mongoConfig.getPort());
//                MongoUtils.addUser(mongoConfig.getDatabase(), mongoConfig.getUsername(), mongoConfig.getPassword(),PERMISSION);
//                MongoUtils.close();
//            } catch (Exception e) {
//                log.error("mongo初始化数据库异常{}", e.getClass().getSimpleName() + ":" + e.getMessage());
//            }
//
//        } else {
//            log.info("mongo初始化未启用");
//        }
//
//    }
//
//
//    // 创建连接
//
//    // 数据库创建用户
//
//    // 数据库删除用户
//
//    // 数据库查询用户信息
//
//    // 关闭连接
//
//
//
//
//}
