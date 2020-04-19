package com.mmj.active.common.feigin;

import java.util.List;

import javax.validation.Valid;

import com.mmj.common.model.*;
import com.mmj.order.common.model.vo.RedPackageUserVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSONObject;
import com.mmj.active.common.model.RedPackageUser;
import com.mmj.active.common.model.UserActive;
import com.mmj.active.common.model.UserMember;
import com.mmj.active.common.model.vo.UserCouponVo;
import com.mmj.active.cut.model.CutUser;
import com.mmj.common.exception.BusinessException;
import com.mmj.common.properties.SecurityConstants;

import feign.hystrix.FallbackFactory;

@Component
public class UserFallbackFactory implements FallbackFactory<UserFeignClient> {

    private final Logger logger = LoggerFactory.getLogger(UserFallbackFactory.class);

    @Override
    public UserFeignClient create(Throwable cause) {
        logger.info("UserFallbackFactory error message is {}", cause.getMessage());
        return new UserFeignClient() {
            @Override
            public BaseUser getUserById(Long id) {
                throw new BusinessException("调用用户接口报错," + cause.getMessage(), 500);
            }

            @Override
            public UserActive queryWinner(@RequestParam UserActive userActive) {
                throw new BusinessException("校验中奖人接口报错," + cause.getMessage(), 500);
            }

            @Override
            public List<UserActive> queryJoinUserList(UserActive userActive) {
                throw new BusinessException("查询参与的用户信息接口报错," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData addCutUser(CutUser cutUser) {
                return new ReturnData(SecurityConstants.EXCEPTION_CODE, "fail");
            }

            @Override
            public ReturnData<List<CutUser>> cutUsers(CutUser cutUser) {
                return new ReturnData(SecurityConstants.EXCEPTION_CODE, "fail");
            }

            @Override
            public ReturnData<List<CutUser>> myCutList(CutUser cutUser) {
                return new ReturnData(SecurityConstants.EXCEPTION_CODE, "fail");
            }

            @Override
            public RedPackageUser getRedPackage(String redCode, String unionId) {
                throw new BusinessException("获取红包接口报错," + cause.getMessage(), 500);
            }

            @Override
            public boolean updateRedPackage(RedPackageUser redPackageUser) {
                throw new BusinessException("修改红包状态接口报错," + cause.getMessage(), 500);
            }

            @Override
            public RedPackageUser redPacketInfo(RedPackageUser redPackageUser) {
                throw new BusinessException("查询红包接口报错," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<Boolean> hasReceive(@Valid @RequestBody UserCouponVo userCouponVo) {
                throw new BusinessException("查询用户是否领取过该优惠券报错," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<String> clickShare(JSONObject uLog) {
                throw new BusinessException("分享商品增加买买金报错," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<UserMember> queryUserMemberInfoByUserId(@PathVariable("userId") Long userId) {
                throw new BusinessException("查询会员信息接口报错," + cause.getMessage(), 500);
            }

            @Override
            public List<UserActive> getActiveByCode(UserActive userActive) {
                throw new BusinessException("查询参与活动的用户接口," + cause.getMessage(), 500);
            }
            @Override
			public ReturnData<UserLogin> getUserLoginInfoByUserName(
					String openId) {
				throw new BusinessException("获取用户登录账号接口报错：" + cause.getMessage(), 500);
			}

            @Override
            public ReturnData<BaseUserDto> queryUserInfoByUserId(String param) {
                throw new BusinessException("获取用户当前端信息接口报错：" + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<Integer> queryMemberTotalCount() {
                return new ReturnData(SecurityConstants.EXCEPTION_CODE,"查询会员总数量失败");
            }

            @Override
            public ReturnData<List<UserCouponDto>> myCouponInfoByCouponId(@PathVariable("couponId") Integer couponId){
                return new ReturnData(SecurityConstants.EXCEPTION_CODE,"根据优惠券id查询优惠券报错");
            }

            @Override
            public boolean addRedPackage(RedPackageUserVo redPackage) {
                return false;
            }
        };
    }

}
