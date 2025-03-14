//package com.shdy.admin;
//
//import cn.hutool.json.JSONUtil;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.dows.app.DependOnMysql;
//import org.dows.app.PoolSettings;
//import org.dows.app.api.AppDataSourceProvider;
//import org.dows.app.api.AppDependenceApi;
//import org.dows.app.api.admin.response.AppDependOnResponse;
//import org.dows.app.config.DynamicDataSource;
//import org.dows.app.handler.pool.HikariPoolManager;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import javax.sql.DataSource;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * @description: </br>
// * @author: lait.zhang@gmail.com
// * @date: 11/18/2024 9:57 AM
// * @history: </br>
// * <author>      <time>      <version>    <desc>
// * 修改人姓名      修改时间        版本号       描述
// */
//@Component
//@Slf4j
//@RequiredArgsConstructor
//public class JdbcAppDataSourceProvider implements AppDataSourceProvider {
//
//
//    private final AppDependenceApi appDependenceApi;
//
//    private final Map<Object, Object> dataSourceMap ;
//
//    private final DynamicDataSource dynamicDataSource;
//    @Value("${spring.profiles.active}")
//    private String env;
//
//    @Override
//    public DataSource createDataSource(String appid) {
//        Object dataSource =  dataSourceMap.get(appid);
//        if (dataSource != null) {
//            return (DataSource) dataSource;
//        }
//        // 根据appid和配置信息创建数据源
//        AppDependOnResponse appDependOnMairaDb = appDependenceApi.getAppDependOnMairaDb(appid, env);
//        if (appDependOnMairaDb != null) {
//            log.info("根据appid和配置信息创建数据源");
//            String jsonConfig = appDependOnMairaDb.getConfigJson();
//            //DataSourceProperties bean = JSONUtil.toBean(jsonConfig, DataSourceProperties.class);
//            /*DriverManagerDataSource dataSource = new DriverManagerDataSource();
//            dataSource.setDriverClassName(bean.getDriverClassName());
//            dataSource.setUrl(bean.getUrl() + appid);
//            dataSource.setUsername(bean.getUsername());
//            dataSource.setPassword(bean.getPassword());*/
//            DependOnMysql dependOnMysql = JSONUtil.toBean(jsonConfig, DependOnMysql.class);
//            //HikariDataSource hikariDataSource = HikariPoolManager.buildHikariDataSource(dependOnMysql);
//            return createDatasourceIfAbsent(appid, dependOnMysql);
//        }
//        dataSourceMap.put(appid, dataSourceMap.get("default"));
////        throw new RuntimeException("未找到数据源配置");
//        return null;
//    }
//
//
//    private DataSource createDatasourceIfAbsent(String appid, DependOnMysql dependOnMysql) {
//        // 1. 重点：如果数据源不存在，则进行创建
//        if (isDataSourceNotExist(appid)) {
//            PoolSettings poolSettings = JSONUtil.toBean(dependOnMysql.getPool(), PoolSettings.class);
//            // 问题一：为什么要加锁？因为，如果多个线程同时执行到这里，会导致多次创建数据源
//            // 问题二：为什么要使用 poolName 加锁？保证多个不同的 poolName 可以并发创建数据源
//            // 问题三：为什么要使用 intern 方法？因为，intern 方法，会返回一个字符串的常量池中的引用
//            // intern 的说明，可见 https://www.cnblogs.com/xrq730/p/6662232.html 文章
//            synchronized (poolSettings.getPoolName().intern()) {
//                if (isDataSourceNotExist(appid)) {
//                    log.debug("创建数据源：{}", poolSettings.getPoolName());
//                    DataSource dataSource = null;
//                    try {
//                        dataSource = HikariPoolManager.buildHikariDataSource(dependOnMysql);
//                    } catch (Exception e) {
//                        log.error("动态创建应用数据源出错", e);
//                        if (e.getMessage().contains("Unknown database")) {
//                            throw new RuntimeException("应用ID不存在");
//                        }
//                        throw e;
//                    }
//                    dataSourceMap.put(appid, dataSource);
//                    dynamicDataSource.setTargetDataSources(dataSourceMap);
//                    dynamicDataSource.afterPropertiesSet();
//                    return dataSource;
//                }
//            }
//        }
//        log.debug("应用ID: {}对应的数据源已存在,无需创建：{}", appid, appid);
//        throw new RuntimeException(String.format("应用ID: %s对应的数据源: %s已存在,无需创建", appid, appid));
//    }
//
//    private boolean isDataSourceNotExist(String appid) {
//        return !dataSourceMap.containsKey(appid);
//    }
//}