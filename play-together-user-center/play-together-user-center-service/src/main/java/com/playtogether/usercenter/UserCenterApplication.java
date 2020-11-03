package com.playtogether.usercenter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

/**
 * @author guanlibin
 * @version 1.0
 * @since 2020/11/2 11:16
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.playtogether.usercenter.mapper")
@ComponentScan(basePackages = {"com.playtogether.usercenter", "com.playtogether.common"})
public class UserCenterApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserCenterApplication.class);
    }
}
