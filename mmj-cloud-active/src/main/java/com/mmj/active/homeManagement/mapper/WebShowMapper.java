package com.mmj.active.homeManagement.mapper;

import com.mmj.active.homeManagement.model.GoodClassEx;
import com.mmj.active.homeManagement.model.WebShow;
import com.baomidou.mybatisplus.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 页面展示表 Mapper 接口
 * </p>
 *
 * @author dashu
 * @since 2019-06-05
 */
public interface WebShowMapper extends BaseMapper<WebShow> {

    List<GoodClassEx> selectGoodClass();
}
