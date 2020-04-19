package com.mmj.notice.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.notice.model.NoticeDelaySms;

/**
 * <p>
 * 短信延迟发送表 服务类
 * </p>
 *
 * @author shenfuding
 * @since 2019-08-30
 */
public interface DelaySmsService extends IService<NoticeDelaySms> {

    void sendSMS();
}
