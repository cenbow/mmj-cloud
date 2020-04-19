package com.mmj.active.common.service;


public interface ShareGoodService {
	
	void sendMsg(Long shareUserId, Integer goodId);
	
	/**
	 * 处理分享商品的任务
	 * @param shareUserId
	 * @param goodId
	 */
	void shareGood(Long userId, Long shareUserId, Integer goodId);
	
	void handlePrizewheels(Long userId, Long shareUserId, Integer goodId);

	void handleMMKing(Long userId, Long shareUserId, Integer goodId);
}
