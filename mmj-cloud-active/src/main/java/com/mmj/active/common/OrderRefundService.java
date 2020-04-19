package com.mmj.active.common;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mmj.active.common.model.OrderInfo;
import com.mmj.common.constants.MQCommonTopic;
import com.mmj.common.constants.TemplateIdConstants;
import com.mmj.common.model.TemplateMessage;
import com.mmj.common.model.order.UserLotteryDto;
import com.mmj.common.utils.DateUtils;
import com.mmj.common.utils.PriceConversion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class OrderRefundService {


    @Autowired
    private MQProducer mqProducer;

    public void refund(String orderNo) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("orderNo", orderNo);
        mqProducer.send(jsonObject, MQCommonTopic.REFUND_TOPIC);
    }

    public void batchRefund(Collection<String> orderNoList, Integer price) {
        List<JSONObject> jsonObjectList = new ArrayList<>();
        for (String orderNo : orderNoList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("orderNo", orderNo);
            jsonObject.put("price", price);
            jsonObjectList.add(jsonObject);
        }
        mqProducer.send(jsonObjectList, MQCommonTopic.REFUND_TOPIC);
    }

    public void batchSendRefundMsg(List<UserLotteryDto> list, String goodsName, Integer price) {
        for (UserLotteryDto active : list) {
            this.refundMsg(active.getUserId(), goodsName, price, active.getOrderNo());
        }
    }

    //抽奖退款消息
    private void refundMsg(Long userId, String goodsName, Integer payAmount, String orderNo) {
        TemplateMessage message = new TemplateMessage();
        String page = "pkgOrder/orderDetail/main?orderNo=" + orderNo + "&ed=1";
        message.setPage(page);
        message.setUserId(userId);
        message.setTemplateId(TemplateIdConstants.REFUND_SEND_MESSAGE);
        message.setKeyword1("【抽奖】" + goodsName);
        message.setKeyword2(DateUtils.getDate(new Date(), DateUtils.DATE_PATTERN_1));
        message.setKeyword3(PriceConversion.intToString(payAmount) + "元");
        message.setKeyword4(orderNo);
        message.setKeyword5("退款已经原路返回，具体到账时间可能会有1-3天延迟");
        message.setKeyword6("恭喜您抽中二等奖！获得奖品优惠券，您可进入小程序-我的-优惠券里查看使用");
        mqProducer.sendTemplateMessage(message);
    }

    public void batchUpdateOrderStatus(List<UserLotteryDto> list, int status) {
        List<JSONObject> jsonObjectList = new ArrayList<>();
        for (UserLotteryDto active : list) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("orderNo", active.getOrderNo());
            jsonObject.put("userId", active.getUserId());
            jsonObject.put("status", status);
            jsonObjectList.add(jsonObject);
        }
        mqProducer.send(jsonObjectList, MQCommonTopic.UPDATE_ORDER_TOPIC);
        log.info("活动开奖异步修改订单状态:{}", JSON.toJSONString(jsonObjectList));
    }

    public void batchCloseOrder(List<OrderInfo> list, int status) {
        List<JSONObject> jsonObjectList = new ArrayList<>();
        for (OrderInfo order : list) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("orderNo", order.getOrderNo());
            jsonObject.put("userId", order.getCreaterId());
            jsonObject.put("status", status);
            jsonObjectList.add(jsonObject);
        }
        mqProducer.send(jsonObjectList, MQCommonTopic.UPDATE_ORDER_TOPIC);
        log.info("活动开奖异步关闭订单:{}", JSON.toJSONString(jsonObjectList));
    }
}
