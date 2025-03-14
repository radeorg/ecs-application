//package com.shdy.admin;
//
//import cn.hutool.core.util.StrUtil;
//import jakarta.servlet.*;
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.dows.app.api.AppContext;
//import org.dows.app.api.AppDataSourceProvider;
//import org.dows.app.config.DataSourceContextHolder;
//
//import java.io.IOException;
//
//@RequiredArgsConstructor
//@Slf4j
//public class HeaderFilter implements Filter {
//
//    private final AppDataSourceProvider appDataSourceProvider;
//
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//        // 在这里可以初始化Filter
//    }
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//            throws IOException, ServletException {
//        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
//        String appId = httpServletRequest.getHeader("appId");
//        if (StrUtil.isNotBlank(appId)) {
//            AppContext.setAppId(appId);
//        }
//        if (appId != null) {
//            appDataSourceProvider.createDataSource(appId);
//            DataSourceContextHolder.setAppDataSource(appId);
//        }
//        try {
//            chain.doFilter(request, response);
//        } finally {
////            log.info("清除线程资源");
//            DataSourceContextHolder.clearAppDataSource();
//            AppContext.removeAppId();
//        }
////        chain.doFilter(request, response);
//    }
//
//
//    @Override
//    public void destroy() {
//        // 在这里可以销毁Filter
//    }
//}