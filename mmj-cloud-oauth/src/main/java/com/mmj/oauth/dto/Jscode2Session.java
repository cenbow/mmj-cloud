package com.mmj.oauth.dto;

import lombok.Data;

@Data
public class Jscode2Session {
	
    private String openid;

    private String session_key;

    private String unionid;

    private int errcode;

    private String errmsg;

}