package com.mmj.user.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mmj.common.constants.MQTopicConstant;
import com.mmj.common.model.ReturnData;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.user.common.constants.CommonConstants;
import com.mmj.user.common.feigin.WxFeignClient;
import com.mmj.user.common.model.WeCatMessage;
import com.mmj.user.common.model.dto.WxMediaDto;
import com.mmj.user.recommend.service.RedPackageUserService;
import com.mmj.user.userFocus.constants.UserFocusConstants;
import com.mmj.user.userFocus.service.UserFocusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component(value = "wx_receive_msg")
public class MQConsumer {

    private static final Logger logger = LoggerFactory.getLogger(MQConsumer.class);

    @Autowired
    private UserFocusService userFocusService;

    @Autowired
    private WxFeignClient wxFeignClient;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${weChatTmpId.officialAppid}")
    private String officialAppid;//公众号appid

    @Value("${weChatTmpId.minAppid}")
    private String minAppid;//小程序appid

    @Autowired
    private RedPackageUserService redPackageUserService;

    /**
     * 订阅微信消息（小程序）
     *
     * @param params
     */
    @KafkaListener(topics = {MQTopicConstant.WX_MIN_MSG})
    public void receiveWxMinMsg(List<String> params) {
        if (params != null && !params.isEmpty()) {
            logger.info("----------------------------receiveWxMinMsg:" + params);
            for (String message : params) {
                if (message != null && message.length() > 0) {
                    logger.info("----------------------------receiveWxMinMsg:" + message);
                    WeCatMessage weCatMessage = JSON.parseObject(message, WeCatMessage.class);
                    String msgType = weCatMessage.getMsgType();
                    if (CommonConstants.WeCatMsgType.MINIPROGRAMPAGE.equals(msgType)) {//卡片消息
                        String pagePath = weCatMessage.getPagePath().split("sessionFromId")[1].split("&")[0].replace("=", "");
                        if (pagePath.length() != 0) {
                            String mediaId = "";
                            String param = "";
                            StringBuilder sb = null;
                            if (pagePath.startsWith(CommonConstants.WeCatMsgType.FLOW_POOL_FP)) {//公众号二维码
                                //FP1902_wx77a4d1dccaab522c_1_1
                                param = pagePath;//wx77a4d1dccaab522c_1_1
                                //获取mediaId
                                //OFFICIAL:FOCUS:OFFICIAL_IMAGE:wx77a4d1dccaab522c_1_1
                                sb = new StringBuilder(UserFocusConstants.FOCUS_OFFICIAL_MEDIA).append(param);
                            } else if (pagePath.startsWith(CommonConstants.WeCatMsgType.FLOW_POOL_GP)) {//群二维码
                                //GP1902_1_1
                                param = pagePath;
                                //获取mediaId
                                //OFFICIAL:FOCUS:GROUP_IMAGE:GP1902_1_1
                                sb = new StringBuilder(UserFocusConstants.FOCUS_GROUP_MEDIA).append(param);
                            }
                            Object o = redisTemplate.opsForValue().get(sb.toString());
                            if (o != null && !"".equals(o)) {
                                mediaId = String.valueOf(o);
                            } else {
                                if (pagePath.startsWith(CommonConstants.WeCatMsgType.FLOW_POOL_FP)) {//公众号二维码
                                    JSONObject jsonM = new JSONObject();
                                    jsonM.put("mpAppid", officialAppid);
                                    jsonM.put("sceneStr", param);
                                    jsonM.put("minAppid", minAppid);
                                    jsonM.put("businessId", param);
                                    jsonM.put("businessName", "流量池-公众号");
                                    ReturnData<WxMediaDto> returnData = wxFeignClient.createQrcode(jsonM.toJSONString());
                                    logger.info("----------------------------receiveWxMinMsg1:" + returnData);
                                    if (returnData != null && returnData.getCode() == SecurityConstants.SUCCESS_CODE && returnData.getData() != null) {
                                        WxMediaDto data = returnData.getData();
                                        mediaId = data.getMediaId();
                                        redisTemplate.opsForValue().set(sb.toString(), mediaId, 2, TimeUnit.DAYS);
                                    }
                                } else if (pagePath.startsWith(CommonConstants.WeCatMsgType.FLOW_POOL_GP)) {//群二维码
                                    WxMediaDto wxMedia = new WxMediaDto();
                                    wxMedia.setAppid(minAppid);
                                    wxMedia.setBusinessName("流量池-群");
                                    wxMedia.setMediaType("temporary");
                                    wxMedia.setMediaUrl(String.valueOf(redisTemplate.opsForValue().get(UserFocusConstants.FOCUS_GROUP_IMAGE + param)));
                                    ReturnData<WxMediaDto> returnData = wxFeignClient.upload(wxMedia);
                                    logger.info("----------------------------receiveWxMinMsg2:" + returnData);
                                    if (returnData != null && returnData.getCode() == SecurityConstants.SUCCESS_CODE && returnData.getData() != null) {
                                        WxMediaDto data = returnData.getData();
                                        mediaId = data.getMediaId();
                                        redisTemplate.opsForValue().set(sb.toString(), mediaId, 2, TimeUnit.DAYS);
                                    }
                                }

                            }
                            if (mediaId != null && mediaId.length() != 0) {
                                //发送图片消息
                                JSONObject json = new JSONObject();
                                json.put("appid", minAppid);
                                json.put("touser", weCatMessage.getFromUserName());
                                json.put("msgtype", CommonConstants.WeCatMsgType.IMAGE);
                                JSONObject o1 = new JSONObject();
                                o1.put("media_id", mediaId);
                                json.put("image", o1);
                                /**
                                 * 发送图片消息
                                 * {
                                 *   "touser":"OPENID",
                                 *   "msgtype":"image",
                                 *   "image": {
                                 *     "media_id":"MEDIA_ID"
                                 *   }
                                 * }
                                 */
                                wxFeignClient.sendCustom(json.toJSONString());
                                logger.info("----------------------------receiveWxMinMsg3:" + json);
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * 订阅微信消息（H5）
     *
     * @param params
     */
    @KafkaListener(topics = {MQTopicConstant.WX_H5_MSG})
    public void receiveWxH5Msg(List<String> params) {
        if (params != null && !params.isEmpty()) {
            logger.info("----------------------------receiveWxH5Msg:" + params);
            for (String message : params) {
                WeCatMessage weCatMessage = JSON.parseObject(message, WeCatMessage.class);
                String msgType = weCatMessage.getMsgType();
                String event = weCatMessage.getEvent();
                String eventKey = weCatMessage.getEventKey();
                String content = weCatMessage.getContent();
                if (CommonConstants.WeCatMsgType.EVENT.equals(msgType)) {//事件
                    if (CommonConstants.WeCatEvent.SUBSCRIBE.equals(event)) {//用户关注公众号
                        if (CommonConstants.WeCat.MSG_PREFIX.equals(eventKey)) {//记录关注信息
                            userFocusService.subscribe(weCatMessage.getFromUserName());
                        } else {
                            if (eventKey.contains(CommonConstants.WeCatMsgType.FLOW_POOL_FP)) {//流量池引导关注
                                userFocusService.subscribe(eventKey.replace(CommonConstants.WeCat.MSG_PREFIX, ""), weCatMessage.getFromUserName());
                            }
                        }
                    } else if (CommonConstants.WeCatEvent.UNSUBSCRIBE.equals(event)) {//用户取消关注
                        userFocusService.unsubscribe(weCatMessage.getFromUserName());
                    }
                } else if (CommonConstants.WeCatMsgType.TEXT.equals(msgType)) {//文本消息
                    logger.info("user接收到公众号消息:{}", message);
                    if (content.startsWith("LCJ") || content.startsWith("MFS") || content.startsWith("CUT")) {//领红包消息
                        try {
                            redPackageUserService.getRedPacketFromMQ(message);
                        } catch (Exception e) {
                            logger.error("领红包异常,{}", e.getMessage(), e);
                        }
                    }
                }
            }
        }
    }
}
