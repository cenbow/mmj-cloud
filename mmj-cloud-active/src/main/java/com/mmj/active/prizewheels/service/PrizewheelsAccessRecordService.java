package com.mmj.active.prizewheels.service;

import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.active.prizewheels.model.PrizewheelsAccessRecord;

/**
 * <p>
 * 用户访问转盘活动的时间记录表 服务类
 * </p>
 *
 * @author shenfuding
 * @since 2019-06-26
 */
public interface PrizewheelsAccessRecordService extends IService<PrizewheelsAccessRecord> {

	/**
	 * 保存访问记录
	 * @param userId
	 */
	void save(Long userId);
	
	/**
	 * 获取12小时内访问过转盘活动的用户
	 * @param accessTime
	 * @return
	 */
	List<PrizewheelsAccessRecord> getLatestActiveUser(Date accessTime);
	
	/**
	 * 修改userId
	 * @param oldUserId
	 * @param newUserId
	 */
	void updateUserId(long oldUserId, long newUserId);
	
}
