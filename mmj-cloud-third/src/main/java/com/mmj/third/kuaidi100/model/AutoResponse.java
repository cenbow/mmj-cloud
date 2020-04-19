package com.mmj.third.kuaidi100.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @Description: 快递100单号归属公司智能判断接口返回
 * @Auther: KK
 * @Date: 2018/10/17
 */
public class AutoResponse {
    private String comCode;
    @JsonIgnore
    private String id;
    @JsonIgnore
    private String noCount;
    @JsonIgnore
    private String noPre;
    @JsonIgnore
    private String startTime;

    public String getComCode() {
        return comCode;
    }

    public void setComCode(String comCode) {
        this.comCode = comCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNoCount() {
        return noCount;
    }

    public void setNoCount(String noCount) {
        this.noCount = noCount;
    }

    public String getNoPre() {
        return noPre;
    }

    public void setNoPre(String noPre) {
        this.noPre = noPre;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
}
