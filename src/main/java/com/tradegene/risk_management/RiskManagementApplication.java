package com.tradegene.risk_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@EnableFeignClients
@SpringBootApplication
@ComponentScan(basePackages = "com.tradegene")
public class RiskManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(RiskManagementApplication.class, args);
    }
}
// korean is good
