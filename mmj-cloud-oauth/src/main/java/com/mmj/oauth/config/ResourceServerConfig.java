package com.mmj.oauth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.mmj.common.handler.AccessDeniedHandler;
import com.mmj.common.properties.SecurityProperties;
import com.mmj.common.utils.PermitAllUrl;
import com.mmj.oauth.code.config.OpenIdAuthenticationSecurityConfig;
import com.mmj.oauth.code.config.SmsCodeAuthenticationSecurityConfig;
import com.mmj.oauth.code.config.ValidateCodeSecurityConfig;
import com.mmj.oauth.supper.AuthExceptionEntryPoint;

@Order(6)
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
  
  @Autowired
  private AuthenticationSuccessHandler appLoginInSuccessHandler;

  @Autowired
  private AuthenticationFailureHandler appLoginFailureHandler;
  
  @Autowired
  private SecurityProperties securityProperties;
  
  @Autowired
  private OAuth2WebSecurityExpressionHandler expressionHandler;
  
  @Autowired
  AccessDeniedHandler accessDeniedHandler;
  
  @Autowired
  private SmsCodeAuthenticationSecurityConfig smsCodeAuthenticationSecurityConfig;

  @Autowired
  private ValidateCodeSecurityConfig validateCodeSecurityConfig;
  
  @Autowired
  OpenIdAuthenticationSecurityConfig openIdAuthenticationSecurityConfig;
  
  @Override
  public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
    resources.expressionHandler(expressionHandler);
    resources.accessDeniedHandler(accessDeniedHandler);
    resources.authenticationEntryPoint(new AuthExceptionEntryPoint());
  }


  @Override
  public void configure(HttpSecurity http) throws Exception {
    ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = http.authorizeRequests();
    registry.and().formLogin()
                    .successHandler(appLoginInSuccessHandler)
                    .failureHandler(appLoginFailureHandler)
                    .and()
                    .headers().frameOptions().disable()
                    .and()
                    .exceptionHandling().authenticationEntryPoint(new AuthExceptionEntryPoint())
                    .and()
                    .apply(smsCodeAuthenticationSecurityConfig)
                    .and()
                    .apply(validateCodeSecurityConfig)
                    .and()
                    .apply(openIdAuthenticationSecurityConfig)
                    .and()
                    .csrf().disable();
    registry.antMatchers(PermitAllUrl.permitAllUrl(securityProperties.getIgnoreurls())).permitAll();
  }
  
  @Bean
  public OAuth2WebSecurityExpressionHandler oAuth2WebSecurityExpressionHandler(ApplicationContext applicationContext) {
      OAuth2WebSecurityExpressionHandler expressionHandler = new OAuth2WebSecurityExpressionHandler();
      expressionHandler.setApplicationContext(applicationContext);
      return expressionHandler;
  }
  
  

}
