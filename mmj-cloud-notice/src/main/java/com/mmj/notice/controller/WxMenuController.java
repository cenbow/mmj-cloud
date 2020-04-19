package com.mmj.notice.controller;


import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.notice.model.WxMenuEx;
import com.mmj.notice.service.WxMenuService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  公众号菜单栏配置表 前端控制器
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-26
 */
@RestController
@RequestMapping("/wxMenu")
public class WxMenuController extends BaseController {

    @Autowired
    WxMenuService wxMenuService;

    @ApiOperation(value="保存公众号菜单栏信息")
    @PostMapping("save")
    public ReturnData<WxMenuEx> save(@RequestBody List<WxMenuEx> wxMenuExes){
        wxMenuService.save(wxMenuExes);
        return initSuccessResult();
    }

    @ApiOperation(value="查询公众号菜单栏配置信息")
    @PostMapping("query/{appid}")
    public ReturnData<WxMenuEx> query(@PathVariable("appid") String appid){
        WxMenuEx wxMenuEx = wxMenuService.query(appid);
        return initSuccessObjectResult(wxMenuEx);
    }
}

