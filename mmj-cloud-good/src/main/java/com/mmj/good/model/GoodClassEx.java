package com.mmj.good.model;

import java.util.List;

public class GoodClassEx extends GoodClass {

    private String parentCode;

    private Integer level;

    private Integer isShow;

    private String noClassCode;

    private List<GoodClassEx> goodClassExes;

    private GoodBanner goodBanner;

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getIsShow() {
        return isShow;
    }

    public void setIsShow(Integer isShow) {
        this.isShow = isShow;
    }

    public String getNoClassCode() {
        return noClassCode;
    }

    public void setNoClassCode(String noClassCode) {
        this.noClassCode = noClassCode;
    }

    public List<GoodClassEx> getGoodClassExes() {
        return goodClassExes;
    }

    public void setGoodClassExes(List<GoodClassEx> goodClassExes) {
        this.goodClassExes = goodClassExes;
    }

    public GoodBanner getGoodBanner() {
        return goodBanner;
    }

    public void setGoodBanner(GoodBanner goodBanner) {
        this.goodBanner = goodBanner;
    }
}
