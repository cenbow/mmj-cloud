package com.mmj.order.model.dto;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode()
@Accessors(chain = true)
public class ShopCartsDto {

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


    public ShopCartsDto(Integer goodsId, String goodsSkuId, String goodsTitle, String goodsImage, String goodsSkuData, Integer goodsNum, String unitPrice, String originalPrice, Integer stockNum, Boolean selected) {
        
    }
}
