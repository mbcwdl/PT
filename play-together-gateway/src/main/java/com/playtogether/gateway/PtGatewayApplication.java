package com.playtogether.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * @author guanlibin
 * @version 1.0
 * @since 2020/11/3 16:21
 */
@EnableZuulProxy
@SpringBootApplication
public class PtGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(PtGatewayApplication.class);
    }
}
