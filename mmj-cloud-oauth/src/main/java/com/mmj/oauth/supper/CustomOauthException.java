package com.mmj.oauth.supper;

import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = CustomOauthExceptionSerializer.class)
public class CustomOauthException extends OAuth2Exception {
    
    /** @Fields serialVersionUID: */
      	
    private static final long serialVersionUID = 7422068185042023441L;

    public CustomOauthException(String msg) {
        super(msg);
    }

}
