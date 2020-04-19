package com.mmj.common.model;

import java.io.Serializable;
import lombok.Data;

@Data
public class GrayRule implements Serializable{
    
    
    /** @Fields serialVersionUID: */
      	
    private static final long serialVersionUID = 6542462079459917728L;

    /**
     *  最大版本号，用于基于版本灰度发布
     */
    private String maxVersion;
    
    /**
     * 灰度类型  VERSION 按版本发布  USER 按指定用户灰度  
     */
    private String grayType;
    
    /**
     * 基于用户灰度时指定用户携带编码
     */
    private String grayCode;
    
    public GrayRule() {}
    
    public GrayRule(String maxVersion, String grayType, String grayCode) {
        this.maxVersion = maxVersion;
        this.grayType = grayType;
        this.grayCode = grayCode;
    }

}
