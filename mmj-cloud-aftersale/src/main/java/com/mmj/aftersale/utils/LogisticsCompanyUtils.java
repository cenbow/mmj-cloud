package com.mmj.aftersale.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @Description: 快递公司获取(resources/data/logisticsCompany.json)
 * @Auther: KK
 * @Date: 2018/10/11
 */
@Component
public class LogisticsCompanyUtils {
    private List<LogisticsCompany> logisticsCompanies;

    @Value("classpath:data/logisticsCompany.json")
    private Resource logistics;

    @PostConstruct
    public void init(){
        InputStream inputStream = null;
        try {
            inputStream = logistics.getInputStream();
            JSONArray array = JSONObject.parseObject(inputStream, JSONArray.class);
            this.logisticsCompanies = JSONObject.parseArray(array.toJSONString(),LogisticsCompany.class);
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

    static class LogisticsCompany {
        private String lcCode;//快递公司简称
        private String logisticsCompany; //快递公司

        public String getLcCode() {
            return lcCode;
        }

        public void setLcCode(String lcCode) {
            this.lcCode = lcCode;
        }

        public String getLogisticsCompany() {
            return logisticsCompany;
        }

        public void setLogisticsCompany(String logisticsCompany) {
            this.logisticsCompany = logisticsCompany;
        }
    }

    public List<LogisticsCompany> getLogisticsCompanies() {
        return logisticsCompanies;
    }

    public void setLogisticsCompanies(List<LogisticsCompany> logisticsCompanies) {
        this.logisticsCompanies = logisticsCompanies;
    }

    public Resource getLogistics() {
        return logistics;
    }

    public void setLogistics(Resource logistics) {
        this.logistics = logistics;
    }
}
