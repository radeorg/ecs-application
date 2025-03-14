//package com.shdy.admin;
//
//import cn.hutool.core.util.StrUtil;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.dows.app.api.AppContext;
//import org.dows.app.api.AppDataSourceProvider;
//import org.dows.app.config.DataSourceContextHolder;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.lang.Nullable;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//
//@Slf4j
//@Component
//public class AppDataSourceInterceptor implements HandlerInterceptor {
//
//    @Autowired
//    private AppDataSourceProvider appDataSourceProvider;
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
////        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
//        String appId = request.getHeader("appId");
//        if (StrUtil.isNotBlank(appId)) {
//            AppContext.setAppId(appId);
//        }
//        if (appId != null) {
//            appDataSourceProvider.createDataSource(appId);
//            DataSourceContextHolder.setAppDataSource(appId);
//        }
////        try {
////            chain.doFilter(request, response);
////        } finally {
//////            log.info("清除线程资源");
////            DataSourceContextHolder.clearAppDataSource();
////            AppContext.removeAppId();
////        }
//        /*if (handler instanceof HandlerMethod) {
//            ViewName methodAnnotation = ((HandlerMethod) handler).getMethodAnnotation(ViewName.class);
//            if (null != methodAnnotation) {
//                AppContext.setControllerViewName(methodAnnotation.value());
//            }
//
//        }
//
//        String appId = request.getHeader("appId");
//        if (StrUtil.isNotBlank(appId)) {
//            AppContext.setAppId(appId);
//        }*/
//        return true;
//    }
//
//    @Override
//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
//        DataSourceContextHolder.clearAppDataSource();
//        AppContext.removeAppId();
////        AppContext.removeAppId();
////        AppContext.removeControllerViewName();
//    }
//}
