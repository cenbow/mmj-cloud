package com.mmj.user.shopCart.model.vo;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Description: 购物车下单校验
 * @Auther: zhangyicao
 * @Date: 2019-06-03
 */
public class ShopCartsValidVo {
    private List<Goods> goods;

    public List<Goods> getGoods() {
        return goods;
    }

    public void setGoods(List<Goods> goods) {
        this.goods = goods;
    }

    public static class Goods {
        @NotNull
        private String goodId;
        @NotNull
        private String goodSkuId;
        @NotNull
        private Integer goodNum;

        public String getGoodId() {
            return goodId;
        }

        public void setGoodId(String goodId) {
            this.goodId = goodId;
        }

        public String getGoodSkuId() {
            return goodSkuId;
        }

        public void setGoodSkuId(String goodSkuId) {
            this.goodSkuId = goodSkuId;
        }

        public Integer getGoodNum() {
            return goodNum;
        }

        public void setGoodNum(Integer goodNum) {
            this.goodNum = goodNum;
        }
    }
}
