package com.mmj.common.model;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * Zuul日志记录
 */
@Data
public class UserLog implements Serializable{
    
    private static final long serialVersionUID = -17372507989203440L;
    
    private Integer logId;
    
    private String serviceId;
    
    private String requestUri;
    
    private String requestMethod;
    
    private Long userId;
    
    private String requestHost;
    
    private Date requestTime;
    
    /**
     * 请求到响应所花的时间
     */
    private Long requestSpendTime;
    
    private Integer httpCode;
    
}
