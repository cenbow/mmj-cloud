package com.mmj.notice.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.mmj.notice.model.WxMpSendRecord;

/**
 * <p>
 * 公众号主动推送消息发送记录表 服务类
 * </p>
 *
 * @author shenfuding
 * @since 2019-08-10
 */
public interface WxMpSendRecordService extends IService<WxMpSendRecord> {


    /**
     * 查询公众号信息的客服消息群发记录
     * @param page
     * @return
     */
    Page<WxMpSendRecord> query(Page page);

    /**
     * 再次发送群发的记录
     * @param appid
     */
    void send(String appid);
}
