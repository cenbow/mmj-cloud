package com.mmj.active.threeSaleTenner.model;

import com.mmj.active.common.model.ActiveGood;

import java.util.List;

public class ThreeSaleTennerEx extends ThreeSaleTenner{
    private List<ActiveGood> goodList;

    public List<ActiveGood> getGoodList() {
        return goodList;
    }

    public void setGoodList(List<ActiveGood> goodList) {
        this.goodList = goodList;
    }
}
