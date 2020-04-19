package com.mmj.good.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.mmj.good.model.GoodCombination;
import com.mmj.good.model.GoodCombinationEx;

import java.util.List;

/**
 * <p>
 * 组合商品表 Mapper 接口
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-12
 */
public interface GoodCombinationMapper extends BaseMapper<GoodCombination> {

    List<GoodCombinationEx> queryList(String goodSpu);
}
