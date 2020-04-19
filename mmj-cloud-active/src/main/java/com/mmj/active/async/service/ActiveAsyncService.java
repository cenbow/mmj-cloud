package com.mmj.active.async.service;

import com.alibaba.fastjson.JSONObject;
import com.mmj.common.model.UserMerge;

/**
 * 活动模块异步处理的业务
 * @author shenfuding
 *
 */
public interface ActiveAsyncService {
	
	void handleOfficalAccountReplyForPrizewheels(JSONObject jsonObject);
	
	void mergePrizewheelsActiveTables(UserMerge userMerge);

	void mergeThreeSaleFission(UserMerge userMerge);

	void mergeFocusInfo(UserMerge userMerge);
}
