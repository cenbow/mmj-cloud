package com.mmj.common.model;

/**
 * 
 * @author shenfuding
 *
 */
public class UserMerge {
	
	/**
	 * 旧用户ID
	 */
	private Long oldUserId;
	
	/**
	 * 要改成的新的用户ID
	 */
	private Long newUserId;

	public UserMerge() {
	}

	public UserMerge(Long oldUserId, Long newUserId) {
		this.oldUserId = oldUserId;
		this.newUserId = newUserId;
	}

	public Long getOldUserId() {
		return oldUserId;
	}

	public void setOldUserId(Long oldUserId) {
		this.oldUserId = oldUserId;
	}

	public Long getNewUserId() {
		return newUserId;
	}

	public void setNewUserId(Long newUserId) {
		this.newUserId = newUserId;
	}

}
