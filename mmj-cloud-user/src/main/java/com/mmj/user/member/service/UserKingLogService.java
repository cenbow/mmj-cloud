package com.mmj.user.member.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.common.model.order.OrdersMQDto;
import com.mmj.user.member.model.UserKingLog;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 买买金日志表 服务类
 * </p>
 *
 * @author zhangyicao
 * @since 2019-07-10
 */
public interface UserKingLogService extends IService<UserKingLog> {

    List<UserKingLog> getMyKingLog();

    Double getSumKingNum();

    Map<String, Object> getActCnt();

    void clickInsert(UserKingLog uLog);

    void actInsert(Long userId,String type);

    void addMMKing(Long userId, String orderNo);

    int orderKingProd(Map<String,Object> map);

    boolean degradeProces(String orderNo, Long userId);

    int getOweKingNum(String orderNo, Long userId);

    Integer procMMKing(OrdersMQDto dto);
}
