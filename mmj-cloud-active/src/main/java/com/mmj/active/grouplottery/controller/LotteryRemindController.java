package com.mmj.active.grouplottery.controller;


import com.mmj.active.grouplottery.service.LotteryRemindService;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 用户关注抽奖信息表 前端控制器
 * </p>
 *
 * @author cgf
 * @since 2019-08-26
 */
@RestController
@RequestMapping("/grouplottery/lotteryRemind")
@Slf4j
public class LotteryRemindController extends BaseController {

    @Autowired
    private LotteryRemindService remindService;

    @RequestMapping(value = "/openC/{userId}", method = RequestMethod.POST)
    public ReturnData openC(@PathVariable("userId") Long userId) {
        try {
            remindService.remind(userId, "C", "remind");
            return initSuccessResult();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return initExcetionObjectResult(e.getMessage());
        }
    }

    @RequestMapping(value = "/openO/{userId}", method = RequestMethod.POST)
    public ReturnData openO(@PathVariable("userId") Long userId) {
        try {
            remindService.remind(userId, "O", "remind");
            return initSuccessResult();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return initExcetionObjectResult(e.getMessage());
        }
    }

    @RequestMapping(value = "/closeC/{userId}", method = RequestMethod.POST)
    public ReturnData closeC(@PathVariable("userId") Long userId) {
        try {
            remindService.remind(userId, "C", "cancle");
            return initSuccessResult();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return initExcetionObjectResult(e.getMessage());
        }
    }

    @RequestMapping(value = "/closeO/{userId}", method = RequestMethod.POST)
    public ReturnData closeO(@PathVariable("userId") Long userId) {
        try {
            remindService.remind(userId, "O", "cancle");
            return initSuccessResult();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return initExcetionObjectResult(e.getMessage());
        }
    }
}

