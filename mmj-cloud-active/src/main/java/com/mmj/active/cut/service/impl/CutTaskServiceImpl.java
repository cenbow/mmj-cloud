package com.mmj.active.cut.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.mmj.active.common.constants.ActiveGoodsConstants;
import com.mmj.active.common.feigin.UserFeignClient;
import com.mmj.active.cut.model.CutReward;
import com.mmj.active.cut.model.CutTask;
import com.mmj.active.cut.mapper.CutTaskMapper;
import com.mmj.active.cut.model.dto.CutUserTaskDto;
import com.mmj.active.cut.service.CutRewardService;
import com.mmj.active.cut.service.CutTaskService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.common.model.BaseUser;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.utils.PriceConversion;
import com.mmj.common.utils.SecurityUserUtil;
import com.mmj.order.common.model.vo.RedPackageUserVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * <p>
 * 砍价任务 服务实现类
 * </p>
 *
 * @author KK
 * @since 2019-07-26
 */
@Slf4j
@Service
public class CutTaskServiceImpl extends ServiceImpl<CutTaskMapper, CutTask> implements CutTaskService {

    @Autowired
    private CutRewardService cutRewardService;
    @Autowired
    private UserFeignClient userFeignClient;

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
    public void addSponsorNumber() {

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addAssistNumber(BaseUser baseUser, Long sponsorUserId, Long assistUserId, String cutNo, Integer cutId) {
        Assert.isTrue(Objects.nonNull(sponsorUserId) && Objects.nonNull(assistUserId), "缺少砍价用户信息");
        if (!sponsorUserId.equals(assistUserId)) {
            CutTask queryCutTask = new CutTask();
            queryCutTask.setUserId(sponsorUserId);
            EntityWrapper entityWrapper = new EntityWrapper(queryCutTask);
            CutTask cutTask = selectOne(entityWrapper);
            boolean result;
            if (Objects.isNull(cutTask)) {
                cutTask = new CutTask();
                cutTask.setUserId(sponsorUserId);
                cutTask.setSponsorNumber(0);
                cutTask.setAssistNumber(1);
                result = insert(cutTask);
                Assert.isTrue(result, "添加记录失败");
            } else {
                cutTask.setAssistNumber(cutTask.getAssistNumber() + 1);
                int assistNumber = cutTask.getAssistNumber();
                if (Objects.isNull(cutTask.getTaskThreeAssistId()) && assistNumber >= 10) {
                    CutReward cutReward = new CutReward();
                    cutReward.setRewardType(2);
                    cutReward.setUserId(sponsorUserId);
                    cutReward.setCutId(cutId);
                    cutReward.setCutNo(cutNo);
                    cutReward.setRewardValue(BigDecimal.valueOf(0.3)); //0.3 ~ 0.9之间
                    String redCode = produceRedCode(baseUser, cutId, cutNo, cutReward.getRewardValue().toString());
                    cutReward.setRedCode(redCode);
                    cutReward.setExpireTime(new Date(System.currentTimeMillis() + 2592000000L));
                    result = cutRewardService.insert(cutReward);
                    Assert.isTrue(result, "添加任务三失败");
                    cutTask.setTaskThreeAssistId(cutReward.getRewardId());
                    cutTask.setTaskThreeRewardAmount(cutReward.getRewardValue());
                    cutTask.setTaskThreeRewardTime(cutReward.getCreateTime());
                    cutTask.setRedCode(redCode);
                } else if (Objects.isNull(cutTask.getTaskTwoAssistId()) && assistNumber >= 5) {
                    CutReward cutReward = new CutReward();
                    cutReward.setRewardType(1);
                    cutReward.setUserId(sponsorUserId);
                    cutReward.setCutId(cutId);
                    cutReward.setCutNo(cutNo);
                    cutReward.setRewardValue(BigDecimal.valueOf(5)); //0.3 ~ 0.9之间
                    cutReward.setExpireTime(new Date(System.currentTimeMillis() + 2592000000L));
                    result = cutRewardService.insert(cutReward);
                    Assert.isTrue(result, "添加任务二失败");
                    cutTask.setTaskTwoAssistId(cutReward.getRewardId());
                    cutTask.setTaskTwoRewardAmount(cutReward.getRewardValue());
                    cutTask.setTaskTwoRewardTime(cutReward.getCreateTime());
                } else if (Objects.isNull(cutTask.getTaskOneAssistId()) && assistNumber >= 3) {
                    CutReward cutReward = new CutReward();
                    cutReward.setRewardType(1);
                    cutReward.setUserId(sponsorUserId);
                    cutReward.setCutId(cutId);
                    cutReward.setCutNo(cutNo);
                    cutReward.setRewardValue(BigDecimal.valueOf(2)); //0.3 ~ 0.9之间
                    cutReward.setExpireTime(new Date(System.currentTimeMillis() + 2592000000L));
                    result = cutRewardService.insert(cutReward);
                    Assert.isTrue(result, "添加任务一失败");
                    cutTask.setTaskOneAssistId(cutReward.getRewardId());
                    cutTask.setTaskOneRewardAmount(cutReward.getRewardValue());
                    cutTask.setTaskOneRewardTime(cutReward.getCreateTime());
                }

                result = updateById(cutTask);
                Assert.isTrue(result, "添加任务失败");
            }
        }
    }

    private String produceRedCode(BaseUser baseUser, Integer cutId, String cutNo, String rewardAmount) {
        RedPackageUserVo packageUserVo = new RedPackageUserVo();
        packageUserVo.setPackageAmount(PriceConversion.stringToInt(rewardAmount));
        packageUserVo.setPackageCode(getRedCode());
        packageUserVo.setActiveType(ActiveGoodsConstants.ActiveType.CUT);
        packageUserVo.setPackageSource("砍价");
        packageUserVo.setUnionId(baseUser.getUnionId());
        packageUserVo.setPackageStatus(0);
        packageUserVo.setOrderNo(cutNo);
        packageUserVo.setUserId(baseUser.getUserId());
        packageUserVo.setBusinessId(cutId);
        boolean bool = userFeignClient.addRedPackage(packageUserVo);
        log.info("砍价任务三发放红包结果:{}, {}", bool, JSON.toJSONString(packageUserVo));
        return packageUserVo.getPackageCode();
    }

    /**
     * 获取红包码
     *
     * @return
     */
    private String getRedCode() {
        Long rm = (long) (Math.random() * 6 * Math.pow(10, 6 - 1)) + (long) Math.pow(10, 6 - 1);
        return "CUT" + rm.toString();
    }

    @Override
    public CutUserTaskDto getCutUserTask() {
        JwtUserDetails userDetails = getUserDetails();
        CutUserTaskDto cutUserTaskDto = new CutUserTaskDto();
        CutTask queryCutTask = new CutTask();
        queryCutTask.setUserId(userDetails.getUserId());
        EntityWrapper entityWrapper = new EntityWrapper(queryCutTask);
        CutTask cutTask = selectOne(entityWrapper);
        if (Objects.nonNull(cutTask)) {
            cutUserTaskDto.setAssistNumber(cutTask.getAssistNumber());
            CutReward cutReward;
            if (Objects.nonNull(cutTask.getTaskOneAssistId())) {
                cutReward = cutRewardService.selectById(cutTask.getTaskOneAssistId());
                if (Objects.nonNull(cutReward) && cutReward.getDelFlag() == 0) {
                    cutUserTaskDto.setTaskOneStatus(cutReward.getUseFlag() == 1 ? 2 : 1);
                }
            }
            if (Objects.nonNull(cutTask.getTaskTwoAssistId())) {
                cutReward = cutRewardService.selectById(cutTask.getTaskTwoAssistId());
                if (Objects.nonNull(cutReward) && cutReward.getDelFlag() == 0) {
                    cutUserTaskDto.setTaskTwoStatus(cutReward.getUseFlag() == 1 ? 2 : 1);
                }
            }
            if (Objects.nonNull(cutTask.getTaskThreeAssistId())) {
                cutReward = cutRewardService.selectById(cutTask.getTaskThreeAssistId());
                if (Objects.nonNull(cutReward) && cutReward.getDelFlag() == 0) {
                    cutUserTaskDto.setTaskThreeStatus(cutReward.getUseFlag() == 1 ? 2 : 1);
                    cutUserTaskDto.setRedCode(cutReward.getRedCode());
                    cutUserTaskDto.setCutNo(cutReward.getCutNo());
                    cutUserTaskDto.setRedAmount(cutReward.getRewardValue());
                }
            }
        }
        return cutUserTaskDto;
    }
}
