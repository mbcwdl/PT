package com.playtogether.authcenter.config;

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
 
    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        requestTemplate.header("Authorization", request.getHeader("Authorization"));
    }
}