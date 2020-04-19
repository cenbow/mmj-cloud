package com.mmj.third.kuaidi100.model;

import java.util.List;

/**
 * @description: 百世快递查询返回
 * @auther: KK
 * @date: 2019/6/4
 */
public class BestResponse {
    /**
     * 结果描述，true成功，false失败
     */
    private boolean result;
    /**
     * 备注
     */
    private String remark;
    /**
     * 错误码
     */
    private String errorCode;
    /**
     * 错误描述
     */
    private String errorDescription;

    /**
     * 流转信息列表
     */
    private List<BestTraceLogsResponse> traceLogs;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public List<BestTraceLogsResponse> getTraceLogs() {
        return traceLogs;
    }

    public void setTraceLogs(List<BestTraceLogsResponse> traceLogs) {
        this.traceLogs = traceLogs;
    }
}
