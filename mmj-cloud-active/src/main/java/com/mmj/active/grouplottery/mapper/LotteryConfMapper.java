package com.mmj.active.grouplottery.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.active.grouplottery.model.LotteryConf;
import com.mmj.active.grouplottery.model.vo.LotteryConfSearchVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 抽奖配置表 Mapper 接口
 * </p>
 *
 * @author lyf
 * @since 2019-06-05
 */
@Repository
public interface LotteryConfMapper extends BaseMapper<LotteryConf> {

    List<Map<String, Object>> getLotteryGoodsNow(@Param("lotteryStart") Integer lotteryStart, @Param("lotterySize") Integer lotterySize);

    int getLotteryGoodsNowCnt();

    List<Integer> selectActiveGood(@Param("goodsName") String goodsName);

    List<LotteryConf> list(@Param("page") Page<LotteryConf> page,@Param("searchVo") LotteryConfSearchVo searchVo,
                           @Param("ids") List<Integer> ids);
}
