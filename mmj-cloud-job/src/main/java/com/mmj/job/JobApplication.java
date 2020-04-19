package com.mmj.job;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@EnableEurekaClient
@EnableFeignClients
@ComponentScan(basePackages = {"com.mmj.*"})
@SpringBootApplication
public class JobApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(JobApplication.class, args);
    }

}
