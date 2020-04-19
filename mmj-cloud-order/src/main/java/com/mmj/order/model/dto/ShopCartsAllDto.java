package com.mmj.order.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ShopCartsAllDto {


    /**
     * 商品id
     */
    private Integer goodsId;

    /**
     * 商品规格id
     */
    private String goodsSkuId;

    /**
     * 商品标题
     */
    private String goodsTitle;

    /**
     * 商品图片
     */
    private String goodsImage;

    /**
     * 商品规格数据
     */
    private String goodsSkuData;

    /**
     * 购买数量
     */
    private Integer goodsNum;


    /**
     * 单价
     */
    private String unitPrice;

    /**
     * 原价
     */
    private String originalPrice;

    /**
     * 库存数
     */
    private Integer stockNum = 0;


    /**
     * 是否选中 0否 1是
     */
    private Boolean selected;



}
