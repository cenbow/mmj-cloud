package com.mmj.active.prizewheels.service;

import com.mmj.active.prizewheels.model.PrizewheelsCoinsRecord;
import com.baomidou.mybatisplus.service.IService;

/**
 * <p>
 * 转盘活动 - 用户买买币变更记录表 服务类
 * </p>
 *
 * @author shenfuding
 * @since 2019-06-26
 */
public interface PrizewheelsCoinsRecordService extends IService<PrizewheelsCoinsRecord> {

	/**
	 * 获取用户当天是否有签到
	 * @param userid
	 * @return
	 */
	boolean queryUserCurrentDayHasSign(Long userId);
	
	/**
	 * 查询endDate之前连续签到的天数，endDate所在的日期不纳入统计
	 * @param userid
	 * @param endDate
	 * @return
	 */
	Integer getContinuousSigninDays(Long userid, String endDate);
	
	/**
	 * 修改userId
	 * @param oldUserId
	 * @param newUserId
	 */
	void updateUserId(long oldUserId, long newUserId);
	
}
