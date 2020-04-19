package com.mmj.user.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mmj.common.constants.MQTopicConstant;
import com.mmj.common.constants.MQTopicConstantDelay;
import com.mmj.common.model.DelayTaskDto;
import com.mmj.common.utils.DateUtils;
import com.mmj.common.utils.SnowflakeIdWorker;
import com.mmj.user.manager.dto.UserCouponDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class MQProducer {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    SnowflakeIdWorker snowflakeIdWorker;

    /**
     * 卡券过期提醒-优惠券有效期≥1天的在有效期截止日期前24h触发推送；优惠券有效期＜1天的在有效期截止日期前3h触发推送
     *
     * @param couponCode
     * @param userCouponDto
     * @param expireTime
     */
    public void couponTimeout1(String couponCode, UserCouponDto userCouponDto, Date expireTime) {
        Long key = snowflakeIdWorker.nextId();
        String data = JSONObject.toJSONString(userCouponDto);
        DelayTaskDto delayTaskDto = new DelayTaskDto(couponCode, data, MQTopicConstantDelay.COUPON_TIMEOUT_1, DateUtils.getDate(expireTime, DateUtils.DATE_PATTERN_1));
        String content = JSON.toJSONString(delayTaskDto);
        kafkaTemplate.send(MQTopicConstant.WX_DELAY_TASK_SEND, key + "", content);
        log.info("放入延时处理队列 send topic:{},msg {} success", MQTopicConstant.WX_DELAY_TASK_SEND, content);
    }

    /**
     * 卡券过期提醒-用户领取后2小时内未使用触发推送
     *
     * @param couponCode
     * @param userCouponDto
     * @param expireTime
     */
    public void couponTimeout2(String couponCode, UserCouponDto userCouponDto, Date expireTime) {
        Long key = snowflakeIdWorker.nextId();
        String data = JSONObject.toJSONString(userCouponDto);
        DelayTaskDto delayTaskDto = new DelayTaskDto(couponCode, data, MQTopicConstantDelay.COUPON_TIMEOUT_2, DateUtils.getDate(expireTime, DateUtils.DATE_PATTERN_1));
        String content = JSON.toJSONString(delayTaskDto);
        kafkaTemplate.send(MQTopicConstant.WX_DELAY_TASK_SEND, key + "", content);
        log.info("放入延时处理队列 send topic:{},msg {} success", MQTopicConstant.WX_DELAY_TASK_SEND, content);
    }
}
