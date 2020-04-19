package com.mmj.active.grouplottery.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.active.grouplottery.model.LotteryBannerConf;

/**
 * <p>
 * 抽奖横幅配置表 服务类
 * </p>
 *
 * @author zhangyicao
 * @since 2019-07-04
 */
public interface LotteryBannerConfService extends IService<LotteryBannerConf> {

    public void updateBanner(LotteryBannerConf lotteryBannerConf);
}
