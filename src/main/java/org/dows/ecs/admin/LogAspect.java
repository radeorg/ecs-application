package org.dows.ecs.admin;//package org.dows.log.handler.aspect;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.dows.aac.api.AacApi;
import org.dows.aac.api.AacUser;
import org.dows.app.api.AppContext;
import org.dows.log.api.LogActionApi;
import org.dows.log.api.admin.request.RequestLog;
import org.dows.log.api.annotation.Actlog;
import org.dows.log.api.util.HttpServletUtil;
import org.dows.log.api.util.IpUtil;
import org.dows.log.config.LogConfig;
import org.dows.log.handler.LogActlogHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

@RequiredArgsConstructor
@Component
@Aspect
@Slf4j
public class LogAspect {


    private final LogActionApi logActionApi;
    private final LogActlogHandler logActlogHandler;
    private final AacApi aacApi;
    private final LogConfig logConfig;
    ThreadLocal<Long> currentTime = new ThreadLocal<>();

    ThreadLocal<Object> methodResult = new ThreadLocal<>();
    private final ThreadPoolExecutor threadPoolExecutor;


    /**
     * 配置切入点
     */
    @Pointcut("@annotation(org.dows.log.api.annotation.Actlog)")
    public void logPointcut() {
        // 该方法无方法体,主要为了让同类中其他方法使用此切入点
    }

    /**
     * 配置环绕通知,使用在方法logPointcut()上注册的切入点
     *
     * @param joinPoint join point for advice
     */
    @Around("logPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
//        String name = joinPoint.getSignature().getName();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        // 判断请求是否是POST类型
        // todo 从数据库查询出请求日志的配置集合（放缓存），然后根据签名判断是否开启请求日志
        currentTime.set(System.currentTimeMillis());
        if (logActionApi.logEnable(method.toString())) {
            Object result = joinPoint.proceed();
            methodResult.set(result);
            final Long aLong = currentTime.get();
            currentTime.remove();
            saveLog(joinPoint, "INFO", System.currentTimeMillis() - aLong, "NULL");
            return result;
        }
        return joinPoint.proceed();
    }

    /**
     * 配置异常通知
     *
     * @param joinPoint join point for advice
     * @param e         exception
     */
    @AfterThrowing(pointcut = "logPointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        long time = System.currentTimeMillis() - currentTime.get();
        currentTime.remove();
        StackTraceElement[] stackTraceElements = e.getStackTrace();
        String rootExceptionName = e.getClass().getName();
        StringBuilder resultContent = new StringBuilder("异常类：" + rootExceptionName);
        int count = 0;
        int maxTrace = 3;
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            if (stackTraceElement.getClassName().contains("org.dows") && count < maxTrace) {
                resultContent.append("\n出现于").append(stackTraceElement.getClassName())
                        .append("类中的").append(stackTraceElement.getMethodName())
                        .append("方法中 位于该类文件的第").append(stackTraceElement.getLineNumber())
                        .append("行)");
                count++;
                if (count == maxTrace) {
                    break;
                }
            }
        }
        saveLog(joinPoint, "ERROR", time, resultContent.toString());
    }
    /*public void saveLogAsync(JoinPoint joinPoint, String logTyp, long time, String ex) {
        threadPoolExecutor.execute(() -> {
            saveLog(joinPoint, logTyp, time, ex);
        });
    }*/

    /**
     * 保持审计日志
     *
     * @param joinPoint
     * @param logTyp
     * @param time
     * @param ex
     */
    private void saveLog(JoinPoint joinPoint, String logTyp, long time, String ex) {
        HttpServletRequest request = getHttpServletRequest();
        // 获取请求参数
//        RequestLog requestLog = getRequestParams(request);
        RequestLog requestLog = RequestLog.builder().build();
        // 获取方法参数
//        DomainMetadata domainMetadata = getDomainMetadataByMethodAnnotation(joinPoint);
//        // 获取登录状态
//        domainMetadata.setFieldValue("method_result", getResultCode());
//        // 获取登录信息
//        domainMetadata.setFieldValue("method_descr", getResultDesc());
//        // 获取登录角色
//        domainMetadata.setFieldValue("account_role", getResultRole());
        getClass(joinPoint, requestLog);

        requestLog.setAppId(AppContext.getAppId());
//        AppContext.removeAppId();
//        requestLog.setAppId(logConfig.getAppName());
        try {
            requestLog.setResponse(JSONUtil.toJsonStr(methodResult.get()));
        } catch (Exception e) {
            requestLog.setResponse(methodResult.get().toString());
        }
        fillAccountInfo(requestLog);
        //requestLog.setDescr(getResultDesc());

//        log.info("日志信息:{}", requestLog);
        threadPoolExecutor.execute(()->{
            RequestLog requestParams = getRequestParams(request);
            requestParams.setLogType(logTyp);
            requestParams.setTime(time);
            requestParams.setTables(requestLog.getTables());
            requestParams.setMethod(requestLog.getMethod());
            requestParams.setAppId(requestLog.getAppId());
            requestParams.setResponse(requestLog.getResponse());
            requestParams.setExceptionDetail(ex);
            requestParams.setAccountId(requestLog.getAccountId());
            requestParams.setUsername(requestLog.getUsername());
            requestParams.setCreateTime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            logActlogHandler.save(requestParams);
        });

        // 移除threalocal，避免内存溢出
        methodResult.remove();
        //insertService.insert(domainMetadata);
        //logService.save(getUsername(), IpUtil.getBrowser(request), IpUtil.getIp(request), joinPoint, log);
    }

    /**
     * 获取返回值code
     */
    /*private Integer getResultCode() {
        JSONObject jsonObject = JSONUtil.parseObj(methodResult.get());
        return (Integer) jsonObject.get("code");
    }*/

    /**
     * 获取返回提示信息
     */
    /*private String getResultDesc() {
        JSONObject jsonObject = JSONUtil.parseObj(methodResult.get());
        return (String) jsonObject.get("descr");
    }*/

    /**
     * 获取返回角色
     */
    private String getResultRole() {
        JSONObject jsonObject = JSONUtil.parseObj(methodResult.get());
        JSONObject roleObject = JSONUtil.parseObj(jsonObject.get("data"));
        String role = "";
        if (roleObject != null && roleObject.size() != 0) {
            role = (String) roleObject.get("role");
            if (role.equals("superadmin")) {
                role = "超级管理员";
            }
            if (role.equals("admin")) {
                role = "管理员";
            }
            if (role.equals("member")) {
                role = "健管师";
            }
        }
        return role;
    }

