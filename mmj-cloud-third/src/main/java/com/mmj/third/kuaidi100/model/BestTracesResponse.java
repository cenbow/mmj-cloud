package com.mmj.third.kuaidi100.model;

import java.util.List;

/**
 * @description: 流转信息
 * @auther: KK
 * @date: 2019/6/4
 */
public class BestTracesResponse {
    private List<Trace> trace;

    public List<Trace> getTrace() {
        return trace;
    }

    public void setTrace(List<Trace> trace) {
        this.trace = trace;
    }

    public static class Trace{
        private String acceptTime;
        private String acceptAddress;
        private String scanType;
        private String remark;

        public String getAcceptTime() {
            return acceptTime;
        }

        public void setAcceptTime(String acceptTime) {
            this.acceptTime = acceptTime;
        }

        public String getAcceptAddress() {
            return acceptAddress;
        }

        public void setAcceptAddress(String acceptAddress) {
            this.acceptAddress = acceptAddress;
        }

        public String getScanType() {
            return scanType;
        }

        public void setScanType(String scanType) {
            this.scanType = scanType;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }
    }
}
