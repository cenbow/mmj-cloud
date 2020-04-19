package com.mmj.order.common.feign;

import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.BaseUser;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.order.OrdersMQDto;
import com.mmj.order.common.model.vo.*;
import com.mmj.order.model.OrderInfo;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class UserFallbackFactory implements FallbackFactory<UserFeignClient> {

    private final Logger logger = LoggerFactory.getLogger(UserFallbackFactory.class);

    @Override
    public UserFeignClient create(Throwable cause) {
        logger.info("UserFallbackFactory error message is {}", cause.getMessage());
        return new UserFeignClient() {
            @Override
            public BaseUser getByById(Long id) {
                throw new BusinessException("调用查询用户信息接口报错," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<UserShardVo> getFreeOrderRelation(Long shardTo) {
                throw new BusinessException("调用查询免费送信息接口报错," + cause.getMessage(), 500);
            }

            @Override
            public boolean delFreeOrderRelation(Long shardTo) {
                throw new BusinessException("调用删除免费送信息接口报错," + cause.getMessage(), 500);
            }

            @Override
            public boolean addRedPackage(RedPackageUserVo redPackage) {
                throw new BusinessException("调用新增红包信息接口报错," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData removes(ShopCartsRemoveVo removeVo) {
                throw new BusinessException("清空购物车报错," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData add(ShopCartsAddVo addVo) {
                throw new BusinessException("加入购物车报错:," + cause.getMessage(), 500);
            }

            @Override
            public int orderKingProc(Map<String, Object> map) {
                throw new BusinessException("订单获得买买金报错," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<Boolean> verify(Long userId, Integer count) {
                throw new BusinessException("下单校验买买金报错," + cause.getMessage(), 500);
            }

            @Override
            public boolean isGiveBuy(String orderNo, Long userId) {
                throw new BusinessException("查询买送资格报错," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<Object> saveUserMember(OrderInfo orders) {
                throw new BusinessException("调用下单成为会员异常，" + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<Object> queryUserMemberInfoByUserId(Long userId) {
                throw new BusinessException("查询会员信息接口报错，" + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<Boolean> isMember(Long userId) {
                throw new BusinessException("查询判断用户是否是会员报错，" + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<List<UserRecommendOrder>> selectByGoodSku(Map<String, Object> map) {
                throw new BusinessException("订单调用, 判断该商品是展示 去写推荐 or 分享得返现，" + cause.getMessage(), 500);
            }


            @Override
            public ReturnData<List<UserRecommendOrder>> selectByOrderNo(Map<String, Object> map) {
                throw new BusinessException("订单调用, 判断该订单是展示 去写推荐 or 分享得返现，" + cause.getMessage(), 500);
            }


            @Override
            public ReturnData use(UseUserCouponVo useUserCouponVo) {
                throw new BusinessException("订单调用优惠券接口异常:" + cause.getMessage(), 500);
            }

            @Override
            public ReturnData batchReceive(UserCouponBatchVo userCouponBatchVo) {
                return new ReturnData(-1, "批量发放优惠券失败:" + cause.getMessage());
            }

            @Override
            public Integer exchageProc(OrdersMQDto dto) {
                throw new BusinessException("买买金抵扣失败:" + cause.getMessage(), 500);
            }
        };
    }
}
