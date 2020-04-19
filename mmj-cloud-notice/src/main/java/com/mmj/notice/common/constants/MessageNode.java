package com.mmj.notice.common.constants;

import org.springframework.util.Assert;

public enum MessageNode {

    FLASH_PAY("001001","限时秒杀-支付成功后"),
    FLASH_CANCELED("001002","限时秒杀-取消订单"),
    TEN_THREE_PAY("002001","10元三件-支付成功后"),
    NEWCOMERS_PAY("003001","接力购-支付成功后"),
    NEWCOMERS_REFUNDED("003002","接力购-未成团退款后"),
    BARGAIN_PAY("004001","砍价-支付成功后"),
    LOTTERY_PAY("005001","一分钱抽奖-支付成功后"),
    LOTTERY_DRAW("005002","一分钱抽奖-开奖结果通知"),
    LOTTERY_GROUPED("005003","一分钱抽奖-拼团成功后"),
    TEN_YUAN_SHOP_UNPAY("006001","店铺订单-下单结算未支付"),
    TEN_YUAN_SHOP_PAY("006002","店铺订单-支付成功后"),
    LOGISTICS_SEND("007001","物流-物流派件"),
    LOGISTICS_RECEIVED("007002","物流-确认收货"),
    COUPON_EXPIRE("008001","优惠券-到期提醒");

    private String code;

    private String msg;

    public static String getMsgByCode(String code){
        Assert.notNull(code,"code can not be null");
        for (MessageNode node : MessageNode.values()){
            if (node.code.equals(code)){
                return node.getMsg();
            }
        }
        return null;
    }

    MessageNode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
