package com.mmj.order.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mmj.common.constants.MQCommonTopic;
import com.mmj.common.constants.MQTopicConstant;
import com.mmj.common.constants.MQTopicConstantDelay;
import com.mmj.common.constants.OrderStatus;
import com.mmj.common.model.UserMerge;
import com.mmj.common.model.order.OrderProduceDto;
import com.mmj.common.model.wx.RefundSuccess;
import com.mmj.order.async.service.OrderAsyncService;
import com.mmj.order.constant.OrderGroupStatus;
import com.mmj.order.model.OrderGood;
import com.mmj.order.model.OrderGroup;
import com.mmj.order.model.OrderInfo;
import com.mmj.order.model.vo.OrderLogisticsVo;
import com.mmj.order.model.vo.UserOrderVo;
import com.mmj.order.service.*;
import com.mmj.order.utils.pay.PayModel;
import com.mmj.order.utils.pay.WxpayOrder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class MQConsumer {

    long H = 1000 * 60 * 60;
    long M = 1000 * 60;
    long S = 1000;

    // 拆单Topic  mmj-order-packageParse
    private static final String topicPayPackageParse = "mmj-order-pay-packageParse";

    //   活动订单--拆单Topic
    final static String topicActiveOrderPackageParse = "mmj-order-active-packageParse";

    //  订单物流
    private static final String ORDER_LOGISTICS_TOPIC = "mmj-order-logistics-topic";

    //  取消订单
    private static final String ORDER_CANCEL_TOPIC = "mmj-order-cancel-topic";

    // 会员省钱
    final static String topicPreferential = "mmj-user-preferential";

    @Autowired
    private OrderPackageService orderPackageService;

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private OrderLogisticsService orderLogisticsService;

    @Autowired
    private OrderKingService orderKingService;

    @Autowired
    private OrderGroupService orderGroupService;

    @Autowired
    private OrderGoodService orderGoodService;

    @Autowired
    private MessageUtils messageUtils;

    @KafkaListener(topics = {MQTopicConstant.WX_DELAY_TASK_ACCEPT})
    public void delayTask(List<String> paramsList) {
        log.info("延时任务接收:{}", paramsList);
        for (String params : paramsList) {
            if (null == params)
                continue;

            DelayTaskDto delayTaskDto = JSONObject.parseObject(params, DelayTaskDto.class);
            if (MQTopicConstantDelay.ORDER_TIMEOUT.equals(delayTaskDto.getBusinessType())) {
                orderInfoService.timeoutCancel(delayTaskDto.getBusinessData());

            } else if (MQTopicConstantDelay.ORDER_AUTO_RECEIPT.equals(delayTaskDto.getBusinessType())) {
                orderInfoService.autoReceipt(delayTaskDto.getBusinessData());

            } else if (MQTopicConstantDelay.WAITE_PAY_MSG.equals(delayTaskDto.getBusinessType())) {
                OrderInfo orderInfo = orderInfoService.getByOrderNo(delayTaskDto.getBusinessId());
                if (null == orderInfo)
                    return;
                if (OrderStatus.PENDING_PAYMENT.getStatus() != orderInfo.getOrderStatus())
                    return;
                messageUtils.sendWaitePayMsg(orderInfo.getCreaterId(), orderInfo.getOrderNo(),
                        delayTaskDto.getBusinessData(), orderInfo.getOrderAmount());

            } else if (MQTopicConstantDelay.WAITE_GROUP_MSG.equals(delayTaskDto.getBusinessType())) {
                OrderInfo orderInfo = orderInfoService.getByOrderNo(delayTaskDto.getBusinessId());
                log.info("待成团订单:{}", orderInfo);
                if (null == orderInfo)
                    return;

                OrderGroup group = orderGroupService.getByGroupNo(delayTaskDto.getBusinessData());
                log.info("待成团团信息:{}", group);
                if (null == group)
                    return;


                if (OrderGroupStatus.JOINING.getStatus() != group.getGroupStatus())
                    return;

                Date endTime = group.getExpireDate();
                if (endTime.getTime() < new Date().getTime())
                    return; //订单已过期

                List<OrderGood> list = orderGoodService.selectByOrderNo(orderInfo.getOrderNo());
                log.info("待成团商品信息:{}", JSON.toJSONString(list));
                if (null == list || list.size() == 0)
                    return;
                OrderGood good = list.get(0);

                long sub = endTime.getTime() - new Date().getTime();
                long hours = sub / H;
                long minutes = sub % H / M;
                long seconds = sub % H % M / S;
                String result = String.format("%d小时%d分%d秒", hours, minutes, seconds);

                String page = "/pages/empty/main?userid=" + orderInfo.getCreaterId() + "&groupNo=" +
                        group.getGroupNo() + "&orderNo=" + orderInfo.getOrderNo() + "&type=4";
                messageUtils.sendGroupMsg(orderInfo.getCreaterId(), orderInfo.getOrderNo(),
                        good.getGoodName(), PriceConversion.intToString(good.getGoodAmount()),
                        PriceConversion.intToString(good.getGoodPrice()),
                        (group.getGroupPeople() - group.getCurrentPeople()),
                        result, "邀请朋友成团才有可能中奖哦，看看谁人品好中大奖！", page);
            }
        }
    }

    /**
     * 支付成功后拼团逻辑处理
     *
     * @param params
     */
    @KafkaListener(topics = {MQTopicConstant.ORDER_GROUP_TOPIC})
    public void payBackGroupListen(String params) {
        log.info("支付后拼团逻辑处理:{}", params);
        OrderInfo info = JSONObject.parseObject(params, OrderInfo.class);
        orderGroupService.payOrder(info);
    }

    /**
     * 拆单
     *
     * @param records
     */
    @KafkaListener(topics = {MQTopicConstant.ORDER_PACKAGE_TOPIC})
    public void packageParse(List<ConsumerRecord<?, ?>> records) {
        log.info("拆单 listen records size {}", records.size());
        for (ConsumerRecord<?, ?> record : records) {
            Optional<?> kafkaMessage = Optional.ofNullable(record.value());
            log.info("Received: " + record);
            if (kafkaMessage.isPresent()) {
                Object message = record.value();
                String topic = record.topic();
                log.info("开始进入拆单部分 topic={},message={}", topic, message);
                OrderInfo orderInfo = JSON.parseObject(message.toString(), OrderInfo.class);
                orderPackageService.unpick(orderInfo);
                log.info("拆单结束");
            }
        }
    }

    /**
     * 发货
     *
     * @param records
     */
    @KafkaListener(topics = {MQTopicConstant.ORDER_PACKAGE_TO_BE_DELIVERED_TOPIC})
    public void packageToBeDelivered(List<ConsumerRecord<?, ?>> records) {
        log.info("发货 listen records size {}", records.size());
        for (ConsumerRecord<?, ?> record : records) {
            Optional<?> kafkaMessage = Optional.ofNullable(record.value());
            log.info("Received: " + record);
            if (kafkaMessage.isPresent()) {
                Object message = record.value();
                String topic = record.topic();
                log.info("开始进入发货流程 topic={},message={}", topic, message);
                List<String> packageNos = JSON.parseArray(message.toString(), String.class);
                orderPackageService.packageToBeDelivered(packageNos);
                log.info("发货结束");
            }
        }
    }

    /**
     * 上传ERP状态同步
     *
     * @param records
     */
    @KafkaListener(topics = {MQTopicConstant.ORDER_UPLOAD_STATUS_TO_BE_DELIVERED_TOPIC})
    public void listenToBeDeliveredStatus(List<ConsumerRecord<?, ?>> records) {
        for (ConsumerRecord<?, ?> record : records) {
            Optional<?> kafkaMessage = Optional.ofNullable(record.value());
            log.info("Received: " + record);
            if (kafkaMessage.isPresent()) {
                Object message = record.value();
                String topic = record.topic();
                log.info("=> 上传ERP状态同步 -> topic:{},message:{}", topic, message);
                List<String> uploadPackageNoList = JSONObject.parseArray(message.toString(), String.class);
                if (Objects.nonNull(uploadPackageNoList) && uploadPackageNoList.size() > 0) {
                    orderPackageService.updateUploadErpStatus(uploadPackageNoList);
                }
            }
        }
    }


    /**
     * 快递单号填写
     *
     * @param records
     */
    @KafkaListener(topics = {ORDER_LOGISTICS_TOPIC})
    public void orderLogistics(List<ConsumerRecord<?, ?>> records) {
        log.info("进入快递单号方法中....");
        for (ConsumerRecord<?, ?> record : records) {
            Optional<?> kafkaMessage = Optional.ofNullable(record.value());
            log.info("接受消息为为:" + record);
            if (kafkaMessage.isPresent()) {
                Object message = record.value();
                String topic = record.topic();
                if (message == null) {
                    log.info("订单快递单号填写,参数为空，{}", message);
                    continue;
                }
                OrderLogisticsVo orderLogisticsVo = JSON.parseObject(message.toString(), OrderLogisticsVo.class);
                log.info("开始进入快递单号保存方法中,快递信息为:{}", orderLogisticsVo);
                orderLogisticsService.updateLogistics(orderLogisticsVo);
                // todo  生产环境需放开
//                groceryLlistUtils.updateOrder(orderLogisticsVo.getOrderNo(), 4);//同步购物单状态已发货
                log.info("Received topic={} message={}", topic, message);
            }
        }

    }


    @KafkaListener(topics = {MQTopicConstant.WX_ORDER_TOPIC})
    public void payBackListener(List<ConsumerRecord<?, ?>> records) {
        log.info("进入支付回调处理方法");
        for (ConsumerRecord<?, ?> record : records) {
            Optional<?> kafkaMessage = Optional.ofNullable(record.value());
            if (!kafkaMessage.isPresent()) {
                continue;
            }
            Object message = record.value();
            if (null == message) {
                log.error("支付回调报错，回调参数是空, {}" + message);
                continue;
            }
            log.info("开始处理支付回调，{}", message);
            WxpayOrder wxpayOrder = JSONObject.parseObject(message.toString(), WxpayOrder.class);
            PayModel payModel = new PayModel();
            if (wxpayOrder != null) {
                payModel.setOrderno(wxpayOrder.getOutTradeNo());
                payModel.setAmount(wxpayOrder.getTotalFee());
                payModel.setOuter_pay_id(wxpayOrder.getTransactionId());
//                payModel.setPay_date(wxpayOrder.getCreaterTime());
                payModel.setPay_date(new Date());
                payModel.setUserId(wxpayOrder.getCreaterId());
                payModel.setAppId(wxpayOrder.getAppId());
                payModel.setOpenId(wxpayOrder.getOpenId());

            }
            if (StringUtils.isBlank(payModel.outer_pay_id)) {
                log.error("参数有误，{}", payModel);
                continue;   //此处应该记录下信息，重试。不能抛异常，避免队列堵塞
            }
            payBackHandler(payModel);
        }
    }

    private void payBackHandler(PayModel payModel) {
        // TODO: 2019/6/12  处理第二单返现逻辑
        orderInfoService.addOrderPayInfo(payModel.orderno, payModel.amount, payModel.outer_pay_id,
                payModel.pay_date, payModel.getUserId(), payModel.getAppId(), payModel.getOpenId());
        //处理模板消息

    }

    /**
     * 取消订单
     *
     * @param records
     */
    @KafkaListener(topics = {ORDER_CANCEL_TOPIC})
    public void sendCancelOrder(List<ConsumerRecord<?, ?>> records) throws Exception {
        log.info("进入快递单号方法中....");
        for (ConsumerRecord<?, ?> record : records) {
            Optional<?> kafkaMessage = Optional.ofNullable(record.value());
            log.info("接受消息为为:" + record);
            if (kafkaMessage.isPresent()) {
                Object message = record.value();
                String topic = record.topic();
                if (message == null) {
                    log.info("对接聚水潭取消订单,参数为空，{}", message);
                    continue;
                }
                Map<String, String> map = JSONObject.parseObject(message.toString(), Map.class);
                orderInfoService.jstCancelOrder(map);

            }
        }

    }


    @KafkaListener(topics = {MQCommonTopic.UPDATE_ORDER_TOPIC})
    public void batchUpdateOrderStatus(List<ConsumerRecord<?, ?>> records) {
        log.info("进入抽奖订单修改状态方法方法中....");
        for (ConsumerRecord<?, ?> record : records) {
            Optional<?> kafkaMessage = Optional.ofNullable(record.value());
            if (!kafkaMessage.isPresent())
                continue;
            Object message = record.value();
            log.info("抽奖关闭订单数据:{}", record.value());
            List<JSONObject> array = JSONArray.parseArray(message.toString(), JSONObject.class);
            for (JSONObject object : array) {
                if (!object.containsKey("orderNo")) {
                    log.info("订单号不存在,跳过:{}", object);
                    continue;
                }

                boolean bool = orderInfoService.batchUpdateStatus(object.getString("orderNo"), object.getInteger("status"));
                log.info("抽奖订单修改状态结果:{} -->orderNo:{}  -->status:{}", bool, object.getString("orderNo"),
                        object.getInteger("status"));
            }
        }
    }

    @KafkaListener(topics = {MQCommonTopic.FROZEN_TOPIC})
    public void frozenMMKing(String params) {
        log.info("进入申请售后冻结买买金方法方法方法中....,{}", params);
        if (StringUtils.isBlank(params))
            return;
        JSONObject object = JSONObject.parseObject(params);
        if (!object.containsKey("orderNo")) {
            return;
        }
        if (!object.containsKey("userId"))
            return;
        if (!object.containsKey("status"))
            return;
        boolean bool = orderKingService.updateMMKing(object.getString("orderNo"), object.getLong("userId"),
                object.getInteger("status"));
        log.info("申请售后是冻结买买金结果:{},订单号:{},用户Id:{}", bool, object.getString("orderNo"), object.getLong("userId"));
    }

    @KafkaListener(topics = MQTopicConstant.AFTER_STATUS_SYNCHRONIZATION)
    public void synDeleteAfterSale(String params) {
        log.info("售后订单状态流转同步 params:{}", params);
        if (StringUtils.isBlank(params))
            return;
        JSONObject object = JSONObject.parseObject(params);
        String orderNo = object.getString("orderNo");
        String userId = object.getString("userId");
        String afterNo = object.getString("afterNo");
        UserOrderVo userOrderVo = new UserOrderVo();
        userOrderVo.setOrderNo(orderNo);
        userOrderVo.setUserId(userId);
        userOrderVo.setHasAfterSale(false);
        orderInfoService.updateAfterSaleFlag(userOrderVo);
    }

    /**
     * 生成活动订单
     */
    @KafkaListener(topics = {MQTopicConstant.SYNC_ACTIVE_ORDER_TOPIC})
    public void produceActiveOrder(List<ConsumerRecord<?, ?>> records) {
        for (ConsumerRecord<?, ?> record : records) {
            Optional<?> kafkaMessage = Optional.ofNullable(record.value());
            if (!kafkaMessage.isPresent())
                continue;
            Object message = record.value();
            log.info("生成活动订单:{}", record.value());
            try {
                OrderProduceDto orderProduceDto = JSONObject.parseObject(message.toString(), OrderProduceDto.class);
                orderInfoService.produceActiveOrder(orderProduceDto);
            } catch (Exception e) {
                log.warn("生成活动订单错误:{}", e.toString());
                throw new IllegalArgumentException(e);
            }
        }
    }

    /**
     * 话费订单充值成功后状态同步
     */
    @KafkaListener(topics = {MQTopicConstant.SYNC_RECHARGE_ORDER_STATUS})
    public void syncRechargeOrderStatus(String orderNo) {
        orderInfoService.updateOrderStatus(OrderStatus.COMPLETED.getStatus(), orderNo);
    }


    /**
     * 微信退款后关闭订单
     */
    @KafkaListener(topics = {MQTopicConstant.WX_REFUND_SUCCESS})
    public void refundCloseOrder(List<String> params) {
        log.info("订单模块，微信退款完成，修改订单状态:{}", params.toString());
        for (String s : params) {
            RefundSuccess rs = JSONObject.parseObject(s, RefundSuccess.class);
            if (null == rs)
                return;
            boolean result = orderInfoService.updateOrderStatus(OrderStatus.CLOSED.getStatus(), rs.getOutTradeNo());
            log.info("退款修改订单状态结果:{}", result);
        }
    }


    @Autowired
    private OrderAsyncService orderAsyncService;

    @KafkaListener(topics = {MQTopicConstant.TOPIC_USER_MERGE})
    public void merge(List<String> params) {
        if (params == null || params.isEmpty())
            return;
        for (String data : params) {
            log.info("-->order模块监听到用户合并的主题消息：{}", data);
            UserMerge userMerge = JSONObject.parseObject(data, UserMerge.class);
            //合并订单用户数据
            orderAsyncService.mergeOrder(userMerge);
        }
    }
}
