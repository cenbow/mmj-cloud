package com.mmj.third.jushuitan.model.response;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @description: 操作返回
 * @auther: KK
 * @date: 2019/6/6
 */
@Data
public class JushuitanResponse {
    /**
     * 非0失败
     */
    private Integer code;
    /**
     * true:false
     */
    @JsonProperty("issuccess")
    @JSONField(name = "issuccess")
    private Boolean issuccess;
    /**
     * 错误信息
     */
    private String msg;
}
