package com.mmj.notice.async;

import com.alibaba.fastjson.JSONObject;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.notice.service.DelaySmsService;
import com.mmj.notice.service.WxImageService;
import com.mmj.notice.service.WxdelayTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 本控制器为mmj-cloud-notice下共用，提供不需要进行令牌认证的方法给消息服务以及定时任务调度进行调用
 *
 * @author shenfuding
 */
@Slf4j
@RestController
@RequestMapping("/async")
@Api(value = "通知模块异步处理控制器")
public class NoticeAsyncController extends BaseController {


    @Autowired
    private DelaySmsService delaySmsService;

    @Autowired
    WxdelayTaskService wxdelayTaskService;

    @Autowired
    WxImageService wxImageService;

    @RequestMapping(value = "/sendSMS", method = RequestMethod.POST)
    public ReturnData<String> sendSMS() {
        log.info("发送短信定时任务被触发");
        try {
            delaySmsService.sendSMS();
            return initSuccessResult();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return initErrorObjectResult(e.getMessage());
        }
    }

    @ApiOperation("修复延时队列遗漏的数据")
    @PostMapping("/wxDelayTask/repair")
    public Object repair() {
        wxdelayTaskService.repair();
        return initSuccessResult();
    }

    @ApiOperation("免费送图片合成")
    @RequestMapping(value = "/freeGoodsCompose", method = RequestMethod.POST)
    public ReturnData<String> freeGoodsCompose(@RequestBody JSONObject params) {
        String image = wxImageService.freeGoodsCompose(params.getString("url"));
        return initSuccessObjectResult(image);
    }
}
