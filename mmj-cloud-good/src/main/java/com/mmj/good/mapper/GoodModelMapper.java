package com.mmj.good.mapper;

import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.good.model.GoodModel;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.mmj.good.model.GoodModelEx;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 商品规格表 Mapper 接口
 * </p>
 *
 * @author lyf
 * @since 2019-06-03
 */
public interface GoodModelMapper extends BaseMapper<GoodModel> {

    List<GoodModelEx> queryList(Page<GoodModelEx> page, GoodModel goodModel);


    List<GoodModelEx> queryListBySku(@Param("goodSkus") List<String> goodSkus);
}
