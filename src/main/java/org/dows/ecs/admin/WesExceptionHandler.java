package org.dows.ecs.admin;

import com.auth0.jwt.exceptions.TokenExpiredException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.dows.aac.config.AacConfig;
import org.dows.framework.api.Response;
import org.dows.framework.api.exceptions.BaseException;
import org.dows.framework.api.exceptions.SignatureException;
import org.dows.framework.api.i18n.UnifiedMessageSource;
import org.dows.framework.api.status.AuthStatusCode;
import org.dows.framework.api.status.CommonStatusCode;
import org.dows.framework.api.status.ResponseStatusCode;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.util.Objects;
import java.util.zip.DataFormatException;

import static org.springframework.util.StringUtils.hasText;

/***
 * 统一封装异常、统一处理出参
 */
@Slf4j
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE - 1)
public class WesExceptionHandler /*implements ResponseBodyAdvice<Object>*/ {

    /**
     * 生产环境
     */
    private final static String ENV_PRD = "prd";


    @Value("${spring.application.name:}")
    private String serviceName;

    /**
     * 当前环境
     */
    @Value("${spring.profiles.active:}")
    private String profile;

    @Autowired
    private UnifiedMessageSource unifiedMessageSource;

    @Autowired
    private AacConfig aacConfig;

    /**
     * 自定义异常
     *
     * @param request  请求参数
     * @param response 响应参数
     * @param e        异常
     * @return 异常结果
     */
    @ExceptionHandler(value = BaseException.class)
    public Response<?> handleBaseException(HttpServletRequest request, HttpServletResponse response,
                                           BaseException e) {
        log.error("调用={}服务出现自定义异常，请求的url是={}，请求的方法是={}，原因={}", serviceName, request.getRequestURL(),
                request.getMethod(), e.getMessage(), e);
        if (e.getStatusCode() != null) {
            return Response.failed(e.getStatusCode());
        }
        return Response.failed(getMessage(e));
    }

    /**
     * Controller上一层相关异常
     *
     * @param request  请求参数
     * @param response 响应参数
     * @param e        异常
     * @return 异常结果
     */
    //@ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({NoHandlerFoundException.class,
            HttpMediaTypeNotSupportedException.class, HttpMediaTypeNotAcceptableException.class,
            MissingPathVariableException.class,
            HttpMessageNotReadableException.class, HttpMessageNotWritableException.class,
            ServletRequestBindingException.class, ConversionNotSupportedException.class,
            MissingServletRequestPartException.class})
    public Response<?> handleServletException(HttpServletRequest request, HttpServletResponse response,
                                              Exception e) {
        log.error("调用={}服务出现controller的上层出现异常，请求的url是={}，请求的方法是={}，原因={}", serviceName, request.getRequestURL(),
                request.getMethod(), e);
        int code = CommonStatusCode.SERVER_ERROR.getCode();
        try {
            ResponseStatusCode servletExceptionEnum = ResponseStatusCode.valueOf(e.getClass().getSimpleName());
            code = servletExceptionEnum.getCode();
        } catch (Exception e1) {
            log.error("class [{}] not defined in enum {}", e.getClass().getName(), ResponseStatusCode.class.getName());
        }

        if (ENV_PRD.equals(profile)) {
            // 当为生产环境, 不适合把具体的异常信息展示给用户, 比如404.
            code = CommonStatusCode.SERVER_ERROR.getCode();
            BaseException baseException = new BaseException(CommonStatusCode.SERVER_ERROR);
            String message = getMessage(baseException);
            return Response.failed(code, message);
        }

        return Response.failed(code, e.getMessage());
    }

    /**
     * 参数绑定异常
     *
     * @param request  请求参数
     * @param response 响应参数
     * @param e        异常
     * @return 异常结果
     */
    @ExceptionHandler(value = BindException.class)
    public Response<?> handleBindException(HttpServletRequest request, HttpServletResponse response, BindException e) {
        log.error("调用={}服务出现参数绑定校验异常，请求的url是={}，请求的方法是={}，原因={}",
                serviceName, request.getRequestURL(), request.getMethod(), e);
        return wrapperBindingResult(e.getBindingResult());
    }

    /**
     * 参数校验(Valid)异常，将校验失败的所有异常组合成一条错误信息
     *
     * @param request  请求参数
     * @param response 响应参数
     * @param e        异常
     * @return 异常结果
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Response<?> handleValidException(HttpServletRequest request, HttpServletResponse response,
                                            MethodArgumentNotValidException e) {
        log.error("调用={}服务出现方法参数校验异常，请求的url是={}，请求的方法是={}，原因={}", serviceName, request.getRequestURL(),
                request.getMethod(), e);
        return wrapperBindingResult(e.getBindingResult());
    }

    /**
     * 其他未定义的异常
     *
     * @param request  请求参数
     * @param response 响应参数
     * @param e        异常
     * @return 异常结果
     */
    @ExceptionHandler(value = Exception.class)
    public Object resolveException(HttpServletRequest request, HttpServletResponse response, Exception e) {
        log.error("调用={}服务出现异常了，请求的url是={}，请求的方法是={}，原因={}",
                serviceName, request.getRequestURL(), request.getMethod(), e.getMessage(), e);

        if (isAjax(request)) {
            if (ENV_PRD.equals(profile)) {
                // 当为生产环境, 不适合把具体的异常信息展示给用户, 比如数据库异常信息.
                BaseException baseException = new BaseException(CommonStatusCode.SERVER_ERROR);
                String message = getMessage(baseException);
                return Response.failed(CommonStatusCode.SERVER_ERROR);
            }
            return Response.failed(e.getMessage());
        } else {
            ModelAndView mav = new ModelAndView();
            mav.addObject("exception", e);
            mav.addObject("url", request.getRequestURL());
            mav.setViewName("error");
            return mav;
        }

    }

    /***
     * 统一封装返回值，如果返回值是void,自动构造请求成功的返回值<br>
     * 如果返回值本身是ResponseDto，不做处理<br>
     * 其他的统一做添加请求成功的返回码。
     */
