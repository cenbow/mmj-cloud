package com.mmj.notice.controller;


import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.notice.model.WxQrcodeManager;
import com.mmj.notice.service.WxQrcodeManagerService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 公众号二维码 前端控制器
 * </p>
 *
 * @author shenfuding
 * @since 2019-08-13
 */
@RestController
@RequestMapping("/wxQrcodeManager")
public class WxQrcodeManagerController extends BaseController {

    @Autowired
    WxQrcodeManagerService wxQrcodeManagerService;

    @ApiOperation("公众号二维码保存")
    @PostMapping("save")
     public ReturnData<Object> save(@RequestBody WxQrcodeManager wxQrcodeManager){
        try {
            wxQrcodeManagerService.save(wxQrcodeManager);
        } catch (Exception e) {
            return initExcetionObjectResult(e.getMessage());
        }
        return initSuccessResult();
     }

    @ApiOperation("分页查询公众号二维码列表信息")
    @PostMapping("queryPage")
     public ReturnData<Page> queryPage(@RequestBody WxQrcodeManager wxQrcodeManager){
        Page<WxQrcodeManager> wxQrcodeManagerPage = wxQrcodeManagerService.queryPage(wxQrcodeManager);
        return initSuccessObjectResult(wxQrcodeManagerPage);
    }

    @ApiOperation("根据id查询公众号二维码信息")
    @PostMapping("query/{id}")
    public ReturnData<WxQrcodeManager> query(@PathVariable String id){
        WxQrcodeManager wxQrcodeManagerPage = wxQrcodeManagerService.query(id);
        return initSuccessObjectResult(wxQrcodeManagerPage);
    }
}

