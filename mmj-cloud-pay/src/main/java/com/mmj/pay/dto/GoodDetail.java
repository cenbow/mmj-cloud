package com.mmj.pay.dto;

public class GoodDetail {
	
    /**
     * 商品id
     */
    private Integer goodId;
    
    /**
     * 商品类型
     */
    private String goodType;


    /**
     * 商品SKU
     */
    private String skuId;

    /**
     * 商品单价，单位：元
     */
    private Double unitPrice;
    
    /**
     * 会员价
     */
    private Double memberprice;
    
    /**
     * 商品数量
     */
    private Integer count;

    /**
     * 优惠金额
     */
    private Double preferentialMoney = 0.0;

    /**
     * 运费
     */
    private Double freight;

    /**
     * 砍价订单id
     */
    private Integer orderId;
    
    /**
     * 买买金抵扣金额
     */
    private Double exchangeMoney;
    
    /**
     * 订单类型
     */
    private Integer orderType;

}
