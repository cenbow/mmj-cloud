package com.mmj.active.common.feigin;

import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.active.common.model.*;
import com.mmj.active.common.model.dto.CutGoodDto;
import com.mmj.active.common.model.vo.CutGoodVo;
import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.GoodStock;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.ThreeSaleTennerOrder;
import com.mmj.common.properties.SecurityConstants;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
            public ReturnData batchVerifyGoodSpu(List<String> spuList) {
                throw new BusinessException("调用商品SPU验证接口报错," + cause.getMessage(), 500);
            }

            @Override
            public GoodInfo getById(Integer id) {
                throw new BusinessException("调用查询商品接口报错," + cause.getMessage(), 500);
            }

            @Override
            public String queryGoodImgUrl(Integer id) {
                throw new BusinessException("调用查询商品封面图片接口报错," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<Object> queryGroupByInfo(CutGoodVo goodVo) {
                return new ReturnData<>(SecurityConstants.EXCEPTION_CODE, "调用商品信息失败");
            }

            @Override
            public ReturnData<List<GoodClass>> queryGoodClassDetail(@RequestBody GoodClassBase goodClassBase){
                return new ReturnData<>(SecurityConstants.EXCEPTION_CODE, "调用商品分类查询失败");
            }

            @Override
            public ReturnData<Object> queryBaseList(@RequestBody GoodInfoBaseQueryEx entityEx){
                return new ReturnData<>(SecurityConstants.EXCEPTION_CODE, "调用商品基础资料列表查询失败");
            }

            @Override
            public ReturnData saveFile(@RequestBody List<GoodFile> goodFiles){
                return new ReturnData<>(SecurityConstants.EXCEPTION_CODE, "调用文件信息保存接口失败");
            }

            @Override
            public ReturnData<List<Map<String, Object>>> queryTopGood(){
                return new ReturnData<>(SecurityConstants.EXCEPTION_CODE, "调用查询销量前十商品接口失败");
            }

            @Override
            public ReturnData<List<GoodSale>> queryList(GoodSaleEx goodSaleEx) {
                return new ReturnData<>(SecurityConstants.EXCEPTION_CODE, "商品销售资料列表查询失败");
            }

            @Override
            public ReturnData<Page<GoodInfoEx>> searchGoods(@RequestBody String content){
                return new ReturnData<>(SecurityConstants.EXCEPTION_CODE, "调用搜索商品接口失败");
            }

            @Override
            public ReturnData<List<GoodInfo>> queryGood(@RequestBody GoodInfo goodInfo){
                return new ReturnData<>(SecurityConstants.EXCEPTION_CODE,"条件查询商品异常");
            }


            @Override
            public ReturnData<List<Integer>> queryGoodIdOrder(@RequestBody GoodInfoEx entityEx) {
                return new ReturnData<>(SecurityConstants.EXCEPTION_CODE, "调用查询id排序-专题拼团接口失败");
            }

            @Override
            public ReturnData<List<GoodNum>> queryGoodNum(List<Integer> goodIds) {
                return new ReturnData<>(SecurityConstants.EXCEPTION_CODE, "查询商品库存报错");
            }

            @Override
            public ReturnData<List<Integer>> threeSaleTennerOrder(ThreeSaleTennerOrder threeSaleTennerOrder) {
                return new ReturnData<>(SecurityConstants.EXCEPTION_CODE, "查询商品信息报错");
            }

            @Override
            public ReturnData occupy(@RequestBody List<GoodStock> goodStocks) {
                return new ReturnData<>(SecurityConstants.EXCEPTION_CODE, "占用SKU库存记录报错");
            }

            @Override
            public ReturnData deduct(@RequestBody List<GoodStock> goodStocks) {
                return new ReturnData<>(SecurityConstants.EXCEPTION_CODE, "扣减SKU库存记录报错");
            }

            @Override
            public ReturnData relieve(@RequestBody List<GoodStock> goodStocks) {
                return new ReturnData<>(SecurityConstants.EXCEPTION_CODE, "释放SKU库存记录报错");
            }

            @Override
            public ReturnData rollback(@RequestBody List<GoodStock> goodStocks) {
                return new ReturnData<>(SecurityConstants.EXCEPTION_CODE, "回退SKU库存记录报错");
            }
        };
    }

}
