//package com.shdy.admin;
//
//import cn.hutool.core.collection.CollectionUtil;
//import cn.hutool.core.util.StrUtil;
//import cn.hutool.crypto.SecureUtil;
//import cn.hutool.crypto.digest.HmacAlgorithm;
//import cn.hutool.extra.spring.SpringUtil;
//import cn.hutool.json.JSONUtil;
//import jakarta.annotation.PostConstruct;
//import jakarta.servlet.*;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.dows.app.api.AppContext;
//import org.dows.app.entity.AppSignatureEntity;
//import org.dows.app.handler.AppCache;
//import org.dows.app.handler.AppSignatureHandler;
//import org.dows.app.handler.SignatureConfigJson;
//import org.dows.framework.api.exceptions.SignatureException;
//import org.dows.framework.api.status.CryptoStatusCode;
//import org.dows.log.api.util.HttpServletUtil;
//import org.springframework.web.servlet.HandlerExceptionResolver;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//
//@RequiredArgsConstructor
//@Slf4j
//public class SignatureFilter implements Filter {
//
//    private final AppSignatureHandler appSignatureHandler;
//    private final AppCache appCache;
//    private final HandlerExceptionResolver handlerExceptionResolver;
//
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
//        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
//
//        // 验签
//        if (signHandler(httpServletRequest, httpServletResponse)) {
//            // 确保请求可以继续传递到下一个filter或者目标资源
//            chain.doFilter(request, response);
//        }
//    }
//
//    public boolean signHandler(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
//        String uri = servletRequest.getRequestURI();
//        AppSignatureEntity appSignatureByAppIdAndUrl = appSignatureHandler.getAppSignatureByAppIdAndUrl(AppContext.getAppId(), uri);
//        if (null == appSignatureByAppIdAndUrl) {
//            return true;
//        }
//        Map<String, Object> requestParams = null;
//        try {
//            requestParams = HttpServletUtil.getRequestParams(servletRequest);
//        } catch (IOException e) {
//            handlerExceptionResolver.resolveException(servletRequest, servletResponse, null,
//                    new SignatureException(CryptoStatusCode.REQUEST_TO_BEAN));
//            return false;
//        }
//        String sign = servletRequest.getHeader("signature");
//        if (StrUtil.isBlank(sign)) {
//            handlerExceptionResolver.resolveException(servletRequest, servletResponse, null,
//                    new SignatureException(CryptoStatusCode.PARAM_SIGN_MISSING));
//            return false;
//        }
//        Object requestBody = requestParams.get("requestBody");
//        Map<String, Object> requestBodyMap = new HashMap<>();
//        traverseParams(requestBodyMap, (Map<String, Object>) requestBody);
//
//        // 获取请求参数timestamp 时间戳，
//        Long timestamp = (Long) requestBodyMap.get("timestamp");
//        if (null == timestamp) {
//            handlerExceptionResolver.resolveException(servletRequest, servletResponse, null,
//                    new SignatureException(CryptoStatusCode.PARAM_TIMESTAMP_MISSING));
//            return false;
//        }
//
//        // 获取请求参数timestamp 时间戳，
//        String nonce = requestBodyMap.get("nonce").toString();
//        if (StrUtil.isBlank(nonce)) {
//            handlerExceptionResolver.resolveException(servletRequest, servletResponse, null,
//                    new SignatureException(CryptoStatusCode.PARAM_NONCE_MISSING));
//            return false;
//        }
//        // 判断是否重复请求
//        Object cacheNonce = appCache.get("nonce", nonce);
//        if (null != cacheNonce) {
//            handlerExceptionResolver.resolveException(servletRequest, servletResponse, null,
//                    new SignatureException(CryptoStatusCode.SIGNATURE_FAILED));
//            return false;
//        } else {
//            appCache.put("nonce", nonce, nonce);
//        }
////        String sign = requestBodyMap.get("sign").toString();
//        if (1 == appSignatureByAppIdAndUrl.getMode()) {
//            String configJson = appSignatureByAppIdAndUrl.getConfigJson();
//            SignatureConfigJson signatureConfigJson = JSONUtil.toBean(configJson, SignatureConfigJson.class);
//            // 比较时间，10分钟内为合法请求
//            if (Math.abs(System.currentTimeMillis() - timestamp) > signatureConfigJson.getTimeInterval()) {
//                handlerExceptionResolver.resolveException(servletRequest, servletResponse, null,
//                        new SignatureException(CryptoStatusCode.SIGNATURE_TIMED_OUT));
//                return false;
//            }
////            List<String> params = JSONUtil.parseObj(configJson).getBeanList("params", String.class);
//            List<String> params = signatureConfigJson.getParams();
////      计算签名规则：sign = HMACSHA256("ts=1623388123195&nonce=
////      d50e301d-ee2c-446e-8f28-013f0fee09fb&appSecret=9ZLEzugQHfQd11vS8pd68lxzA")
//            StringBuilder s = new StringBuilder();
//            if (CollectionUtil.isNotEmpty(params)) {
//                for (String param : params) {
//                    s.append(param).append("=").append(requestBodyMap.get(param)).append("&");
//                }
//                s.deleteCharAt(s.length() - 1);
//            }
//            String mySign = SecureUtil.hmac(HmacAlgorithm.HmacMD5, appSignatureByAppIdAndUrl.getSecretKey()).digestHex(s.toString());
//            if (!StrUtil.equals(sign, mySign)) {
//                handlerExceptionResolver.resolveException(servletRequest, servletResponse, null,
//                        new SignatureException(CryptoStatusCode.SIGNATURE_FAILED));
//                return false;
//            }
//        }
//
//        return true;
//    }
//
//    private void traverseParams(Map<String, Object> res, Map<String, Object> params) {
//        for (Map.Entry<String, Object> entry : params.entrySet()) {
//            String key = entry.getKey();
//            Object value = entry.getValue();
//
//            if (value instanceof Map) {
//                // 递归遍历嵌套的Map
//                traverseParams(res, (Map<String, Object>) value);
//            } else {
//                res.put(key, value);
//            }
//        }
//    }
//
//
//    /*public static void main(String[] args) {
//        long millis = System.currentTimeMillis();
//        System.out.println(millis);
//        String nonce = UUID.randomUUID().toString().replace("-", "");
//        System.out.println(nonce);
//        String s = SecureUtil.hmac(HmacAlgorithm.HmacMD5, "acre1234567890jaskfhakjsfh").digestHex("eqptNo=device01&signal=1&vehicleNo=沪B888888&weight=240&nonce=" + nonce + "&timestamp=" + millis);
//        System.out.println(s);
//    }*/
//
//
//    @Override
//    public void destroy() {
//        // 在这里可以销毁Filter
//    }
//}