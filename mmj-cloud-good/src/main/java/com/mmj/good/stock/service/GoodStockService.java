package com.mmj.good.stock.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.good.model.GoodCombination;
import com.mmj.good.model.GoodSale;
import com.mmj.good.stock.model.GoodStock;

import java.util.List;

/**
 * <p>
 * 库存记录表 服务类
 * </p>
 *
 * @author shenfuding
 * @since 2019-08-08
 */
public interface GoodStockService extends IService<GoodStock> {

    void occupyToCache(List<GoodStock> goodStocks);

    void relieveToCache(List<GoodStock> goodStocks);

    void deductToCache(List<GoodStock> goodStocks);

    void rollbackToCache(List<GoodStock> goodStocks);

    void timeOutToCache(List<GoodStock> goodStocks);

    void refreshOccupy(List<GoodSale> goodSales);

    void refreshOccupy(String goodSku, Integer goodNum);

    void refreshZhOccupy(List<GoodCombination> goodCombinations);

    void refreshZhOccupy(String goodSku, Integer goodNum);

    void initToCache(List<GoodStock> goodStocks);

    void cleanExpire();

    boolean checkOccupyTime(String businessId);

    void updateBatch(List<GoodStock> goodStocks);

    void asyncStock();

    void asyncStock(String flag);

}
