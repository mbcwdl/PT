package com.playtogether.auth.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
 
import javax.servlet.http.HttpServletRequest;
 
/**
 * @author lidai
 * @date 2019/2/27 16:14
 * <p>
 * Feign调用的时候添加请求头Token
 */
@Configuration
public class FeignConfiguration implements RequestInterceptor {

    private static final String MICRO_SERVICE_AUTH_TOKEN = "MicroServiceAuthToken";

    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        requestTemplate.header(MICRO_SERVICE_AUTH_TOKEN, request.getHeader(MICRO_SERVICE_AUTH_TOKEN));
    }
}