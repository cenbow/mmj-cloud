package com.mmj.common.exception;

import com.mmj.common.properties.SecurityConstants;

/**
 * 此异常主要是为了方便在service中返回给前端code为0的提示信息
 * @author shenfuding
 *
 */
public class CustomMessageException extends BaseException {
    
	private static final long serialVersionUID = -7831170690950156530L;
	
	private Integer code;
	
	private String message;

	public CustomMessageException(Integer code, String message) {
		this.code = code;
		this.message = message;
    }

	public Integer getCode() {
		return code == null ? SecurityConstants.EXCEPTION_CODE : code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
