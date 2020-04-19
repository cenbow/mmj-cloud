package com.mmj.common.exception;

public class AuthTokenException extends BaseException {

    
    /** @Fields serialVersionUID: */
      	
    private static final long serialVersionUID = -7800042228762054733L;
    
    public AuthTokenException(String message) {
        super(message, 401);
    }

}
