package com.mmj.user.shopCart.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * @Description: 购物车全部
 * @Auther: zhangyicao
 * @Date: 2019/06/11
 */
public class ShopCartsAllDto extends ShopCartsDto {
	
	@JsonSerialize(using = ToStringSerializer.class)
    private Long createBy;

    public ShopCartsAllDto(Integer goodId, String goodSkuId,Integer saleId, String goodName, String goodImages, String modelName, Integer goodNum, String goodType, String goodPrice, String basePrice, Integer stockNum,String memberPrice,Boolean memberFlag,Boolean selectFlag) {
        super(goodId, goodSkuId, saleId,goodName, goodImages, modelName, goodNum, goodType,goodPrice, basePrice, stockNum,memberPrice,memberFlag, selectFlag);
        this.createBy = createBy;
    }

    public Long getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Long createBy) {
        this.createBy = createBy;
    }
}
