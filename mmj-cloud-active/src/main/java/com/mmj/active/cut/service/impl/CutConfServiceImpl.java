package com.mmj.active.cut.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.google.common.collect.Lists;
import com.mmj.active.common.constants.ActiveGoodsConstants;
import com.mmj.active.common.model.ActiveGood;
import com.mmj.active.common.service.ActiveGoodService;
import com.mmj.active.cut.model.CutConf;
import com.mmj.active.cut.mapper.CutConfMapper;
import com.mmj.active.cut.model.CutInfo;
import com.mmj.active.cut.model.dto.BossCutSysDto;
import com.mmj.active.cut.model.vo.BossCutSysEditVo;
import com.mmj.active.cut.service.CutConfService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.active.cut.service.CutInfoService;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.utils.SecurityUserUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 砍价公共配置表 服务实现类
 * </p>
 *
 * @author KK
 * @since 2019-06-10
 */
@Service
public class CutConfServiceImpl extends ServiceImpl<CutConfMapper, CutConf> implements CutConfService {
    @Autowired
    private ActiveGoodService activeGoodService;
    @Autowired
    private CutInfoService cutInfoService;

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

    @Override
    public BossCutSysDto getSys() {
        getUserDetails();
        BossCutSysDto bossCutSysDto = new BossCutSysDto();
        Page<CutConf> cutConfPage = new Page<>(1, 1);
        cutConfPage = selectPage(cutConfPage);
        if (cutConfPage.getRecords().size() > 0) {
            CutConf cutConf = cutConfPage.getRecords().get(0);
            bossCutSysDto.setConfId(cutConf.getConfId());
            bossCutSysDto.setWeixnName(cutConf.getWeixnName());
            bossCutSysDto.setRuleCintext(cutConf.getRuleCintext());
        }
        CutInfo queryCutInfo = new CutInfo();
        queryCutInfo.setDelFlag(0);
        EntityWrapper<CutInfo> entityWrapper = new EntityWrapper<>(queryCutInfo);
        List<CutInfo> cutInfoList = cutInfoService.selectList(entityWrapper);
        List<ActiveGood> activeGoodList = Lists.newArrayList();
        cutInfoList.forEach(cutInfo -> {
            List<ActiveGood> activeGoods = cutInfoService.getActiveGood(cutInfo.getCutId());
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
        List<BossCutSysDto.Item> items = Lists.newArrayListWithCapacity(activeGoods.size());
        activeGoods.stream().forEach(good -> {
            BossCutSysDto.Item item = new BossCutSysDto.Item();
            BeanUtils.copyProperties(good, item);
            item.setGoodOrder(good.getGoodOrder());
            items.add(item);
        });
        bossCutSysDto.setItems(items);
        return bossCutSysDto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editSys(BossCutSysEditVo sysEditVo) {
        getUserDetails();
        CutConf cutConf = new CutConf();
        BeanUtils.copyProperties(sysEditVo, cutConf);
        boolean result = insertOrUpdate(cutConf);
        Assert.isTrue(result, "编辑失败");
        sysEditVo.getItems().stream().forEach(item -> {
            ActiveGood updateActiveGood = new ActiveGood();
            updateActiveGood.setGoodOrder(item.getGoodOrder());
            ActiveGood queryActiveGood = new ActiveGood();
            queryActiveGood.setGoodId(item.getGoodId());
            queryActiveGood.setActiveType(ActiveGoodsConstants.ActiveType.CUT);
            EntityWrapper<ActiveGood> activeGoodEntityWrapper = new EntityWrapper<>(queryActiveGood);
            boolean resultStatus = activeGoodService.update(updateActiveGood, activeGoodEntityWrapper);
            Assert.isTrue(resultStatus, item.getGoodId() + "编辑商品排序失败");
        });
    }
}

