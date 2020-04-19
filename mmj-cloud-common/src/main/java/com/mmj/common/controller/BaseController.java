package com.mmj.common.controller;

import java.util.List;
import org.apache.commons.lang.StringUtils;
import com.mmj.common.model.ReturnData;
import com.mmj.common.properties.SecurityConstants;

public class BaseController {
    

    /**
     * @Description 初始化成功返回Object和list结果
     */
    public <T> ReturnData<T> initSuccessObjectResult(T t,String msg) {
        ReturnData<T> rd = new ReturnData<>();
        rd.setCode(SecurityConstants.SUCCESS_CODE);
        rd.setDesc(StringUtils.isEmpty(msg)?"success":msg);
        rd.setData(t);
        return rd;
    }

    public <T> ReturnData<T> initSuccessObjectResult(T t) {
        ReturnData<T> rd = new ReturnData<>();
        rd.setCode(SecurityConstants.SUCCESS_CODE);
        rd.setDesc("success");
        rd.setData(t);
        return rd;
    }
    
    public <T> ReturnData<T> initSuccessListResult(List<T> list) {
        ReturnData<T> rd = new ReturnData<>();
        rd.setCode(SecurityConstants.SUCCESS_CODE);
        rd.setDesc("success");
        rd.setList(list);
        return rd;
    }
    
    public <T> ReturnData<T> initSuccessListResult(String msg, List<T> list) {
        ReturnData<T> rd = new ReturnData<>();
        rd.setCode(SecurityConstants.SUCCESS_CODE);
        rd.setDesc(msg);
        rd.setList(list);
        return rd;
    }
    
    public <T> ReturnData<T> initSuccessResult() {
        ReturnData<T> rd = new ReturnData<>();
        rd.setCode(SecurityConstants.SUCCESS_CODE);
        rd.setDesc("success");
        rd.setData(null);
        return rd;
    }


    /**
     * @Description 初始化失败返回Object结果
     */
    public <T> ReturnData<T> initErrorObjectResult(String desc) {
        ReturnData<T> rd = new ReturnData<>();
        rd.setCode(SecurityConstants.EXCEPTION_CODE);
        rd.setDesc(desc);
        return rd;
    }


    /**
     * @Description 初始化失败返回Object结果
     */
    public <T> ReturnData<T> initExcetionObjectResult(String desc) {
        ReturnData<T> rd = new ReturnData<>();
        rd.setCode(SecurityConstants.EXCEPTION_CODE);
        rd.setDesc(desc);
        return rd;
    }

}
