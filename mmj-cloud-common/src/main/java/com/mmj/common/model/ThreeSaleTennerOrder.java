package com.mmj.common.model;

import java.util.List;

public class ThreeSaleTennerOrder extends ThreeSaleTenner{

    private List<Integer> goodIdList;

    public List<Integer> getGoodIdList() {
        return goodIdList;
    }

    public void setGoodIdList(List<Integer> goodIdList) {
        this.goodIdList = goodIdList;
    }
}
