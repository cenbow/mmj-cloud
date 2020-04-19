package com.mmj.notice.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.plugins.pagination.PageHelper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.common.constants.MQTopicConstant;
import com.mmj.common.constants.MQTopicConstantDelay;
import com.mmj.common.exception.WxException;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.utils.*;
import com.mmj.notice.common.utils.WechatMessageUtil;
import com.mmj.notice.mapper.WxCustomMsgMapper;
import com.mmj.notice.mapper.WxCustomMsgTxtMapper;
import com.mmj.notice.model.*;
import com.mmj.notice.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 客服消息配置 服务实现类
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-27
 */
@Service
@Slf4j
public class WxCustomMsgServiceImpl extends ServiceImpl<WxCustomMsgMapper, WxCustomMsg> implements WxCustomMsgService {

    @Autowired
    WxMediaService wxMediaService;

    @Autowired
    WxCustomMsgTxtService wxCustomMsgTxtService;

    @Autowired
    WxMessageService wxMessageService;

    @Autowired
    WxMpSendRecordService wxMpSendRecordService;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    WxCustomMsgTxtMapper wxCustomMsgTxtMapper;

    /**
     * 客服消息配置保存
     *
     * @param wxCustomMsgExes
     * @return
     */
    @Override
    @Transactional
    public void save(List<WxCustomMsgEx> wxCustomMsgExes) {
//        boolean tag = false;
//        if(WxCustomMsgEx.ACCEPTTYPE.keyword.name().equals(wxCustomMsgExes.get(0).getAcceptType())){ //如果保存的是关键词回复
//            List<WxCustomMsgTxt> wxCustomMsgTxts = new ArrayList<>();
//            for (WxCustomMsgEx wxCustomMsgEx: wxCustomMsgExes){
//                wxCustomMsgTxts.addAll(wxCustomMsgEx.getWxCustomMsgTxts());
//            }
//            for (WxCustomMsgEx wxCustomMsgEx: wxCustomMsgExes){
//                if(!tag && null == wxCustomMsgEx.getWxCustomMsgTxts().get(0).getId()){ //说明是新增 这个时候才要去校验规则名称
//                    List<String> ruleNames =  wxCustomMsgEx.getWxCustomMsgTxts().parallelStream().map(WxCustomMsgTxt::getRuleName).collect(Collectors.toList());
//                    EntityWrapper<WxCustomMsgTxt> entityWrapper = new EntityWrapper<>();
//                    entityWrapper.in("RULE_NAME", ruleNames);
//                    List<WxCustomMsgTxt> recordRuleNames = wxCustomMsgTxtService.selectList(entityWrapper);
//                    if(recordRuleNames.size() > 0){ //说明规则名称重复了
//                        throw new WxException("规则名称" + recordRuleNames.get(0).getRuleName() + "重复了");
//                    }
//                    tag = true;
//                }
//                if(null == wxCustomMsgEx.getWxCustomMsgTxts().get(0).getId()){
//                    List<String> keyWords = wxCustomMsgEx.getWxCustomMsgTxts().parallelStream().map(WxCustomMsgTxt::getKeyWord).collect(Collectors.toList());
//                    Wrapper<WxCustomMsgTxt> entityWrapper = new EntityWrapper<>();
//                    entityWrapper.in("KEY_WORD", keyWords);
//                    List<WxCustomMsgTxt> recordKeyWords = wxCustomMsgTxtService.selectList(entityWrapper);
//                    if(recordKeyWords.size() > 0){ //说明关键字重复了
//                        throw new WxException("关键字" + recordKeyWords.get(0).getKeyWord() + "重复了");
//                    }
//                }
//            }
//        }
        for (WxCustomMsgEx wxCustomMsgEx: wxCustomMsgExes){
            String replyContent = getReplyContent(wxCustomMsgEx);
            wxCustomMsgEx.setReplyContent(replyContent);
            //保存客服消息配置记录 先删除以前的 再插入
            //1.删除
            EntityWrapper<WxCustomMsg> wrapper = new EntityWrapper<>();
            wrapper.eq("APPID", wxCustomMsgEx.getAppid()).
                    eq("ACCEPT_TYPE", wxCustomMsgEx.getAcceptType());
            delete(wrapper);
            //2.插入
            JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
            wxCustomMsgEx.setCreateId(userDetails.getUserId());
            wxCustomMsgEx.setCreateName(userDetails.getUserFullName());
            insert(wxCustomMsgEx);
            String acceptType = wxCustomMsgEx.getAcceptType();
            if(WxCustomMsgEx.ACCEPTTYPE.keyword.name().equals(acceptType)){ //如果是关键字回复
                List<WxCustomMsgTxt> wxCustomMsgTxts = wxCustomMsgEx.getWxCustomMsgTxts();
                wxCustomMsgTxts.parallelStream().forEach( wxCustomMsgTxt -> {
                    WxCustomMsgEx wxCustomMsgEx1 = new WxCustomMsgEx();
                    wxCustomMsgEx1.setReplyImg(wxCustomMsgTxt.getReplyImg());
                    wxCustomMsgEx1.setAppid(wxCustomMsgTxt.getAppid());
                    wxCustomMsgEx1.setReplyContent(wxCustomMsgTxt.getReplyContent());
                    wxCustomMsgEx1.setReplyType(wxCustomMsgTxt.getReplyType());
                    String replyContent1 = getReplyContent(wxCustomMsgEx1);
                    wxCustomMsgTxt.setReplyContent(replyContent1);
                    wxCustomMsgTxt.setCreateTime(new Date());
                    wxCustomMsgTxtService.insertOrUpdate(wxCustomMsgTxt);
                });
            }else if(WxCustomMsgEx.ACCEPTTYPE.push.name().equals(acceptType)){ //如果是主动推送 那么还要创建一个发送记录
                WxMpSendRecord wxMpSendRecord = new WxMpSendRecord();
                wxMpSendRecord.setAppId(wxCustomMsgEx.getAppid());
                wxMpSendRecord.setAppName(wxCustomMsgEx.getAppName());
                String replyType = wxCustomMsgEx.getReplyType();
                switch (replyType){
                    case WechatMessageUtil.MESSAGE_TEXT:
                        replyType = "文本消息"; break;
                    case WechatMessageUtil.MESSAGE_MIN_PAGE:
                        replyType = "小程序卡片"; break;
                    case WechatMessageUtil.MESSAGE_IMG:
                        replyType = "图片消息"; break;
                    case WechatMessageUtil.MESSAGE_NEWS:
                        replyType = "图文消息"; break;
                }
                wxMpSendRecord.setMsgType(replyType);
                wxMpSendRecord.setMsgSendTime(wxCustomMsgEx.getReplyTime());
                wxMpSendRecord.setTotalNum(wxMessageService.queryTotal(wxCustomMsgEx.getAppid()));
                wxMpSendRecord.setCreateTime(new Date());
                wxMpSendRecordService.insert(wxMpSendRecord);
                //将延迟消息放入Kafka
                WxdelayTask wxdelayTask = new WxdelayTask();
                wxdelayTask.setBusinessType(MQTopicConstantDelay.WXCUSTOMMSG_PUSH);
                wxdelayTask.setBusinessId(wxCustomMsgEx.getAppid());
                wxdelayTask.setExecuteTime(wxCustomMsgEx.getReplyTime());
                kafkaTemplate.send(MQTopicConstant.WX_DELAY_TASK_SEND, JSON.toJSONString(wxdelayTask));
            }
        }
    }

