package com.mmj.aftersale.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * @Description: 地区json解析
 * @Auther: KK
 * @Date: 2018/10/18
 */
@Component
public class AreaDataUtils {
    private final static String DEFAULT_86 = "86";

    private JSONObject areaData;

    @Value("classpath:data/china-area-data.json")
    private Resource logistics;
    @PostConstruct
    public void init(){
        InputStream inputStream = null;
        try {
            inputStream = logistics.getInputStream();
            this.areaData = JSONObject.parseObject(inputStream, JSONObject.class);
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if(inputStream != null){
                try {
                    inputStream.close();
                }catch (IOException e){}
            }
        }
    }

    /**
     * @Description: 根据areaCode获取省市区
     * @author: KK
     * @date: 2018/10/18
     * @param: [areaCode]
     * @return: java.lang.String
     */
    public String getArea(String areaCode){
        if(StringUtils.isBlank(areaCode) || areaCode.length() < 6)
            return "";
        String a = areaCode.substring(0,2) + "0000";
        String b = areaCode.substring(0,4) + "00";
        String c = areaCode;
        String aStr = get(a,DEFAULT_86);
        String bStr = StringUtils.isBlank(aStr) ? "" : get(b,a);
        String cStr = StringUtils.isBlank(bStr) ? "" : get(c,b);
        return aStr+bStr+cStr;
    }

    public String get(String code,String parentCode){
        Object o = this.areaData.get(parentCode);
        if(Objects.isNull(o)) return "";
        return ((JSONObject)o).getString(code);
    }

}
