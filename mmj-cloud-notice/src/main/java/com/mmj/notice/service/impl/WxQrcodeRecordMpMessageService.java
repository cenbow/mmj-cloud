package com.mmj.notice.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.mmj.common.utils.StringUtils;
import com.mmj.notice.common.utils.WechatMessageUtil;
import com.mmj.notice.model.WxQrcodeManager;
import com.mmj.notice.model.WxQrcodeRecord;
import com.mmj.notice.service.WxMessageService;
import com.mmj.notice.service.WxQrcodeManagerService;
import com.mmj.notice.service.WxQrcodeRecordService;
import com.mmj.notice.service.WxTagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@EnableAsync
@Slf4j
public class WxQrcodeRecordMpMessageService extends AdapterMpMessageService {

    @Autowired
    WxQrcodeManagerService wxQrcodeManagerService;

    @Autowired
    WxQrcodeRecordService wxQrcodeRecordService;

    @Autowired
    WxTagService wxTagService;

    @Autowired
    WxMessageService wxMessageService;

    public static String eventKeyPrefix = "wx_mp_manager_";

    /**
     * 扫描二维码事件
     *
     * @param map      事件信息
     * @param userInfo 用户信息
     */
    @Override
    @Async
    public void scan(Map<String, String> map, JSONObject userInfo) {
        doBusiness(map, userInfo, WechatMessageUtil.MESSAGE_EVENT_SCAN);
    }

    /**
     * 关注事件
     *
     * @param map      事件信息
     * @param userInfo 用户信息
     */
    @Override
    public void subscribe(Map<String, String> map, JSONObject userInfo) {
        doBusiness(map, userInfo, WechatMessageUtil.MESSAGE_EVENT_SUBSCRIBE);
    }

    private void doBusiness(Map<String, String> map, JSONObject userInfo, String source){
        String appid = map.get("appid");
        String eventKey = map.get("EventKey");
        String openid = map.get("FromUserName");
        if(StringUtils.isEmpty(eventKey)){
            return;
        }
        eventKey = eventKey.replace("qrscene_", "");
        if(StringUtils.isNotEmpty(eventKey) && eventKey.startsWith(eventKeyPrefix)){ //这种是通过boss后台创建的二维码扫码进来的
            eventKey = eventKey.replaceAll(eventKeyPrefix,"");
            EntityWrapper<WxQrcodeManager> entityWrapper = new EntityWrapper<>();
            String[] arr = eventKey.split("_");
            String qrcodeName = arr[0];
            String channelName = arr[1];
            entityWrapper.eq("APP_ID", appid);
            entityWrapper.eq("CHANNEL_NAME", channelName);
            WxQrcodeManager wxQrcodeManager = wxQrcodeManagerService.selectOne(entityWrapper);
            if(null != wxQrcodeManager){ //说明有配置这种情况的二维码
                EntityWrapper<WxQrcodeRecord> wxQrcodeRecordEntityWrapper = new EntityWrapper<>();
                wxQrcodeRecordEntityWrapper.eq("OPEN_ID", openid).eq("APPID", appid);
                WxQrcodeRecord wxQrcodeRecord = wxQrcodeRecordService.selectOne(wxQrcodeRecordEntityWrapper);
                if(null == wxQrcodeRecord){ //说明是之前没有扫描过渠道 那么这次就要算上次数 一个公众号只算一次
                    saveQrcodeRecord(wxQrcodeManager, map, userInfo);
                }
                if(WechatMessageUtil.MESSAGE_EVENT_SUBSCRIBE.equals(source)
                        && StringUtils.isNotEmpty(wxQrcodeManager.getUserTagNames())){ //如果是扫描后关注 那么就要打标签
                    doTag(wxQrcodeManager, map);
                }
                doReply(wxQrcodeManager, map); //处理回复
            }
        }
    }

    /**
     * 保存扫描记录
     * @param wxQrcodeManager
     * @param map
     * @param userInfo
     */
    private void saveQrcodeRecord(WxQrcodeManager wxQrcodeManager,Map<String, String> map, JSONObject userInfo){
        String appid = map.get("appid");
        String openid = map.get("FromUserName");
        String unionid = userInfo.getString("unionid");
        String nickname = userInfo.getString("nickname");
        Integer personCount = wxQrcodeManager.getPersonCount();
        wxQrcodeManager.setPersonCount(null == personCount?1:(personCount+1));
        wxQrcodeManagerService.updateById(wxQrcodeManager);
        WxQrcodeRecord wxQrcodeRecord = new WxQrcodeRecord();
        wxQrcodeRecord.setAppid(appid);
        wxQrcodeRecord.setRefId(wxQrcodeManager.getId());
        wxQrcodeRecord.setOpenId(openid);
        wxQrcodeRecord.setUnionId(unionid);
        wxQrcodeRecord.setNickName(nickname);
        wxQrcodeRecordService.insert(wxQrcodeRecord);
    }

    /**
     * 给用户打标签
     * @param wxQrcodeManager
     * @param map
     */
    private void doTag(WxQrcodeManager wxQrcodeManager,Map<String, String> map){
        String appid = map.get("appid");
        String openid = map.get("FromUserName");
        String userTagNames = wxQrcodeManager.getUserTagNames();
        JSONObject params = new JSONObject();
        params.put("appid", appid);
        params.put("openid", openid);
        List<String> tagName = Arrays.asList(userTagNames.split(","));
        params.put("tagNames", tagName);
        wxTagService.doTag(params);
    }

    /**
     * 处理回复
     * @param wxQrcodeManager
     * @param map
     */
    private  void doReply(WxQrcodeManager wxQrcodeManager,Map<String, String> map){
        String replyOneContent = wxQrcodeManager.getReplyOneContent();
        if(StringUtils.isNotEmpty(replyOneContent)){ //回复1
            sendMsg(map, replyOneContent);
        }
        String replyTwoContent = wxQrcodeManager.getReplyTwoContent();
        if(StringUtils.isNotEmpty(replyTwoContent)){ //回复2
            sendMsg(map, replyTwoContent);
        }
        String replyThridContent = wxQrcodeManager.getReplyThridContent();
        if(StringUtils.isNotEmpty(replyThridContent)){ //回复3
            sendMsg(map, replyThridContent);
        }
    }

    /**
     * 发送客服消息
     * @param map
     * @param content
     */
    private void sendMsg(Map<String, String> map, String content){
        JSONObject msgJson = JSON.parseObject(content);
        String appid = map.get("appid");
        String openid = map.get("FromUserName");
        msgJson.put("appid", appid);
        msgJson.put("touser", openid);
        wxMessageService.sendCustom(msgJson);
    }
}
