package com.mmj.user.member.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.utils.SecurityUserUtil;
import com.mmj.user.common.feigin.OrderFeignClient;
import com.mmj.user.manager.service.CouponUserService;
import com.mmj.user.manager.vo.UserCouponVo;
import com.mmj.user.member.mapper.KingRepalceMapper;
import com.mmj.user.member.model.KingRepalce;
import com.mmj.user.member.model.KingUser;
import com.mmj.user.member.service.KingRepalceService;
import com.mmj.user.member.service.KingUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 买买金兑换表 服务实现类
 * </p>
 *
 * @author zhangyicao
 * @since 2019-07-10
 */
@Service
public class KingRepalceServiceImpl extends ServiceImpl<KingRepalceMapper, KingRepalce> implements KingRepalceService {

    private final KingUserService kingUserService;

    private final OrderFeignClient orderFeignClient;

    private final CouponUserService couponUserService;

    public KingRepalceServiceImpl(KingUserService kingUserService, OrderFeignClient orderFeignClient, CouponUserService couponUserService) {
        this.kingUserService = kingUserService;
        this.orderFeignClient = orderFeignClient;
        this.couponUserService = couponUserService;
    }

    @Override
    @Transactional
    public void batchSaveOrUpdate(List<KingRepalce> list) {
        verify(list);
        delete(null);
        insertBatch(list);
    }

    @Override
    @Transactional
    public void getCoupon(Integer templateId) {
        KingRepalce kr = new KingRepalce();
        kr.setBusinessId(templateId);
        EntityWrapper<KingRepalce> wrapper = new EntityWrapper<>(kr);
        kr = selectOne(wrapper);
        Assert.notNull(kr, "兑换商品不存在");
        JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
        Assert.notNull(userDetails, "用户未登录");
        KingUser kingUser = kingUserService.getByUserId(userDetails.getUserId());
        Assert.notNull(kingUser, "买买金账户不存在");

        //        要扣掉冻结的买买金
        int frozen = orderFeignClient.frozenKingNum(userDetails.getUserId());
        Assert.isTrue(kingUser.getKingNum() >= kr.getKingNum() + frozen, "买买金不足");
        UserCouponVo vo = new UserCouponVo();
        vo.setCouponId(templateId);
        vo.setUserId(userDetails.getUserId());
        vo.setCouponSource("MMKING");
        couponUserService.receive(vo);
    }

    private void verify(List<KingRepalce> list) {
        for (KingRepalce repalce : list) {
            Assert.notNull(repalce.getKingNum(), "兑换商品的买买金数量不能为空");
            Assert.notNull(repalce.getType(), "兑换商品的类型不能为空");
            Assert.notNull(repalce.getAmount(), "兑换商品的金额不能为空");
            Assert.notNull(repalce.getBusinessId(), "兑换商品的id不能为空");
            Assert.notNull(repalce.getImage(), "兑换商品的图片不能为空");
            Assert.notNull(repalce.getName(), "兑换商品的名称不能为空");
            repalce.setCreateTime(new Date());
        }
    }
}
