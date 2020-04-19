package com.mmj.common.properties;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class OAuth2Properties {

    private String jwtSigningKey = "mmj@2019#2A";
    
    private List<OAuth2ClientProperties> clients = new ArrayList<OAuth2ClientProperties>();
    
    public OAuth2Properties() {
      super();
    }
    
}