    /**
     * 获取实际回复的json格式
     * @param wxCustomMsgEx
     * @return
     */
    private String getReplyContent(WxCustomMsgEx wxCustomMsgEx){
        String img = wxCustomMsgEx.getReplyImg();
        String appid = wxCustomMsgEx.getAppid();
        String replyContent = wxCustomMsgEx.getReplyContent();
        String replyType = wxCustomMsgEx.getReplyType();
        if(WechatMessageUtil.MESSAGE_IMG.equals(replyType)
                || WechatMessageUtil.MESSAGE_MIN_PAGE.equals(replyType)){  //如果是回复的图片或者小程序 那么就要上传获取mediaid
            WxMedia wxMedia = new WxMedia();
            wxMedia.setMediaUrl(img);
            wxMedia.setAppid(appid);
            wxMedia.setMediaType(WxMediaEx.mediaType.forever.name());
            wxMedia.setBusinessName("公众号客服消息");
            wxMedia = wxMediaService.upload(wxMedia);
            String mediaId = wxMedia.getMediaId();
            JSONObject contentJson = JSON.parseObject(replyContent);
            if(WechatMessageUtil.MESSAGE_IMG.equals(replyType)){ //如果是图片消息
                contentJson.getJSONObject("image").put("media_id", mediaId);
            }else if(WechatMessageUtil.MESSAGE_MIN_PAGE.equals(replyType)){ //如果是小程序消息
                contentJson.getJSONObject("miniprogrampage").put("thumb_media_id", mediaId);
            }
            replyContent = contentJson.toJSONString();
        }
        return replyContent;
    }

