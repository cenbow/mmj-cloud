package com.mmj.common.constants;

public interface MQCommonTopic {

    //发送模板消息
    String SEND_TEMPLATE_MESSAGE = "mmj-template-message";

    // 退款
    String REFUND_TOPIC = "mmj-order-refund";

    //新增抽奖码
    String LOTTERY_CODE_TOPIC = "mmj-lottery_code";

    //增加买买金
    String MMKING_TOPIC = "mmj-add-mmking-topic";

    //订单使用买买金
//    String USED_MMKING_TOPIC = "mmj-used-mmking-topic";

    //订单取消、售后反还买买金
    String RETURN_MMKING_TOPIC = "mmj-return-mmking-topic";

    //冻结买买金
    String FROZEN_TOPIC = "mmj-after-frozen";

    //短信消息
    String SMS_TOPIC = "mmj-auto-sms";

    //修改订单状态
    String UPDATE_ORDER_TOPIC = "mmj-order-update-status";

    // 会员省钱
    String MEMBER_PREFERENTIAL = "mmj-user-member-preferential";
}
