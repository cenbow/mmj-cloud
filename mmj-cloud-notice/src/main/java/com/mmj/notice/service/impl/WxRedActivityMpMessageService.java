package com.mmj.notice.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.mmj.common.exception.WxException;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.WxConfig;
import com.mmj.common.utils.EnvUtil;
import com.mmj.common.utils.HttpTools;
import com.mmj.common.utils.MD5Util;
import com.mmj.common.utils.SecurityUserUtil;
import com.mmj.notice.common.utils.WechatMessageUtil;
import com.mmj.notice.feigin.PayFeignClient;
import com.mmj.notice.feigin.WxMessageFeignClient;
import com.mmj.notice.model.WxConstants;
import com.mmj.notice.model.WxRedActivity;
import com.mmj.notice.model.WxRedActivityRecord;
import com.mmj.notice.model.WxpayRedpack;
import com.mmj.notice.service.WxMessageService;
import com.mmj.notice.service.WxRedActivityRecordService;
import com.mmj.notice.service.WxRedActivityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 公众号红包码
 */
@Slf4j
@Service
@EnableAsync
public class WxRedActivityMpMessageService extends AdapterMpMessageService {

    @Autowired
    WxRedActivityRecordService wxRedActivityRecordService;

    @Autowired
    WxRedActivityService wxRedActivityService;

    @Autowired
    WxMessageService wxMessageService;

    @Autowired
    WxMessageFeignClient wxMessageFeignClient;

    @Autowired
    PayFeignClient payFeignClient;

    @Value("${spring.cloud.config.profile}")
    private String profile;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    /**
     * 用户发送文本消息事件
     *
     * @param map      事件信息
     * @param userInfo 用户信息
     */
    @Override
    @Async
    public void text(Map<String, String> map, JSONObject userInfo) {
        String content =  map.get("Content").trim();
        String toUserName = map.get("ToUserName");
        log.info("红包码业务接受参数======={},当前是否是正式环境{},当前ToUserName{},当前判断正式环境{},接受文本消息{},红包码1{},红包码2{},状态判断{}",
                content, EnvUtil.isPro(profile),toUserName, EnvUtil.isPro(profile) && WxConstants.WX_NO_MMJ.equals(toUserName),content, !content.startsWith("MMJRED"),
                !content.startsWith("mmj"),!content.startsWith("MMJRED") || !content.startsWith("mmj"));
        if(content.startsWith("MMJRED") || content.startsWith("mmj")){//是以这个开头的处理
            log.info("开始发送红包码业务逻辑");
            doSendHdPackage(map, userInfo);
        }
    }

