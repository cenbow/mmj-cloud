package com.mmj.user.member.service;

import com.mmj.user.common.model.vo.OrderDetailVo;
import com.mmj.user.member.constant.SaveMoneySource;
import com.mmj.user.member.model.Vo.SaveMoneyVo;

/**
 * 会员节省金额服务
 */
public interface SaveMoneyService {
    void saveMoney(OrderDetailVo orderDetailVo);

    void save(SaveMoneySource source, Double money, Long userId, Long memberId, String orderNo);

}
