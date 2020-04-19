package com.mmj.active.group.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.google.common.collect.Lists;
import com.mmj.active.common.constants.ActiveGoodsConstants;
import com.mmj.active.common.model.ActiveGood;
import com.mmj.active.common.service.ActiveGoodService;
import com.mmj.active.group.model.dto.BossRelayGroupListDto;
import com.mmj.active.group.model.dto.BossTwoGroupListDto;
import com.mmj.active.group.model.vo.*;
import com.mmj.active.group.service.RelayGroupService;
import com.mmj.common.utils.PriceConversion;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;

/**
 *
 * 接力购boss服务
 */
@Service
public class RelayGroupServiceImpl implements RelayGroupService {

    @Autowired
    private ActiveGoodService activeGoodService;

    @Override
    public void addGoods(List<BossRelayGroupGoodsAddVo> relayGroupGoodsAddVos) {
        Assert.isTrue(Objects.nonNull(relayGroupGoodsAddVos) && relayGroupGoodsAddVos.size() > 0, "缺少商品信息");
        List<ActiveGood> activeGoods = Lists.newArrayListWithCapacity(relayGroupGoodsAddVos.size());
        relayGroupGoodsAddVos.forEach(twoGroupGoodsVo -> {
            ActiveGood activeGood = new ActiveGood();
            BeanUtils.copyProperties(twoGroupGoodsVo, activeGood);
            activeGood.setBasePrice(PriceConversion.stringToInt(twoGroupGoodsVo.getBasePrice()));
            activeGood.setActivePrice(PriceConversion.stringToInt(twoGroupGoodsVo.getActivePrice()));
            activeGood.setMemberPrice(PriceConversion.stringToInt(twoGroupGoodsVo.getMemberPrice()));
            activeGood.setActiveType(ActiveGoodsConstants.ActiveType.GROUP_JIELIGOU);//接力购
            activeGood.setGoodStatus("1");//上架
            activeGoods.add(activeGood);
        });
        boolean result = activeGoodService.insertBatch(activeGoods);
        Assert.isTrue(result, "新增失败");
    }

    @Override
    public void editGoods(List<BossRelayGroupGoodsEditVo> twoGroupGoodsEditVos) {
        Assert.isTrue(Objects.nonNull(twoGroupGoodsEditVos) && twoGroupGoodsEditVos.size() > 0, "缺少商品信息");
        List<ActiveGood> activeGoods = Lists.newArrayListWithCapacity(twoGroupGoodsEditVos.size());
        twoGroupGoodsEditVos.forEach(twoGroupGoodsVo -> {
            ActiveGood activeGood = new ActiveGood();
            BeanUtils.copyProperties(twoGroupGoodsVo, activeGood);
            if(StringUtils.isNotBlank(twoGroupGoodsVo.getActivePrice())) {
                activeGood.setActivePrice(PriceConversion.stringToInt(twoGroupGoodsVo.getActivePrice()));
            }
            activeGoods.add(activeGood);
        });
        boolean result = activeGoodService.updateBatchById(activeGoods);
        Assert.isTrue(result, "编辑失败");
    }

    @Override
    public void editGoodsStatus(List<BossRelayGroupGoodsEditVo> bossRelayGroupGoodsEditVo) {
        bossRelayGroupGoodsEditVo.forEach(relayGroupGoods->{
            ActiveGood activeGood = new ActiveGood();
            activeGood.setMapperyId(relayGroupGoods.getMapperyId());
            activeGood.setGoodStatus(String.valueOf(relayGroupGoods.getGoodStatus()));
            boolean result = activeGoodService.updateById(activeGood);
            Assert.isTrue(result, "操作失败");
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeGoods(BossTwoGroupGoodsVo bossTwoGroupGoodsVo) {
        boolean result = activeGoodService.deleteBatchIds(bossTwoGroupGoodsVo.getMapperyIds());
        Assert.isTrue(result, "删除失败");
    }

    @Override
    public Page<BossRelayGroupListDto> relayGroupGoods(BossTwoGroupListVo bossTwoGroupListVo) {
        ActiveGood queryActiveGood = new ActiveGood();
        queryActiveGood.setActiveType(ActiveGoodsConstants.ActiveType.GROUP_JIELIGOU);
        EntityWrapper<ActiveGood> entityWrapper = new EntityWrapper<>(queryActiveGood);
        Page<ActiveGood> results = new Page(bossTwoGroupListVo.getCurrentPage(), bossTwoGroupListVo.getPageSize(), "MAPPERY_ID", false);
        results = activeGoodService.selectPage(results, entityWrapper);
        List<BossRelayGroupListDto> bossTwoGroupListDtoList = Lists.newArrayListWithCapacity(results.getRecords().size());
        results.getRecords().stream().forEach(activeGood -> {
            BossRelayGroupListDto bossTwoGroupListDto = new BossRelayGroupListDto();
            BeanUtils.copyProperties(activeGood, bossTwoGroupListDto);
            bossTwoGroupListDto.setGoodStatus(Integer.parseInt(activeGood.getGoodStatus()));
            bossTwoGroupListDto.setBasePrice(PriceConversion.intToString(activeGood.getBasePrice()));
            bossTwoGroupListDto.setActivePrice(PriceConversion.intToString(activeGood.getActivePrice()));
            bossTwoGroupListDto.setMemberPrice(PriceConversion.intToString(activeGood.getMemberPrice()));
            //TODO 缺少店铺价格-商品库存
            bossTwoGroupListDto.setShopPrice("10");
            bossTwoGroupListDto.setGoodNum(0);
            bossTwoGroupListDtoList.add(bossTwoGroupListDto);
        });
        Page<BossRelayGroupListDto> resultPage = new Page<>();
        resultPage.setTotal(results.getTotal());
        resultPage.setCurrent(results.getCurrent());
        resultPage.setSize(results.getSize());
        resultPage.setCondition(results.getCondition());
        resultPage.setRecords(bossTwoGroupListDtoList);
        return resultPage;
    }

}
