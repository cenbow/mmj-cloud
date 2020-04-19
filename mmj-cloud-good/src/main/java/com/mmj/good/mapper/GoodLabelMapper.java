package com.mmj.good.mapper;

import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.good.model.GoodLabel;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.mmj.good.model.GoodLabelEx;

import java.util.List;

/**
 * <p>
 * 商品标签表 Mapper 接口
 * </p>
 *
 * @author lyf
 * @since 2019-06-03
 */
public interface GoodLabelMapper extends BaseMapper<GoodLabel> {

    List<GoodLabelEx> queryList(Page<GoodLabelEx> page, GoodLabelEx goodLabelEx);

}
