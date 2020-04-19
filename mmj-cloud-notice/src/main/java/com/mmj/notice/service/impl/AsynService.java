package com.mmj.notice.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.mmj.common.constants.MQTopicConstant;
import com.mmj.notice.model.WxdelayTask;
import com.mmj.notice.service.WxdelayTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@EnableAsync
public class AsynService {

    @Autowired
    WxdelayTaskService wxdelayTaskService;

    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    /**
     * 延迟队列处理
     */
    @Async
    public void doDelayQueue(){
        EntityWrapper<WxdelayTask> wxdelayTaskEntityWrapper = new EntityWrapper<>();
        Calendar calendar = Calendar.getInstance ();
        calendar.add(Calendar.SECOND, -2);
        wxdelayTaskEntityWrapper.between("EXECUTE_TIME", calendar.getTime(), new Date());
        List<WxdelayTask> wxdelayTasks = wxdelayTaskService.selectList(wxdelayTaskEntityWrapper);
        wxdelayTasks.parallelStream().forEach( wxdelayTask -> {
            Long increment = redisTemplate.opsForValue().increment("wx_doLoopTask"+ wxdelayTask.getBusinessId() + "_" + wxdelayTask.getBusinessType(), 1);
            redisTemplate.expire("wx_doLoopTask"+ wxdelayTask.getBusinessId() + "_" + wxdelayTask.getBusinessType(), 60, TimeUnit.SECONDS);
            if(increment > 1){
                log.info("延迟队列重复操作,不执行", JSONObject.toJSON(wxdelayTask));
            }else {
                kafkaTemplate.send(MQTopicConstant.WX_DELAY_TASK_ACCEPT, JSON.toJSONString(wxdelayTask));
                log.info("延迟队列消费消息" + JSON.toJSONString(wxdelayTask));
                wxdelayTaskService.deleteById(wxdelayTask.getId());
            }
        });
    }
}
