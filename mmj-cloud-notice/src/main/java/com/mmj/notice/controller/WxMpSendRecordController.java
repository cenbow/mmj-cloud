package com.mmj.notice.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.notice.model.WxMpSendRecord;
import com.mmj.notice.service.WxMpSendRecordService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 公众号主动推送消息发送记录表 前端控制器
 * </p>
 *
 * @author shenfuding
 * @since 2019-08-10
 */
@RestController
@RequestMapping("/wxMpSendRecord")
public class WxMpSendRecordController extends BaseController {

    @Autowired
    WxMpSendRecordService wxMpSendRecordService;

    @ApiOperation("查询公众号信息的客服消息群发记录")
    @PostMapping("query")
    public ReturnData<Page<WxMpSendRecord>> query(@RequestBody String params){
        JSONObject paramJson = JSON.parseObject(params);
        Page page = new Page(paramJson.getInteger("currentPage"), paramJson.getInteger("pageSize"));
        Page<WxMpSendRecord> wxMpSendRecordPage = wxMpSendRecordService.query(page);
        return initSuccessObjectResult(wxMpSendRecordPage);
    }

    @ApiOperation("再次发送群发的记录")
    @PostMapping("send/{appid}")
    public ReturnData send(@PathVariable String appid){
        wxMpSendRecordService.send(appid);
        return initSuccessResult();
    }
}

