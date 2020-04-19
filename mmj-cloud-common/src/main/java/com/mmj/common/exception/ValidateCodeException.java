package com.mmj.common.exception;

import org.springframework.security.core.AuthenticationException;

public class ValidateCodeException extends AuthenticationException {

    
    /** @Fields serialVersionUID: */
      	
    private static final long serialVersionUID = 8962603038721063952L;

    public ValidateCodeException(String msg) {
        super(msg);
    }
}
