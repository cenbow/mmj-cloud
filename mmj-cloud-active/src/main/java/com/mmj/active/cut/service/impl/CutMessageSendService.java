package com.mmj.active.cut.service.impl;

import com.mmj.active.common.MessageUtils;
import com.mmj.common.constants.TemplateIdConstants;
import com.mmj.common.model.TemplateMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @description: 砍价消息发送
 * @auther: KK
 * @date: 2019/9/25
 */
@Service
public class CutMessageSendService {
    @Autowired
    private MessageUtils messageUtils;

    /**
     * 砍价进度通知
     *
     * @param type          0（砍价金额还剩10%时） 1（砍价刀数达到1+第一段配置的刀数时提醒）
     * @param userId        用户标识
     * @param cutNo         砍价号
     * @param cutAmount     已砍金额
     * @param surplusAmount 剩余金额
     * @param goodName      商品名称
     * @param goodAmount    商品价格
     * @param expireTime    过期时间
     */
    public void sendSchedule(int type, long userId, String cutNo, String cutAmount, String surplusAmount, String goodName, String goodAmount, long expireTime) {
        TemplateMessage templateMessage = new TemplateMessage();
        templateMessage.setUserId(userId);
        templateMessage.setTemplateId(TemplateIdConstants.CUT_SCHEDULE);
        templateMessage.setPage("pkgMarkdownFreeGet/cutProduct/main?showDialog=true&cutNo=" + cutNo);
        long interval = expireTime / 1000;
        long h = interval / 3600;
        long m = (interval - h * 3600) / 60;
        long s = (interval - h * 3600) % 60;
        StringBuilder keyword4 = new StringBuilder();
        keyword4.append(h > 10 ? h : "0" + h);
        keyword4.append("小时");
        keyword4.append(m > 10 ? m : "0" + m);
        keyword4.append("分钟");
        keyword4.append(s > 10 ? s : "0" + s);
        keyword4.append("秒");
        String keyword5 = type == 0 ? "快分享给好友帮您砍价吧，马上就可以获得商品了哦！" : "你已砍了" + cutAmount + "元，已低于绝大多数的市场价，库存有限，为避免被抢空，建议立即支付抢购；";
        templateMessage.setKeyword1(String.format("已砍%s元，还剩%s元", cutAmount, surplusAmount));
        templateMessage.setKeyword2(goodName);
        templateMessage.setKeyword3(goodAmount + "元");
        templateMessage.setKeyword4(keyword4.toString());
        templateMessage.setKeyword5(keyword5);
        messageUtils.send(templateMessage);
    }

    /**
     * 好友帮砍时通知
     *
     * @param userId        用户标识
     * @param cutNo         砍价号：当发送给帮砍用户时，不要传入
     * @param userNick      用户昵称：发送给帮砍用户时，传入砍价发起人昵称，否则拿帮砍用户昵称
     * @param cutAmount     帮砍金额
     * @param surplusAmount 剩余金额
     */
    public void sendAssistBargain(long userId, String cutNo, String userNick, String cutAmount, String surplusAmount) {
        String page = Objects.isNull(cutNo) ? "pkgMarkdownFreeGet/products/main" : "pkgMarkdownFreeGet/cutProduct/main?showDialog=true&cutNo=" + cutNo;
        String keyword2 = Objects.isNull(cutNo) ? String.format("您帮%s砍了%s元", userNick, cutAmount) : String.format("好友帮您砍了%s元", cutAmount);
        String keyword3 = Objects.isNull(cutNo) ? "您获得了首砍金额翻倍的奖励" : String.format("还差%s元即可免费获得商品", surplusAmount);
        String keyword4 = Objects.isNull(cutNo) ? "快去发起砍价免费拿吧，首砍金额翻倍奖励加成，更快获得商品哦" : "分享给从未参与的用户可以加速砍价哦！";
        TemplateMessage templateMessage = new TemplateMessage();
        templateMessage.setUserId(userId);
        templateMessage.setTemplateId(TemplateIdConstants.CUT_ASSIST_BARGAIN);
        templateMessage.setPage(page);
        templateMessage.setKeyword1(userNick);
        templateMessage.setKeyword2(keyword2);
        templateMessage.setKeyword3(keyword3);
        templateMessage.setKeyword4(keyword4);
        messageUtils.send(templateMessage);
    }

    /**
     * 砍价成功时通知
     *
     * @param userId        用户标识
     * @param cutNo         砍价号
     * @param orderNo       底价零元时，生成的订单号
     * @param goodName      商品标题
     * @param goodAmount    商品价格
     * @param baseUnitPrice 底价
     */
    public void sendCutSuccess(long userId, String cutNo, String orderNo, String goodName, String goodAmount, String baseUnitPrice) {
        String page = Objects.isNull(orderNo) ? "pkgMarkdownFreeGet/cutProduct/main?showDialog=true&cutNo=" + cutNo : "pkgOrder/orderDetail/main?orderNo=" + orderNo + "&ed=1";
        String keyword4 = Objects.isNull(orderNo) ? String.format("恭喜您，已经将商品砍到最低价，只需要支付%s元，就可以获得该商品，快去支付吧。", baseUnitPrice) : " 恭喜您，已经成功获得商品，我们将尽快为您发货，请您留意订单状态变化。";
        TemplateMessage templateMessage = new TemplateMessage();
        templateMessage.setUserId(userId);
        templateMessage.setTemplateId(TemplateIdConstants.CUT_SUCCESS);
        templateMessage.setPage(page);
        templateMessage.setKeyword1("恭喜您，砍价成功！");
        templateMessage.setKeyword2(goodName);
        templateMessage.setKeyword3(goodAmount + "元");
        templateMessage.setKeyword4(keyword4);
        messageUtils.send(templateMessage);
    }
}
