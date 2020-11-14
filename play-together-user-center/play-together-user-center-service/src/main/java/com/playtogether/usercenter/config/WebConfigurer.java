package com.playtogether.usercenter.config;

import com.playtogether.common.inteceptor.PTMicroServiceAuthInteceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableConfigurationProperties({JwtProperties.class})
public class WebConfigurer implements WebMvcConfigurer {

    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry
                .addInterceptor(new PTMicroServiceAuthInteceptor(jwtProperties.getMicroServicePublicKey()))
                .addPathPatterns("/**");
    }
}