package com.mmj.common.utils;

import com.mmj.common.model.ReturnData;

public class ResultUtil {
    
    public static ReturnData<Object> success(Object object) {
        ReturnData<Object> result = new ReturnData<Object>();
        result.setCode(0);
        result.setDesc("success");;
        result.setData(object);
        return result;
    }

    public static ReturnData<Object> success() {
        return success(null);
    }

    public static ReturnData<Object> error(Integer code, String msg) {
        ReturnData<Object> result = new ReturnData<Object>();
        result.setCode(code);
        result.setDesc(msg);
        return result;
    }
}
