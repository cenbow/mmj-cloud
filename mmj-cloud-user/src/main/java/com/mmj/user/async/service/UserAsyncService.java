package com.mmj.user.async.service;

import com.mmj.common.model.UserMerge;
import org.springframework.scheduling.annotation.Async;

public interface UserAsyncService {
	
	/**
	 * 处理会员表数据合并
	 * @param userMerge
	 */
	void mergeUserMemberTables(UserMerge userMerge);

	/**
	 * 处理收货地址管理表数据合并
	 * @param userMerge
	 */
	void mergeBaseUserAddrTables(UserMerge userMerge);

	/**
	 * 处理商品推荐表数据合并
	 * @param userMerge
	 */
	void mergeUserRecommend(UserMerge userMerge);

	/**
	 * 处理推荐返现表数据合并
	 * @param userMerge
	 */
	void mergeUserShard(UserMerge userMerge);

	/**
	 * 处理流量池关注信息表数据合并
	 * @param userMerge
	 */
	void mergeUserFocus(UserMerge userMerge);

    @Async
    void mergeMMKing(UserMerge userMerge);
}
