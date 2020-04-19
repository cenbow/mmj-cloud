package com.mmj.zuul.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@ComponentScan(basePackages = {"com.mmj.*"})
public class ZuulCoreConfig {
    
    private static final String PATH_ALL = "/**";
	private static final String ALL = "*";

	@Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.addAllowedOrigin(ALL);
        corsConfiguration.addAllowedHeader(ALL);
        corsConfiguration.addAllowedMethod(ALL);
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration(PATH_ALL, corsConfiguration);
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }
    
}
