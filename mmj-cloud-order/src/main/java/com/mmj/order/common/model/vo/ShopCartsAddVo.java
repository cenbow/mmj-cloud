package com.mmj.order.common.model.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Description: 新增购物车商品
 * @Auther: zhangyicao
 * @Date: 2019-06-03
 */
@Data
public class ShopCartsAddVo {
    /**
     * 商品id
     */
    @NotNull
    private Integer goodId;
    /**
     * 商品规格
     */
    @NotNull
    private String goodSku;

    /**
     * 销售id
     */
    @NotNull
    private Integer saleId;

    /**
     * 商品标题
     */
    @NotNull
    private String goodName;

    /**
     * 商品图片
     */
    @NotNull
    private String goodImages;

    /**
     * 商品规格数据
     */
    @NotNull
    private String modelName;

    /**
     * 商品类型
     */
    @NotNull
    private String goodType;

    /**
     * 购买数量
     */
    @NotNull
    private Integer goodNum;

    /**
     * 单价
     */
    @NotNull
    private String goodPrice;

    /**
     * 原价
     */
    @NotNull
    private String basePrice;

    /**
     * 会员价
     */
    @NotNull
    private String memberPrice;

    /**
     * 是否会员专属
     */
    @NotNull
    private Boolean memberFlag;

    /**
     * 是否组合商品
     */
    @NotNull
    private Boolean combinaFlag;

    /**
     * 是否虚拟商品
     */
    @NotNull
    private Boolean virtualFlag;

    @NotNull
    private Boolean selectFlag;
}
