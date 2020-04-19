package com.mmj.good.model;

import java.util.List;

public class GoodInfoBaseEx extends GoodInfo {

    private String goodClassLike;

    private List<GoodFile> goodFiles; //商品图片信息

    public String getGoodClassLike() {
        return goodClassLike;
    }

    public void setGoodClassLike(String goodClassLike) {
        this.goodClassLike = goodClassLike;
    }

    public List<GoodFile> getGoodFiles() {
        return goodFiles;
    }

    public void setGoodFiles(List<GoodFile> goodFiles) {
        this.goodFiles = goodFiles;
    }
}
