package com.mmj.active.grouplottery.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.mmj.active.grouplottery.model.LotteryBannerConf;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 抽奖横幅配置表 Mapper 接口
 * </p>
 *
 * @author zhangyicao
 * @since 2019-07-04
 */
@Repository
public interface LotteryBannerConfMapper extends BaseMapper<LotteryBannerConf> {

    int delAll();

    int insertSelective(LotteryBannerConf conf);
}
