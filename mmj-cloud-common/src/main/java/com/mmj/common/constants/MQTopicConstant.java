package com.mmj.common.constants;

public interface MQTopicConstant {

    /**
     * H5微信消息
     */
    String WX_H5_MSG = "WX_H5_MSG";

    /**
     * 小程序微信消息
     */
    String WX_MIN_MSG = "WX_MIN_MSG";

    /**
     * 流量池消息
     */
    String FP1902_MSG = "FP1902_MSG";

    /**
     * 微信支付成功以后的消息
     */
    String WX_ORDER_TOPIC = "WX_ORDER";

    /**
     * 退款成功通知
     */
    String WX_REFUND_SUCCESS = "WX_REFUND_SUCCESS";

    /**
     * 发送小程序模板消息
     */
    String WX_MIN_TEMPLATE = "WX_MIN_TEMPLATE";

    /**
     * 存延迟队列任务的topic
     * 放进的数据以json字符串格式放入 参数如下:
     * {
     * "businessId":"业务id",
     * "businessData": "业务数据",
     * "businessType": "业务类型", 对应的业务类型存在了MQTopicConstantDelay里面
     * "executeTime": "执行时间"  //如果执行时间小于当前时间 那么相当于删除
     * }  到了执行时间的时候 会把这些数据原封不动的放在topic:WX_DELAY_TASK_ACCEPT里面
     * 业务要根据businessType来判断是否是属于自己模块的任务
     * businessId和businessType组合成唯一约束 就是发送了两次的话 会用后面的更新前面的
     */
    String WX_DELAY_TASK_SEND = "WX_DELAY_TASK_SEND";

    /**
     * 接受迟队列任务的topic 频率为一秒钟
     */
    String WX_DELAY_TASK_ACCEPT = "WX_DELAY_TASK_ACCEPT";

    /**
     * 支付回调后获取订单信息
     */
    String PAY_CALL_BACK_ALL_TOPIC = "PAY_CALL_BACK_ORDERGOOD";

    /**
     * 商品基本信息更新消息
     */
    String GOOD_INFO_UPDATE = "GOOD_INFO_UPDATE";

    /**
     * 商品销售信息更新消息
     */
    String GOOD_SALE_UPDATE = "GOOD_SALE_UPDATE";

    /**
     * 商品状态变更消息
     */
    String GOOD_STATUS_UPDATE = "GOOD_STATUS_UPDATE";

    /**
     * 支付后同步信息
     */
    String MMJ_SYNC_ORDER_PAY_TO_ES_TOPIC = "mmj-sync-order-pay-to-es";

    /**
     * 主订单状态变更消息
     */
    String SYNC_ORDER_STATUS_TO_ES_TOPIC = "mmj-sync-order-status-to-es";

    /**
     * 同步订单
     */
    String SYNC_ORDER_TO_ES_TOPIC = "mmj-sync-order-to-es";

    /**
     * 同步活动订单
     */
    String SYNC_ACTIVE_ORDER_TOPIC = "mmj-sync-active-order";

    /**
     * 同步下单失败订单号
     */
    String SYNC_PRODUCE_ORDER_FAIL = "mmj-sync-order-produce-fail";

    /**
     * 同步包裹订单
     */
    String SYNC_PACKAGE_TO_ES_TOPIC = "mmj-sync-package-to-es";

    /**
     * 子订单状态变更消息
     */
    String SYNC_PACKAGE_STATUS_TO_ES_TOPIC = "mmj-sync-package-status-to-es";

    /**
     * 合并用户消息
     */
    String TOPIC_USER_MERGE = "USER_MERGE";

    /**
     * 支付成功后处理团订单
     */
    String ORDER_GROUP_TOPIC = "mmj-handle-group-order";

    /**
     * 支付成功后拆单
     */
    String ORDER_PACKAGE_TOPIC = "mmj-handle-pay-packageParse";

    /**
     * 包裹发货
     */
    String ORDER_PACKAGE_TO_BE_DELIVERED_TOPIC = "mmj-handle-package-toBeDelivered";

    /**
     * 上传聚水潭发货
     */
    String ORDER_UPLOAD_JST_TOPIC = "mmj-upload-jst-toBeDelivered";

    /**
     * 上传ERP状态
     */
    String ORDER_UPLOAD_STATUS_TO_BE_DELIVERED_TOPIC = "mmj-upload-status-toBeDelivered";

    /**
     * 转盘邀请好友，好友点击分享后发送此主题消息
     */
    String TOPIC_PRIZEWHEELS_INVITE = "prizewheels_invite";

    /**
     * 分享商品，好友点击分享后发送此主题消息
     */
    String TOPIC_GOODSHARE = "good_share";

    /**
     * 售后状态变更同步
     */
    String AFTER_STATUS_SYNCHRONIZATION = "mmj-after-status-synchronization";

    /**
     * 同步用户关注信息-流量池
     */
    String SYNC_USER_FOCUS_INFO = "SYNC_USER_FOCUS_INFO";

    /**
     * 话费充值结果同步
     */
    String SYNC_RECHARGE_RESULT = "mmj-sync-recharge-result";

    /**
     * 话费订单充值成功后状态同步
     */
    String SYNC_RECHARGE_ORDER_STATUS = "mmj-sync-recharge-order-status";

    /**
     * 库存使用同步消息
     */
    String HOLD_GOOD_STOCK = "HOLD_GOOD_STOCK";
}
