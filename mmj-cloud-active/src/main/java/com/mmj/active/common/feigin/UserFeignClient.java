package com.mmj.active.common.feigin;

import com.alibaba.fastjson.JSONObject;
import com.mmj.active.common.model.RedPackageUser;
import com.mmj.active.common.model.UserActive;
import com.mmj.active.common.model.UserMember;
import com.mmj.active.common.model.vo.UserCouponVo;
import com.mmj.active.cut.model.CutUser;
import com.mmj.common.interceptor.FeignRequestInterceptor;
import com.mmj.common.model.*;
import com.mmj.order.common.model.vo.RedPackageUserVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@FeignClient(name = "mmj-cloud-user", fallbackFactory = UserFallbackFactory.class, configuration = FeignRequestInterceptor.class)
public interface UserFeignClient {

    @RequestMapping(value = "/async/user/{id}", method = RequestMethod.POST)
    BaseUser getUserById(@PathVariable("id") Long id);

    @RequestMapping(value = "/user/userActive/activeQueryWinner", method = RequestMethod.POST)
    @ResponseBody
    UserActive queryWinner(@RequestBody UserActive userActive);

    @RequestMapping(value = "/user/userActive/queryJoinUserList", method = RequestMethod.POST)
    @ResponseBody
    List<UserActive> queryJoinUserList(@RequestBody UserActive userActive);

    /**
     * 新增砍价记录
     *
     * @param cutUser
     * @return
     */
    @RequestMapping(value = "/user/cutUser/add", method = RequestMethod.POST)
    @ResponseBody
    ReturnData addCutUser(@RequestBody CutUser cutUser);

    /**
     * 砍价记录列表
     *
     * @param cutUser
     * @return
     */
    @RequestMapping(value = "/user/cutUser/list", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<List<CutUser>> cutUsers(@RequestBody CutUser cutUser);

    /**
     * 查询用户的砍价记录列表
     *
     * @param cutUser
     * @return
     */
    @RequestMapping(value = "/user/cutUser/my", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<List<CutUser>> myCutList(@RequestBody CutUser cutUser);

    @RequestMapping(value = "/getRedPackage/{redCode}/{unionId}", method = RequestMethod.POST)
    RedPackageUser getRedPackage(@PathVariable("redCode") String redCode,
                                 @PathVariable("unionId") String unionId);

    @RequestMapping(value = "/updateRedPackage", method = RequestMethod.POST)
    boolean updateRedPackage(@RequestBody RedPackageUser redPackageUser);

    @RequestMapping(value = "/recommend/redPackageUser/redPacketInfo", method = RequestMethod.POST)
    RedPackageUser redPacketInfo(@RequestBody RedPackageUser redPackageUser);

    @RequestMapping(value = "/user/couponUser/hasReceive", method = RequestMethod.POST)
    ReturnData<Boolean> hasReceive(@Valid @RequestBody UserCouponVo userCouponVo);

    @RequestMapping(value = "/async/clickShare", method = RequestMethod.POST)
    ReturnData<String> clickShare(@RequestBody JSONObject uLog);

    @RequestMapping(value = "/member/query/{userId}", method = RequestMethod.POST)
    ReturnData<UserMember> queryUserMemberInfoByUserId(@PathVariable("userId") Long userId);

    @RequestMapping(value = "/async/getActiveByCode", method = RequestMethod.POST)
    List<UserActive> getActiveByCode(@RequestBody UserActive userActive);

    @RequestMapping(value = "/async/loginInfo/{loginName}", method = RequestMethod.POST)
    ReturnData<UserLogin> getUserLoginInfoByUserName(@PathVariable("loginName") String loginName);

    @RequestMapping(value = "/user/queryUserInfo", method = RequestMethod.POST)
    ReturnData<BaseUserDto> queryUserInfoByUserId(@RequestBody String param);

    /**
     * 查询会员总数量
     *
     * @return
     */
    @RequestMapping(value = "/async/totalCount", method = RequestMethod.POST)
    ReturnData<Integer> queryMemberTotalCount();

    @ApiOperation("根据优惠券ID获取优惠券信息")
    @RequestMapping(value = "/user/couponUser/my/couponId/{couponId}", method = RequestMethod.POST)
    ReturnData<List<UserCouponDto>> myCouponInfoByCouponId(@PathVariable("couponId") Integer couponId);

    /**
     * 添加红包码
     *
     * @param redPackage
     * @return
     */
    @RequestMapping(value = "/async/addRedPackage", method = RequestMethod.POST)
    boolean addRedPackage(@RequestBody RedPackageUserVo redPackage);
}
