package com.mmj.notice.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mmj.common.constants.MQCommonTopic;
import com.mmj.common.constants.MQTopicConstant;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.TemplateMessage;
import com.mmj.common.model.WxConfig;
import com.mmj.common.utils.StringUtils;
import com.mmj.notice.common.utils.WechatMessageUtil;
import com.mmj.notice.feigin.UserFeignClient;
import com.mmj.notice.feigin.WxMessageFeignClient;
import com.mmj.notice.service.MpMessageCommonService;
import com.mmj.notice.service.WxMessageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class WxMsgConsumer {

    static String APPID = "wx7a01aef90c714fe2";

    @Autowired
    List<MpMessageCommonService> mpMessageCommonServices;

    @Autowired
    WxMessageService wxMessageService;

    @Autowired
    WxMessageFeignClient wxMessageFeignClient;

    /**
     * 订阅微信消息（H5）
     *
     * @param msg
     */
    @KafkaListener(topics = {MQTopicConstant.WX_H5_MSG})
    public void receiveWxH5Msg(List<String> msg) {
        msg.parallelStream().forEach(message -> {
            log.info("notice模块接收到微信客服消息" + message);
            Map<String, Object> map = JSONObject.parseObject(message, Map.class);
            JSONObject userInfo = (JSONObject) map.get("ex");
            map.remove("ex");
            Map<String, String> strMap = JSONObject.parseObject(JSON.toJSONString(map), Map.class);
            ReturnData<WxConfig> wxConfigReturnData = wxMessageFeignClient.queryByWxNo(strMap.get("ToUserName"));
            String event = strMap.get("Event");
            strMap.put("appid", wxConfigReturnData.getData().getAppId());
            if (StringUtils.isNotEmpty(event)) { //如果有事件 那么判断事件类型 转发到不同模块
                switch (event) {
                    case WechatMessageUtil.MESSAGE_EVENT_CLICK: //点击事件
                        mpMessageCommonServices.parallelStream().forEach(mpMessageCommonService -> {
                            mpMessageCommonService.click(strMap, userInfo);
                        });
                        break;
                    case WechatMessageUtil.MESSAGE_EVENT_SUBSCRIBE: //关注事件
                        mpMessageCommonServices.parallelStream().forEach(mpMessageCommonService -> {
                            mpMessageCommonService.subscribe(strMap, userInfo);
                        });
                        break;
                    case WechatMessageUtil.MESSAGE_EVENT_SCAN: //扫码时间
                        mpMessageCommonServices.parallelStream().forEach(mpMessageCommonService -> {
                            mpMessageCommonService.scan(strMap, userInfo);
                        });
                        break;
                }
            }
            String msgType = (String) map.get("MsgType"); //接受到的輸入消息类型
            if (StringUtils.isNotEmpty(msgType)) {
                switch (msgType) {
                    case WechatMessageUtil.MESSAGE_TEXT: //文本消息
                        mpMessageCommonServices.parallelStream().forEach(mpMessageCommonService -> {
                            mpMessageCommonService.text(strMap, userInfo);
                        });
                        break;
                }
            }
        });
    }

    /**
     * 订阅小程序模板消息
     *
     * @param msg
     */
    @KafkaListener(topics = {MQTopicConstant.WX_MIN_TEMPLATE})
    public void receiveMinTemplateMsg(List<String> msg) {
        msg.parallelStream().forEach(message -> {
            log.info("notice模块接收到小程序模板消息" + message);
            wxMessageService.sendTemplateM(JSONObject.parseObject(message));
        });
    }

    @KafkaListener(topics = {MQCommonTopic.SEND_TEMPLATE_MESSAGE})
    public void listenTempMsg(List<ConsumerRecord<?, ?>> records) throws Exception {
        for (ConsumerRecord<?, ?> record : records) {
            Optional<?> kafkaMessage = Optional.ofNullable(record.value());
            if (!kafkaMessage.isPresent()) {
                continue;
            }
            log.info("接收到微信模板消息:{}", record.value().toString());
            TemplateMessage tm = JSONObject.parseObject(record.value().toString(), TemplateMessage.class);
            send(tm);
        }
    }


    @Autowired
    private UserFeignClient userFeignClient;

    private void send(TemplateMessage tm) throws NoSuchFieldException, IllegalAccessException {
        if (null == tm)
            return;
        Map<String, Object> map = new HashMap<>();

        for (int i = 1; i <= 10; i++) {
            Field field = tm.getClass().getDeclaredField("keyword" + i);
            field.setAccessible(true);
            Object v = field.get(tm);
            if (null == v)
                continue;

            Map<String, Object> kyMap = new HashMap<>();
            kyMap.put("value", v);
            kyMap.put("color", "#173177");
            map.put(field.getName(), kyMap);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("data", map);
        data.put("template_id", tm.getTemplateId());
        data.put("page", tm.getPage());
        if (null == tm.getTouser()) {
            JSONObject object = new JSONObject();
            object.put("userId", tm.getUserId());
            object.put("appId", APPID);
            log.info("查询用户openId,参入:{}", object.toJSONString());
            String openId = userFeignClient.getUserOpenId(object);
            if (StringUtils.isEmpty(openId)) {
                log.error("用户不存在,不能发送模板消息:{}", openId);
                return;
            }
            data.put("touser", openId);
        }

        data.put("appid", APPID);
        log.info("组装的模板消息:{}", data);
        wxMessageService.sendTemplateM(data);
    }
}
