package com.mmj.common.constants;

public interface MQTopicConstantDelay {

    String WXCUSTOMMSG_PUSH = "WXCUSTOMMSG_PUSH"; //微信客服消息延迟队列

    String WXCUSTOMMSG_SUBSCRIBE = "WXCUSTOMMSG_SUBSCRIBE"; //微信客服消息关注

    String ORDER_TIMEOUT = "ORDER_TIMEOUT"; //订单未支付超时

    String ORDER_AUTO_RECEIPT = "ORDER_AUTO_RECEIPT"; //订单自动确认收货

    String COUPON_TIMEOUT_1 = "COUPON_TIMEOUT_1"; //卡券过期提醒-优惠券有效期≥1天的在有效期截止日期前24h触发推送；优惠券有效期＜1天的在有效期截止日期前3h触发推送

    String COUPON_TIMEOUT_2 = "COUPON_TIMEOUT_2"; //卡券过期提醒-用户领取后2小时内未使用触发推送

    String WAITE_GROUP_MSG = "WAITE_GROUP_MSG";   //待成团模板消息

    String WAITE_PAY_MSG = "WAITE_PAY_MSG";   //待支付模板消息
}
