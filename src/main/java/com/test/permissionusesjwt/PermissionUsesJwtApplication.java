package com.test.permissionusesjwt;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableRabbit
public class PermissionUsesJwtApplication {

    public static void main(String[] args) {
        SpringApplication.run(PermissionUsesJwtApplication.class, args);
    }

}