//    private void saveReturn() {
//        HttpServletRequest request = getHttpServletRequest();
//        // 获取请求参数
//        Map<String, String> requestParam = getRequestParams(request);
//        // 获取方法参数
//        DomainMetadata domainMetadata = getDomainMetadataByMethodAnnotation(joinPoint, requestParam);
//        insertService.insert(domainMetadata);
//        //logService.save(getUsername(), IpUtil.getBrowser(request), IpUtil.getIp(request), joinPoint, log);
//    }

//    public String getUsername() {
//        try {
//        try {
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            if (authentication == null) {
//                //throw new BadRequestException(HttpStatus.UNAUTHORIZED, "当前登录状态过期");
//            }
//            if (authentication.getPrincipal() instanceof UserDetails) {
//                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//                return userDetails.getUsername();
//            }
//        } catch (Exception e) {
//            return "error";
//        }
//        //throw new AuthException(HttpStatus.UNAUTHORIZED, "找不到当前登录的信息");
//        return "";
//    }


    private HttpServletRequest getHttpServletRequest() {
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
    }


    /**
     * @param request
     * @return
     * @throws Exception
     * @author tianwyam
     * @description 从POST请求中获取参数
     */
    private RequestLog getRequestParams(HttpServletRequest request) {
        String jsonStr = "{}";
        try {
            jsonStr = JSONUtil.toJsonStr(HttpServletUtil.getRequestParams(request));
        } catch (IOException e) {
            log.error("参数解析错误: {}", e.getMessage());
        }
        RequestLog requestLog = RequestLog.builder()
                .address(IpUtil.getHttpCityInfo(IpUtil.getIp(request)))
                .browser(IpUtil.getBrowser(request))
                .operateSystem(IpUtil.getOperatingSystem(request))
                .requestIp(IpUtil.getIp(request))
                .dt(new Date())
                .params(jsonStr)
                .build();
//        fillAccountInfo(requestLog);

//        params.put("operate_system", IpUtil.getOperatingSystem(request));
//        params.put("account_name", getAccountName(request));
//        params.put("dt", dayAd8(new Date()));
//        params.put("browser", IpUtil.getBrowser(request));
//        params.put("ip", IpUtil.getIp(request));
//        params.put("address", IpUtil.getHttpCityInfo(IpUtil.getIp(request)));
        // 设备指纹
//        params.put("device_id", "");
        return requestLog;
    }

    private void fillAccountInfo(RequestLog requestLog) {

        try {
            AacUser currentAccUser = aacApi.getCurrentAccUser();
            requestLog.setAccountId(currentAccUser.getAccountId());
            requestLog.setUsername(currentAccUser.getUsername());
        } catch (Exception e) {
            log.error("AacApi.getCurrentAccUser() 异常：{}", e.getMessage());
        }


    }


    /**
     * @param date
     * @return
     * @description 时间加8个小时
     */
    public String dayAd8(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) + 8);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
    }


    private void getClass(JoinPoint joinPoint, RequestLog requestLog) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Actlog actlog = method.getAnnotation(Actlog.class);

        String tables = String.join(",", Arrays.stream(actlog.tables()).map(String::valueOf).toList());
        requestLog.setTables(tables);
        // 方法路径
        String methodName = joinPoint.getTarget().getClass().getName() + "." + signature.getName() + "()";
        requestLog.setMethod(methodName);

    }
/*

    private DomainMetadata getDomainMetadataByMethodAnnotation(JoinPoint joinPoint, Map<String, String> params) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Actlog actlog = method.getAnnotation(Actlog.class);

        Class<?>[] tables = actlog.tables();
//        Class aClass = (Class) tables;
        DomainMetadata domainMetadata = DomainContextHolder.get("");

        // 方法路径
        String methodName = joinPoint.getTarget().getClass().getName() + "." + signature.getName() + "()";
        params.put("method_name", methodName);
        StringBuilder stringBuilder = new StringBuilder("{");
        //参数值
        List<Object> argValues = new ArrayList<>(Arrays.asList(joinPoint.getArgs()));
        //参数名称
        for (Object argValue : argValues) {
            stringBuilder.append(argValue).append(" ");
        }
        // 参数
        params.put("method_params", stringBuilder + " }");
//        // 描述
//        params.put("method_descr", auditLog.value());
        //方法
        params.forEach((k, v) -> {
            domainMetadata.setFieldValue(k, v);
        });

        return domainMetadata;
    }*/


}
