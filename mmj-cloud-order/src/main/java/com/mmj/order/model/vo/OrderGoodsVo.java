package com.mmj.order.model.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * 下单时商品请求体
 * 2 * @Author: pengwenhao
 * 3 * @Date: 2019/6/4 10:43
 * 4
 */
@Data
@EqualsAndHashCode()
@Accessors(chain = true)
public class OrderGoodsVo {
    /**
     * 商品名称
     */
    @NotNull
    private String goodTitle;
    /**
     * 商品ID
     */
    @NotNull
    private String saleId;
    /**
     * 商品SKU
     */
    @NotNull
    private String goodSku;
    /**
     * 购买数量
     */
    @NotNull
    private Integer goodNum;
    /**
     * 下单价
     */
    @NotNull
    private String unitPrice;
    /**
     * 会员价
     */
    @NotNull
    private String memberPrice;

}
