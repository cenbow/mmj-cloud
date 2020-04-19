package com.mmj.user.member.controller;


import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.user.member.dto.MyKingExchangeParam;
import com.mmj.user.member.model.UserKingLog;
import com.mmj.user.member.service.KingUserService;
import com.mmj.user.member.service.UserKingLogService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 用户买买金表 前端控制器
 * </p>
 *
 * @author cgf
 * @since 2019-07-10
 */
@RestController
@RequestMapping("/member/kingUser")
@Slf4j
public class KingUserController extends BaseController {

    private final UserKingLogService userKingLogService;

    private final KingUserService kingUserService;

    public KingUserController(UserKingLogService userKingLogService, KingUserService kingUserService) {
        this.userKingLogService = userKingLogService;
        this.kingUserService = kingUserService;
    }
/*

    @RequestMapping(value = "/clickShare", method = RequestMethod.POST)
    public ReturnData<String> clickShare(@RequestBody UserKingLog uLog) {
        if (StringUtils.isBlank(uLog.getShareType()) || null == uLog.getUserId()
                || null == uLog.getGoodId()) {
            log.info("mark 参数异常");
            return initErrorObjectResult("参数错误");
        }
        try {
            userKingLogService.clickInsert(uLog);
            return initSuccessResult();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return initErrorObjectResult(e.getMessage());
        }
    }
*/

    /**
     * 买买金流水
     */
    @RequestMapping(value = "/getKingLog", method = RequestMethod.POST)
    public ReturnData<UserKingLog> getKingLog() {
        try {
            return initSuccessListResult(userKingLogService.getMyKingLog());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return initErrorObjectResult(e.getMessage());
        }
    }

    /**
     * 获取我的买买金
     *
     * @return
     */
    @RequestMapping(value = "/getMyKing", method = RequestMethod.POST)
    public ReturnData<Map<String, Object>> getMyKing(Long userId) {
        try {
            return initSuccessObjectResult(kingUserService.getMyKing(userId));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return initErrorObjectResult(e.getMessage());
        }
    }

    @RequestMapping(value = "/verifyMMKing/{userId}/{count}", method = RequestMethod.POST)
    public ReturnData<Boolean> verify(@PathVariable("userId") Long userId,
                                      @PathVariable("count") Integer count) {
        try {
            return initSuccessObjectResult(kingUserService.verify(userId, count));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return initErrorObjectResult(e.getMessage());
        }
    }


    @RequestMapping(value = "/getActCnt", method = RequestMethod.POST)
    public ReturnData<Map<String, Object>> getActCnt() {
        try {
            return initSuccessObjectResult(userKingLogService.getActCnt());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return initErrorObjectResult(e.getMessage());
        }
    }
/*

    @RequestMapping(value = "/orderKingProd", method = RequestMethod.POST)
    public int orderKingProd(@RequestBody Map<String, Object> map) {
        return userKingLogService.orderKingProd(map);
    }
*/

    @RequestMapping(value = "/degradeProces/{orderNo}/{userId}", method = RequestMethod.POST)
    public boolean degradeProces(@PathVariable("orderNo") String orderNo, @PathVariable("userId") Long userId) {
        try {
            return userKingLogService.degradeProces(orderNo, userId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @RequestMapping(value = "/oweKingNum/{orderNo}/{userId}", method = RequestMethod.POST)
    public int getOweKingNum(@PathVariable("orderNo") String orderNo, @PathVariable("userId") Long userId) {
        try {
            return userKingLogService.getOweKingNum(orderNo, userId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return 0;
        }
    }

    @RequestMapping(value = "/kingExchangeInfo", method = RequestMethod.POST)
    @ApiOperation("计算买买金抵扣金额")
    public ReturnData<Map<String, Object>> getMyKingExchangeInfo(@RequestBody MyKingExchangeParam param) {
        log.info("-->支付前获取买买金抵扣信息，参数：{}", param);
        return this.initSuccessObjectResult(kingUserService.getMyKingExchangeInfo(param));
    }

}

