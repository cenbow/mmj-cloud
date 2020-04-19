package com.mmj.order.common.feign;

import com.mmj.common.interceptor.FeignRequestInterceptor;
import com.mmj.common.model.BaseUser;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.order.OrdersMQDto;
import com.mmj.order.common.model.vo.*;
import com.mmj.order.model.OrderInfo;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@FeignClient(name = "mmj-cloud-user", fallbackFactory = UserFallbackFactory.class, configuration = FeignRequestInterceptor.class)
public interface UserFeignClient {

    @RequestMapping(value = "/async/user/{id}", method = RequestMethod.POST)
    BaseUser getByById(@PathVariable("id") Long id);

    @RequestMapping(value = "/async/userShard/get/{shardTo}", method = RequestMethod.POST)
    ReturnData<UserShardVo> getFreeOrderRelation(@PathVariable("shardTo") Long shardTo);

    @RequestMapping(value = "/async/userShard/del/{shardTo}", method = RequestMethod.POST)
    boolean delFreeOrderRelation(@PathVariable("shardTo") Long shardTo);

    @RequestMapping(value = "/async/addRedPackage", method = RequestMethod.POST)
    boolean addRedPackage(@RequestBody RedPackageUserVo redPackage);

    /**
     * 清空购物车
     *
     * @param removeVo
     * @return
     */
    @RequestMapping(value = "/shopCart/removes", method = RequestMethod.POST)
    ReturnData removes(@Valid @RequestBody ShopCartsRemoveVo removeVo);

    /**
     * 加入购物车
     *
     * @param addVo
     * @return
     */
    @RequestMapping(value = "/shopCart/add", method = RequestMethod.POST)
    ReturnData add(@Valid @RequestBody ShopCartsAddVo addVo);

    @RequestMapping(value = "/async/orderKingProd", method = RequestMethod.POST)
    int orderKingProc(@RequestBody Map<String, Object> map);

    @RequestMapping(value = "/member/kingUser/verifyMMKing/{userId}/{count}", method = RequestMethod.POST)
    ReturnData<Boolean> verify(@PathVariable("userId") Long userId,
                               @PathVariable("count") Integer count);

    @RequestMapping(value = "/async/isGiveBuy/{orderNo}/{userId}", method = RequestMethod.POST)
    boolean isGiveBuy(@PathVariable("orderNo") String orderNo, @PathVariable("userId") Long userId);

    /**
     * 下单成为会员
     *
     * @param orders
     * @return
     */
    @RequestMapping(value = "/member/send/saveUserMember", method = RequestMethod.POST)
    ReturnData<Object> saveUserMember(OrderInfo orders);

    /**
     * 查询会员信息
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "member/query/{userId}", method = RequestMethod.POST)
    ReturnData<Object> queryUserMemberInfoByUserId(@PathVariable("userId") Long userId);


    /**
     * 查询判断用户是否是会员
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "/user/isMember/{userId}", method = RequestMethod.POST)
    ReturnData<Boolean> isMember(@PathVariable("userId") Long userId);

    /**
     * 给订单调用, 判断该商品是展示 去写推荐 or 分享得返现
     */
    @PostMapping("/recommend/userRecommend/selectByGoodSku")
    @ResponseBody
    ReturnData<List<UserRecommendOrder>> selectByGoodSku(@RequestBody Map<String, Object> map);


    /**
     * &
     * 给订单调用, 判断该订单是展示 去写推荐 or 分享得返现
     */
    @PostMapping("/recommend/userRecommend/selectByOrderNo")
    @ResponseBody
    ReturnData<List<UserRecommendOrder>> selectByOrderNo(@RequestBody Map<String, Object> map);


    /**
     * 使用与解绑优惠券
     *
     * @param useUserCouponVo
     * @return
     */
    @RequestMapping(value = "/async/coupon/use", method = RequestMethod.POST)
    ReturnData use(@Valid @RequestBody UseUserCouponVo useUserCouponVo);

    /**
     * 批量领取优惠券
     *
     * @param userCouponBatchVo
     * @return
     */
    @RequestMapping(value = "/async/receive/batch", method = RequestMethod.POST)
    ReturnData batchReceive(@RequestBody UserCouponBatchVo userCouponBatchVo);

    @RequestMapping(value = "/async/exchageProc", method = RequestMethod.POST)
    Integer exchageProc(@RequestBody OrdersMQDto dto);
}
