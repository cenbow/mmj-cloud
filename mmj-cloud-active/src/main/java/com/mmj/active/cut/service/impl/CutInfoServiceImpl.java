package com.mmj.active.cut.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.google.common.collect.Lists;
import com.mmj.active.common.constants.ActiveGoodsConstants;
import com.mmj.active.common.feigin.GoodFeignClient;
import com.mmj.active.common.model.ActiveGood;
import com.mmj.active.common.model.GoodSale;
import com.mmj.active.common.model.GoodSaleEx;
import com.mmj.active.common.model.dto.CutGoodDto;
import com.mmj.active.common.model.vo.CutGoodVo;
import com.mmj.active.common.service.ActiveGoodService;
import com.mmj.active.cut.model.CutAward;
import com.mmj.active.cut.model.CutInfo;
import com.mmj.active.cut.mapper.CutInfoMapper;
import com.mmj.active.cut.model.dto.*;
import com.mmj.active.cut.model.vo.*;
import com.mmj.active.cut.service.CutAwardService;
import com.mmj.active.cut.service.CutInfoService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.active.cut.utils.PriceCalculationUtils;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.model.ReturnData;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.common.utils.PriceConversion;
import com.mmj.common.utils.SecurityUserUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


/**
 * <p>
 * 砍价信息表 服务实现类
 * </p>
 *
 * @author KK
 * @since 2019-06-10
 */
@Slf4j
@Service
public class CutInfoServiceImpl extends ServiceImpl<CutInfoMapper, CutInfo> implements CutInfoService {
    /**
     * 砍价奖励配置服务
     */
    @Autowired
    private CutAwardService cutAwardService;
    /**
     * 活动商品服务
     */
    @Autowired
    private ActiveGoodService activeGoodService;

    /**
     * 商品服务
     */
    @Autowired
    private GoodFeignClient goodFeignClient;

    /**
     * 获取用户信息
     *
     * @return
     */
    private JwtUserDetails getUserDetails() {
        JwtUserDetails jwtUserDetails = SecurityUserUtil.getUserDetails();
        Assert.notNull(jwtUserDetails, "缺少用户信息");
        return jwtUserDetails;
    }

    /**
     * 转换砍价商品信息
     *
     * @param activeGood
     * @return
     */
    private BossCutItemDto toBossCutInfoItemDto(ActiveGood activeGood) {
        BossCutItemDto bossBossCutInfoItemDto = new BossCutItemDto();
        BeanUtils.copyProperties(activeGood, bossBossCutInfoItemDto);
        bossBossCutInfoItemDto.setActivePrice(PriceCalculationUtils.intToBigDecimal(activeGood.getActivePrice()));
        return bossBossCutInfoItemDto;
    }

    /**
     * 转换砍价奖励信息
     *
     * @param cutAward
     * @return
     */
    private BossCutAwardDto toBossCutInfoAwardDto(CutAward cutAward) {
        BossCutAwardDto bossBossCutInfoAwardDto = new BossCutAwardDto();
        BeanUtils.copyProperties(cutAward, bossBossCutInfoAwardDto);
        return bossBossCutInfoAwardDto;
    }

    /**
     * 获取活动商品
     *
     * @param cutId
     * @return
     */
    @Override
    public List<ActiveGood> getActiveGood(Integer cutId) {
        ActiveGood activeGood = new ActiveGood();
        activeGood.setActiveType(ActiveGoodsConstants.ActiveType.CUT);
        activeGood.setBusinessId(cutId);
        EntityWrapper<ActiveGood> activeGoodEntityWrapper = new EntityWrapper<>(activeGood);
        activeGoodEntityWrapper.groupBy("GOOD_ID");
        activeGoodEntityWrapper.orderBy("GOOD_ORDER");
        return activeGoodService.selectList(activeGoodEntityWrapper);
    }

    /**
     * 获取活动商品的排序
     *
     * @param cutId
     * @return
     */
    private int getActiveGoodOrder(Integer cutId) {
        ActiveGood activeGood = new ActiveGood();
        activeGood.setActiveType(ActiveGoodsConstants.ActiveType.CUT);
        activeGood.setBusinessId(cutId);
        EntityWrapper<ActiveGood> activeGoodEntityWrapper = new EntityWrapper<>(activeGood);
        activeGoodEntityWrapper.orderBy("GOOD_ORDER", false);
        return activeGoodService.selectCount(activeGoodEntityWrapper);
    }

