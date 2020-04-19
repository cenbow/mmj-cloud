package com.mmj.order.common.feign;

import com.mmj.common.interceptor.FeignRequestInterceptor;
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
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "mmj-cloud-good", fallbackFactory = GoodFallbackFactory.class, configuration = FeignRequestInterceptor.class)
public interface GoodFeignClient {

    @RequestMapping(value = "/goodWarehouse/queryWarehouseNameBySku/{goodSku}", method = RequestMethod.POST)
    public @ResponseBody
    ReturnData<List<String>> queryWarehouseNameBySku(@PathVariable("goodSku") String goodSku);

    @RequestMapping(value = "/goodWarehouse/updateBatchById", method = RequestMethod.POST)
    public @ResponseBody
    ReturnData updateBatchById(@RequestBody List<GoodWarehouse> goodWarehouses);

    @RequestMapping(value = "/goodSale/queryList", method = RequestMethod.POST)
    public @ResponseBody
    ReturnData<Object> queryList(@RequestBody GoodSale goodSale);


    @RequestMapping(value = "/goodCombination/queryList", method = RequestMethod.POST)
    public @ResponseBody
    ReturnData<List<GoodCombination>> queryList(@RequestBody GoodCombination GoodCombination);

    @RequestMapping(value = "/goodClass/queryLevel", method = RequestMethod.POST)
    public @ResponseBody
    ReturnData<Map<String, Object>> queryLevel(@RequestBody GoodClassEx goodClassEx);


    @RequestMapping(value = "/goodInfo/getById/{id}", method = RequestMethod.POST)
    @ResponseBody
    GoodInfo getById(@PathVariable("id") Integer id);


    /**
     * 商品销售资料分组查询
     */
    @RequestMapping(value = "/goodSale/queryGroupByInfo", method = RequestMethod.POST)
    public ReturnData<Object> queryGroupByInfo(@RequestBody GoodSaleEx goodSaleEx);

    /**
     * 订单查询商品
     *
     * @param goodSku
     * @return
     */
    @RequestMapping(value = "/goodSale/queryOrderGood", method = RequestMethod.POST)
    ReturnData<List<GoodOrder>> queryOrderGood(@RequestBody List<String> goodSku);

    /**
     * 商品规格表列表查询
     *
     * @param goodSku
     * @return
     */
    @RequestMapping(value = "/goodModel/queryOrderList", method = RequestMethod.POST)
    ReturnData<List<GoodModelEx>> goodModelQueryList(@RequestBody List<String> goodSku);

    /**
     * sku仓库列表查询
     *
     * @param goodSku
     * @return
     */
    @RequestMapping(value = "/goodWarehouse/queryOrderList", method = RequestMethod.POST)
    ReturnData<List<com.mmj.common.model.good.GoodWarehouse>> goodWarehouseQueryList(@RequestBody List<String> goodSku);

    /**
     * 修改商品库存
     */
    @RequestMapping(value = "/async/updateGoodNum", method = RequestMethod.POST)
    public ReturnData updateGoodNum(@RequestBody List<GoodSale> goodSales);

    /**
     * 查询SKU库存使用情况
     *
     * @param goodStock
     * @return
     */
    @RequestMapping(value = "/stock/queryList", method = RequestMethod.POST)
    ReturnData<List<GoodStock>> queryList(@RequestBody GoodStock goodStock);

    /**
     * 占用SKU库存记录
     *
     * @param goodStocks
     * @return
     */
    @RequestMapping(value = "/stock/occupy", method = RequestMethod.POST)
    ReturnData occupy(@RequestBody List<GoodStock> goodStocks);

    /**
     * 校验是否占用库存
     *
     * @param businessId
     * @return
     */
    @RequestMapping(value = "/stock/checkOccupyTime/{businessId}", method = RequestMethod.POST)
    ReturnData<Boolean> checkOccupyTime(@PathVariable("businessId") String businessId);

    /**
     * 扣减SKU库存记录
     *
     * @param goodStocks
     * @return
     */
    @RequestMapping(value = "/stock/deduct", method = RequestMethod.POST)
    ReturnData deduct(@RequestBody List<GoodStock> goodStocks);

    /**
     * 释放SKU库存记录
     *
     * @param goodStocks
     * @return
     */
    @RequestMapping(value = "/stock/relieve", method = RequestMethod.POST)
    ReturnData relieve(@RequestBody List<GoodStock> goodStocks);

    /**
     * 回退SKU库存记录
     *
     * @param goodStocks
     * @return
     */
    @RequestMapping(value = "/stock/rollback", method = RequestMethod.POST)
    ReturnData rollback(@RequestBody List<GoodStock> goodStocks);

}
