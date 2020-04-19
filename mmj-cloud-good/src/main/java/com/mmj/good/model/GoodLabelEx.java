package com.mmj.good.model;

import java.util.List;

public class GoodLabelEx extends GoodLabel {

    private List<GoodLabelMapperEx> goodLabelMapperExs;

    private List<GoodLabelMapper> goodLabelMappers;

    private List<Integer> labelIds;

    private String goodName;

    private Integer goodId;

    public String getGoodName() {
        return goodName;
    }

    public void setGoodName(String goodName) {
        this.goodName = goodName;
    }

    public List<GoodLabelMapperEx> getGoodLabelMapperExs() {
        return goodLabelMapperExs;
    }

    public void setGoodLabelMapperExs(List<GoodLabelMapperEx> goodLabelMapperExs) {
        this.goodLabelMapperExs = goodLabelMapperExs;
    }

    public List<Integer> getLabelIds() {
        return labelIds;
    }

    public void setLabelIds(List<Integer> labelIds) {
        this.labelIds = labelIds;
    }

    public List<GoodLabelMapper> getGoodLabelMappers() {
        return goodLabelMappers;
    }

    public void setGoodLabelMappers(List<GoodLabelMapper> goodLabelMappers) {
        this.goodLabelMappers = goodLabelMappers;
    }

    public Integer getGoodId() {
        return goodId;
    }

    public void setGoodId(Integer goodId) {
        this.goodId = goodId;
    }
}
