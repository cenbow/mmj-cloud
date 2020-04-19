package com.qianmi.open.api.qmcs.endpoint;

public abstract class MessageScheduleTask implements Runnable {
	private Message message;

	public MessageScheduleTask(Message message) {
		this.message = message;
	}

	public Message getMessage() {
		return this.message;
	}
}
