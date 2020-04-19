package com.mmj.user.recommend.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.model.ReturnData;
import com.mmj.common.utils.SecurityUserUtil;
import com.mmj.user.recommend.model.RedPackageUser;
import com.mmj.user.recommend.service.RedPackageUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 用户红包表 前端控制器
 * </p>
 *
 * @author dashu
 * @since 2019-06-20
 */
@RestController
@RequestMapping("/recommend/redPackageUser")
@Slf4j
public class RedPackageUserController extends BaseController {

    @Autowired
    private RedPackageUserService redPackageUserService;

    @RequestMapping(value = "/getRedPackage/{redCode}/{unionId}", method = RequestMethod.POST)
    public RedPackageUser getRedPackage(@PathVariable("redCode") String redCode,
                                        @PathVariable("unionId") String unionId) {
        try {
            RedPackageUser packageUser = new RedPackageUser();
            packageUser.setUnionId(unionId);
            packageUser.setPackageCode(redCode);
            EntityWrapper<RedPackageUser> wrapper = new EntityWrapper<>(packageUser);
            return redPackageUserService.selectOne(wrapper);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;

    }

    @RequestMapping(value = "/updateRedPackage", method = RequestMethod.POST)
    public boolean updateRedPackage(@RequestBody RedPackageUser redPackageUser) {
        try {
            if (null == redPackageUser.getPackageId() || null == redPackageUser.getPackageStatus())
                return false;
            return redPackageUserService.updateById(redPackageUser);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }


    @RequestMapping(value = "/redPacketInfo", method = RequestMethod.POST)
    public RedPackageUser redPacketInfo(@RequestBody RedPackageUser redPackageUser) {
        try {
            EntityWrapper<RedPackageUser> wrapper = new EntityWrapper<>(redPackageUser);
            return redPackageUserService.selectOne(wrapper);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    @RequestMapping(value = "/getRedPacket/{orderNo}", method = RequestMethod.POST)
    public ReturnData<RedPackageUser> getRedPacket(@PathVariable("orderNo") String orderNo) {
        try {
            JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
            if (null == userDetails)
                return initExcetionObjectResult("用户未登录");
            RedPackageUser redPackageUser = new RedPackageUser();
            redPackageUser.setOrderNo(orderNo);
            redPackageUser.setUserId(userDetails.getUserId());
            EntityWrapper<RedPackageUser> wrapper = new EntityWrapper<>(redPackageUser);
            redPackageUser = redPackageUserService.selectOne(wrapper);
            return initSuccessObjectResult(redPackageUser);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return initExcetionObjectResult(e.getMessage());
        }
    }
}