package com.mmj.common.exception;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mmj.common.model.ReturnData;
import com.mmj.common.properties.SecurityConstants;

@ControllerAdvice
@ResponseBody
@ConditionalOnMissingClass("bossGlobalExceptionHandler")
public class GlobalExceptionHandler {
    
    private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(BaseException.class)
    public ReturnData<String> baseExceptionHandler(HttpServletResponse response, BaseException ex) {
        logger.error(ex.getMessage(),ex);
        response.setStatus(500);
        return new ReturnData<String>(SecurityConstants.EXCEPTION_CODE, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ReturnData<String> otherExceptionHandler(HttpServletResponse response, Exception ex) {
        response.setStatus(500);
        logger.error(ex.getMessage(),ex);
        return new ReturnData<String>(SecurityConstants.EXCEPTION_CODE, ex.getMessage());
    }

    @ExceptionHandler(AuthTokenException.class)
    public ReturnData<String> authTokenExceptionHandler(HttpServletResponse response, AuthTokenException ex) {
        response.setStatus(401);
        logger.error(ex.getMessage(),ex);
        return new ReturnData<String>(SecurityConstants.EXCEPTION_CODE, ex.getMessage());
    }
    
    @ExceptionHandler(ValidateCodeException.class)
    public ReturnData<String> validateCodeExceptionHandler(HttpServletResponse response, AuthTokenException ex) {
        response.setStatus(401);
        logger.error(ex.getMessage(),ex);
        return new ReturnData<String>(SecurityConstants.EXCEPTION_CODE, ex.getMessage());
    }

    @ExceptionHandler(CustomException.class)
    public ReturnData<String> customExceptionHandler(HttpServletResponse response, CustomException ex) {
        response.setStatus(200);
        logger.error(ex.getMessage(),ex);
        return new ReturnData<String>(SecurityConstants.EXCEPTION_CODE, ex.getMessage());
    }
    
    @ExceptionHandler(CustomMessageException.class)
    public ReturnData<String> customMessageExceptionHandler(HttpServletResponse response, CustomMessageException ex) {
        response.setStatus(200);
        return new ReturnData<String>(ex.getCode(), ex.getMessage());
    }
    
}
