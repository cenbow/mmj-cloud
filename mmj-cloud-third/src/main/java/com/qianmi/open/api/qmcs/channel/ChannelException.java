package com.qianmi.open.api.qmcs.channel;

import com.qianmi.open.api.qmcs.QmcsException;

public class ChannelException extends QmcsException {

	private static final long serialVersionUID = -7096550837646522371L;

	public ChannelException(String message) {
		super(message);
	}

	public ChannelException(int errorCode, String message) {
		super(errorCode, message);
	}

	public ChannelException(String message, Exception innerException) {
		super(message, innerException);
	}
	
	public ChannelException(String message, Throwable innerException) {
		super(message, innerException);
	}
}
