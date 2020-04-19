package com.mmj.zuul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.mmj.zuul.fallback.ZuulFallbackProvider;

@EnableRetry
@EnableEurekaClient
@EnableZuulProxy
@EnableGlobalMethodSecurity(prePostEnabled = true)
@SpringBootApplication
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class, DruidDataSourceAutoConfigure.class})
public class ZuulApplication {
  
  public static void main(String[] args) {
    SpringApplication.run(ZuulApplication.class, args);
  }

  @Bean
  public FallbackProvider routeAPIAZuulFallbackProvider() {
      return new ZuulFallbackProvider();
  }
  

}