//    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType mediaType,
                                  Class<? extends HttpMessageConverter<?>> arg3, ServerHttpRequest arg4, ServerHttpResponse arg5) {
        // 1.获取返回参数的全类名
        final String returnTypeName = returnType.getParameterType().getName();

        /*if (Objects.equals("void", returnTypeName)) {
            return Response.ok(null);
        }*/
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (Objects.nonNull(requestAttributes)) {
            HttpServletRequest request = requestAttributes.getRequest();
            String contextPath = request.getContextPath();
//            String contextPathExcludes = responseBodyAdviceExcludeProperties.getContextPathExclude();
//            if (hasText(contextPathExcludes) && contextPathExcludes.equals(contextPath)) {
//                return body;
//            }
        }
        if (body instanceof Response || Objects.equals(Response.class.getName(), returnTypeName)) {
            return body;
        }

        return Response.ok(body);
    }

    /**
     * 是否支持做的统一返回值处理，<br>
     * 如果返回false，将不会进入beforeBodyWrite方法
     */
    //@Override
    public boolean supports(MethodParameter arg0, Class<? extends HttpMessageConverter<?>> arg1) {
        return true;
    }

    /**
     * 获取国际化消息
     *
     * @param e 异常
     * @return 国际化消息
     */
    public String getMessage(BaseException e) {
        String message = "";
        if (null != e.getStatusCode()) {
            String code = "response." + e.getStatusCode().toString();
            message = unifiedMessageSource.getMessage(code, e.getArgs());
        }

        if (!hasText(message)) {
            return e.getMessage();
        }

        return message;
    }

    /**
     * 包装绑定异常结果
     *
     * @param bindingResult 绑定结果
     * @return 异常结果
     */
    private Response<?> wrapperBindingResult(BindingResult bindingResult) {
        StringBuilder msg = new StringBuilder();

        for (ObjectError error : bindingResult.getAllErrors()) {
            msg.append(", ");
            if (error instanceof FieldError) {
                msg.append(((FieldError) error).getField()).append(": ");
            }
            msg.append(error.getDefaultMessage() == null ? "" : error.getDefaultMessage());

        }
        String descr = msg.substring(2);
        return Response.validateFailed(descr);
    }


    /**
     * http请求的方法不正确
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public Response httpRequestMethodNotSupportedExceptionHandler(HttpRequestMethodNotSupportedException e) {
        log.error("http请求的方法不正确:【" + e.getMessage() + "】", e);
        return Response.failed("http请求的方法不正确");
    }

    /**
     * 请求参数不全
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public Response missingServletRequestParameterExceptionHandler(MissingServletRequestParameterException e) {
        log.error("请求参数不全:【" + e.getMessage() + "】", e);
        return Response.failed("请求参数不全");
    }

    /**
     * 请求参数类型不正确
     */
    @ExceptionHandler(TypeMismatchException.class)
    @ResponseBody
    public Response typeMismatchExceptionHandler(TypeMismatchException e) {
        log.error("请求参数类型不正确:【" + e.getMessage() + "】", e);
        return Response.failed("请求参数类型不正确");
    }

    /**
     * 数据格式不正确
     */
    @ExceptionHandler(DataFormatException.class)
    @ResponseBody
    public Response dataFormatExceptionHandler(DataFormatException e) {
        log.error("数据格式不正确:【" + e.getMessage() + "】", e);
        return Response.failed("数据格式不正确");
    }

    /**
     * 非法输入
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public Response illegalArgumentExceptionHandler(IllegalArgumentException e) {
        log.error("非法输入:【" + e.getMessage() + "】", e);
        return Response.failed("非法输入:" + e.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseBody
    public Response authenticationExceptionHandler(AuthenticationException e) {
        log.info("认证失败授权:【" + e.getMessage() + "】", e);
        // 如果有开启则返回ok

        if (aacConfig.isEnableLogin()) {
            if (e instanceof BadCredentialsException) {
                return Response.authFailed(AuthStatusCode.PASSWORD_ERROR);
            } else if (e instanceof UsernameNotFoundException) {
                return Response.authFailed(AuthStatusCode.PASSWORD_ERROR);
            } else {
                return Response.unauthorized(e.getMessage());
            }
        }
        return Response.ok();
    }


    @ExceptionHandler(SQLException.class)
    @ResponseBody
    public Response authenticationExceptionHandler(SQLException e) {
        log.info("sql:【" + e.getMessage() + "】", e);
        return Response.failed(e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public Response authenticationExceptionHandler(AccessDeniedException e) {
        log.info("拒绝访问:【" + e.getMessage() + "】", e);
        return Response.failed(e.getMessage());
    }

//    @ExceptionHandler  //处理其他异常
//    @ResponseBody
//    public Object allExceptionHandler(Exception e, HttpServletRequest request, HttpServletResponse response) {
//        System.out.println(e.getStackTrace());
//        log.error("具体错误信息:【" + e.getMessage() + "】"); //会记录出错的代码行等具体信息
//        e.printStackTrace();
//        if (isAjax(request)) {
//            return Response.failed(e.getMessage());
//        } else {
//            ModelAndView mav = new ModelAndView();
//            mav.addObject("exception", e);
//            mav.addObject("url", request.getRequestURL());
//            mav.setViewName("error");
//            return mav;
//        }
//    }

    /**
     * 判断是否ajax请求
     *
     * @param httpRequest
     * @return
     */
    public static boolean isAjax(HttpServletRequest httpRequest) {
        return (httpRequest.getHeader("X-Requested-With") != null
                && "XMLHttpRequest".equals(httpRequest.getHeader("X-Requested-With").toString()));
    }

    @ExceptionHandler(value = AsyncRequestTimeoutException.class)
    public Response<?> asyncRequestTimeoutHandler(HttpServletRequest request, HttpServletResponse response, AsyncRequestTimeoutException e) {
        log.warn("异步请求超时:【" + e.getMessage() + "】");
        return Response.failed(304, "长轮询超时重试");
    }


    @ExceptionHandler(value = {BadSqlGrammarException.class})
    public Response<?> badSqlGrammarExceptionHandler(HttpServletRequest request, HttpServletResponse response, BadSqlGrammarException e) {
        log.warn("sql错误:【" + e.getMessage() + "】");
        return Response.failed(500, e.getSQLException().getMessage());
    }

    @ExceptionHandler(value = {SQLSyntaxErrorException.class})
    public Response<?> sqlSyntaxErrorExceptionHandler(HttpServletRequest request, HttpServletResponse response, SQLSyntaxErrorException e) {
        log.warn("sql语法错误:【" + e.getMessage() + "】");
        return Response.failed(500, e.getCause().getMessage());
    }

    @ExceptionHandler(value = {TokenExpiredException.class})
    public Response<?> tokenExpiredExceptionHandler(HttpServletRequest request, HttpServletResponse response, TokenExpiredException e) {
        return Response.failed(401, e.getMessage());
    }


    @ExceptionHandler(value = {RuntimeException.class})
    public Response<?> runtimeExceptionHandler(HttpServletRequest request, HttpServletResponse response, RuntimeException e) {
        return Response.failed(500, e.getMessage());
    }

    @ExceptionHandler(value = {SignatureException.class})
    public Response<?> signatureExceptionHandler(SignatureException e) {
        return Response.failed(500, e.getMessage());
    }

    @ExceptionHandler(value = {DuplicateKeyException.class})
    public Response<?> duplicateKeyExceptionHandler(DuplicateKeyException e) {
        return Response.failed(500, e.getMessage());
    }
}
