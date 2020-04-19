package com.qianmi.open.api;

/**
 * 规则异常
 */
public class ApiRuleException extends ApiException {

    public ApiRuleException(String errCode, String errMsg) {
        super(errCode, errMsg);
    }
}
