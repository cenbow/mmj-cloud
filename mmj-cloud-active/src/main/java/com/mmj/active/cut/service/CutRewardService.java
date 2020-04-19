package com.mmj.active.cut.service;

import com.mmj.active.cut.model.CutReward;
import com.baomidou.mybatisplus.service.IService;
import com.mmj.active.cut.model.dto.FirstRateRewardDto;

import java.math.BigDecimal;

/**
 * <p>
 * 用户砍价奖励表 服务类
 * </p>
 *
 * @author KK
 * @since 2019-07-26
 */
public interface CutRewardService extends IService<CutReward> {

    /**
     * 获取用户帮砍时获取的首砍提升比例
     *
     * @param userId
     * @return
     */
    FirstRateRewardDto getFirstRateReward(Long userId);

    /**
     * 获取最大的首奖
     *
     * @param userId
     * @return
     */
    CutReward getMaxCutReward(Long userId);

    /**
     * 编辑使用状态
     *
     * @param rewardId
     * @param cutNo
     */
    void editUseFlagById(Integer rewardId, String cutNo);

    /**
     * 新增帮砍时获取砍价奖励信息
     *
     * @param userId
     * @param cutId
     * @param cutNo
     * @return
     */
    CutReward addFirstCutReward(Long userId, Integer cutId, String cutNo);

    /**
     * 新增砍价订单支付成功时奖励
     *
     * @param userId
     * @param cutId
     * @param cutNo
     * @return
     */
    CutReward addCutPaySuccessReward(Long userId, Integer cutId, String cutNo);
}
