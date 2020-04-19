package com.mmj.active.grouplottery.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.active.grouplottery.model.LotteryRemind;

/**
 * <p>
 * 用户关注抽奖信息表 服务类
 * </p>
 *
 * @author shenfuding
 * @since 2019-08-26
 */
public interface LotteryRemindService extends IService<LotteryRemind> {


    void remind(Long userId, String type, String method);

    int getRemind(Long userId, Integer from);
}
