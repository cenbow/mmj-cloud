package com.mmj.order.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.mmj.common.constants.MQCommonTopic;
import com.mmj.common.constants.MQTopicConstant;
import com.mmj.common.constants.MQTopicConstantDelay;
import com.mmj.common.model.GoodStock;
import com.mmj.common.model.SmsDto;
import com.mmj.common.model.TemplateMessage;
import com.mmj.common.utils.DateUtils;
import com.mmj.common.utils.SnowflakeIdWorker;
import com.mmj.common.utils.StringUtils;
import com.mmj.order.common.model.UserActive;
import com.mmj.order.model.MessageInfo;
import com.mmj.order.model.OrderInfo;
import com.mmj.order.model.dto.OrderByPayCallBackDto;
import com.mmj.order.model.dto.RecommendDto;
import com.mmj.order.model.request.OrdersUploadRequest;
import com.mmj.order.model.vo.OrderDetailVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class MQProducer {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    SnowflakeIdWorker snowflakeIdWorker;

    // 测试
    final static String topic = "mmj-test2";

    // 拆单Topic
    private static final String topicPayPackageParse = "mmj-order-pay-packageParse";

    //   活动订单--拆单Topic
    final static String topicActiveOrderPackageParse = "mmj-order-active-packageParse";

    // 上传聚水潭
    final static String topicOrderUpload = "mmj-order-pay-success";


    // 下单时(待付款)--推荐返现
    private static final String RECOMMEND_SHARD_WAIT_PAY_TOPIC = "mmj-order-recommend-shard-wait-pay-topic";

    //确定收货--推荐返现
    private static final String RECOMMEND_SHARD_CONFIRM_GOOD_TOPIC = "mmj-order-recommend-shard-confirm-good-topic";

    // 取消订单
    private static final String RECOMMEND_SHARD_CANCEL_PAY_TOPIC = "mmj-order-recommend-shard-cancel-pay-topic";

    // 取消订单插入售后订单
    private static final String RECOMMEND_AFTER_ADD_TOPIC = "mmj-order-recommend-after-add-topic";

    //十元三件 - 确定收货
    private static final String THREE_SALE_CONFIRM_GOOD_TOPIC = "mmj-order-three-sale-confirm-good-topic";

    //十元三件 - 取消订单
    private static final String THREE_SALE_CANCEL_PAY_TOPIC = "mmj-order-three-sale-cancel-pay-topic";

    /**
     * 测试方法
     *
     * @param msg
     */
    public void send(MessageInfo msg) {
        Long key = snowflakeIdWorker.nextId();
        kafkaTemplate.send(topic, key + "", JSON.toJSONString(msg));
        log.info("MQProducer send msg {} success", JSON.toJSONString(msg));
    }
/*

    //消费买买金
    public void usedMMKing(SaveOrderDto saveOrderDto, OrderSaveVo orderSaveVo) {
        log.info("订单号:{} 是否使用买买金,{},{}", saveOrderDto.getOrderNo(), orderSaveVo.getUseKingNum(), orderSaveVo.getKingSelected());
        if (null == orderSaveVo.getUseKingNum() || !orderSaveVo.getKingSelected()) {
            log.info("不使用买买金,{},{}", orderSaveVo.getUseKingNum(), orderSaveVo.getKingSelected());
            return;
        }

        JSONObject object = new JSONObject();
        object.put("orderNo", saveOrderDto.getOrderNo());
        object.put("userId", saveOrderDto.getUserId());
        object.put("kingNum", orderSaveVo.getUseKingNum());
        Long key = snowflakeIdWorker.nextId();
        kafkaTemplate.send(MQCommonTopic.USED_MMKING_TOPIC, key + "", JSON.toJSONString(object));
        log.info("消费买买金消息入队成功,{}", object);
    }
*/

    //反还买买金
    public void returnMMKing(Long userId, String orderNO, int kingNum) {
        log.info("反还买买金 userId:{} orderNO,{},kingNum:{}", userId, orderNO, kingNum);
        if (null == userId || StringUtils.isEmpty(orderNO) || kingNum <= 0) {
            log.info("不反还用买买金,{},{},{}", userId, orderNO, kingNum);
            return;
        }
        JSONObject object = new JSONObject();
        object.put("orderNo", orderNO);
        object.put("userId", userId);
        object.put("kingNum", kingNum);
        Long key = snowflakeIdWorker.nextId();
        kafkaTemplate.send(MQCommonTopic.RETURN_MMKING_TOPIC, key + "", JSON.toJSONString(object));
        log.info("反还买买金消息入队成功,{}", object);
    }

    /**
     * 发送模板消息
     *
     * @param msg
     */
    public void sendTemplateMessage(TemplateMessage msg) {
        Long key = snowflakeIdWorker.nextId();
        kafkaTemplate.send(MQCommonTopic.SEND_TEMPLATE_MESSAGE, key + "", JSON.toJSONString(msg));
        log.info("order发送模板消息 {} success", JSON.toJSONString(msg));
    }


    public void sendSmsMsg(SmsDto msg) {
        Long key = snowflakeIdWorker.nextId();
        kafkaTemplate.send(MQCommonTopic.SMS_TOPIC, key + "", JSON.toJSONString(msg));
        log.info("发送短信消息 {} success", JSON.toJSONString(msg));
    }

    public void sendLotteryCode(List<UserActive> list) {
        for (UserActive active : list) {
            Long key = snowflakeIdWorker.nextId();
            kafkaTemplate.send(MQCommonTopic.LOTTERY_CODE_TOPIC, key + "", JSON.toJSONString(active));
        }
        log.info("新增抽奖码队列 send msg {} success", JSON.toJSONString(list));
    }

    /**
     * 生成订单时放入延时队列（判断是否待支付过期）
     *
     * @param orderNo
     * @param expireTime
     */
    public void orderTimeout(String orderNo, Date expireTime) {
        Long key = snowflakeIdWorker.nextId();
        DelayTaskDto delayTaskDto = new DelayTaskDto(orderNo, orderNo, MQTopicConstantDelay.ORDER_TIMEOUT, DateUtils.getDate(expireTime, DateUtils.DATE_PATTERN_1));
        String content = JSON.toJSONString(delayTaskDto);
        kafkaTemplate.send(MQTopicConstant.WX_DELAY_TASK_SEND, key + "", content);
        log.info("放入延时处理队列 send msg {} success", content);
    }

    /**
     * 生成订单时放入延时队列（用于发送待支付模板消息）
     *
     * @param orderNo
     */
    public void orderWaitePayMsg(String orderNo, String goodName) {
        Long key = snowflakeIdWorker.nextId();
        String delayTime = DateUtils.getDate(DateUtils.pushMinute(new Date(), 30), DateUtils.DATE_PATTERN_1);
        DelayTaskDto delayTaskDto = new DelayTaskDto(orderNo, goodName, MQTopicConstantDelay.WAITE_PAY_MSG,
                delayTime);
        String content = JSON.toJSONString(delayTaskDto);
        kafkaTemplate.send(MQTopicConstant.WX_DELAY_TASK_SEND, key + "", content);
        log.info("待支付延时处理队列 send msg {} success", content);
    }

    /**
     * 拼团订单支付完成放入延时队列（用于发送待成团模板消息）
     *
     * @param orderNo
     */
    public void orderWaiteGroupMsg(String orderNo, String groupNo) {
        Long key = snowflakeIdWorker.nextId();
        String delayTime = DateUtils.getDate(DateUtils.pushMinute(new Date(), 60), DateUtils.DATE_PATTERN_1);
        DelayTaskDto delayTaskDto = new DelayTaskDto(orderNo, groupNo, MQTopicConstantDelay.WAITE_GROUP_MSG,
                delayTime);
        String content = JSON.toJSONString(delayTaskDto);
        kafkaTemplate.send(MQTopicConstant.WX_DELAY_TASK_SEND, key + "", content);
        log.info("待成团延时处理队列 send msg {} success", content);
    }

    /**
     * 同步下单失败订单号
     *
     * @param orderNo
     */
    public void produceFail(String orderNo) {
        Long key = snowflakeIdWorker.nextId();
        String content = String.format("{\"orderNo\":\"%s\"}", orderNo);
        kafkaTemplate.send(MQTopicConstant.SYNC_PRODUCE_ORDER_FAIL, key + "", content);
        log.info("下单失败订单号同步 send msg {} success", content);
    }

    /**
     * 同步库存
     *
     * @param goodStockList
     */
    public void synOrderStock(List<GoodStock> goodStockList) {
        Long key = snowflakeIdWorker.nextId();
        String content = JSON.toJSONString(goodStockList);
        kafkaTemplate.send(MQTopicConstant.HOLD_GOOD_STOCK, key + "", content);
        log.info("订单同步库存操作 send msg {} success", content);
    }

    /**
     * 自动收货
     *
     * @param orderNo
     * @param autoReceiptTime
     */
    public void autoReceipt(String orderNo, Date autoReceiptTime) {
        Long key = snowflakeIdWorker.nextId();
        DelayTaskDto delayTaskDto = new DelayTaskDto(orderNo, orderNo, MQTopicConstantDelay.ORDER_AUTO_RECEIPT, DateUtils.getDate(autoReceiptTime, DateUtils.DATE_PATTERN_1));
        String content = JSON.toJSONString(delayTaskDto);
        kafkaTemplate.send(MQTopicConstant.WX_DELAY_TASK_SEND, key + "", content);
        log.info("放入延时处理队列 send msg {} success", content);
    }

    /**
     * 拆单消息队列
     *
     * @param orderInfo
     */
    public void sendPackageParse(OrderInfo orderInfo) {
        Long key = snowflakeIdWorker.nextId();
        String content = JSON.toJSONString(orderInfo);
        kafkaTemplate.send(MQTopicConstant.ORDER_PACKAGE_TOPIC, key + "", content);
        log.info("拆单 send msg {} success", content);
    }

    /**
     * 包裹发货
     *
     * @param packageNos
     */
    public void sendPackageToBeDelivered(List<String> packageNos) {
        Long key = snowflakeIdWorker.nextId();
        String content = JSON.toJSONString(packageNos);
        kafkaTemplate.send(MQTopicConstant.ORDER_PACKAGE_TO_BE_DELIVERED_TOPIC, key + "", content);
        log.info("发货 send msg {} success", content);
    }


    /**
     * 订单上传聚水潭
     *
     * @param ordersUploadRequest
     */
    public void sendOrderUpload(OrdersUploadRequest ordersUploadRequest) {
        List<OrdersUploadRequest> ordersUploadRequests = Lists.newArrayListWithExpectedSize(1);
        ordersUploadRequests.add(ordersUploadRequest);
        sendOrderToJstUpload(ordersUploadRequests);
    }

    /**
     * 上传聚水潭
     *
     * @param ordersUploadRequests
     */
    public void sendOrderToJstUpload(List<OrdersUploadRequest> ordersUploadRequests) {
        if (ordersUploadRequests.size() == 0) {
            return;
        }
        Long key = snowflakeIdWorker.nextId();
        String content = JSON.toJSONString(ordersUploadRequests);
        kafkaTemplate.send(MQTopicConstant.ORDER_UPLOAD_JST_TOPIC, key + "", content);
        log.info("上传聚水潭 send msg {} success", content);
    }

    /**
     * 会员省钱
     *
     * @param orderDetailVo
     */
    public void addMoneyItem(OrderDetailVo orderDetailVo) {
        Long key = snowflakeIdWorker.nextId();
        kafkaTemplate.send(MQCommonTopic.MEMBER_PREFERENTIAL, key + "", JSON.toJSONString(orderDetailVo));
        log.info("MQProducer-user send msg {} success", JSON.toJSONString(orderDetailVo));
    }


    /**
     * 推荐返现
     */
    public void recommend(RecommendDto RecommendDto) {
        Long key = snowflakeIdWorker.nextId();
        kafkaTemplate.send(RECOMMEND_SHARD_WAIT_PAY_TOPIC, key + "", JSON.toJSONString(RecommendDto));
        log.info("下单时推荐返现: seng msg {}", JSON.toJSONString(RecommendDto));
        log.info("MQProducer- send msg {} success", JSON.toJSONString(RecommendDto));
    }


    /**
     * 确定收货
     *
     * @param map
     */
    public void sendReceiveOrder(Map<String, Object> map) {
        Long key = snowflakeIdWorker.nextId();
        kafkaTemplate.send(RECOMMEND_SHARD_CONFIRM_GOOD_TOPIC, key + "", JSON.toJSONString(map));
        log.info("确认收货时发送消息: seng msg {}", JSON.toJSONString(map));
        log.info("MQProducer- send msg {} success", JSON.toJSONString(map));
    }


    /**
     * 确定收货--十元三件
     *
     * @param map
     */
    public void sendReceiveBysysj(Map<String, Object> map) {
        Long key = snowflakeIdWorker.nextId();
        kafkaTemplate.send(THREE_SALE_CONFIRM_GOOD_TOPIC, key + "", JSON.toJSONString(map));
        log.info("十元三件--确认收货时发送消息: seng msg {}", JSON.toJSONString(map));
        log.info("MQProducer- send msg {} success", JSON.toJSONString(map));
    }


    /**
     * 取消订单--推荐返现
     *
     * @param map
     */
    public void sendCancel(Map<String, Object> map) {
        Long key = snowflakeIdWorker.nextId();
        kafkaTemplate.send(RECOMMEND_SHARD_CANCEL_PAY_TOPIC, key + "", JSON.toJSONString(map));
        log.info("取消订单发送消息:{}", JSON.toJSONString(map));
    }

    /**
     * 取消订单插入售后订单
     *
     * @param map
     */
    public void addAfterSale(Map<String, Object> map) {
        Long key = snowflakeIdWorker.nextId();
        kafkaTemplate.send(RECOMMEND_AFTER_ADD_TOPIC, key + "", JSONObject.toJSONString(map));
        log.info("取消订单添加售后信息:{}", JSON.toJSONString(map));
    }


    /**
     * 取消订单--十元三件
     *
     * @param map
     */
    public void sendCancelBysysj(Map<String, Object> map) {
        Long key = snowflakeIdWorker.nextId();
        log.info("十元三件类型订单---取消订单时发送消息start:{}", JSON.toJSONString(map));
        kafkaTemplate.send(THREE_SALE_CANCEL_PAY_TOPIC, key + "", JSON.toJSONString(map));
        log.info("十元三件类型订单---取消订单时发送消息end:{}", JSON.toJSONString(map));
    }


    /**
     * 支付回调后--获取订单所有信息
     */
    public void sendAllByPayCallBack(OrderByPayCallBackDto orderByPayCallBackDto) {
        Long key = snowflakeIdWorker.nextId();
        kafkaTemplate.send(MQTopicConstant.PAY_CALL_BACK_ALL_TOPIC, key + "", JSON.toJSONString(orderByPayCallBackDto));
        log.info("支付回调后--发送当前订单:{},订单所有信息: seng msg {}", orderByPayCallBackDto.getOrderNo(), JSON.toJSONString(orderByPayCallBackDto));
        log.info("MQProducer- send msg {} success", JSON.toJSONString(orderByPayCallBackDto));
    }

    /**
     * 测试取消订单
     *
     * @param object
     * @param topic
     */
    public void testOrderCancel(Object object, String topic) {
        Long key = snowflakeIdWorker.nextId();
        kafkaTemplate.send(topic, key.toString(), JSONObject.toJSONString(object));
        log.info("MQProducer send msg key {} success", key.toString());
    }

    /**
     * 测试订单物流
     *
     * @param object
     * @param topic
     */
    public void testOrderlogistics(Object object, String topic) {
        Long key = snowflakeIdWorker.nextId();
        kafkaTemplate.send(topic, key.toString(), JSONObject.toJSONString(object));
        log.info("MQProducer send msg key {} success", key.toString());
    }


    public void addMMKing(Long userId, String type) {
        Long key = snowflakeIdWorker.nextId();
        JSONObject object = new JSONObject();
        object.put("userId", userId);
        object.put("type", type);
        kafkaTemplate.send(MQCommonTopic.MMKING_TOPIC, key.toString(), object.toJSONString());
        log.info("活动新增买买金 key {} success", key.toString());
    }

}