    /**
     * 通过goodId查询商品信息
     *
     * @param goodIds
     * @return
     */
    @Override
    public List<GoodSale> getGoodInfo(List<Integer> goodIds) {
        GoodSaleEx goodSaleEx = new GoodSaleEx();
        goodSaleEx.setGoodIds(goodIds);
        ReturnData<List<GoodSale>> returnData = goodFeignClient.queryList(goodSaleEx);
        Assert.isTrue(SecurityConstants.SUCCESS_CODE == returnData.getCode().intValue() && returnData.getData().size() > 0, returnData.getDesc());
        return returnData.getData();
    }

    /**
     * 通过条件查询砍价商品
     *
     * @param goodName
     * @param goodSpu
     * @param entityWrapper
     * @return
     */
    private boolean queryActiveGood(String goodName, String goodSpu, EntityWrapper<CutInfo> entityWrapper) {
        if (StringUtils.isBlank(goodName) && StringUtils.isBlank(goodSpu)) {
            return true;
        }
        EntityWrapper<ActiveGood> activeGoodEntityWrapper = new EntityWrapper<>();
        activeGoodEntityWrapper.eq("ACTIVE_TYPE", ActiveGoodsConstants.ActiveType.CUT);
        activeGoodEntityWrapper.groupBy("BUSINESS_ID");
        if (StringUtils.isNotBlank(goodName)) {
            activeGoodEntityWrapper.like("GOOD_NAME", goodName);
        } else if (StringUtils.isNotBlank(goodSpu)) {
            activeGoodEntityWrapper.like("GOOD_SPU", goodSpu);
        }
        List<ActiveGood> activeGoods = activeGoodService.selectList(activeGoodEntityWrapper);
        if (Objects.isNull(activeGoods) || activeGoods.size() == 0)
            return false;
        List<Integer> cutIds = Lists.newArrayListWithCapacity(activeGoods.size());
        activeGoods.stream().forEach(good -> cutIds.add(good.getBusinessId()));
        entityWrapper.in("CUT_ID", cutIds);
        return true;
    }

    @Override
    public Page<BossCutDto> query(BossCutQueryVo queryVo) {
        JwtUserDetails jwtUserDetails = getUserDetails();
        CutInfo queryCutInfo = new CutInfo();
        queryCutInfo.setDelFlag(0);
        EntityWrapper<CutInfo> entityWrapper = new EntityWrapper<>(queryCutInfo);
        boolean queryActiveGoodStatus = queryActiveGood(queryVo.getGoodName(), queryVo.getGoodSpu(), entityWrapper);
        if (!queryActiveGoodStatus)
            return new Page<>(queryVo.getCurrentPage(), queryVo.getPageSize());
        Page<CutInfo> results = new Page<>(queryVo.getCurrentPage(), queryVo.getPageSize(), "CUT_ID", false);
        results = selectPage(results, entityWrapper);
        List<BossCutDto> bossCutDtos = Lists.newArrayListWithCapacity(results.getRecords().size());
        results.getRecords().stream().forEach(cutInfo -> {
            BossCutDto bossCutDto = new BossCutDto();
            BeanUtils.copyProperties(cutInfo, bossCutDto);
            List<ActiveGood> activeGoods = getActiveGood(cutInfo.getCutId());
//            List<ActiveGood> bossBossCutInfoItemDtos = Lists.newArrayListWithCapacity(activeGoods.size());
//            activeGoods.stream().forEach(good -> bossBossCutInfoItemDtos.add(toBossCutInfoItemDto(good)));
            bossCutDto.setItems(activeGoods);
            CutAward queryCutAward = new CutAward();
            queryCutAward.setCutId(cutInfo.getCutId());
            EntityWrapper<CutAward> cutAwardEntityWrapper = new EntityWrapper<>(queryCutAward);
            List<CutAward> cutAwards = cutAwardService.selectList(cutAwardEntityWrapper);
            List<BossCutAwardDto> cutAwardDtos = Lists.newArrayListWithCapacity(cutAwards.size());
            cutAwards.stream().forEach(award -> cutAwardDtos.add(toBossCutInfoAwardDto(award)));
            bossCutDto.setAwards(cutAwardDtos);
            bossCutDtos.add(bossCutDto);
        });
        Page<BossCutDto> bossCutInfoDtoPage = new Page<>();
        bossCutInfoDtoPage.setTotal(results.getTotal());
        bossCutInfoDtoPage.setCurrent(results.getCurrent());
        bossCutInfoDtoPage.setSize(results.getSize());
        bossCutInfoDtoPage.setCondition(results.getCondition());
        bossCutInfoDtoPage.setRecords(bossCutDtos);
        return bossCutInfoDtoPage;
    }

