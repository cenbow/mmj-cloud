package com.mmj.good.model;

public class GoodCombinationExcel {

    //组合商品编码 sku
    private String combinsku;

    //组合款式编码 spu
    private String combinspu;

    //商品编码 sku
    private String singlesku;

    //数量-包装含量
    private Integer packagenum;

    public String getCombinspu() {
        return combinspu;
    }

    public void setCombinspu(String combinspu) {
        this.combinspu = combinspu;
    }

    public String getCombinsku() {
        return combinsku;
    }

    public void setCombinsku(String combinsku) {
        this.combinsku = combinsku;
    }

    public String getSinglesku() {
        return singlesku;
    }

    public void setSinglesku(String singlesku) {
        this.singlesku = singlesku;
    }

    public Integer getPackagenum() {
        return packagenum;
    }

    public void setPackagenum(Integer packagenum) {
        this.packagenum = packagenum;
    }
}
