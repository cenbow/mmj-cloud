package com.mmj.oauth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@EnableEurekaClient
@EnableFeignClients
@ComponentScan(basePackages = {"com.mmj.*"})
@MapperScan(basePackages = {"com.mmj.oauth.mapper","com.mmj.oauth.channel.mapper","com.mmj.oauth.**.mapper"})
@SpringBootApplication
public class Oauth2Application {
  
  public static void main(String[] args) {
    SpringApplication.run(Oauth2Application.class, args);
  }
  
  
}
