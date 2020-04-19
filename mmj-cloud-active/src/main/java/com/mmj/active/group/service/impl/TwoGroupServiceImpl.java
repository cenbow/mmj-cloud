package com.mmj.active.group.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.google.common.collect.Lists;
import com.mmj.active.common.constants.ActiveGoodsConstants;
import com.mmj.active.common.feigin.GoodFeignClient;
import com.mmj.active.common.model.ActiveGood;
import com.mmj.active.common.model.GoodInfoBaseQueryEx;
import com.mmj.active.common.model.GoodSale;
import com.mmj.active.common.model.GoodSaleEx;
import com.mmj.active.common.service.ActiveGoodService;
import com.mmj.active.group.model.dto.BossTwoGroupListDto;
import com.mmj.active.group.model.vo.BossTwoGroupGoodsAddVo;
import com.mmj.active.group.model.vo.BossTwoGroupGoodsEditVo;
import com.mmj.active.group.model.vo.BossTwoGroupGoodsVo;
import com.mmj.active.group.model.vo.BossTwoGroupListVo;
import com.mmj.active.group.service.TwoGroupService;
import com.mmj.common.model.ReturnData;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.common.utils.PriceConversion;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @description: 二人团
 * @auther: KK
 * @date: 2019/7/22
 */
@Service
public class TwoGroupServiceImpl implements TwoGroupService {
    @Autowired
    private ActiveGoodService activeGoodService;
    @Autowired
    private GoodFeignClient goodFeignClient;

