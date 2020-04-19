package com.mmj.notice.controller;


import com.mmj.common.controller.BaseController;
import com.mmj.notice.service.DelaySmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 短信延迟发送表 前端控制器
 * </p>
 *
 * @author cgf
 * @since 2019-08-30
 */
@RestController
@RequestMapping("/delaySms")
public class DelaySmsController extends BaseController {

    @Autowired
    private DelaySmsService delaySmsService;
}

