package com.mmj.active.channel.model.dto;

public class ChannelDayDto {
    private Integer personDayCount;   //每天扫码人数

    private Integer scanDayCount;   //每天扫码次数

    private String date;  //时间

    public Integer getPersonDayCount() {
        return personDayCount;
    }

    public void setPersonDayCount(Integer personDayCount) {
        this.personDayCount = personDayCount;
    }

    public Integer getScanDayCount() {
        return scanDayCount;
    }

    public void setScanDayCount(Integer scanDayCount) {
        this.scanDayCount = scanDayCount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
