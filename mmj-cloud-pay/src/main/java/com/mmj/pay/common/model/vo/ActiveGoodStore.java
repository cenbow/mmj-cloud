package com.mmj.pay.common.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class ActiveGoodStore {
    private String orderNo;

    private Integer activeType; //活动类型

    private String passingData; //参数  activeId(businessId) 活动ID

    private List<GoodSales> goodSales; //sku信息

    private Boolean orderCheck = true; //校验不扣减库存

    private Long userId;

    @Data
    public static class GoodSales {
        private Integer goodId; //商品id
        private String spu;
        private Integer saleId; //销售ID
        private String sku;
        private String unitPrice;    //单价
        private String memberPrice;     //会员价
        private Integer goodNum;    //购买数量
    }
}


