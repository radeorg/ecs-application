//package com.shdy.admin;
//
//import com.baomidou.mybatisplus.annotation.DbType;
//import com.baomidou.mybatisplus.annotation.IdType;
//import com.baomidou.mybatisplus.core.MybatisConfiguration;
//import com.baomidou.mybatisplus.core.config.GlobalConfig;
//import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
//import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
//import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
//import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
//import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
//import org.apache.ibatis.logging.stdout.StdOutImpl;
//import org.apache.ibatis.session.SqlSessionFactory;
//import org.apache.ibatis.type.JdbcType;
//import org.dows.aac.api.AacApi;
//import org.dows.app.config.DynamicDataSource;
//import org.dows.rbac.handler.RbacDataPermissionHandler;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.core.io.Resource;
//import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
//import org.springframework.jdbc.datasource.DataSourceTransactionManager;
//import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
//
//import javax.sql.DataSource;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @description: </br>
// * @author: lait.zhang@gmail.com
// * @date: 11/18/2024 9:50 AM
// * @history: </br>
// * <author>      <time>      <version>    <desc>
// * 修改人姓名      修改时间        版本号       描述
// */
////@RequiredArgsConstructor
//@Configuration
////@AutoConfigureBefore(MybatisPlusAutoConfiguration.class)
//// 排除 DataSourceAutoConfiguration 的自动配置，避免环形调用
////@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
//public class DynamicDataSourceConfig {
//
//    @Bean
//    @ConfigurationProperties("spring.datasource")
//    public DataSourceProperties defaultDataSourceProperties() {
//        return new DataSourceProperties();
//    }
//
//    @Bean
//    public DataSource defaultDataSource() {
//        return defaultDataSourceProperties().initializeDataSourceBuilder().build();
//    }
//
////    @Bean
////    @ConfigurationProperties("spring.datasource.dynamic")
////    public DataSourceProperties dynamicDataSourceProperties() {
////        return new DataSourceProperties();
////    }
//
//    //@Bean
//    public DynamicDataSource dynamicDataSource(){
//        return new DynamicDataSource();
//    }
//
//    @Bean
//    public Map<Object, Object> dataSourceMap() {
//        Map<Object, Object> dataSourceMap = new HashMap<>();
//        dataSourceMap.put("default", defaultDataSource());
//        return dataSourceMap;
//    }
//
//    @Bean(name = "dynamicDataSource")
//    @Qualifier("dynamicDataSource")
//    public AbstractRoutingDataSource dynamicDataSource(@Qualifier("dataSourceMap") Map<Object, Object> dataSourceMap) {
//        DynamicDataSource dynamicDataSource = dynamicDataSource();
//        dynamicDataSource.setTargetDataSources(dataSourceMap);
//        dynamicDataSource.setDefaultTargetDataSource(defaultDataSource());
//
//        return dynamicDataSource;
//    }
//
//
//    @Bean
//    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSourceMap") Map<Object, Object> dataSourceMap) throws Exception {
//        MybatisSqlSessionFactoryBean sqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();
//        sqlSessionFactoryBean.setDataSource(dynamicDataSource(dataSourceMap));
//        //对新的SqlSessionFactory配置 修改mybatis-plus Page自动分页失效问题 以及 找不到xml问题
//
//        sqlSessionFactoryBean.setGlobalConfig(globalConfig());
//        sqlSessionFactoryBean.setConfiguration(configuration());
//        sqlSessionFactoryBean.setMapperLocations(mapperLocations());
//        sqlSessionFactoryBean.setPlugins(interceptor());
//
//        //sqlSessionFactoryBean.afterPropertiesSet();
//        return sqlSessionFactoryBean.getObject();
//    }
//
//
//
//
//
//}