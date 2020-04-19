package com.mmj.common.model;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
public class ReturnData<T> implements Serializable{

    
    /** @Fields serialVersionUID: */
      	
    private static final long serialVersionUID = -244831112300878039L;

    private Integer code;

    private String desc;

    private T data;

    private List<T> list;

    private Integer recordCount;

    public ReturnData() {}
    
    public ReturnData(Integer code, String desc, T data, List<T> list) {
        this.code = code;
        this.desc = desc;
        this.data = data;
        this.list = list;
    }
    
    public ReturnData(Integer code, String desc, T data) {
        this.code = code;
        this.desc = desc;
        this.data = data;
    }
    
    public ReturnData(Integer code, String desc, List<T> list) {
        this.code = code;
        this.desc = desc;
        this.list = list;
    }
    
    public ReturnData(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