    /**
     * 客服消息查询,不包含关键字查询
     *
     * @param appid
     * @return
     */
    @Override
    public List<WxCustomMsg> query(String appid) {
        EntityWrapper<WxCustomMsg> customMsgEntityWrapper = new EntityWrapper<>();
        customMsgEntityWrapper.eq("APPID", appid);
        List<WxCustomMsg> wxCustomMsgs = selectList(customMsgEntityWrapper);
        return wxCustomMsgs;
    }

    /**
     * 查询客服消息的关键字回复
     *
     * @param wxCustomMsgTxt
     * @return
     */
    @Override
    public  Page<Map<String, String>> queryKey(WxCustomMsgTxt wxCustomMsgTxt) {
        Page<Map<String, String>> page = new Page<>();
        page.setSize(wxCustomMsgTxt.getPageSize());
        page.setCurrent(wxCustomMsgTxt.getCurrentPage());
        PageHelper.setPagination(page);
        List<Map<String, String>> records = wxCustomMsgTxtMapper.selectByPage(wxCustomMsgTxt);
        page.setRecords(records);
        return page;
    }

    /**
     * 预览发送客服消息
     *
     * @param wxCustomMsgExes
     */
    @Override
    public void send(List<WxCustomMsgEx> wxCustomMsgExes) {
        wxCustomMsgExes.forEach(wxCustomMsgEx -> {
            String replyContent = getReplyContent(wxCustomMsgEx);
            JSONObject params = JSON.parseObject(replyContent);
            params.put("touser", wxCustomMsgEx.getOpenid());
            params.put("appid", wxCustomMsgEx.getAppid());
            wxMessageService.sendCustom(params);
        });
    }

    /**
     * 发送主动推送的客服消息
     *
     * @param appid
     */
    @Override
    public void sendPush(String appid) {
        EntityWrapper<WxCustomMsg> customMsgEntityWrapper = new EntityWrapper<>();
        customMsgEntityWrapper.eq("APPID", appid).
                eq("ACCEPT_TYPE", WxCustomMsgEx.ACCEPTTYPE.push.name());
        WxCustomMsg wxCustomMsg = selectOne(customMsgEntityWrapper);
        if(null != wxCustomMsg && StringUtils.isNotEmpty(wxCustomMsg.getReplyContent())){
            wxMpSendRecordService.send(appid);
        }
    }

    /**
     * 发送关注以后的客服消息
     *
     * @param wxdelayTask
     */
    @Override
    public void sendSubscribeDelay(WxdelayTask wxdelayTask) {
        String touser = wxdelayTask.getBusinessId();
        JSONObject jsonObject = JSON.parseObject(wxdelayTask.getBusinessData());
        String appid = jsonObject.getString("appid");
        EntityWrapper<WxCustomMsg> customMsgEntityWrapper = new EntityWrapper<>();
        customMsgEntityWrapper.eq("APPID", appid).
                eq("ACCEPT_TYPE", WxCustomMsgEx.ACCEPTTYPE.delay.name());
       WxCustomMsg  wxCustomMsg = selectOne(customMsgEntityWrapper);
        if(null != wxCustomMsg && StringUtils.isNotEmpty(wxCustomMsg.getReplyContent())){
            String replyContent = wxCustomMsg.getReplyContent();
            JSONObject params = JSON.parseObject(replyContent);
            params.put("touser", touser);
            params.put("appid", appid);
            wxMessageService.sendCustom(params);
        }
    }
}