    @Override
    public BossCutDto queryByCutId(Integer cutId) {
        Assert.notNull(cutId, "缺少参数");
        JwtUserDetails jwtUserDetails = getUserDetails();
        CutInfo cutInfo = selectById(cutId);
        Assert.notNull(cutInfo, "信息不存在");
        BossCutDto bossCutDto = new BossCutDto();
        BeanUtils.copyProperties(cutInfo, bossCutDto);
        List<ActiveGood> activeGoods = getActiveGood(cutInfo.getCutId());
//        List<ActiveGood> bossBossCutInfoItemDtos = Lists.newArrayListWithCapacity(activeGoods.size());
//        activeGoods.stream().forEach(good -> bossBossCutInfoItemDtos.add(toBossCutInfoItemDto(good)));
        bossCutDto.setItems(activeGoods);
        CutAward cutAward = new CutAward();
        cutAward.setCutId(cutInfo.getCutId());
        EntityWrapper<CutAward> cutAwardEntityWrapper = new EntityWrapper<>(cutAward);
        List<CutAward> cutAwards = cutAwardService.selectList(cutAwardEntityWrapper);
        List<BossCutAwardDto> bossBossCutInfoAwardDtos = Lists.newArrayListWithCapacity(cutAwards.size());
        cutAwards.stream().forEach(award -> bossBossCutInfoAwardDtos.add(toBossCutInfoAwardDto(award)));
        bossCutDto.setAwards(bossBossCutInfoAwardDtos);
        return bossCutDto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BossCutEditDto add(BossCutAddVo addVo) {
        JwtUserDetails jwtUserDetails = getUserDetails();
        CutInfo cutInfo = new CutInfo();
        BeanUtils.copyProperties(addVo, cutInfo);
        cutInfo.setDelFlag(0);
        cutInfo.setCreaterId(jwtUserDetails.getUserId());
        cutInfo.setModifyId(jwtUserDetails.getUserId());
        boolean result = insert(cutInfo);
        Assert.isTrue(result, "插入失败");
        List<ActiveGood> itemList = addVo.getItems();
        List<ActiveGood> activeGoods = Lists.newArrayListWithCapacity(itemList.size());
        List<Integer> goodIds = Lists.newArrayListWithCapacity(itemList.size());
        itemList.stream().forEach(item -> goodIds.add(item.getGoodId()));
        List<GoodSale> goodSaleList = getGoodInfo(goodIds);
        AtomicInteger order = new AtomicInteger(getActiveGoodOrder(null));
        itemList.stream().forEach(item -> {
            List<GoodSale> goodSales = goodSaleList.stream().filter(goodSale -> goodSale.getGoodId().equals(item.getGoodId())).collect(Collectors.toList());
            Assert.isTrue(goodSales.size() > 0, item.getGoodId() + "商品信息未找到");
            goodSales.forEach(goodSale -> {
                ActiveGood activeGood = new ActiveGood();
                BeanUtils.copyProperties(item, activeGood);
                BeanUtils.copyProperties(goodSale, activeGood);
                activeGood.setActiveType(ActiveGoodsConstants.ActiveType.CUT);
                activeGood.setBusinessId(cutInfo.getCutId());
                activeGood.setActivePrice(goodSale.getBasePrice());
                activeGood.setGoodOrder(order.getAndIncrement());
                activeGoods.add(activeGood);
            });
        });
        //新增砍价商品
        result = activeGoodService.insertBatch(activeGoods);
        Assert.isTrue(result, "插入商品失败");
        List<BossCutAddAwardVo> awardList = addVo.getAwards();
        List<CutAward> cutAwards = Lists.newArrayListWithCapacity(awardList.size());
        awardList.stream().forEach(award -> {
            CutAward cutAward = new CutAward();
            BeanUtils.copyProperties(award, cutAward);
            cutAward.setCutId(cutInfo.getCutId());
            cutAwards.add(cutAward);
        });
        result = cutAwardService.insertBatch(cutAwards);
        Assert.isTrue(result, "插入帮砍奖励失败");
        return new BossCutEditDto(cutInfo.getCutId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BossCutEditDto edit(BossCutEditVo editVo) {
        JwtUserDetails jwtUserDetails = getUserDetails();
        CutInfo cutInfo = new CutInfo();
        BeanUtils.copyProperties(editVo, cutInfo);
        cutInfo.setModifyId(jwtUserDetails.getUserId());
        boolean result = updateById(cutInfo);
        Assert.isTrue(result, "插入失败");
        List<ActiveGood> activeGoodsDelete = getActiveGood(cutInfo.getCutId());
        List<ActiveGood> itemList = editVo.getItems();
        List<ActiveGood> activeGoodsInsert = Lists.newArrayListWithCapacity(itemList.size());
        List<Integer> goodIds = Lists.newArrayListWithCapacity(itemList.size());
        itemList.stream().forEach(item -> goodIds.add(item.getGoodId()));
        List<GoodSale> goodSaleList = getGoodInfo(goodIds);
        AtomicInteger order = new AtomicInteger(getActiveGoodOrder(cutInfo.getCutId()));
        //新增商品，删除商品
        itemList.stream().forEach(item -> {
            boolean removeStatus = activeGoodsDelete.removeIf(good -> good.getGoodSpu().equals(item.getGoodSpu()));
            if (!removeStatus) { //新增
                List<GoodSale> goodSales = goodSaleList.stream().filter(goodSale -> goodSale.getGoodId().equals(item.getGoodId())).collect(Collectors.toList());
                Assert.isTrue(goodSales.size() > 0, item.getGoodId() + "商品信息未找到");
                goodSales.forEach(goodSale -> {
                    ActiveGood activeGood = new ActiveGood();
                    BeanUtils.copyProperties(item, activeGood);
                    BeanUtils.copyProperties(goodSale, activeGood);
                    activeGood.setBusinessId(cutInfo.getCutId());
                    activeGood.setActiveType(ActiveGoodsConstants.ActiveType.CUT);
                    activeGood.setGoodId(item.getGoodId());
                    activeGood.setGoodSpu(item.getGoodSpu());
                    activeGood.setActivePrice(goodSale.getBasePrice());
                    activeGood.setGoodOrder(order.getAndIncrement());
                    activeGoodsInsert.add(activeGood);
                });
            }
        });
        if (activeGoodsDelete.size() > 0) {
            List<String> goodSpuList = Lists.newArrayListWithCapacity(activeGoodsDelete.size());
            activeGoodsDelete.stream().forEach(good -> goodSpuList.add(good.getGoodSpu()));
            EntityWrapper<ActiveGood> activeGoodEntityWrapper = new EntityWrapper<>();
            activeGoodEntityWrapper.eq("ACTIVE_TYPE", ActiveGoodsConstants.ActiveType.CUT);
            activeGoodEntityWrapper.eq("BUSINESS_ID", cutInfo.getCutId());
            activeGoodEntityWrapper.in("GOOD_SPU", goodSpuList);
            boolean deleteStatus = activeGoodService.delete(activeGoodEntityWrapper);
            Assert.isTrue(deleteStatus, "删除砍价商品失败");
        }
        if (activeGoodsInsert.size() > 0) {
            boolean insertStatus = activeGoodService.insertBatch(activeGoodsInsert);
            Assert.isTrue(insertStatus, "新增砍价商品失败");
        }

        CutAward cutAwardQuery = new CutAward();
        cutAwardQuery.setCutId(cutInfo.getCutId());
        EntityWrapper<CutAward> cutAwardEntityWrapper = new EntityWrapper<>(cutAwardQuery);
        boolean deleteStatus = cutAwardService.delete(cutAwardEntityWrapper);
        Assert.isTrue(deleteStatus, "删除帮砍奖励失败");
        List<BossCutEditAwardVo> awardList = editVo.getAwards();
        List<CutAward> cutAwards = Lists.newArrayListWithCapacity(awardList.size());
        awardList.stream().forEach(award -> {
            CutAward cutAward = new CutAward();
            BeanUtils.copyProperties(award, cutAward);
            cutAward.setCutId(cutInfo.getCutId());
            cutAwards.add(cutAward);
        });
        result = cutAwardService.insertBatch(cutAwards);
        Assert.isTrue(result, "更新帮砍奖励失败");
        return new BossCutEditDto(cutInfo.getCutId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByCutId(Integer cutId) {
        Assert.notNull(cutId, "缺少参数");
        JwtUserDetails jwtUserDetails = getUserDetails();
        CutInfo cutInfo = selectById(cutId);
        Assert.notNull(cutInfo, "信息不存在");
        CutInfo updateCutInfo = new CutInfo();
        updateCutInfo.setDelFlag(1);
        updateCutInfo.setModifyId(jwtUserDetails.getUserId());
        updateCutInfo.setModifyTime(new Date());
        CutInfo queryCutInfo = new CutInfo();
        queryCutInfo.setCutId(cutId);
        EntityWrapper<CutInfo> entityWrapper = new EntityWrapper<>(queryCutInfo);
        boolean result = update(updateCutInfo, entityWrapper);
        Assert.isTrue(result, "删除失败");
    }

    @Override
    public List<CutGoodListDto> goodList() {
        getUserDetails();
        CutInfo queryCutInfo = new CutInfo();
        queryCutInfo.setDelFlag(0);
        EntityWrapper<CutInfo> entityWrapper = new EntityWrapper<>(queryCutInfo);
        List<CutInfo> cutInfoList = selectList(entityWrapper);
        List<ActiveGood> activeGoodList = Lists.newArrayList();
        cutInfoList.forEach(cutInfo -> {
            List<ActiveGood> activeGoods = getActiveGood(cutInfo.getCutId());
            if (Objects.nonNull(activeGoods) && activeGoods.size() > 0) {
                activeGoods.forEach(activeGood -> {
                    if ("1".equals(activeGood.getGoodStatus()))
                        activeGoodList.add(activeGood);
                });
            }
        });
        List<ActiveGood> activeGoods = activeGoodList.stream().sorted(new Comparator<ActiveGood>() {
            @Override
            public int compare(ActiveGood o1, ActiveGood o2) {
                return o1.getGoodOrder().compareTo(o2.getGoodOrder());
            }
        }).collect(Collectors.toList());
        List<CutGoodListDto> cutGoodListDtos = Lists.newArrayListWithCapacity(activeGoods.size());
        List<CutInfo> cutInfos = Lists.newArrayList();
        activeGoods.stream().forEach(good -> {
            CutGoodListDto cutGoodListDto = new CutGoodListDto();
            BeanUtils.copyProperties(good, cutGoodListDto);
            cutGoodListDto.setCutId(good.getBusinessId());
            cutGoodListDto.setActivePrice(new BigDecimal(PriceConversion.intToString(good.getActivePrice())));
            CutInfo cutInfo = cutInfos.stream().filter(info -> cutGoodListDto.getCutId().equals(info.getCutId())).findAny().orElse(null);
            if (Objects.nonNull(cutInfo)) { //获取底价
                cutGoodListDto.setBaseUnitPrice(cutInfo.getBasePrice());
            } else {
                cutInfo = selectById(cutGoodListDto.getCutId());
                cutInfos.add(cutInfo);
                cutGoodListDto.setBaseUnitPrice(cutInfo.getBasePrice());
            }
            cutGoodListDtos.add(cutGoodListDto);
        });
        return cutGoodListDtos;
    }
}
