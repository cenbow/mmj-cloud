package com.mmj.notice.controller;


import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.notice.model.WxForm;
import com.mmj.notice.service.WxFormService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 微信模板消息 前端控制器
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-19
 */
@RestController
@RequestMapping("wxForm")
public class WxFormController extends BaseController {

    @Autowired
    WxFormService wxFormService;

    @ApiOperation("保存模板消息id")
    @PostMapping("save")
    public ReturnData<WxForm> save(@RequestBody WxForm wxForm){
        wxForm = wxFormService.save(wxForm);
        return initSuccessObjectResult(wxForm);
    }

    @ApiOperation("删除七天以前的formid")
    @PostMapping("del")
    public ReturnData del(){
         wxFormService.del();
        return initSuccessResult();
    }
}

