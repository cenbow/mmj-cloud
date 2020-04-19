package com.mmj.third.kuaidi100.model;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @Description: 订阅请求实体
 * @Auther: KK
 * @Date: 2018/10/13
 */
public class PollRequest {
    private String company;
    private String number;
    private String key;
    private Map<String,String> parameters = Maps.newHashMap();

    public PollRequest setCompany(String company) {
        this.company = company;
        return this;
    }

    public PollRequest setNumber(String number) {
        this.number = number;
        return this;
    }

    public PollRequest setKey(String key) {
        this.key = key;
        return this;
    }

    public PollRequest setCallbackurl(String callbackurl){
        this.parameters.put("callbackurl",callbackurl);
        return this;
    }

    public String getCallbackurl(){
        if(this.parameters.containsKey("callbackurl")){
            return this.parameters.get("callbackurl");
        }
        return null;
    }

    public String getCompany() {
        return company;
    }

    public String getNumber() {
        return number;
    }

    public String getKey() {
        return key;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }
}
