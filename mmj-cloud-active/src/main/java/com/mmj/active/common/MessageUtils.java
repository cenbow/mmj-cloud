package com.mmj.active.common;

import com.mmj.active.common.feigin.OrderFeignClient;
import com.mmj.common.constants.TemplateIdConstants;
import com.mmj.common.model.TemplateMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Slf4j
@Component
public class MessageUtils {

    @Autowired
    private MQProducer mqProducer;
    @Autowired
    private OrderFeignClient orderFeignClient;

    public void send(TemplateMessage message) {
        mqProducer.sendTemplateMessage(message);
    }

    //商家结果活动通知
    public void openLotteryMsg(Long userId, String activeName, String goodsName, String orderNo) {
        TemplateMessage message = new TemplateMessage();
        String page = "pkgOrder/orderDetail/main?orderNo=" + orderNo + "&ed=1";
        message.setPage(page);
        message.setUserId(userId);
        message.setTemplateId(TemplateIdConstants.ACTIVITY_RESULT_MESSAGE);
        message.setKeyword1(activeName);
        message.setKeyword2(goodsName);
        message.setKeyword3("您参与的抽奖活动已结束开奖，点击进入查看活动开奖公示，感谢你的参与和关注。");
        mqProducer.sendTemplateMessage(message);
    }

    //抽奖中奖消息
    public void winLotteryMsg(Long userId, String orderNo, String goodsName,
                              String code, String nickName) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        String openDate = String.format("%d年%d月%d日", year, month, day);

        TemplateMessage message = new TemplateMessage();
        String page = "pkgOrder/orderDetail/main?orderNo=" + orderNo + "&ed=1";
        message.setPage(page);
        message.setUserId(userId);
        message.setTemplateId(TemplateIdConstants.WINN_LOTTERY_MESSAGE);
        message.setKeyword1(goodsName);
        message.setKeyword2(openDate);
        message.setKeyword3(code);
        message.setKeyword4("我们会配送到您如下支付时选择的地址");
        message.setKeyword5(nickName);
        message.setKeyword6(orderFeignClient.getLogistics(orderNo));
        message.setKeyword7("请不要轻信任何人的电话，索要运费等，防止上当受骗");
        mqProducer.sendTemplateMessage(message);
    }

}