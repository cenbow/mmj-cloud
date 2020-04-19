package com.mmj.active.cut.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.mmj.active.cut.model.CutSponsor;
import com.mmj.active.cut.mapper.CutSponsorMapper;
import com.mmj.active.cut.service.CutRewardService;
import com.mmj.active.cut.service.CutSponsorService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.active.cut.service.CutUserService;
import com.mmj.active.cut.utils.CutFlag;
import com.mmj.common.constants.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 用户发起砍价表 服务实现类
 * </p>
 *
 * @author KK
 * @since 2019-07-26
 */
@Service
public class CutSponsorServiceImpl extends ServiceImpl<CutSponsorMapper, CutSponsor> implements CutSponsorService {
    @Autowired
    private CutUserService cutUserService;
    @Autowired
    private CutRewardService cutRewardService;

    @Override
    public int getSuccessCutNumberByUserId(Long userId) {
        CutSponsor cutSponsor = new CutSponsor();
        cutSponsor.setCutFlag(CutFlag.COMPLETED);
        cutSponsor.setUserId(userId);
        EntityWrapper entityWrapper = new EntityWrapper(cutSponsor);
        return selectCount(entityWrapper);
    }

    @Override
    public CutSponsor getCutSponsorByCutNo(String cutNo) {
        Assert.hasLength(cutNo, "缺少砍价编码");
        CutSponsor cutSponsor = new CutSponsor();
        cutSponsor.setCutNo(cutNo);
        EntityWrapper entityWrapper = new EntityWrapper(cutSponsor);
        return selectOne(entityWrapper);
    }

    @Override
    public List<CutSponsor> batchGetCutSponsor(List<Integer> sponsorIds) {
        EntityWrapper<CutSponsor> entityWrapper = new EntityWrapper();
        entityWrapper.in("SPONSOR_ID", sponsorIds);
        return selectList(entityWrapper);
    }

    @Override
    public void editCutFlagBySponsorId(Integer sponsorId, Integer cutFlag) {
        CutSponsor queryCutSponsor = new CutSponsor();
        queryCutSponsor.setSponsorId(sponsorId);
        EntityWrapper entityWrapper = new EntityWrapper(queryCutSponsor);
        CutSponsor updateCutSponsor = new CutSponsor();
        updateCutSponsor.setCutFlag(cutFlag);
        boolean result = update(updateCutSponsor, entityWrapper);
        Assert.isTrue(result, "更改砍价状态失败");
    }

    @Override
    public void addOrderInfoByCutNo(String cutNo, String orderNo, Integer orderStatus, Long userId) {
        CutSponsor queryCutSponsor = new CutSponsor();
        queryCutSponsor.setCutNo(cutNo);
        EntityWrapper entityWrapper = new EntityWrapper(queryCutSponsor);
        CutSponsor updateCutSponsor = new CutSponsor();
        updateCutSponsor.setOrderNo(orderNo);
        updateCutSponsor.setOrderStatus(orderStatus);
        updateCutSponsor.setCutFlag(CutFlag.COMPLETED);
        boolean result = update(updateCutSponsor, entityWrapper);
        Assert.isTrue(result, "新增砍价订单失败");
        cutUserService.addCutFreeList(userId);
    }

    @Override
    public void editOrderStatusByOrderNo(String orderNo, Integer orderStatus) {
        CutSponsor queryCutSponsor = new CutSponsor();
        queryCutSponsor.setOrderNo(orderNo);
        EntityWrapper entityWrapper = new EntityWrapper(queryCutSponsor);
        CutSponsor cutSponsors = selectOne(entityWrapper);
        if (Objects.nonNull(cutSponsors)) {
            CutSponsor updateCutSponsor = new CutSponsor();
            updateCutSponsor.setOrderStatus(orderStatus);
            boolean result = update(updateCutSponsor, entityWrapper);
            Assert.isTrue(result, "编辑砍价订单状态失败");
            if (orderStatus == OrderStatus.PAYMENTED.getStatus() && cutSponsors.getBasePrice().compareTo(BigDecimal.valueOf(0)) == 1) {
                cutRewardService.addCutPaySuccessReward(cutSponsors.getUserId(), cutSponsors.getCutId(), cutSponsors.getCutNo());
            }
        }
    }
}
