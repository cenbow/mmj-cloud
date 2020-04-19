package com.mmj.notice.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.mmj.common.constants.MQTopicConstant;
import com.mmj.common.constants.MQTopicConstantDelay;
import com.mmj.common.utils.DateUtils;
import com.mmj.notice.feigin.WxMessageFeignClient;
import com.mmj.notice.model.*;
import com.mmj.notice.service.BaseDictService;
import com.mmj.notice.service.WxCustomMsgService;
import com.mmj.notice.service.WxCustomMsgTxtService;
import com.mmj.notice.service.WxMessageService;
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
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 客服消息处理
 */
@Slf4j
@Service
@EnableAsync
public class WxCustomMsgMpMessageService extends AdapterMpMessageService {

    @Autowired
    WxCustomMsgService wxCustomMsgService;

    @Autowired
    WxMessageService wxMessageService;

    @Autowired
    WxCustomMsgTxtService wxCustomMsgTxtService;

    @Autowired
    BaseDictService baseDictService;

    @Autowired
    WxMessageFeignClient wxMessageFeignClient;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    KafkaTemplate kafkaTemplate;

    /**
     * 关注事件
     *
     * @param map      事件信息
     * @param userInfo 用户信息
     */
    @Override
    @Async
    public void subscribe(Map<String, String> map, JSONObject userInfo) {
        String wxNo = map.get("ToUserName");
        String touser = map.get("FromUserName");
        EntityWrapper<WxCustomMsg> customMsgEntityWrapper = new EntityWrapper<>();
        customMsgEntityWrapper.eq("WX_NO", wxNo).orderBy("CREATE_TIME desc");
        List<WxCustomMsg> wxCustomMsgs = wxCustomMsgService.selectList(customMsgEntityWrapper);
        if(!wxCustomMsgs.isEmpty()){  //说明有此公众号的关注操作
            List<WxCustomMsg> subCollect = wxCustomMsgs.stream().filter(n -> WxCustomMsgEx.ACCEPTTYPE.subscribe.name().equals(n.getAcceptType())).collect(Collectors.toList());
            List<WxCustomMsg> delCollect = wxCustomMsgs.stream().filter(n -> WxCustomMsgEx.ACCEPTTYPE.delay.name().equals(n.getAcceptType())).collect(Collectors.toList());
            if(!subCollect.isEmpty()){ //关注后回复
                WxCustomMsg wxCustomMsg = subCollect.get(0);
                doSendMsg(wxCustomMsg, touser);
            }
            if(!delCollect.isEmpty()){ //关注后多少小时发送
                WxCustomMsg wxCustomMsg = delCollect.get(0);
                Integer replyDelay = wxCustomMsg.getReplyDelay(); //延迟的小时数
                Date afterByHourse = DateUtils.getAfterByHourse(replyDelay);
                Calendar instance = Calendar.getInstance();
                instance.setTime(afterByHourse);
                int hourse = instance.get(Calendar.HOUR_OF_DAY); //获取延迟指定时间以后的小时数
                if(0 <= hourse && hourse <= 6){ //如果时间在 0点到七点之间 那么就取七点钟来发
                    replyDelay = replyDelay - hourse + 7;
                }
                JSONObject params = new JSONObject();
                params.put("businessId", touser);
                params.put("businessData", JSONObject.toJSONString(wxCustomMsg));
                params.put("businessType", MQTopicConstantDelay.WXCUSTOMMSG_SUBSCRIBE);
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.HOUR_OF_DAY, replyDelay);
                params.put("executeTime", calendar.getTime());
                kafkaTemplate.send(MQTopicConstant.WX_DELAY_TASK_SEND, params.toJSONString());
            }
        }
    }

    /**
     * 用户发送文本消息事件
     *
     * @param map      事件信息
     * @param userInfo 用户信息
     */
    @Override
    public void text(Map<String, String> map, JSONObject userInfo) {
        EntityWrapper<WxCustomMsgTxt> entityWrapper = new EntityWrapper<>();
        String content =  map.get("Content");
        entityWrapper.eq("WX_NO", map.get("ToUserName"));
        entityWrapper.like("KEY_WORD", content);
        WxCustomMsgTxt wxCustomMsgTxt = wxCustomMsgTxtService.selectOne(entityWrapper);
        if(null == wxCustomMsgTxt){ //都没匹配上 那么就查询字典表能够匹配 目的是为了不处理其他模块匹配上的文字处理 能得话就不做处理，不能得话这个时候就要回复默认回复了
            List<BaseDict> baseDicts = baseDictService.queryByDictType(WxCustomMsgTxtEx.dictType);
            for (BaseDict baseDict: baseDicts){
                if(content.startsWith(baseDict.getDictCode())){
                    return;
                }
            }
            EntityWrapper<WxCustomMsg> customMsgEntityWrapper = new EntityWrapper<>();
            customMsgEntityWrapper.eq("ACCEPT_TYPE", WxCustomMsgEx.ACCEPTTYPE.defaultreply.name())
                    .eq("WX_NO", map.get("ToUserName"));
            WxCustomMsg wxCustomMsg = wxCustomMsgService.selectOne(customMsgEntityWrapper);
            if(null != wxCustomMsg){
                doSendMsg(wxCustomMsg, map.get("FromUserName"));
                return;
            }
        }else if(null != wxCustomMsgTxt && content.equals(wxCustomMsgTxt.getKeyWord())
                || WxCustomMsgTxtEx.matchRule.half.name().equals(wxCustomMsgTxt.getMatchRule())){ //全匹配上了或者半匹配上了
            WxCustomMsg wxCustomMsg = new WxCustomMsg();
            wxCustomMsg.setReplyContent(wxCustomMsgTxt.getReplyContent());
            wxCustomMsg.setAppid(wxCustomMsgTxt.getAppid());
            doSendMsg(wxCustomMsg, map.get("FromUserName"));
            return;
        }
    }

    /**
     * 发送客服消息
     * @param wxCustomMsg
     * @param touser
     */
    private void doSendMsg(WxCustomMsg wxCustomMsg, String touser){
        String replyContent = wxCustomMsg.getReplyContent();
        JSONObject msg = JSON.parseObject(replyContent);
        msg.put("touser", touser);
        msg.put("appid", wxCustomMsg.getAppid());
        wxMessageService.sendCustom(msg);
    }
}
