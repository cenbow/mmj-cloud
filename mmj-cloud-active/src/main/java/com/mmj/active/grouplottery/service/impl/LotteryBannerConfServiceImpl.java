package com.mmj.active.grouplottery.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.active.grouplottery.mapper.LotteryBannerConfMapper;
import com.mmj.active.grouplottery.model.LotteryBannerConf;
import com.mmj.active.grouplottery.service.LotteryBannerConfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * <p>
 * 抽奖横幅配置表 服务实现类
 * </p>
 *
 * @author zhangyicao
 * @since 2019-07-04
 */
@Service
public class LotteryBannerConfServiceImpl extends ServiceImpl<LotteryBannerConfMapper, LotteryBannerConf> implements LotteryBannerConfService {

    @Autowired
    private LotteryBannerConfMapper lotteryBannerConfMapper;

    @Override
    @Transactional
    public void updateBanner(LotteryBannerConf lotteryBannerConf) {
        Assert.notNull(lotteryBannerConf,"配置不能为空");
        lotteryBannerConfMapper.delAll();
        lotteryBannerConfMapper.insertSelective(lotteryBannerConf);
    }
}
