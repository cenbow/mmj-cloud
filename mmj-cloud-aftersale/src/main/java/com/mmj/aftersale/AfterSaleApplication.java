package com.mmj.aftersale;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@EnableEurekaClient
@EnableFeignClients
@ComponentScan(basePackages = {"com.mmj.*"})
@MapperScan(basePackages = {"com.mmj.aftersale.mapper"})
@SpringBootApplication
public class AfterSaleApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(AfterSaleApplication.class, args);
    }
    


}
