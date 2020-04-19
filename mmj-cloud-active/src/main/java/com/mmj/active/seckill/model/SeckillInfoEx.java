package com.mmj.active.seckill.model;

import java.util.Date;
import java.util.List;

public class SeckillInfoEx extends SeckillInfo {

    private Integer seckillPriodNow;    //档期期次

    private Date createrTimeStart;

    private Date createrTimeEnd;

    private List<SeckillTimesEx> seckillTimesExes;

    public Integer getSeckillPriodNow() {
        return seckillPriodNow;
    }

    public void setSeckillPriodNow(Integer seckillPriodNow) {
        this.seckillPriodNow = seckillPriodNow;
    }

    public Date getCreaterTimeStart() {
        return createrTimeStart;
    }

    public void setCreaterTimeStart(Date createrTimeStart) {
        this.createrTimeStart = createrTimeStart;
    }

    public Date getCreaterTimeEnd() {
        return createrTimeEnd;
    }

    public void setCreaterTimeEnd(Date createrTimeEnd) {
        this.createrTimeEnd = createrTimeEnd;
    }

    public List<SeckillTimesEx> getSeckillTimesExes() {
        return seckillTimesExes;
    }

    public void setSeckillTimesExes(List<SeckillTimesEx> seckillTimesExes) {
        this.seckillTimesExes = seckillTimesExes;
    }
}