    /**
     * 直接送红包的傻行为
     * @param map
     * @param userInfo
     */
    private void doSendHdPackage(Map<String, String> map, JSONObject userInfo){
        String toUserName = map.get("ToUserName");
        ReturnData<WxConfig> wxConfigReturnData = wxMessageFeignClient.queryByWxNo(toUserName);
        WxConfig wxConfig = wxConfigReturnData.getData();
        String appId = wxConfig.getAppId();
        String content = map.get("Content");
        String fromUserName = map.get("FromUserName");
        String key1 = "com.mmj.notice.service.impl.WxRedActivityMpMessageService.doSendHdPackage"+ content + fromUserName;
        Long increment1 = redisTemplate.opsForValue().increment(key1, 1);
        redisTemplate.expire(key1, 3, TimeUnit.SECONDS);
        if(increment1 > 1){
            sendText(map, appId, "手速太快了啊 大兄弟 三秒之后再试试!");
            return;
        }
        String key2 = "com.mmj.notice.service.impl.WxRedActivityMpMessageService.doSendHdPackage"+ content;
        Long increment2 = redisTemplate.opsForValue().increment(key2, 1);
        redisTemplate.expire(key2, 3, TimeUnit.SECONDS);
        if(increment2 > 1){
            sendText(map, appId, "呵呵 多个人同时也领不到红包的!");
            return;
        }
        EntityWrapper<WxRedActivity> wxRedActivityEntityWrapper = new EntityWrapper<>();
        wxRedActivityEntityWrapper.eq("RED_CODE", content);
        WxRedActivity wxRedActivity = wxRedActivityService.selectOne(wxRedActivityEntityWrapper);
        if(null == wxRedActivity){ //没有这个红包码什么都不处理
            sendText(map, appId, "小伙伴，该红包码无效!");
            return;
        }
        EntityWrapper<WxRedActivityRecord> wxRedActivityRecordEntityWrapper = new EntityWrapper<>();
        wxRedActivityRecordEntityWrapper.eq("OPENID", fromUserName).eq("RED_ACTIVITY_ID", wxRedActivity.getId());
        WxRedActivityRecord wxRedActivityRecord = wxRedActivityRecordService.selectOne(wxRedActivityRecordEntityWrapper);
        if(null != wxRedActivityRecord){
            sendText(map, appId, "小伙子，一个人只能领一个!");
            return;
        }
        wxRedActivityRecordEntityWrapper = new EntityWrapper<>();
        wxRedActivityRecordEntityWrapper.eq("RED_ACTIVITY_ID", wxRedActivity.getId());
        int currentNum = wxRedActivityRecordService.selectCount(wxRedActivityRecordEntityWrapper);
        Integer limitTimes = wxRedActivity.getLimitTimes();
        log.info("活动红包当前已经领了"+currentNum+"=========一共"+limitTimes);
        if(currentNum >= limitTimes){
            sendText(map, appId, "小伙子，这个红包码被抢完了!");
            return;
        }
        Integer redMinMoney = wxRedActivity.getRedMinMoney();
        Integer redMaxMoney = wxRedActivity.getRedMaxMoney();
        int amount = redMinMoney + (int)(Math.random() * (redMaxMoney-redMinMoney+1));
        WxpayRedpack wxpayRedpack = new WxpayRedpack();
        wxpayRedpack.setMchBillno(MD5Util.MD5Encode(amount + content + fromUserName, "utf-8"));
        wxpayRedpack.setSendName(wxConfig.getWxName());
        wxpayRedpack.setReOpenid(fromUserName);
        wxpayRedpack.setTotalAmount(amount);
        wxpayRedpack.setWishing("恭喜得红包 嗯!");
        wxpayRedpack.setRemark("红包码送红包");
        wxpayRedpack.setWxappid(appId);
        wxpayRedpack.setActName(wxConfig.getWxName());
        try {
            payFeignClient.sendRedpack(wxpayRedpack);
            wxRedActivityRecord = new WxRedActivityRecord();
            wxRedActivityRecord.setOpenid(fromUserName);
            wxRedActivityRecord.setUnionid(userInfo.getString("unionid"));
            wxRedActivityRecord.setRedMoney(amount);
            wxRedActivityRecord.setNickname(userInfo.getString("nickname"));
            wxRedActivityRecord.setSex(userInfo.getInteger("sex"));
            wxRedActivityRecord.setCity(userInfo.getString("city"));
            wxRedActivityRecord.setProvice(userInfo.getString("provice"));
            wxRedActivityRecord.setRedActivityId(content);
            wxRedActivityRecord.setCreateTime(new Date());
            wxRedActivityRecordService.insert(wxRedActivityRecord);
            sendText(map, appId, "恭喜你获得红包!");
        } catch (Throwable e) { //发送失败了 根据情况提示
            JSONObject error = JSON.parseObject(e.getCause().getMessage().split("content:\n")[1]);
            sendText(map, appId,  error.getString("desc"));
        }
    }


    /**
     * 发送文本消息
     * @param map
     * @param appid
     * @param msg
     */
    private void sendText(Map<String, String> map,String appid, String msg){
        String fromUserName = map.get("FromUserName");
        JSONObject jsonObjectText = new JSONObject();
        jsonObjectText.put("touser", fromUserName);
        jsonObjectText.put("msgtype", WechatMessageUtil.MESSAGE_TEXT);
        JSONObject textJson = new JSONObject();
        textJson.put("content", msg);
        jsonObjectText.put("text", textJson);
        jsonObjectText.put("appid", appid);
        wxMessageService.sendCustom(jsonObjectText);
    }
}
