package com.mmj.common.properties;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;


@Data
@Configuration("SecurityProperties")
@ConfigurationProperties(prefix = "custom.security")
public class SecurityProperties {

  private OAuth2Properties oauth2 = new OAuth2Properties();
  
  private List<String> ignoreurls = new ArrayList<String>();
  
  private ValidateCodeProperties code = new ValidateCodeProperties();
  
  
  
  
  
}
