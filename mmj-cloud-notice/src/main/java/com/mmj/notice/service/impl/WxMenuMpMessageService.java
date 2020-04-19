package com.mmj.notice.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.WxConfig;
import com.mmj.notice.common.utils.WechatMessageUtil;
import com.mmj.notice.feigin.WxMessageFeignClient;
import com.mmj.notice.model.WxMenuEx;
import com.mmj.notice.model.WxMenuKey;
import com.mmj.notice.service.WxMenuKeyService;
import com.mmj.notice.service.WxMenuService;
import com.mmj.notice.service.WxMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


/**
 * 微信菜单栏处理service
 */
@Service
@EnableAsync
@Slf4j
public class WxMenuMpMessageService extends AdapterMpMessageService {

    @Autowired
    WxMenuKeyService wxMenuKeyService;

    @Autowired
    WxMenuService wxMenuService;

    @Autowired
    WxMessageFeignClient wxMessageFeignClient;


    @Autowired
    WxMessageService wxMessageService;

    /**
     * 菜单栏点击事件
     *
     * @param map      事件信息
     * @param userInfo 用户信息
     */
    @Override
    @Async
    public void click(Map<String, String> map, JSONObject userInfo) {
        String wxNo = map.get("ToUserName");
        String touser = map.get("FromUserName");
        String eventKey = map.get("EventKey");
        ReturnData<WxConfig> wxConfigReturnData = wxMessageFeignClient.queryByWxNo(wxNo);
        String appid = wxConfigReturnData.getData().getAppId();
        //查询公众号菜单栏的信息
        WxMenuEx wxMenuEx = wxMenuService.query(appid);
        if(null == wxMenuEx){ //说明此公众号没有配置任何信息
            return;
        }
        List<WxMenuKey> wxMenuKeys = wxMenuEx.getWxMenuKeys(); //公众号菜单栏的操作信息
        if(wxMenuKeys.size() == 0){
            return;
        }
        //公众号菜单栏上对应的操作
        WxMenuKey wxMenuKey = wxMenuKeys.stream().filter(n -> eventKey.equals(n.getKeyWord())).findFirst().get();
        if(null == wxMenuKey){ //没有对应的操作
            return;
        }
        String replyContent = wxMenuKey.getReplyContent(); //回复的内容
        String replyType = wxMenuKey.getReplyType(); //回复的消息类型
        JSONObject params = new JSONObject();
        switch (replyType){
            case WechatMessageUtil.MESSAGE_IMG: //图片消息
                params.put("msgtype", WechatMessageUtil.MESSAGE_IMG);
                JSONObject imageJson = new JSONObject();
                imageJson.put("media_id", replyContent);
                params.put("image", imageJson);
                break;
                case WechatMessageUtil.MESSAGE_TEXT: //文字消息
                    params = JSON.parseObject(replyContent);
        }
        params.put("touser", touser);
        params.put("appid", appid);
        wxMessageService.sendCustom(params);
    }
}
