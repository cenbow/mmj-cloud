package com.mmj.notice.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.mmj.notice.model.WxCustomMsg;
import com.mmj.notice.model.WxCustomMsgEx;
import com.mmj.notice.model.WxCustomMsgTxt;
import com.mmj.notice.model.WxdelayTask;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 客服消息配置 服务类
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-27
 */
public interface WxCustomMsgService extends IService<WxCustomMsg> {

    /**
     * 客服消息配置保存
     * @param wxCustomMsgExes
     * @return
     */
    void save(List<WxCustomMsgEx> wxCustomMsgExes);

    /**
     * 客服消息查询,不包含关键字查询
     * @param appid
     * @return
     */
    List<WxCustomMsg> query(String appid);

    /**
     * 查询客服消息的关键字回复
     * @param wxCustomMsgTxt
     * @return
     */
    Page<Map<String, String>> queryKey(WxCustomMsgTxt wxCustomMsgTxt);

    /**
     * 预览发送客服消息
     * @param wxCustomMsgExes
     */
    void send(List<WxCustomMsgEx> wxCustomMsgExes);

    /**
     * 发送主动推送的客服消息
     * @param appid
     */
    void sendPush(String appid);

    /**
     * 发送关注以后的延迟客服消息
     * @param wxdelayTask
     */
    void sendSubscribeDelay(WxdelayTask wxdelayTask);
}
