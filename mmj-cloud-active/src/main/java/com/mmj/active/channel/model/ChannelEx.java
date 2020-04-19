package com.mmj.active.channel.model;

public class ChannelEx extends Channel {
    private String startTime;  //开始时间

    private String endTime;    //结束时间

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
