package com.mmj.active.grouplottery.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.mmj.active.common.model.UserActive;
import com.mmj.active.grouplottery.model.LotteryConf;
import com.mmj.active.grouplottery.model.vo.LotteryConfSearchVo;

import java.util.Map;

/**
 * <p>
 * 抽奖配置表 服务类
 * </p>
 *
 * @author lyf
 * @since 2019-06-05
 */
public interface LotteryConfService extends IService<LotteryConf> {

    Integer saveVo(LotteryConf confVo);

    void deleteByLotteryId(Integer id);

    Integer updateVo(LotteryConf confVo);

    Integer updateOpenDetail(LotteryConf conf);

    Page<LotteryConf> list(LotteryConfSearchVo entity);

    LotteryConf getLotteryById(Integer id);

    Map<String,Object> getLotteryGoodsNow(Integer page,Integer size);

    Map<String, Object> getLotteryActivityWinTips(Integer page,Integer size);

    String drawLottery(LotteryConf conf);

    void autoDrawLottery();

    Map<String, Object> getLotteryGroup(String groupNo);
}
