package com.playtogether.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author guanlibin
 * @version 1.0
 * @since 2020/11/2 11:16
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.playtogether.user.mapper")
@ComponentScan(basePackages = {"com.playtogether.user", "com.playtogether.common"})
public class PtUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(PtUserApplication.class);
    }
}
