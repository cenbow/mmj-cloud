package com.mmj.common.model;

public interface MessageConstants {
    interface NoticeType {
        int NODE = 0;   //节点消息
        int DEFINE = 1; //自定义消息
    }

    interface SendType {
        int NOW = 0;    //即时
        int DELAY = 1;  //延后
        int FORWARD = 2;//提前
        int DEFINE = 3; //具体定时
    }

    interface NoticeStatus {
        int OFF = 0;    //停用
        int ON = 1;     //启用
    }

    interface smsSendStatus {
        String WAIT = "001";    //待发送 001;
        String SENDING = "002"; //发送中 002;
        String SUCCESS = "003"; //发送成功 003;
        String FAIL = "004";    //发送失败 004;
        String LATE = "005";    //过期
        String CANCEL = "006";    //取消
    }

    interface module {
        String FLASH = "001";              //限时秒杀
        String TEN_THREE = "002";          //10元三件
        String GROUP_NEWCOMERS = "003";    //接力购
        String BARGAIN = "004";            //砍价
        String LOTTERY = "005";            //一分钱抽奖
        String TEN_YUAN_SHOP = "006";      //店铺订单
        String LOGISTICS = "007";          //物流
        String COUPON = "008";             //优惠券
        String GROUP = "009";             //团订单(不包含接力购和抽奖)
    }

    interface type {
        String FLASH_ONE = "001001";              //限时秒杀-支付成功后
        String FLASH_TWO = "001002";              //限时秒杀-取消订单
        String TEN_THREE_ONE = "002001";          //10元三件-支付成功后
        String TEN_THREE_TWO = "002002";          //10元三件-支付成功后
        String GROUP_NEWCOMERS_ONE = "003001";    //接力购-支付成功后
        String GROUP_NEWCOMERS_TWO = "003002";    //接力购-未成团退款后
        String BARGAIN_ONE = "004001";            //砍价-支付成功后
        String LOTTERY_ONE = "005001";            //一分钱抽奖-支付成功后
        String LOTTERY_TWO = "005002";            //一分钱抽奖-开奖结果通知
        String LOTTERY_THREE = "005003";            //一分钱抽奖-拼团成功后
        String TEN_YUAN_SHOP_ONE = "006001";      //店铺订单-下单结算未支付
        String TEN_YUAN_SHOP_TWO = "006002";      //店铺订单-支付成功后
        String LOGISTICS_ONE = "007001";          //物流-物流派件
        String LOGISTICS_TWO = "007002";          //物流-确认收货
        String COUPON_ONE = "008001";             //优惠券-到期提醒
        String GROUP_ONE = "009001";             //团订单(不包含接力购和抽奖)-下单未结算
    }

    interface msgType {
        int NODE = 0;    //节点消息，都是延迟消息
        int DEFINE = 1;  //自定义消息
    }

}
