package com.playtogether.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author guanlibin
 * @version 1.0
 * @since 2020/11/3 11:24
 */
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan(basePackages = {"com.playtogether.auth", "com.playtogether.common"})
public class PtAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(PtAuthApplication.class);
    }
}
