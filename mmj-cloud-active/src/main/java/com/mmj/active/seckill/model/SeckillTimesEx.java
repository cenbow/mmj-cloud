package com.mmj.active.seckill.model;

import com.mmj.active.common.model.ActiveGoodEx;

import java.util.List;

public class SeckillTimesEx extends SeckillTimes{

    private List<ActiveGoodEx> activeGoodExes;

    private String times;           //档期

    public List<ActiveGoodEx> getActiveGoodExes() {
        return activeGoodExes;
    }

    public void setActiveGoodExes(List<ActiveGoodEx> activeGoodExes) {
        this.activeGoodExes = activeGoodExes;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }


}
