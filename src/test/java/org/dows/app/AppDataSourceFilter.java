//package com.shdy.admin;
//
//import cn.hutool.core.util.StrUtil;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.dows.app.api.AppDataSourceProvider;
//import org.dows.app.config.DataSourceContextHolder;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.Objects;
//
///**
// * @description: </br>
// * @author: lait.zhang@gmail.com
// * @date: 11/18/2024 2:53 PM
// * @history: </br>
// * <author>      <time>      <version>    <desc>
// * 修改人姓名      修改时间        版本号       描述
// */
//@RequiredArgsConstructor
//@Component
//@Order(0)
//public class AppDataSourceFilter extends OncePerRequestFilter {
//
//    public static final String HEADER_APP_ID = "appId";
//
//    private final AppDataSourceProvider appDataSourceProvider;
//
//    public static String getAppId(HttpServletRequest request) {
//        String appId = StringUtils.hasLength(request.getHeader(HEADER_APP_ID)) ?
//                request.getHeader(HEADER_APP_ID) : request.getHeader(HEADER_APP_ID.toLowerCase());
//        if (StrUtil.isEmpty(appId)) {
//            appId = getQueryParam(request.getQueryString(), HEADER_APP_ID);
//        }
//        return StrUtil.isNotBlank(appId) ? appId : null;
//    }
//
//    public static String getQueryParam(String query, String key) {
//        if (Objects.isNull(query)) {
//            return null;
//        }
//        String[] params = query.split("&");
//        for (String param : params) {
//            String[] keyValue = param.split("=");
//            if (Objects.equals(key.toLowerCase(), keyValue[0].toLowerCase()) && keyValue.length > 1) {
//                return keyValue[1];
//            }
//        }
//        return null;
//    }
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
//        String appId = getAppId(request);
//        if (appId != null) {
//            appDataSourceProvider.createDataSource(appId);
//            DataSourceContextHolder.setAppDataSource(appId);
//        }
//        try {
//            chain.doFilter(request, response);
//        } finally {
//            DataSourceContextHolder.clearAppDataSource();
//        }
//    }
//}