package com.playtogether.registry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author guanlibin
 * @version 1.0
 * @since 2020/11/3 10:34
 */
@EnableEurekaServer
@SpringBootApplication
public class RegistryCenterApplication {
    public static void main(String[] args) {
        SpringApplication.run(RegistryCenterApplication.class);
    }
}
