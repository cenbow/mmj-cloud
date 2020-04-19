package com.mmj.user.member.service;


import com.baomidou.mybatisplus.service.IService;
import com.mmj.user.member.model.UserMemberSend;
import com.mmj.user.member.model.Vo.PayIsBuyGiveVo;
import com.mmj.user.member.model.Vo.PayRecordQualificationsVo;

import java.util.Map;

/**
 * <p>
 * 买送 服务类
 * </p>
 *
 * @author zhangyicao
 * @since 2019-07-11
 */
public interface UserMemberSendService extends IService<UserMemberSend> {

    /**
     * 回去买送结束时间
     *
     * @return
     */
    Map<String, Object> getActivitySurplusTime();

    /**
     * 写入买送资格
     *
     * @param orderNo
     * @param orderAmount
     * @param createBy
     * @return
     */
    boolean saveBuyGive(String orderNo, Double orderAmount, Long createBy);

    /**
     * 取消会员买送资格
     *
     * @param userId
     * @return
     */
    boolean editBuyGice(Long userId);

    /**
     * 根据订单号获取是否有买送资格
     *
     * @param orderNo
     * @return
     */
    boolean getOrderIdBuyGive(String orderNo, Long userId);

    /**
     * 支付时获取当单是否享受买送
     *
     * @param payAmount
     * @param userid
     * @param orderNo
     * @param orderType
     * @param goodsAmount
     * @return
     */
    boolean getPayIsBuyGive(double payAmount, Long userid, String orderNo, Integer orderType, double goodsAmount);

    /**
     * 支付回调写入买送资格
     *
     * @param prq
     */
    void recordQualifications(PayRecordQualificationsVo prq);

    /**
     * 下单成为会员
     *
     * @param userId
     * @param orders
     */
    void saveUserMember(Long userId, String orderNo, Integer orderType, String appId, String openId, double orderAmount);

    /**
     * 微服务下单查询是否享受买送
     *
     * @param payIsBuyGiveVo
     * @return
     */
    boolean getPayIsBuyGiveClient(PayIsBuyGiveVo payIsBuyGiveVo);

}
