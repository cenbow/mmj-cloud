package com.mmj.good.model;

import com.alibaba.excel.annotation.ExcelProperty;

import java.util.Date;

public class GoodInfoProperty {

    private Integer goodId;
    private String goodClass;
    private String goodName;
    private String sellingPoint;
    private String shortName;
    private String goodSpu;
    private Integer autoShow;
    private Integer virtualFlag;
    private Integer memberFlag;
    private Integer combinaFlag;
    private Date upTime;
    private Integer saleDays;
    private String goodStatus;

    public Integer getGoodId() {
        return goodId;
    }

    public void setGoodId(Integer goodId) {
        this.goodId = goodId;
    }

    public String getGoodClass() {
        return goodClass;
    }

    public void setGoodClass(String goodClass) {
        this.goodClass = goodClass;
    }

    public String getGoodName() {
        return goodName;
    }

    public void setGoodName(String goodName) {
        this.goodName = goodName;
    }

    public String getSellingPoint() {
        return sellingPoint;
    }

    public void setSellingPoint(String sellingPoint) {
        this.sellingPoint = sellingPoint;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getGoodSpu() {
        return goodSpu;
    }

    public void setGoodSpu(String goodSpu) {
        this.goodSpu = goodSpu;
    }

    public Integer getAutoShow() {
        return autoShow;
    }

    public void setAutoShow(Integer autoShow) {
        this.autoShow = autoShow;
    }

    public Integer getVirtualFlag() {
        return virtualFlag;
    }

    public void setVirtualFlag(Integer virtualFlag) {
        this.virtualFlag = virtualFlag;
    }

    public Integer getMemberFlag() {
        return memberFlag;
    }

    public void setMemberFlag(Integer memberFlag) {
        this.memberFlag = memberFlag;
    }

    public Integer getCombinaFlag() {
        return combinaFlag;
    }

    public void setCombinaFlag(Integer combinaFlag) {
        this.combinaFlag = combinaFlag;
    }

    public Date getUpTime() {
        return upTime;
    }

    public void setUpTime(Date upTime) {
        this.upTime = upTime;
    }

    public Integer getSaleDays() {
        return saleDays;
    }

    public void setSaleDays(Integer saleDays) {
        this.saleDays = saleDays;
    }

    public String getGoodStatus() {
        return goodStatus;
    }

    public void setGoodStatus(String goodStatus) {
        this.goodStatus = goodStatus;
    }
}
