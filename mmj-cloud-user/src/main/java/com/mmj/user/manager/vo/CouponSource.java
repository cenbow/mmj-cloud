package com.mmj.user.manager.vo;

/**
 * @description: 优惠券来源
 * @auther: KK
 * @date: 2019/7/11
 */
public interface CouponSource {
    /**
     * 系统发送
     */
    String SYSTEM_SEND = "SYSTEM";

    /**
     * 专题页领取
     */
    String TOPIC_SEND = "TOPIC";

    /**
     * 刷一刷获取
     */
    String BRUSH_SEND = "BRUSH";

    /**
     * 弹出优惠券领用
     */
    String INDEX_SEND = "INDEX";

    /**
     * 二维码领取
     */
    String QR_RECIEVE = "QR";

    /**
     * 拼团抽奖活动
     */
    String LOTTERY = "LOTTERY";

    /**
     * 现金签到
     */
    String SIGN = "SIGN";

    /**
     * 转盘活动
     */
    String PRIZEWHEELS = "PRIZEWHEELS";

    /**
     * 添加小程序领取
     */
    String ADD_MINI_APPS = "ADD_MINI_APPS";

    /**
     * 用户授权
     */
    String ACCREDIT = "ACCREDIT";

    /**
     * 会员买多少送多少活动
     */
    String BUY_GIVE = "BUY_GIVE";

    /**
     * 会员日领券活动
     */
    String MEMBER_DAY = "MEMBER_DAY";

    /**
     * 商详页领券
     */
    String GOODS_DETAILS = "GOODS_DETAILS";
}
