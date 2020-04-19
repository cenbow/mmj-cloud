package com.mmj.good.mapper;

import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.good.model.GoodClass;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.mmj.good.model.GoodClassEx;

import java.util.List;

/**
 * <p>
 * 商品分类表 Mapper 接口
 * </p>
 *
 * @author lyf
 * @since 2019-06-03
 */
public interface GoodClassMapper extends BaseMapper<GoodClass> {

    /**
     * 分层级查询商品分类 一层
     * @param goodClassEx
     * @return
     */
    List<GoodClassEx> query(Page<GoodClassEx> page, GoodClassEx goodClassEx);

    /**
     * 分层级查询商品分类 二层
     * @param goodClassEx
     * @return
     */
    List<GoodClassEx> queryTwoGoodClassExes(Page<GoodClassEx> page, GoodClassEx goodClassEx);

    /**
     * 分层级查询商品分类 三层
     * @param goodClassEx
     * @return
     */
    List<GoodClassEx> queryThirdGoodClassExes(Page<GoodClassEx> page, GoodClassEx goodClassEx);
}
