package com.mmj.order.utils;

import com.mmj.common.constants.TemplateIdConstants;
import com.mmj.common.model.TemplateMessage;
import com.mmj.common.utils.DateUtils;
import com.mmj.order.common.model.dto.OrderPaySuccessDto;
import com.mmj.order.constant.OrderType;
import com.mmj.order.model.LogisticsShipDto;
import com.mmj.order.model.OrderGood;
import com.mmj.order.service.OrderGoodService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class MessageUtils {

    @Autowired
    private MQProducer mqProducer;

    List<String> houses = new ArrayList<>();
    @Autowired
    private OrderGoodService orderGoodService;

    @PostConstruct
    public void initHouses() {
        houses.add("多项式科技（深圳）有限公司");
        houses.add("买买家东莞仓");
        houses.add("买买家南通海门仓");
        houses.add("买买家-季然南通仓");
    }

    /**
     * 发送模板消息
     *
     * @param message
     */
    public void send(TemplateMessage message) {
        mqProducer.sendTemplateMessage(message);
    }

    public String getSendMsg(String orderNo, boolean isWeb) {
        List<OrderGood> list = orderGoodService.selectByOrderNo(orderNo);
        if (null == list || list.size() == 0) {
            if (isWeb) {
                return "预计三天内为您操作发货";
            } else {
                return "我们预计三天内发货";
            }

        }

        boolean flag = true;
        for (OrderGood orderGood : list) {
            if (!this.houses.contains(orderGood.getWarehouseId())) {
                flag = false;
                break;
            }
        }
        if (flag) {
            if (isWeb) {
                return "预计24小时内为您操作发货";
            } else {
                return "我们预计24小时内发货";
            }
        } else {
            if (isWeb) {
                return "预计三天内为您操作发货";
            } else {
                return "我们预计三天内发货";
            }
        }
    }

    public void payGroup(int orderType, OrderPaySuccessDto dto) {
        if (orderType == 0)
            return;
        if (OrderType.LOTTERY == orderType) {
            //抽奖
            payLottery(dto);
        } else if (OrderType.RELAY_LOTTERY == orderType) {
            //接力购抽奖
        } else if (orderType == OrderType.TWO_GROUP) {
            //二人团
            payTwoGroup(dto);
        } else if (orderType == OrderType.NEWCOMERS) {
            //接力购
            payNewComers(dto, 1);
        }
    }

    private void payLottery(OrderPaySuccessDto paySuccessDto) {
        TemplateMessage message = new TemplateMessage();
        String page = null;
        if (paySuccessDto.isGroupStatus()) {
            //已成团
            page = String.format("/pages/empty/main?groupNo=%s&orderNo=%s&type=4&ot=%s",
                    paySuccessDto.getGroupNo(), paySuccessDto.getOrderNo(), paySuccessDto.getOrderNo());
        } else {
            page = "pkgProbationFree/main?ot=" + paySuccessDto.getOrderNo();
        }
        message.setPage(page);
        message.setUserId(paySuccessDto.getUserId());
        message.setTemplateId(TemplateIdConstants.PAY_SUCCESS_MESSAGE);
        message.setKeyword1("【抽奖】" + paySuccessDto.getGoodsTitle());
        message.setKeyword2(DateUtils.getDate(new Date(), DateUtils.DATE_PATTERN_6));
        message.setKeyword3(paySuccessDto.getAmount() + "元");
        message.setKeyword4(paySuccessDto.isGroupStatus() ? "已成团" : "待成团");
        message.setKeyword5(paySuccessDto.isGroupStatus() ? getSendMsg(paySuccessDto.getOrderNo(), false) :
                "搜索公众号：\"买买家\"在公众号内回复 抽奖群；可进群互拼，群内有每日专属抽奖活动；耳机、纸巾、洗衣液等等！");
        mqProducer.sendTemplateMessage(message);

    }

    private void paySpike(OrderPaySuccessDto paySuccessDto) {
        TemplateMessage message = new TemplateMessage();
        String page = String.format("/pages/empty/main?groupNo=%s&orderNo=%s&type=2&ot=%s",
                paySuccessDto.getGroupNo(), paySuccessDto.getOrderNo(), paySuccessDto.getOrderNo());
        message.setPage(page);
        message.setUserId(paySuccessDto.getUserId());
        message.setTemplateId(TemplateIdConstants.PAY_SUCCESS_MESSAGE);
        message.setKeyword1(paySuccessDto.getGoodsTitle());
        message.setKeyword2(DateUtils.getDate(new Date(), DateUtils.DATE_PATTERN_6));
        message.setKeyword3(paySuccessDto.getAmount() + "元");
        message.setKeyword4(paySuccessDto.isGroupStatus() ? "已成团" : "待成团");
        message.setKeyword5(paySuccessDto.isGroupStatus() ? getSendMsg(paySuccessDto.getOrderNo(), false)
                : "拼团成功才能发货哦,赶紧邀请好友跟你一起拼团吧");
        mqProducer.sendTemplateMessage(message);
    }


    private void payNewComers(OrderPaySuccessDto paySuccessDto, int cnt) {
        String page = String.format("/pages/empty/main?groupNo=%s&orderNo=%s&type=2&ot=%s",
                paySuccessDto.getGroupNo(), paySuccessDto.getOrderNo(), paySuccessDto.getOrderNo());
        TemplateMessage message = new TemplateMessage();
        message.setPage(page);
        message.setUserId(paySuccessDto.getUserId());
        message.setTemplateId(TemplateIdConstants.PAY_SUCCESS_MESSAGE);
        message.setKeyword1(paySuccessDto.getGoodsTitle());
        message.setKeyword2(DateUtils.getDate(new Date(), DateUtils.DATE_PATTERN_6));
        message.setKeyword3(paySuccessDto.getAmount() + "元");
        message.setKeyword4(paySuccessDto.isGroupStatus() ? "已成团" : "待成团");
        message.setKeyword5(paySuccessDto.isGroupStatus() ? getSendMsg(paySuccessDto.getOrderNo(), false)
                : "只需邀请" + cnt + "位好友参与即可成团，快去分享吧。");
        mqProducer.sendTemplateMessage(message);
    }

    private void payTwoGroup(OrderPaySuccessDto paySuccessDto) {
        String page = String.format("/pages/empty/main?groupNo=%s&orderNo=%s&type=2&ot=%s",
                paySuccessDto.getGroupNo(), paySuccessDto.getOrderNo(), paySuccessDto.getOrderNo());
        TemplateMessage message = new TemplateMessage();
        message.setPage(page);
        message.setUserId(paySuccessDto.getUserId());
        message.setTemplateId(TemplateIdConstants.PAY_SUCCESS_MESSAGE);
        message.setKeyword1(paySuccessDto.getGoodsTitle());
        message.setKeyword2(DateUtils.getDate(new Date(), DateUtils.DATE_PATTERN_6));
        message.setKeyword3(paySuccessDto.getAmount() + "元");
        message.setKeyword4(paySuccessDto.isGroupStatus() ? "已成团" : "待成团");
        message.setKeyword5(paySuccessDto.isGroupStatus() ? getSendMsg(paySuccessDto.getOrderNo(), false)
                : "只需邀请1位好友参与即可成团，快去分享吧。");
        mqProducer.sendTemplateMessage(message);
    }

    public void paySuccess(OrderPaySuccessDto paySuccessDto) {
        log.info("支付成功模板消息:{}", paySuccessDto);
        String msg = null;
        String page = null;
        if (null == paySuccessDto.getOrderType() || paySuccessDto.getOrderType() != OrderType.TEN_FOR_THREE_PIECE) {
            msg = getSendMsg(paySuccessDto.getOrderNo(), false);
            page = "pkgOrder/orderDetail/main?orderNo=" + paySuccessDto.getOrderNo()
                    + "&ed=1&ot=" + paySuccessDto.getOrderNo();
        } else {
            msg = "\uD83C\uDF81恭喜你获得了一次0.01元抽爆款商品的机会，千万别错过，点击立即抽奖>>";
            page = "pkgLottery/lottery/main?ot=" + paySuccessDto.getOrderNo();
        }
        TemplateMessage message = new TemplateMessage();
        message.setPage(page);
        message.setUserId(paySuccessDto.getUserId());
        message.setTemplateId(TemplateIdConstants.PAY_SUCCESS_MESSAGE);
        message.setKeyword1(paySuccessDto.getGoodsTitle());
        message.setKeyword2(DateUtils.getDate(new Date(), DateUtils.DATE_PATTERN_6));
        message.setKeyword3(paySuccessDto.getAmount() + "元");
        message.setKeyword4("支付成功，配送中");
        message.setKeyword5(msg);
        mqProducer.sendTemplateMessage(message);
    }

    public void logisticsShip(LogisticsShipDto logisticsShipDto) {
        log.info("物流发货通知:{}", logisticsShipDto);
        String orderNo = logisticsShipDto.getOrderNo();
        int packageIndex = 0;
        int index = StringUtils.indexOf(logisticsShipDto.getOrderNo(), "-");
        if (index != -1) {
            String[] data = StringUtils.split(logisticsShipDto.getOrderNo(), "-");
            orderNo = data[0];
            packageIndex = Integer.parseInt(data[1]);
        }
        String page = "pkgOrder/orderDetail/main?orderNo=" + orderNo + "&ed=1";
        TemplateMessage message = new TemplateMessage();
        message.setPage(page);
        message.setUserId(logisticsShipDto.getUserId());
        message.setTemplateId(TemplateIdConstants.LOGISTICS_SEND_MESSAGE);
        message.setKeyword1(orderNo);
        message.setKeyword2(logisticsShipDto.getLogisticsCompany());
        message.setKeyword3(logisticsShipDto.getLId());
        message.setKeyword4(DateUtils.getDate(logisticsShipDto.getSendTime(), DateUtils.DATE_PATTERN_1));
        String cozyMsg = logisticsShipDto.getCozyMsg();
        if (StringUtils.isNotEmpty(cozyMsg)) {
            message.setKeyword5(cozyMsg);
        } else {
            message.setKeyword5(packageIndex == 0 ? "尊敬的用户您好，您的订单已发货" :
                    "尊敬的用户您好，您的订单中包裹" + packageIndex + "已发货，点击可查看物流信息");
        }
        mqProducer.sendTemplateMessage(message);
    }

    public void sendGroupedMsg(Long userId, String actName, String goodName,
                               Integer groupPeople, String code, String orderNo) {
        log.info("抽奖成团提醒:{},{},{},{},{},{}", userId, actName, goodName, groupPeople, code, orderNo);
        String page = "pkgOrder/orderDetail/main?orderNo=" + orderNo + "&ed=1";
        TemplateMessage message = new TemplateMessage();
        message.setPage(page);
        message.setUserId(userId);
        message.setTemplateId(TemplateIdConstants.LOTTERY_GROUPED_MESSAGE);
        message.setKeyword1(actName);
        message.setKeyword2(groupPeople == null ? "2" : groupPeople.toString());
        message.setKeyword3(goodName);
        message.setKeyword4(DateUtils.SDF1.format(new Date()));
        message.setKeyword5("您的抽奖编码为 " + code + "，请关注“买买家”关注抽奖结果！");
        mqProducer.sendTemplateMessage(message);
    }

    public void sendTwoGroupedMsg(OrderPaySuccessDto paySuccessDto, Integer originalAmount, Integer discountAmount) {
        log.info("二人团成团提醒:{}", paySuccessDto);

        String page = String.format("/pages/empty/main?groupNo=%s&orderNo=%s&type=2",
                paySuccessDto.getGroupNo(), paySuccessDto.getOrderNo());
        TemplateMessage message = new TemplateMessage();
        message.setPage(page);
        message.setUserId(paySuccessDto.getUserId());
        message.setTemplateId(TemplateIdConstants.TWO_GROUPED_MESSAGE);
        message.setKeyword1(paySuccessDto.getGoodsTitle());
        message.setKeyword2(paySuccessDto.getAmount());
        message.setKeyword3("原价" + PriceConversion.intToString(originalAmount) +
                "元，优惠" + PriceConversion.intToString(discountAmount) + "元！");
        message.setKeyword4("商家将于1天内发货");
        message.setKeyword5("如果超时未发货，请您关注“买买帮”公众号，点击底部客服进行咨询处理，感谢您的支持。");
        mqProducer.sendTemplateMessage(message);
    }

    public void sendWaitePayMsg(Long userId, String orderNo, String goodName, Integer amount) {
        log.info("待支付提醒:{},{},{}", userId, goodName, amount);

        String page = "pkgOrder/orderDetail/main?orderNo=" + orderNo + "&ed=1";

        TemplateMessage message = new TemplateMessage();
        message.setPage(page);
        message.setUserId(userId);
        message.setTemplateId(TemplateIdConstants.WAITE_PAY_MESSAGE);
        message.setKeyword1(goodName);
        message.setKeyword2("未支付，即将自动取消");
        message.setKeyword3(PriceConversion.intToString(amount) + "元");
        message.setKeyword4("点击进入订单详情页完成支付");
        mqProducer.sendTemplateMessage(message);
    }

    public void sendGroupMsg(Long userId, String orderNo, String goodName,
                             String price, String groupPrice, Integer surplusCount,
                             String surplusTime, String remark, String page) {
        log.info("待成团提醒:{},{},{}", userId, goodName, price);

        if (null == page)
            page = "pkgOrder/orderDetail/main?orderNo=" + orderNo + "&ed=1";

        TemplateMessage message = new TemplateMessage();
        message.setPage(page);
        message.setUserId(userId);
        message.setTemplateId(TemplateIdConstants.WAITE_PAY_MESSAGE);
        message.setKeyword1(goodName);
        message.setKeyword2(price + "元");
        message.setKeyword3(groupPrice + "元");
        message.setKeyword4(surplusCount + "人");
        message.setKeyword5(surplusTime);
        message.setKeyword6(remark);
        mqProducer.sendTemplateMessage(message);
    }
}