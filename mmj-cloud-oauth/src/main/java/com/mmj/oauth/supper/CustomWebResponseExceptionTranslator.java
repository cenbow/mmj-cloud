package com.mmj.oauth.supper;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.stereotype.Component;

import com.mmj.common.exception.OauthPasswordException;

@Component("customWebResponseExceptionTranslator")
public class CustomWebResponseExceptionTranslator implements WebResponseExceptionTranslator {
    @Override
    public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception {

        if(e instanceof OAuth2Exception) {
            OAuth2Exception oAuth2Exception = (OAuth2Exception) e;
            return ResponseEntity
                    .status(oAuth2Exception.getHttpErrorCode())
                    .body(new CustomOauthException(oAuth2Exception.getMessage()));
        } else if(e instanceof AuthenticationException) {
            AuthenticationException e1 = (AuthenticationException) e;
            return ResponseEntity
                    .status(200)
                    .body(new CustomOauthException(e1.getMessage()));
        } else if(e instanceof OauthPasswordException) {
        	OauthPasswordException e1 = (OauthPasswordException)e;
        	return ResponseEntity
                    .status(200)
                    .body(new CustomOauthException(e1.getMessage()));
        }
        return null;
    }
}
