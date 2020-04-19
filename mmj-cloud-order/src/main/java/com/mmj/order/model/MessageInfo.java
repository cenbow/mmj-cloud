package com.mmj.order.model;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
public class MessageInfo implements Serializable{
    
    
    /** @Fields serialVersionUID: */
      	
    private static final long serialVersionUID = 6204292737329527348L;

    private String id;

    private String msg;

    private  String sendTime;

}
