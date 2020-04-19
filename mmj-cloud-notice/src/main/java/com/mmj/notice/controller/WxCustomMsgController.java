package com.mmj.notice.controller;


import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.notice.model.WxCustomMsg;
import com.mmj.notice.model.WxCustomMsgEx;
import com.mmj.notice.model.WxCustomMsgTxt;
import com.mmj.notice.service.WxCustomMsgService;
import com.mmj.notice.service.WxCustomMsgTxtService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 客服消息配置 前端控制器
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-27
 */
@RestController
@RequestMapping("/wxCustomMsg")
public class WxCustomMsgController extends BaseController {

    @Autowired
    WxCustomMsgService wxCustomMsgService;

    @Autowired
    WxCustomMsgTxtService wxCustomMsgTxtService;

    @ApiOperation("客服消息配置保存")
    @PostMapping("save")
    public ReturnData<WxCustomMsgEx> save(@RequestBody List<WxCustomMsgEx> wxCustomMsgExes){
        try {
            wxCustomMsgService.save(wxCustomMsgExes);
        } catch (Exception e) {
            return initExcetionObjectResult(e.getMessage());
        }
        return initSuccessResult();
    }

    @ApiOperation("客服消息查询,不包含关键字查询")
    @PostMapping("query/{appid}")
    public ReturnData<List<WxCustomMsg>> query(@PathVariable String appid){
        List<WxCustomMsg> wxCustomMsgExes = wxCustomMsgService.query(appid);
        return initSuccessObjectResult(wxCustomMsgExes);
    }

    @ApiOperation("查询客服消息的关键字回复")
    @PostMapping("queryKey")
    public ReturnData<Page<Map<String, String>>> queryKey(@RequestBody WxCustomMsgTxt wxCustomMsgTxt){
        Page<Map<String, String>> wxCustomMsgExes = wxCustomMsgService.queryKey(wxCustomMsgTxt);
        return initSuccessObjectResult(wxCustomMsgExes);
    }

    @ApiOperation("删除小程序关键字回复")
    @PostMapping("del")
    public ReturnData del(@RequestBody List<Integer> ids){
        ids.forEach( id ->{
            wxCustomMsgTxtService.deleteById(id);
        });
        return initSuccessResult();
    }

    @ApiOperation("预览发送客服消息")
    @PostMapping("send")
    public ReturnData send(@RequestBody List<WxCustomMsgEx> wxCustomMsgExes){
        wxCustomMsgService.send(wxCustomMsgExes);
        return initSuccessResult();
    }
}

