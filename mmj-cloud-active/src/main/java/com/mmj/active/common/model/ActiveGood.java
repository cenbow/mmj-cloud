package com.mmj.active.common.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.mmj.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 活动商品关联表
 * </p>
 *
 * @author KK
 * @since 2019-06-15
 */
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_active_good")
@ApiModel(value="ActiveGood对象", description="活动商品关联表")
public class ActiveGood extends BaseModel {

    @ApiModelProperty(value = "关联ID")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId(value = "MAPPERY_ID", type = IdType.AUTO)
    private Long mapperyId;

    @ApiModelProperty(value = "活动类型  1 抽奖 2 接力购 3 接力购抽奖 4十元三件 5 秒杀  6 优惠券 7 砍价 8 主题 9 猜你喜欢 10 免邮热卖 11 分类商品  12 拼团  13 免费送  14 转盘商品 15 虚拟商品 16 店铺商品 17 买买金兑换商品")
    @TableField("ACTIVE_TYPE")
    private Integer activeType;

    @ApiModelProperty(value = "活动ID")
    @TableField("BUSINESS_ID")
    private Integer businessId;

    @ApiModelProperty(value = "商品ID")
    @TableField("GOOD_ID")
    private Integer goodId;

    @ApiModelProperty(value = "卖点")
    @TableField("SELLING_POINT")
    private String sellingPoint;

    @ApiModelProperty(value = "市场价")
    @TableField("BASE_PRICE")
    private Integer basePrice;

    @ApiModelProperty(value = "活动价格")
    @TableField("ACTIVE_PRICE")
    private Integer activePrice;

    @ApiModelProperty(value = "会员价")
    @TableField("MEMBER_PRICE")
    private Integer memberPrice;

    @ApiModelProperty(value = "销售ID")
    @TableField("SALE_ID")
    private Integer saleId;

    @ApiModelProperty(value = "商品名称")
    @TableField("GOOD_NAME")
    private String goodName;

    @ApiModelProperty(value = "简称")
    @TableField("SHORT_NAME")
    private String shortName;

    @ApiModelProperty(value = "商品分类")
    @TableField("GOOD_CLASS")
    private String goodClass;

    @ApiModelProperty(value = "规格名称")
    @TableField("MODEL_NAME")
    private String modelName;

    @ApiModelProperty(value = "规格ID")
    @TableField("MODEL_ID")
    private Integer modelId;

    @ApiModelProperty(value = "规格值")
    @TableField("MODEL_VALUE")
    private String modelValue;

    @ApiModelProperty(value = "商品图片")
    @TableField("GOOD_IMAGE")
    private String goodImage;

    @ApiModelProperty(value = "商品排序")
    @TableField("GOOD_ORDER")
    private Integer goodOrder;

    @ApiModelProperty(value = "商品SKU")
    @TableField("GOOD_SKU")
    private String goodSku;

    @ApiModelProperty(value = "活动库存")
    @TableField("ACTIVE_STORE")
    private Integer activeStore;

    @ApiModelProperty(value = "活动虚拟库存存")
    @TableField("ACTIVE_VIRTUAL")
    private Integer activeVirtual;

    @ApiModelProperty(value = "商品SPU")
    @TableField("GOOD_SPU")
    private String goodSpu;

    @ApiModelProperty(value = "是否限购 0不限购 1限购")
    @TableField("GOOD_LIMIT")
    private Integer goodLimit;

    @ApiModelProperty(value = "限购模式 1每单限购 如果是接力购 接力购模式：0:正常模式，1:老带新，2:所有人")
    @TableField("LIMIT_TYPE")
    private Integer limitType;

    @ApiModelProperty(value = "限购数量")
    @TableField("LIMIT_NUM")
    private Integer limitNum;

    @ApiModelProperty(value = "团人数")
    @TableField("GROUP_PERSON")
    private Integer groupPerson;

    @ApiModelProperty(value = "是否会员商品")
    @TableField("MEMBER_FLAG")
    private Integer memberFlag;

