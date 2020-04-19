package com.mmj.common.model.good;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.mmj.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 商品销售信息表
 * </p>
 *
 * @author lyf
 * @since 2019-06-03
 */
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_good_sale")
@ApiModel(value="GoodSale对象", description="商品销售信息表")
public class GoodSale extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "销售ID")
    @TableId(value = "SALE_ID", type = IdType.AUTO)
    private Integer saleId;

    @ApiModelProperty(value = "商品ID")
    @TableField("GOOD_ID")
    private Integer goodId;

    @ApiModelProperty(value = "SKU编码")
    @TableField("GOOD_SKU")
    private String goodSku;

    @ApiModelProperty(value = "普通单价")
    @TableField("BASE_PRICE")
    private Integer basePrice;

    @ApiModelProperty(value = "店铺价格")
    @TableField("SHOP_PRICE")
    private Integer shopPrice;

    @ApiModelProperty(value = "拼团价")
    @TableField("TUAN_PRICE")
    private Integer tuanPrice;

    @ApiModelProperty(value = "会员价")
    @TableField("MEMBER_PRICE")
    private Integer memberPrice;

    @ApiModelProperty(value = "销量")
    @TableField("SALE_NUM")
    private Integer saleNum;

    @ApiModelProperty(value = "库存")
    @TableField("GOOD_NUM")
    private Integer goodNum;

    @ApiModelProperty(value = "创建人")
    @TableField("CREATER_ID")
    private Long createrId;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATER_TIME")
    private Date createrTime;

    @ApiModelProperty(value = "修改人")
    @TableField("MODIFY_ID")
    private Long modifyId;

    @ApiModelProperty(value = "修改时间")
    @TableField("MODIFY_TIME")
    private Date modifyTime;

    @ApiModelProperty(value = "普通单价")
    @TableField(exist = false)
    private Double baseAmount;  //普通单价

    @ApiModelProperty(value = "店铺价格")
    @TableField(exist = false)
    private Double shopAmount;  //店铺价格

    @ApiModelProperty(value = "拼团价")
    @TableField(exist = false)
    private Double tuanAmount;  //拼团价

    @ApiModelProperty(value = "会员价")
    @TableField(exist = false)
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
        if (basePrice == null) {
            this.baseAmount = null;
        } else {
            if (basePrice == 0.0) {
                this.baseAmount = 0.0;
            } else {
                BigDecimal b1 = new BigDecimal(String.valueOf(basePrice));
                BigDecimal b2 = new BigDecimal("100");
                this.baseAmount = b1.divide(b2).doubleValue();//普通单价
            }
        }
    }

    public Integer getShopPrice() {
        return shopPrice;
    }

    public void setShopPrice(Integer shopPrice) {
        this.shopPrice = shopPrice;

        if (shopPrice == null) {
            this.shopAmount = null;
        } else {
            if (shopPrice == 0.0) {
                this.shopAmount = 0.0;
            } else {
                BigDecimal b1 = new BigDecimal(String.valueOf(shopPrice));
                BigDecimal b2 = new BigDecimal("100");
                this.shopAmount = b1.divide(b2).doubleValue();//店铺价格
            }
        }
    }

    public Integer getTuanPrice() {
        return tuanPrice;
    }

    public void setTuanPrice(Integer tuanPrice) {
        this.tuanPrice = tuanPrice;

        if (tuanPrice == null) {
            this.tuanAmount = null;
        } else {
            if (tuanPrice == 0.0) {
                this.tuanAmount = 0.0;
            } else {
                BigDecimal b1 = new BigDecimal(String.valueOf(tuanPrice));
                BigDecimal b2 = new BigDecimal("100");
                this.tuanAmount = b1.divide(b2).doubleValue();//拼团价
            }
        }
    }

    public Integer getMemberPrice() {
        return memberPrice;
    }

    public void setMemberPrice(Integer memberPrice) {
        this.memberPrice = memberPrice;

        if (memberPrice == null) {
            this.memberAmount = null;
        } else {
            if (memberPrice == 0.0) {
                this.memberAmount = 0.0;
            } else {
                BigDecimal b1 = new BigDecimal(String.valueOf(memberPrice));
                BigDecimal b2 = new BigDecimal("100");
                this.memberAmount = b1.divide(b2).doubleValue();//会员价
            }
        }
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

        if (baseAmount == null) {
            this.basePrice = null;
        } else {
            if (baseAmount == 0) {
                this.basePrice = 0;
            } else {
                BigDecimal b1 = new BigDecimal(String.valueOf(baseAmount));
                BigDecimal b2 = new BigDecimal("100");
                this.basePrice = b1.multiply(b2).intValue();//普通单价
            }
        }
    }

    public Double getShopAmount() {
        return shopAmount;
    }

    public void setShopAmount(Double shopAmount) {
        this.shopAmount = shopAmount;

        if (shopAmount == null) {
            this.shopPrice = null;
        } else {
            if (shopAmount == 0) {
                this.shopPrice = 0;
            } else {
                BigDecimal b1 = new BigDecimal(String.valueOf(shopAmount));
                BigDecimal b2 = new BigDecimal("100");
                this.shopPrice = b1.multiply(b2).intValue();//普通单价
            }
        }
    }

    public Double getTuanAmount() {
        return tuanAmount;
    }

    public void setTuanAmount(Double tuanAmount) {
        this.tuanAmount = tuanAmount;

        if (tuanAmount == null) {
            this.tuanPrice = null;
        } else {
            if (tuanAmount == 0) {
                this.tuanPrice = 0;
            } else {
                BigDecimal b1 = new BigDecimal(String.valueOf(tuanAmount));
                BigDecimal b2 = new BigDecimal("100");
                this.tuanPrice = b1.multiply(b2).intValue();//拼团单价
            }
        }
    }

    public Double getMemberAmount() {
        return memberAmount;
    }

    public void setMemberAmount(Double memberAmount) {
        this.memberAmount = memberAmount;

        if (memberAmount == null) {
            this.memberPrice = null;
        } else {
            if (memberAmount == 0) {
                this.memberPrice = 0;
            } else {
                BigDecimal b1 = new BigDecimal(String.valueOf(memberAmount));
                BigDecimal b2 = new BigDecimal("100");
                this.memberPrice = b1.multiply(b2).intValue();//会员单价
            }
        }
    }
}