    /**
     * 判断二人团是否存在该商品
     *
     * @param sku
     * @return
     */
    private boolean hasGoods(String sku) {
        Assert.notNull(sku, "缺少商品SKU");
        ActiveGood queryActiveGood = new ActiveGood();
        queryActiveGood.setActiveType(ActiveGoodsConstants.ActiveType.TUAN);
        queryActiveGood.setGoodSku(sku);
        EntityWrapper<ActiveGood> activeGoodEntityWrapper = new EntityWrapper<>(queryActiveGood);
        return activeGoodService.selectCount(activeGoodEntityWrapper) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addGoods(List<BossTwoGroupGoodsAddVo> twoGroupGoodsVos) {
        Assert.isTrue(Objects.nonNull(twoGroupGoodsVos) && twoGroupGoodsVos.size() > 0, "缺少商品信息");
        List<ActiveGood> activeGoods = Lists.newArrayListWithCapacity(twoGroupGoodsVos.size());
        //根据sku去重
        List<BossTwoGroupGoodsAddVo> distinctByGoodSku = twoGroupGoodsVos.stream().collect(
                Collectors.collectingAndThen(Collectors.toCollection(()
                                -> new TreeSet<>(Comparator.comparing(BossTwoGroupGoodsAddVo::getGoodSku))),
                        ga -> new ArrayList(ga)));
        distinctByGoodSku.forEach(twoGroupGoodsVo -> {
            if (!hasGoods(twoGroupGoodsVo.getGoodSku())) {
                ActiveGood activeGood = new ActiveGood();
                BeanUtils.copyProperties(twoGroupGoodsVo, activeGood);
                activeGood.setBasePrice(Integer.parseInt(twoGroupGoodsVo.getBasePrice()));
                activeGood.setActivePrice(PriceConversion.stringToInt(twoGroupGoodsVo.getActivePrice()));
                activeGood.setMemberPrice(PriceConversion.stringToInt(twoGroupGoodsVo.getMemberPrice()));
                activeGood.setActiveType(ActiveGoodsConstants.ActiveType.TUAN);
                activeGood.setGoodStatus("1");//上架
                activeGoods.add(activeGood);
            }
        });
        if (activeGoods.size() > 0) {
            boolean result = activeGoodService.insertBatch(activeGoods);
            Assert.isTrue(result, "新增失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editGoods(List<BossTwoGroupGoodsEditVo> twoGroupGoodsEditVos) {
        Assert.isTrue(Objects.nonNull(twoGroupGoodsEditVos) && twoGroupGoodsEditVos.size() > 0, "缺少商品信息");
        List<ActiveGood> activeGoods = Lists.newArrayListWithCapacity(twoGroupGoodsEditVos.size());
        twoGroupGoodsEditVos.forEach(twoGroupGoodsVo -> {
            ActiveGood activeGood = new ActiveGood();
            BeanUtils.copyProperties(twoGroupGoodsVo, activeGood);
            if (StringUtils.isNotBlank(twoGroupGoodsVo.getActivePrice())) {
                activeGood.setActivePrice(PriceConversion.stringToInt(twoGroupGoodsVo.getActivePrice()));
            }
            activeGoods.add(activeGood);
        });
        boolean result = activeGoodService.updateBatchById(activeGoods);
        Assert.isTrue(result, "编辑失败");
    }

    @Override
    public void editGoodsStatus(List<BossTwoGroupGoodsEditVo> bossTwoGroupGoodsEditVos) {
        Assert.isTrue(Objects.nonNull(bossTwoGroupGoodsEditVos) && bossTwoGroupGoodsEditVos.size() > 0, "缺少商品信息");
        List<ActiveGood> activeGoods = Lists.newArrayListWithCapacity(bossTwoGroupGoodsEditVos.size());
        bossTwoGroupGoodsEditVos.forEach(bossTwoGroupGoodsEditVo -> {
            ActiveGood activeGood = new ActiveGood();
            activeGood.setMapperyId(bossTwoGroupGoodsEditVo.getMapperyId());
            activeGood.setGoodStatus(bossTwoGroupGoodsEditVo.getGoodStatus().toString());
            activeGoods.add(activeGood);
        });
        boolean result = activeGoodService.updateBatchById(activeGoods);
        Assert.isTrue(result, "操作失败");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeGoods(BossTwoGroupGoodsVo bossTwoGroupGoodsVo) {
        boolean result = activeGoodService.deleteBatchIds(bossTwoGroupGoodsVo.getMapperyIds());
        Assert.isTrue(result, "删除失败");
    }

    @Override
    public Page<BossTwoGroupListDto> twoGroupGoods(BossTwoGroupListVo bossTwoGroupListVo) {
        ActiveGood queryActiveGood = new ActiveGood();
        queryActiveGood.setActiveType(ActiveGoodsConstants.ActiveType.TUAN);
        EntityWrapper<ActiveGood> entityWrapper = new EntityWrapper<>(queryActiveGood);
        if (StringUtils.isNotBlank(bossTwoGroupListVo.getGoodName())) {
            entityWrapper.like("GOOD_NAME", bossTwoGroupListVo.getGoodName());
        }
        if (StringUtils.isNotBlank(bossTwoGroupListVo.getGoodSpu())) {
            entityWrapper.like("GOOD_SPU", bossTwoGroupListVo.getGoodSpu());
        }
        Page<ActiveGood> results = new Page(bossTwoGroupListVo.getCurrentPage(), bossTwoGroupListVo.getPageSize(), "MAPPERY_ID", false);
        results = activeGoodService.selectPage(results, entityWrapper);
        List<BossTwoGroupListDto> bossTwoGroupListDtoList = Lists.newArrayListWithCapacity(results.getRecords().size());

        List<String> goodsSkuList = Lists.newArrayListWithCapacity(bossTwoGroupListDtoList.size());
        results.getRecords().forEach(bossTwoGroupListDto -> {
            if (!goodsSkuList.contains(bossTwoGroupListDto.getGoodSku())) {
                goodsSkuList.add(bossTwoGroupListDto.getGoodSku());
            }
        });
        List<GoodSale> goodSales = Lists.newArrayList();
        if (goodsSkuList.size() > 0) {
            GoodSaleEx goodSaleEx = new GoodSaleEx();
            goodSaleEx.setGoodSkus(goodsSkuList);
            ReturnData<List<GoodSale>> returnData = goodFeignClient.queryList(goodSaleEx);
            Assert.isTrue(SecurityConstants.SUCCESS_CODE == returnData.getCode().intValue(), returnData.getDesc());
            if (Objects.nonNull(returnData.getData())) {
                goodSales.addAll(returnData.getData());
            }

        }
        results.getRecords().stream().forEach(activeGood -> {
            BossTwoGroupListDto bossTwoGroupListDto = new BossTwoGroupListDto();
            BeanUtils.copyProperties(activeGood, bossTwoGroupListDto);
            bossTwoGroupListDto.setMapperyId(activeGood.getMapperyId().toString());
            bossTwoGroupListDto.setGoodStatus(Integer.parseInt(activeGood.getGoodStatus()));
            bossTwoGroupListDto.setBasePrice(PriceConversion.intToString(activeGood.getBasePrice()));
            bossTwoGroupListDto.setActivePrice(PriceConversion.intToString(activeGood.getActivePrice()));
            bossTwoGroupListDto.setMemberPrice(PriceConversion.intToString(activeGood.getMemberPrice()));
            GoodSale goodSale = goodSales.stream().filter(sale -> sale.getGoodSku().equals(activeGood.getGoodSku())).findFirst().orElse(null);
            if (Objects.nonNull(goodSale)) {
                bossTwoGroupListDto.setShopPrice(PriceConversion.intToString(goodSale.getShopPrice()));
                bossTwoGroupListDto.setActiveStore(goodSale.getGoodNum());
            }
            bossTwoGroupListDtoList.add(bossTwoGroupListDto);
        });
        Page<BossTwoGroupListDto> resultPage = new Page<>();
        resultPage.setTotal(results.getTotal());
        resultPage.setCurrent(results.getCurrent());
        resultPage.setSize(results.getSize());
        resultPage.setCondition(results.getCondition());
        resultPage.setRecords(bossTwoGroupListDtoList);
        return resultPage;
    }
}
