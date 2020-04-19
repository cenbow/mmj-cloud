package com.mmj.notice.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.common.constants.OrderStatus;
import com.mmj.common.model.MessageConstants;
import com.mmj.notice.common.constants.SMSConstants;
import com.mmj.notice.common.dto.OrderInfo;
import com.mmj.notice.feigin.OrderFeignClient;
import com.mmj.notice.mapper.DelaySmsMapper;
import com.mmj.notice.model.NoticeDelaySms;
import com.mmj.notice.model.NoticeSendLog;
import com.mmj.notice.service.DelaySmsService;
import com.mmj.notice.service.NoticeSendLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * <p>
 * 短信延迟发送表 服务实现类
 * </p>
 *
 * @author cgf
 * @since 2019-08-30
 */
@Service
@Slf4j
public class DelaySmsServiceImpl extends ServiceImpl<DelaySmsMapper, NoticeDelaySms> implements DelaySmsService {


    @Autowired
    private OrderFeignClient orderFeignClient;

    @Autowired
    private NoticeSendLogService logService;

    public static String getString(String path, String param) {
        BufferedReader in = null;
        try {
            StringBuilder sb = new StringBuilder(path);
            if (StringUtils.isNotEmpty(param)) {
                sb.append("?");
                sb.append(param);
            }
            URL url = new URL(sb.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            StringBuilder result = new StringBuilder();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            in.close();
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public void sendSMS() {
        EntityWrapper<NoticeDelaySms> wrapper = new EntityWrapper<>();
        wrapper.le("SEND_TIME", new Date());
        List<NoticeDelaySms> list = selectList(wrapper);
        if (null == list || list.size() == 0) {
            log.info("暂时没有需要发送的短信~~");
            return;
        }
        //这里的都是延时发送的短信
        Set<Integer> delList = new HashSet<>();
        List<NoticeSendLog> logList = new ArrayList<>();
        for (NoticeDelaySms noticeDelaySms : list) {
            log.info("notice开始发送短信:{}", noticeDelaySms.getOrderNo());
            if (noticeDelaySms.getNeedTemplate() == 1) {
                log.info("发送不需要模板的短信:{}", noticeDelaySms);
                String result = getString(SMSConstants.SmsUrl, noticeDelaySms.getContent());
                log.info("发送不需要模板的短信结果:{}", result);
                NoticeSendLog sendLog = new NoticeSendLog();
                if ("100".equals(result)) {
                    delList.add(noticeDelaySms.getId());
                    //发送成功
                    sendLog.setSendStatus(MessageConstants.smsSendStatus.SUCCESS);
                } else {
                    sendLog.setSendStatus(MessageConstants.smsSendStatus.FAIL);
                }
                sendLog.setNoticeId(noticeDelaySms.getNoticeId());
                sendLog.setCreateTime(new Date());
                logList.add(sendLog);
                delList.add(noticeDelaySms.getId());
                continue;
            }

            if (!isSend(noticeDelaySms, delList)) {
                log.info("订单此时不需要再发送短信了:{},node:{}", noticeDelaySms.getOrderNo(), noticeDelaySms.getNode());
                continue;
            }
            //发送短信
            log.info("发送短信:{}", noticeDelaySms);
            String result = getString(SMSConstants.SmsUrl, noticeDelaySms.getContent());
            log.info("短信发送结果:{}", result);

            NoticeSendLog sendLog = new NoticeSendLog();
            if ("100".equals(result)) {
                delList.add(noticeDelaySms.getId());
                //发送成功
                sendLog.setSendStatus(MessageConstants.smsSendStatus.SUCCESS);
            } else {
                sendLog.setSendStatus(MessageConstants.smsSendStatus.FAIL);
            }
            sendLog.setNoticeId(noticeDelaySms.getNoticeId());
            sendLog.setCreateTime(new Date());
            logList.add(sendLog);
        }
        log.info("list集合数据,delList:{} logList:{}", JSON.toJSONString(delList), JSON.toJSONString(logList));
        if (delList.size() > 0)
            this.deleteBatchIds(delList);
        if (logList.size() > 0)
            logService.insertBatch(logList);
    }

    private boolean isSend(NoticeDelaySms noticeDelaySms, Set<Integer> set) {
        if (noticeDelaySms.getDelayTime().intValue() == 0) {
            log.info("延迟时间为0，则为及时消息:{}", noticeDelaySms.getOrderNo());
            return true;
        }

        if (null == noticeDelaySms.getOrderNo())
            return true;
        OrderInfo info = orderFeignClient.getOrderInfo(noticeDelaySms.getOrderNo());
        log.info("查询到订单:{}", info);
        if (null == info) {
            set.add(noticeDelaySms.getId());
            return false;
        }
        boolean bool = this.isDelay(noticeDelaySms.getType(), info.getOrderStatus());
        log.info("isDelay 返回结果:{}", bool);
        if (!bool) {
            set.add(noticeDelaySms.getId());
            return false;
        }
        //需要发送短信
        return true;
    }

    private boolean isDelay(String type, int status) {
        log.info("判断是否是延迟消息,type:{},orderStatus:{}", type, status);
        boolean result = false;
        switch (type) {
            case MessageConstants.type.FLASH_TWO:
                break;
            case MessageConstants.type.TEN_THREE_TWO:
                log.info("十元三件待付款");
                if (OrderStatus.PENDING_PAYMENT.getStatus() == status)
                    //十元三件待付款
                    result = true;
                break;
            case MessageConstants.type.GROUP_NEWCOMERS_ONE:
                if (OrderStatus.PAYMENTED.getStatus() == status ||
                        OrderStatus.TO_BE_A_GROUP.getStatus() == status)
                    //接力购待成团
                    result = true;
                break;
            case MessageConstants.type.GROUP_NEWCOMERS_TWO:
                if (OrderStatus.CLOSED.getStatus() == status)
                    //接力购未成团退款
                    result = true;
                break;
            case MessageConstants.type.BARGAIN_ONE:
                log.info("砍价支付成功");
                if (OrderStatus.PAYMENTED.getStatus() == status)
                    //砍价支付成功
                    result = true;
                break;
            case MessageConstants.type.LOTTERY_ONE:
                log.info("抽奖订单未成团");
                if (OrderStatus.TO_BE_A_GROUP.getStatus() == status
                        || OrderStatus.PAYMENTED.getStatus() == status)
                    //抽奖未成团
                    result = true;
                break;
            case MessageConstants.type.TEN_YUAN_SHOP_ONE:
                log.info("店铺订单未支付");
                if (OrderStatus.PENDING_PAYMENT.getStatus() == status)
                    //下单结算未支付
                    result = true;
                break;
            case MessageConstants.type.TEN_YUAN_SHOP_TWO:
                log.info("店铺订单支付成功");
                if (OrderStatus.PAYMENTED.getStatus() == status ||
                        OrderStatus.TO_BE_DELIVERED.getStatus() == status)
                    //店铺订单支付成功
                    result = true;
                break;
            case MessageConstants.type.GROUP_ONE:
                log.info("拼团未支付");
                if (OrderStatus.PENDING_PAYMENT.getStatus() == status)
                    //下单结算未支付
                    result = true;
                break;
            default:
                log.info("default");
                break;
        }
        log.info("isDelay result:{}", result);
        return result;
    }
}