    @ApiModelProperty(value = "是否虚拟商品")
    @TableField("VIRTUAL_FLAG")
    private Integer virtualFlag;

    @ApiModelProperty(value = "是否组合商品")
    @TableField("COMBINA_FLAG")
    private Integer combinaFlag;

    @ApiModelProperty(value = "商品状态 -1：删除 0：暂不发布 1：立即上架 2：自动上架 3：上架失败")
    @TableField("GOOD_STATUS")
    private String goodStatus;

    @ApiModelProperty(value = "买买金数量")
    private Integer kingNum;

    @ApiModelProperty(value = "备用字段1 [活动子类型: 1 置顶商品  2 例外商品] [秒杀 - 虚拟库存调节值] [虚拟商品类型：1 优惠券 2 买卖金 3 话费]")
    @TableField("ARG_1")
    private String arg1;

    @ApiModelProperty(value = "备用字段2 [虚拟商品值(优惠券/买买金/花费)]")
    @TableField("ARG_2")
    private String arg2;

    @ApiModelProperty(value = "备用字段3")
    @TableField("ARG_3")
    private String arg3;

    @ApiModelProperty(value = "备用字段4")
    @TableField(value = "ARG_4", strategy = FieldStrategy.IGNORED)
    private String arg4;

    @ApiModelProperty(value = "备用字段5")
    @TableField("ARG_5")
    private String arg5;

    @ApiModelProperty(value = "市场价")
    @TableField(exist = false)
    private Double baseAmount;

    @ApiModelProperty(value = "活动价")
    @TableField(exist = false)
    private Double activeAmount;

    @ApiModelProperty(value = "会员价")
    @TableField(exist = false)
    private Double memberAmount;

    @ApiModelProperty(value = "销量")
    @TableField(exist = false)
    private Integer saleNum;

    public Long getMapperyId() {
        return mapperyId;
    }

    public void setMapperyId(Long mapperyId) {
        this.mapperyId = mapperyId;
    }

    public Integer getActiveType() {
        return activeType;
    }

    public void setActiveType(Integer activeType) {
        this.activeType = activeType;
    }

