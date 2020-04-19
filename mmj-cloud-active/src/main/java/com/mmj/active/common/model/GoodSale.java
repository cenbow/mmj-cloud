package com.mmj.active.common.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

public class GoodSale {

    @ApiModelProperty(value = "销售ID")
    private Integer saleId;

    @ApiModelProperty(value = "商品ID")
    private Integer goodId;

    @ApiModelProperty(value = "SKU编码")
    private String goodSku;

    @ApiModelProperty(value = "普通单价")
    private Integer basePrice;

    @ApiModelProperty(value = "店铺价格")
    private Integer shopPrice;

    @ApiModelProperty(value = "拼团价")
    private Integer tuanPrice;

    @ApiModelProperty(value = "会员价")
    private Integer memberPrice;

    @ApiModelProperty(value = "销量")
    private Integer saleNum;

    @ApiModelProperty(value = "库存")
    private Integer goodNum;

    @ApiModelProperty(value = "创建人")
    private Long createrId;

    @ApiModelProperty(value = "创建时间")
    private Date createrTime;

    @ApiModelProperty(value = "修改人")
    private Long modifyId;

    @ApiModelProperty(value = "修改时间")
    private Date modifyTime;

    @ApiModelProperty(value = "普通单价")
    private Double baseAmount;  //普通单价

    @ApiModelProperty(value = "店铺价格")
    private Double shopAmount;  //店铺价格

    @ApiModelProperty(value = "拼团价")
    private Double tuanAmount;  //拼团价

    @ApiModelProperty(value = "会员价")
    private Double memberAmount;    //会员价

    public Integer getSaleId() {
        return saleId;
    }

    public void setSaleId(Integer saleId) {
        this.saleId = saleId;
    }

    public Integer getGoodId() {
        return goodId;
    }

    public void setGoodId(Integer goodId) {
        this.goodId = goodId;
    }

    public String getGoodSku() {
        return goodSku;
    }

    public void setGoodSku(String goodSku) {
        this.goodSku = goodSku;
    }

    public Integer getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(Integer basePrice) {
        this.basePrice = basePrice;
        this.baseAmount = basePrice == null ? null : Double.valueOf(basePrice) / 100.00;  //普通单价
    }

    public Integer getShopPrice() {
        return shopPrice;
    }

    public void setShopPrice(Integer shopPrice) {
        this.shopPrice = shopPrice;
        this.shopAmount = shopPrice == null ? null : Double.valueOf(shopPrice) / 100.00;  //店铺价格
    }

    public Integer getTuanPrice() {
        return tuanPrice;
    }

    public void setTuanPrice(Integer tuanPrice) {
        this.tuanPrice = tuanPrice;
        this.tuanAmount = tuanPrice == null ? null : Double.valueOf(tuanPrice) / 100.00;  //拼团价
    }

    public Integer getMemberPrice() {
        return memberPrice;
    }

    public void setMemberPrice(Integer memberPrice) {
        this.memberPrice = memberPrice;
        this.memberAmount = memberPrice == null ? null : Double.valueOf(memberPrice) / 100.00;    //会员价
    }

    public Integer getSaleNum() {
        return saleNum;
    }

    public void setSaleNum(Integer saleNum) {
        this.saleNum = saleNum;
    }

    public Integer getGoodNum() {
        return goodNum;
    }

    public void setGoodNum(Integer goodNum) {
        this.goodNum = goodNum;
    }

    public Long getCreaterId() {
        return createrId;
    }

    public void setCreaterId(Long createrId) {
        this.createrId = createrId;
    }

    public Date getCreaterTime() {
        return createrTime;
    }

    public void setCreaterTime(Date createrTime) {
        this.createrTime = createrTime;
    }

    public Long getModifyId() {
        return modifyId;
    }

    public void setModifyId(Long modifyId) {
        this.modifyId = modifyId;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Double getBaseAmount() {
        return baseAmount;
    }

    public void setBaseAmount(Double baseAmount) {
        this.baseAmount = baseAmount;
        this.basePrice = baseAmount == null ? null : (int)(baseAmount * 100);  //普通单价
    }

    public Double getShopAmount() {
        return shopAmount;
    }

    public void setShopAmount(Double shopAmount) {
        this.shopAmount = shopAmount;
        this.shopPrice = shopAmount == null ? null : (int)(shopAmount * 100);  //拼团单价
    }

    public Double getTuanAmount() {
        return tuanAmount;
    }

    public void setTuanAmount(Double tuanAmount) {
        this.tuanAmount = tuanAmount;
        this.tuanPrice = tuanAmount == null ? null : (int)(tuanAmount * 100);  //拼团单价
    }

    public Double getMemberAmount() {
        return memberAmount;
    }

    public void setMemberAmount(Double memberAmount) {
        this.memberAmount = memberAmount;
        this.memberPrice = memberAmount == null ? null : (int)(memberAmount * 100);  //会员单价
    }
}
