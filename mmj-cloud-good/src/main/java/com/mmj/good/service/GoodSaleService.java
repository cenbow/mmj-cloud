package com.mmj.good.service;

import com.mmj.good.model.GoodOrder;
import com.mmj.good.model.GoodSale;
import com.baomidou.mybatisplus.service.IService;
import com.mmj.good.model.GoodSaleEx;

import java.util.List;

/**
 * <p>
 * 商品销售信息表 服务类
 * </p>
 *
 * @author lyf
 * @since 2019-06-03
 */
public interface GoodSaleService extends IService<GoodSale> {

    void save(List<GoodSaleEx> goodSaleExes) throws Exception;

    List<GoodSale> select(GoodSale goodSale);

    List<GoodSaleEx> queryGroupByInfo(GoodSaleEx goodSaleEx);

    void updateGoodNum(List<GoodSale> goodSales);

    Integer updateNum(Integer num,  String goodSku);

    List<GoodOrder> queryOrderGood(List<String> goodSku);

    Integer sumCombNum(Integer goodId);

    Integer queryCombNum(String goodSku);

    Integer sumNum(Integer goodId);

    Integer queryNum(String goodSku);

    void synGoodsStock();
}