    public Integer getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Integer businessId) {
        this.businessId = businessId;
    }

    public Integer getGoodId() {
        return goodId;
    }

    public void setGoodId(Integer goodId) {
        this.goodId = goodId;
    }

    public String getSellingPoint() {
        return sellingPoint;
    }

    public void setSellingPoint(String sellingPoint) {
        this.sellingPoint = sellingPoint;
    }

    public Integer getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(Integer basePrice) {
        this.basePrice = basePrice;
        this.baseAmount = basePrice == null ? null : Double.valueOf(basePrice) / 100.00;
    }

    public Integer getActivePrice() {
        return activePrice;
    }

    public void setActivePrice(Integer activePrice) {
        this.activePrice = activePrice;
        this.activeAmount = activePrice == null ? null : Double.valueOf(activePrice) / 100.00;
    }

    public Integer getMemberPrice() {
        return memberPrice;
    }

    public void setMemberPrice(Integer memberPrice) {
        this.memberPrice = memberPrice;
        this.memberAmount = memberPrice == null ? null : Double.valueOf(memberPrice) / 100.00;
    }

    public Integer getKingNum() {
        return kingNum;
    }

    public void setKingNum(Integer kingNum) {
        this.kingNum = kingNum;
    }

    public Integer getSaleId() {
        return saleId;
    }

    public void setSaleId(Integer saleId) {
        this.saleId = saleId;
    }

    public String getGoodName() {
        return goodName;
    }

    public void setGoodName(String goodName) {
        this.goodName = goodName;
    }

    public String getGoodClass() {
        return goodClass;
    }

    public void setGoodClass(String goodClass) {
        this.goodClass = goodClass;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public Integer getModelId() {
        return modelId;
    }

    public void setModelId(Integer modelId) {
        this.modelId = modelId;
    }

    public String getModelValue() {
        return modelValue;
    }

    public void setModelValue(String modelValue) {
        this.modelValue = modelValue;
    }

    public String getGoodImage() {
        return goodImage;
    }

    public void setGoodImage(String goodImage) {
        this.goodImage = goodImage;
    }

    public Integer getGoodOrder() {
        return goodOrder;
    }

    public void setGoodOrder(Integer goodOrder) {
        this.goodOrder = goodOrder;
    }

    public String getGoodSku() {
        return goodSku;
    }

    public void setGoodSku(String goodSku) {
        this.goodSku = goodSku;
    }

    public Integer getActiveStore() {
        return activeStore;
    }

    public void setActiveStore(Integer activeStore) {
        this.activeStore = activeStore;
    }

    public Integer getActiveVirtual() {
        return activeVirtual;
    }

    public void setActiveVirtual(Integer activeVirtual) {
        this.activeVirtual = activeVirtual;
    }

    public String getGoodSpu() {
        return goodSpu;
    }

    public void setGoodSpu(String goodSpu) {
        this.goodSpu = goodSpu;
    }

    public Integer getGoodLimit() {
        return goodLimit;
    }

    public void setGoodLimit(Integer goodLimit) {
        this.goodLimit = goodLimit;
    }

    public Integer getLimitType() {
        return limitType;
    }

    public void setLimitType(Integer limitType) {
        this.limitType = limitType;
    }

    public Integer getLimitNum() {
        return limitNum;
    }

    public void setLimitNum(Integer limitNum) {
        this.limitNum = limitNum;
    }

    public Integer getGroupPerson() {
        return groupPerson;
    }

    public void setGroupPerson(Integer groupPerson) {
        this.groupPerson = groupPerson;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Integer getMemberFlag() {
        return memberFlag;
    }

    public void setMemberFlag(Integer memberFlag) {
        this.memberFlag = memberFlag;
    }

    public Integer getVirtualFlag() {
        return virtualFlag;
    }

    public void setVirtualFlag(Integer virtualFlag) {
        this.virtualFlag = virtualFlag;
    }

    public Integer getCombinaFlag() {
        return combinaFlag;
    }

    public void setCombinaFlag(Integer combinaFlag) {
        this.combinaFlag = combinaFlag;
    }

    public String getGoodStatus() {
        return goodStatus;
    }

    public void setGoodStatus(String goodStatus) {
        this.goodStatus = goodStatus;
    }

    public String getArg1() {
        return arg1;
    }

    public void setArg1(String arg1) {
        this.arg1 = arg1;
    }

    public String getArg2() {
        return arg2;
    }

    public void setArg2(String arg2) {
        this.arg2 = arg2;
    }

    public String getArg3() {
        return arg3;
    }

    public void setArg3(String arg3) {
        this.arg3 = arg3;
    }

    public String getArg4() {
        return arg4;
    }

    public void setArg4(String arg4) {
        this.arg4 = arg4;
    }

    public String getArg5() {
        return arg5;
    }

    public void setArg5(String arg5) {
        this.arg5 = arg5;
    }

    public Double getBaseAmount() {
        return baseAmount;
    }

    public void setBaseAmount(Double baseAmount) {
        this.baseAmount = baseAmount;
        this.basePrice = baseAmount == null ? null : (int)(baseAmount * 100);
    }

    public Double getActiveAmount() {
        return activeAmount;
    }

    public void setActiveAmount(Double activeAmount) {
        this.activeAmount = activeAmount;
        this.activePrice = activeAmount == null ? null : (int)(activeAmount * 100);
    }

    public Double getMemberAmount() {
        return memberAmount;
    }

    public void setMemberAmount(Double memberAmount) {
        this.memberAmount = memberAmount;
        this.memberPrice = memberAmount == null ? null : (int)(memberAmount * 100);
    }

    public Integer getSaleNum() {
        return saleNum;
    }

    public void setSaleNum(Integer saleNum) {
        this.saleNum = saleNum;
    }
}
