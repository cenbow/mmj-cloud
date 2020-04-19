package com.mmj.third.kuaidi100.model;

/**
 * @Description: 推送返回实体（状态返回）
 * @Auther: KK
 * @Date: 2018/10/13
 */
public class NoticeResponse {
    private Boolean result;
    private String returnCode;
    private String message;

    public NoticeResponse(Boolean result, String returnCode, String message) {
        this.result = result;
        this.returnCode = returnCode;
        this.message = message;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
