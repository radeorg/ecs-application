package org.dows.ecs.admin;

import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import org.dows.app.api.DataSourceProvider;
import org.dows.app.config.AppDatasourceFilter;
import org.dows.framework.api.web.ResponseWrapperHandler;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@EnableWebMvc
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer, WebMvcRegistrations {

    private final DataSourceProvider dataSourceProvider;

    @Bean
    public ResponseWrapperHandler responseWrapperHandler() {
        return new ResponseWrapperHandler();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/ws/**");

        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/")
                .resourceChain(false);

        registry.addResourceHandler("/favicon.ico")//favicon.ico
                .addResourceLocations("classpath:/static/");

        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");

        registry.addResourceHandler("doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/doc.html#/**")
                .addResourceLocations("classpath:/META-INF/resources/");
        // 配置访问前缀
        registry.addResourceHandler("/templates/**")
                //配置文件真实路径
                .addResourceLocations("classpath:/templates/");

    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/swagger-ui/")
                .setViewName("forward:/swagger-ui/index.html");
    }


    @Bean
    public FilterRegistrationBean<Filter> headerFilter() {
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
        AppDatasourceFilter appDatasourceFilter = new AppDatasourceFilter(dataSourceProvider);
        registrationBean.setFilter(appDatasourceFilter);
        registrationBean.addUrlPatterns("/v1/*", "/v2/*");
        registrationBean.setName("headerFilter");
        registrationBean.setOrder(2);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<RequestBodyReaderCustomFilter> requestBodyReaderFilters() {
        FilterRegistrationBean<RequestBodyReaderCustomFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RequestBodyReaderCustomFilter());
        registrationBean.addUrlPatterns("/v1/*", "/v2/*");
        registrationBean.setName("requestBodyReaderFilter");
        registrationBean.setOrder(3);
        return registrationBean;
    }



    /**
     * 跨域处理
     *
     * @return
     */
    @Bean
    @Order(1)
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", buildConfig());
        return new CorsFilter(source);
    }

    private CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        //1,允许任何来源
        corsConfiguration.addAllowedOriginPattern(CorsConfiguration.ALL);
        //2,允许任何请求头
        corsConfiguration.addAllowedHeader(CorsConfiguration.ALL);
        //3,允许任何方法
        corsConfiguration.addAllowedMethod(CorsConfiguration.ALL);
        //4,允许凭证
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setMaxAge(3600L);
        return corsConfiguration;
    }


//    @Bean
//    public FilterRegistrationBean<SignatureFilter> signatureFilter() {
//        FilterRegistrationBean<SignatureFilter> registrationBean = new FilterRegistrationBean<>();
//        registrationBean.setFilter(new SignatureFilter(appSignatureHandler,
//                appCache, SpringUtil.getBean("handlerExceptionResolver", HandlerExceptionResolver.class)));
//        registrationBean.addUrlPatterns("/v1/*", "/v2/*");
//        registrationBean.setName("signatureFilter");
//        registrationBean.setOrder(4);
//        return registrationBean;
//    }


//    @Override
//    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
//        WebMvcConfigurer.super.configureHandlerExceptionResolvers(resolvers);
//    }

    /**
     * 增加头参数解析
     *
     * @param
     */
//    @Override
//    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
//        resolvers.add(new HeaderArgumentResolver());
//    }
//
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        // 注册拦截器
//        registry.addInterceptor(appDataSourceInterceptor);
// /*               .addPathPatterns("/**")
//                .excludePathPatterns("/acc/doLogin")*/
//        ;
//    }


    //    private final AppSignatureHandler appSignatureHandler;
//    private final AppCache appCache;
//    private final AppDataSourceInterceptor appDataSourceInterceptor;

/*
    @Autowired
    private RequestMappingHandlerMapping handlerMapping;
    private final Map<String,String> pathsToRegister = new HashMap<>();
    private final RequestMappingInfo.BuilderConfiguration config = new RequestMappingInfo.BuilderConfiguration();

    @PostConstruct
    public void init(){
        //todo 读取当前应用有哪些接口需要取消注册
        //pathsToRegister.put("","");
        pathsToRegister.forEach(this::cancelMapping);
    }

    */

   /*
    public synchronized void cancelMapping(String pattern,String method) {
        RequestMappingInfo mappingInfo = RequestMappingInfo.paths(pattern)
                .methods(RequestMethod.valueOf(method))
                .options(this.config)
                .build();
        handlerMapping.unregisterMapping(mappingInfo);
    }*/

    /*    @Override
        public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
            converters.add(0, new MappingJackson2HttpMessageConverter());
        }*/


    /*@Override
    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        return new RequestMappingHandlerMapping() {
            protected boolean isHandlerMethodAllowed(HttpServletRequest request,
                                                     HandlerMethod handlerMethod) {
                // 检查方法的路径是否符合预定义的路径集合
                return pathsToRegister.stream()
                        .anyMatch(path -> handlerMethod.getPatternsCondition().getPatterns().stream()
                                .anyMatch(pattern -> pattern.matches(path)));
            }
        };
    }*/
}

