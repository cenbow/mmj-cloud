package com.mmj.good.mapper;

import com.mmj.good.model.GoodOrder;
import com.mmj.good.model.GoodSale;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.mmj.good.model.GoodSaleEx;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 商品销售信息表 Mapper 接口
 * </p>
 *
 * @author lyf
 * @since 2019-06-03
 */
public interface GoodSaleMapper extends BaseMapper<GoodSale> {

    List<GoodSale> select(GoodSale goodSale);

    List<GoodSaleEx> queryGroupByInfo(GoodSaleEx goodSaleEx);

    Integer updateNum(@Param("num") Integer num, @Param("goodSku") String goodSku);

    List<GoodOrder> queryOrderGood(@Param("goodSkus") List<String> goodSkus);
}
