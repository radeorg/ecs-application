package org.dows.ecs.admin;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import jakarta.annotation.Resources;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.apache.ibatis.type.JdbcType;
import org.dows.aac.api.AacApi;
import org.dows.rbac.handler.RbacDataPermissionHandler;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.IOException;

@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = {"org.dows.*"}, annotationClass = Mapper.class)
public class MybatisConfig {


    @jakarta.annotation.Resource
    private FillHandler fillHandler;

    @Lazy
    @jakarta.annotation.Resource
    private RbacDataPermissionHandler rbacDataPermissionHandler;

    @Bean
    Resource[] mapperLocations() throws IOException {
        return new PathMatchingResourcePatternResolver().getResources("classpath*:/mapper/*.xml");
    }

    @Bean
    MybatisConfiguration configuration() {
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setCacheEnabled(true);
        configuration.setMultipleResultSetsEnabled(true);
        configuration.setJdbcTypeForNull(JdbcType.NULL);
        configuration.setLogImpl(StdOutImpl.class);
        return configuration;
    }


    @Bean
    public GlobalConfig globalConfig() {
        GlobalConfig globalConfig = GlobalConfigUtils.defaults();
        globalConfig.setMetaObjectHandler(fillHandler);
        globalConfig.setDbConfig(new GlobalConfig.DbConfig().setIdType(IdType.AUTO));
        globalConfig.setBanner(false);
        return globalConfig;
    }

    @Bean
    public MybatisPlusInterceptor interceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        interceptor.addInnerInterceptor(rbacDataPermissionHandler);
        return interceptor;
    }
}
