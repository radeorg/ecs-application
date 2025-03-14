//package com.shdy.admin;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.dows.framework.api.uim.AccountInfo;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.context.SecurityContextImpl;
//import org.springframework.web.servlet.HandlerInterceptor;
//import org.springframework.web.servlet.ModelAndView;
//
//import java.util.HashMap;
//import java.util.Map;
//
////@Component
//@Slf4j
//public class SecurityInteceptor implements HandlerInterceptor {
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//
//        /*SecurityContext securityContext = new SecurityContextImpl();
//        Map<String, Object> credits = new HashMap<>();
//        credits.put("roleId", 1L);
//        credits.put("role", "admin");
//        AccountInfo accountInfo = new AccountInfo();
//        accountInfo.setId(1L);
//        accountInfo.setAccountName("zhangsan");
//        Authentication authentication = new UsernamePasswordAuthenticationToken(accountInfo, credits);
//        securityContext.setAuthentication(authentication);
//        SecurityContextHolder.setContext(securityContext);*/
//        return true;
//    }
//
//    @Override
//    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        log.info(principal.toString());
//    }
//
//
//    @Override
//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//
//    }
//}
