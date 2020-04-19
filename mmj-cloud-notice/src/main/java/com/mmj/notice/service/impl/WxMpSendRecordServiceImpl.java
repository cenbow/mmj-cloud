package com.mmj.notice.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.notice.mapper.WxMpSendRecordMapper;
import com.mmj.notice.model.WxCustomMsg;
import com.mmj.notice.model.WxCustomMsgEx;
import com.mmj.notice.model.WxMpSendRecord;
import com.mmj.notice.service.CustomCallBack;
import com.mmj.notice.service.WxCustomMsgService;
import com.mmj.notice.service.WxMessageService;
import com.mmj.notice.service.WxMpSendRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 公众号主动推送消息发送记录表 服务实现类
 * </p>
 *
 * @author shenfuding
 * @since 2019-08-10
 */
@Service
public class WxMpSendRecordServiceImpl extends ServiceImpl<WxMpSendRecordMapper, WxMpSendRecord> implements WxMpSendRecordService {

    @Autowired
    WxCustomMsgService wxCustomMsgService;

    @Autowired
    WxMessageService wxMessageService;

    @Autowired
    WxMpSendRecordService wxMpSendRecordService;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    /**
     * 查询公众号信息的客服消息群发记录
     *
     * @param page
     * @return
     */
    @Override
    public Page<WxMpSendRecord> query(Page page) {
        EntityWrapper<WxMpSendRecord> wxMpSendRecordEntityWrapper = new EntityWrapper<>();
        wxMpSendRecordEntityWrapper.orderBy("CREATE_TIME desc");
        page = selectPage(page,wxMpSendRecordEntityWrapper);
        return page;
    }

    /**
     * 再次发送群发的记录
     *
     * @param appid
     */
    @Override
    public void send(String appid) {
        EntityWrapper<WxCustomMsg> customMsgEntityWrapper = new EntityWrapper<>();
        customMsgEntityWrapper.eq("APPID", appid).eq("ACCEPT_TYPE", WxCustomMsgEx.ACCEPTTYPE.push.name());
        WxCustomMsg wxCustomMsg = wxCustomMsgService.selectOne(customMsgEntityWrapper);
        WxMpSendRecord wxMpSendRecord = new WxMpSendRecord();
        wxMpSendRecord.setState("in_progress");
        EntityWrapper<WxMpSendRecord> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("APP_ID", appid);
        update(wxMpSendRecord, entityWrapper);
        String replyContent = wxCustomMsg.getReplyContent();
        wxMessageService.sendCustomCallBack(JSON.parseObject(replyContent), appid, new CustomCallBack(){
            @Override
            public void success(JSONObject message, String appid) {
                if("ok".equals(message.getString("errmsg")) && 0 == message.getInteger("errcode")){ //这种情况加是发送成功了的
                    redisTemplate.opsForValue().increment("WxMpSendRecords_" + appid, 1);
                }else {
                    redisTemplate.opsForValue().increment("WxMpSendRecordf_" + appid, 1);
                }
            }
            @Override
            public void complete(String appid) {
                Long increments = redisTemplate.opsForValue().increment("WxMpSendRecords_" + appid, 1);
                Long incrementf = redisTemplate.opsForValue().increment("WxMpSendRecordf_" + appid, 1);
                Page page = new Page(1,1);
                Page<WxMpSendRecord> query = query(page);
                WxMpSendRecord wxMpSendRecord = query.getRecords().get(0);
                wxMpSendRecord.setState("completed");
                wxMpSendRecord.setSendNum(increments.intValue() -1);
                wxMpSendRecord.setFailNum(incrementf.intValue() -1);
                updateById(wxMpSendRecord);
                redisTemplate.delete("WxMpSendRecords_" + appid);
                redisTemplate.delete("WxMpSendRecordf_" + appid);
            }
        });
    }
}
