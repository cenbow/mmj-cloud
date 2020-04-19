package com.mmj.aftersale.utils;

import com.mmj.common.constants.TemplateIdConstants;
import com.mmj.common.model.TemplateMessage;
import com.mmj.common.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @description: 模板消息发送
 * @auther: KK
 * @date: 2019/9/18
 */
@Slf4j
@Component
public class MessageUtils {
    @Autowired
    private MQProducer mqProducer;

    public void send(TemplateMessage message) {
        mqProducer.sendTemplateMessage(message);
    }

    /**
     * 售后退款通知
     *
     * @param userId
     * @param orderNo
     * @param refundAmount
     */
    public void afterSaleRefund(Long userId, String orderNo, String refundAmount) {
        TemplateMessage message = new TemplateMessage();
        message.setUserId(userId);
        message.setTemplateId(TemplateIdConstants.AFTER_SALE_REFUND);
        message.setPage("pkgOrder/orderDetail/main?orderNo=" + orderNo + "&ed=1");
        message.setKeyword1(orderNo);
        message.setKeyword2(refundAmount + "元");
        message.setKeyword3(DateUtils.getDate(new Date(), DateUtils.DATE_PATTERN_1));
        message.setKeyword4("我们已经为您操作了退款，退款将于3-5个工作日内退到您支付的账户中，请您留意查收，谢谢。");
        send(message);
    }

    /**
     * 退货审核状态通知
     *
     * @param userId
     * @param orderNo
     * @param status
     */
    public void afterSaleReturn(Long userId, String orderNo, boolean status) {
        TemplateMessage message = new TemplateMessage();
        message.setUserId(userId);
        message.setTemplateId(TemplateIdConstants.AFTER_SALE_RETURN);
        message.setPage("pkgOrder/orderDetail/main?orderNo=" + orderNo + "&ed=1");
        message.setKeyword1(status ? "通过" : "不通过");
        message.setKeyword2(orderNo);
        message.setKeyword3(DateUtils.getDate(new Date(), DateUtils.DATE_PATTERN_1));
        message.setKeyword4(status ? "退货申请通过" : "退货申请拒绝 ");
        message.setKeyword5(status ? "您的退货申请已经通过，请您尽快按照要求将商品寄回，我们收到后，将尽快质检并处理您的退货。" : "您的退货描述不符合退货要求，请您联系客服处理 ");
        send(message);
    }

    /**
     * 售后验收通知
     *
     * @param userId
     * @param orderNo
     * @param status
     */
    public void afterSaleAccept(Long userId, String orderNo, boolean status) {
        TemplateMessage message = new TemplateMessage();
        message.setUserId(userId);
        message.setTemplateId(TemplateIdConstants.AFTER_SALE_ACCEPT);
        message.setPage("pkgOrder/orderDetail/main?orderNo=" + orderNo + "&ed=1");
        message.setKeyword1(status ? "通过" : "不通过");
        message.setKeyword2(DateUtils.getDate(new Date(), DateUtils.DATE_PATTERN_1));
        message.setKeyword3(orderNo);
        message.setKeyword4(status ? "我们已收到您的商品，并且收货验收通过，将尽快为您操作退款，请您留意订单状态变化。" : "我们已收到您的商品，抱歉的通知您收货验收不通过，请您联系客服处理。");
        send(message);
    }
}
