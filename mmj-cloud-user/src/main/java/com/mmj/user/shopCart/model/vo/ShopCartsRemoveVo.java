package com.mmj.user.shopCart.model.vo;

import javax.validation.constraints.Size;

/**
 * @Description: 删除购物车信息
 * @Auther: zhangyicao
 * @Date: 2019-06-03
 */
public class ShopCartsRemoveVo {
    @Size(min = 1)
    private String goodSkuId;

    private String goodType;

    private String type;

    public String getGoodSkuId() {
        return goodSkuId;
    }

    public void setGoodSkuId(String goodSkuId) {
        this.goodSkuId = goodSkuId;
    }

    public String getGoodType() {
        return goodType;
    }

    public void setGoodType(String goodType) {
        this.goodType = goodType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
