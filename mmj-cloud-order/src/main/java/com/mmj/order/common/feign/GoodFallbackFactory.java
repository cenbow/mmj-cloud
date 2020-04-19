package com.mmj.order.common.feign;

import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.GoodStock;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.good.GoodModelEx;
import com.mmj.common.model.good.GoodOrder;
import com.mmj.order.common.model.dto.GoodInfo;
import com.mmj.order.common.model.dto.GoodClassEx;
import com.mmj.order.common.model.dto.GoodCombination;
import com.mmj.order.common.model.dto.GoodSale;
import com.mmj.order.common.model.dto.GoodWarehouse;
import com.mmj.order.common.model.vo.GoodSaleEx;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@Component
public class GoodFallbackFactory implements FallbackFactory<GoodFeignClient> {

    private final Logger logger = LoggerFactory.getLogger(GoodFallbackFactory.class);

    @Override
    public GoodFeignClient create(Throwable cause) {
        logger.info("GoodFallbackFactory error message is {}", cause.getMessage());
        return new GoodFeignClient() {
            @Override
            public ReturnData<List<String>> queryWarehouseNameBySku(String goodSku) {
                throw new BusinessException("调用查询仓库Id接口报错," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData updateBatchById(List<GoodWarehouse> goodWarehouses) {
                throw new BusinessException("更新SKU库存报错!," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<Object> queryList(GoodSale goodSale) {
                throw new BusinessException("查询单个商品sku报错!," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<List<GoodCombination>> queryList(@RequestBody GoodCombination GoodCombination) {
                throw new BusinessException("查询组合商品报错!," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<Map<String, Object>> queryLevel(GoodClassEx goodClassEx) {
                throw new BusinessException("查询商品分类报错!," + cause.getMessage(), 500);
            }

            @Override
            public GoodInfo getById(Integer id) {
                throw new BusinessException("获取商品信息报错!," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<Object> queryGroupByInfo(GoodSaleEx goodSaleEx) {
                throw new BusinessException("获取所有商品sku接口异常:" + cause.getMessage(), 500);
            }

            @Override
            public ReturnData updateGoodNum(List<GoodSale> goodSales) {
                throw new BusinessException("修改商品库存接口异常:" + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<List<GoodOrder>> queryOrderGood(List<String> goodSku) {
                throw new BusinessException("订单查询商品信息错误:" + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<List<GoodModelEx>> goodModelQueryList(List<String> goodSku) {
                throw new BusinessException("订单查询商品规格错误:" + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<List<com.mmj.common.model.good.GoodWarehouse>> goodWarehouseQueryList(List<String> goodSku) {
                throw new BusinessException("sku仓库列表查询错误:" + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<List<GoodStock>> queryList(GoodStock goodStock) {
                throw new BusinessException("查询SKU库存使用情况错误:" + cause.getMessage(), 500);
            }

            @Override
            public ReturnData occupy(List<GoodStock> goodStocks) {
                throw new BusinessException("占用SKU库存记录错误:" + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<Boolean> checkOccupyTime(String businessId) {
                throw new BusinessException("校验是否占用库存错误:" + cause.getMessage(), 500);
            }

            @Override
            public ReturnData deduct(List<GoodStock> goodStocks) {
                throw new BusinessException("扣减SKU库存记录错误:" + cause.getMessage(), 500);
            }

            @Override
            public ReturnData relieve(List<GoodStock> goodStocks) {
                throw new BusinessException("释放SKU库存记录错误:" + cause.getMessage(), 500);
            }

            @Override
            public ReturnData rollback(List<GoodStock> goodStocks) {
                throw new BusinessException("回退SKU库存记录错误:" + cause.getMessage(), 500);
            }
        };

    }
}
