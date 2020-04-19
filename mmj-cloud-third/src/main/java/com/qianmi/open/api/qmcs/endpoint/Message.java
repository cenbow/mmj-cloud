package com.qianmi.open.api.qmcs.endpoint;

import java.util.Map;

// just simple version
public class Message {

	public short messageType;

	public int statusCode;
	public String statusPhase;
	public int flag;
	public String token;

	public Map<String, Object> content;
}