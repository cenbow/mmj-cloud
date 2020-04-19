package com.mmj.common.exception;

public class WxException extends BaseException{

    public WxException() {
        super();
    }

    public WxException(String message) {
        super(message);
    }

    public WxException(String message, int status) {
        super(message, status);
    }
}
