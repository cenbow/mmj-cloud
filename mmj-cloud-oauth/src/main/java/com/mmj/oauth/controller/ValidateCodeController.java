package com.mmj.oauth.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.oauth.code.mobile.SmsValidateCodeHelp;

@RestController
@RequestMapping("/code")
public class ValidateCodeController extends BaseController{

    @Autowired
    private SmsValidateCodeHelp smsValidateCodeHelp;

    @RequestMapping(value="/mobile", method=RequestMethod.POST)
    public ReturnData<String> createCode(HttpServletRequest request){
        smsValidateCodeHelp.send(request);
        return initSuccessObjectResult(null);
    }
    
    
}
