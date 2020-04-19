package com.mmj.common.exception;

public class BusinessException extends BaseException{

    public BusinessException() {
        super();
    }

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, int status) {
        super(message, status);
    }
}
