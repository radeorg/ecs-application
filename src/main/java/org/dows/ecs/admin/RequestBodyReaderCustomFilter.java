//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.dows.ecs.admin;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.dows.framework.api.web.RequestWrapper;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;

public class RequestBodyReaderCustomFilter implements Filter {
    public RequestBodyReaderCustomFilter() {
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        try {
            String contentType = request.getContentType();
            if (contentType != null && contentType.toLowerCase().startsWith("multipart/")) {
                chain.doFilter(request, response);
            }else{
                ServletRequest requestWrapper = null;
                if (request instanceof HttpServletRequest) {
                    requestWrapper = new RequestWrapper((HttpServletRequest)request);
                }

                if (requestWrapper == null) {
                    chain.doFilter(request, response);
                } else {
                    chain.doFilter(requestWrapper, response);
                }
            }


        } catch (ServletException | IOException var5) {
            Exception e = var5;
            e.printStackTrace();
        }

    }

    public void destroy() {
    }
}
