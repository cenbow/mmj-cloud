package com.mmj.order.utils;

import com.alibaba.fastjson.JSONObject;
import com.mmj.order.model.dto.PassingDataDto;
import org.apache.commons.lang.StringUtils;

public class PassingDataUtil {

    public static PassingDataDto disPassingData(String passingData){
        if (StringUtils.isBlank(passingData)) return null;
        return JSONObject.parseObject(passingData, PassingDataDto.class);
    }
}
