package com.mmj.notice.controller;


import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.notice.service.WxBoxRedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 物流箱红包码 前端控制器
 * </p>
 *
 * @author shenfuding
 * @since 2019-08-12
 */
@RestController
@RequestMapping("wxBoxRed")
public class WxBoxRedController extends BaseController {

    @Autowired
    WxBoxRedService wxBoxRedService;

    /**
     * 发送物流箱上面的红包
     * @param redCode
     * @return
     */
    @PostMapping("send/{redCode}")
    public ReturnData<Object> send(@PathVariable String redCode){
        try {
            Object result = wxBoxRedService.send(redCode);
            return initSuccessObjectResult(result);
        } catch (Exception e) {
            return initExcetionObjectResult(e.getMessage());
        }
    }
}

