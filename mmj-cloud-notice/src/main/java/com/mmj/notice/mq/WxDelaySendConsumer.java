package com.mmj.notice.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.common.constants.MQTopicConstant;
import com.mmj.common.constants.MQTopicConstantDelay;
import com.mmj.common.utils.StringUtils;
import com.mmj.notice.model.WxdelayTask;
import com.mmj.notice.service.WxCustomMsgService;
import com.mmj.notice.service.WxdelayTaskService;
import com.mmj.notice.service.impl.AsynService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 延迟任务存消息
 */
@Component
@Slf4j
public class WxDelaySendConsumer {

    @Autowired
    WxdelayTaskService wxdelayTaskService;

    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    WxCustomMsgService wxCustomMsgService;

    @Autowired
    AsynService asynService;

    @KafkaListener(topics = {MQTopicConstant.WX_DELAY_TASK_SEND})
    public void receiveTaskSend(List<String> msg){
        msg.parallelStream().forEach(message ->{
            log.info("延迟队列存入消息: " + message);
            WxdelayTask wxdelayTask = JSONObject.parseObject(message, WxdelayTask.class);
            wxdelayTask.setCreateTime(new Date());
            EntityWrapper<WxdelayTask> wxdelayTaskEntityWrapper = new EntityWrapper<>();
            wxdelayTaskEntityWrapper.eq("BUSINESS_ID", wxdelayTask.getBusinessId())
                    .eq("BUSINESS_TYPE", wxdelayTask.getBusinessType());
            wxdelayTaskService.delete(wxdelayTaskEntityWrapper);
            Date executeTime = wxdelayTask.getExecuteTime();
            if(executeTime.getTime() > new Date().getTime()){ //只有执行时间大于当前时间的才处理
                wxdelayTaskService.insert(wxdelayTask);
                redisTemplate.opsForValue().set("WX_DELAY_TASK_SEND_MIN", wxdelayTaskService.getLastTime()+"");
            }
        });
    }


    /**
     * 获取最近的执行时间 如果当最近的时间时间
     * 小于等于一秒的时候 那么就从数据库里面获取数据
     */
    @PostConstruct
    public void doLoopTask(){
        new Thread(){
            @Override
            public void run() {
                redisTemplate.opsForValue().set("WX_DELAY_TASK_SEND_MIN", wxdelayTaskService.getLastTime()+"");
                while (true){
                    try {
                        String wx_delay_task_send_min = redisTemplate.opsForValue().get("WX_DELAY_TASK_SEND_MIN");
                        long minTime =  -1;
                        if(StringUtils.isNotEmpty(wx_delay_task_send_min) && !"-1".equals(wx_delay_task_send_min)){
                            minTime = Long.parseLong(wx_delay_task_send_min) - System.currentTimeMillis();
                        }
                        if(minTime != -1 && minTime <= 1000 ){ // 说明有任务到了执行时间
                            asynService.doDelayQueue();
                            redisTemplate.opsForValue().set("WX_DELAY_TASK_SEND_MIN", wxdelayTaskService.getLastTime()+"");
                        }
                    }catch (Exception e){
                        log.error("延迟队列循环发生错误",  e);
                    }
                }
            }
        }.start();
    }


    /**
     * 如果容器杀死了 那么删除对应的key
     */
    @PreDestroy
    public void delRedis(){
        redisTemplate.delete("wx_doLoopTask*");
    }


    /**
     * 发送关注以后的延迟客服消息
     */
    @KafkaListener(topics = {MQTopicConstant.WX_DELAY_TASK_ACCEPT})
    public void receiveTaskAccept(List<String>  msg){
       msg.parallelStream().forEach(message->{
           log.info("延迟队列获取消息: " + message);
           WxdelayTask wxdelayTask = JSONObject.parseObject(message, WxdelayTask.class);
           String businessType = wxdelayTask.getBusinessType();
           switch (businessType) {
               case  MQTopicConstantDelay.WXCUSTOMMSG_PUSH:
                   wxCustomMsgService.sendPush(wxdelayTask.getBusinessId()); break;
               case MQTopicConstantDelay.WXCUSTOMMSG_SUBSCRIBE:
                   wxCustomMsgService.sendSubscribeDelay(wxdelayTask); break;
           }
       });
    }
}

