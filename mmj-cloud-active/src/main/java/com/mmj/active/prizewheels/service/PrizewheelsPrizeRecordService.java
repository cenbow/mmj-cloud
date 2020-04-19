package com.mmj.active.prizewheels.service;

import com.mmj.active.prizewheels.model.PrizewheelsPrizeRecord;
import com.baomidou.mybatisplus.service.IService;

/**
 * <p>
 * 转盘 - 我的奖品记录表 服务类
 * </p>
 *
 * @author shenfuding
 * @since 2019-06-26
 */
public interface PrizewheelsPrizeRecordService extends IService<PrizewheelsPrizeRecord> {
	
	/**
	 * 修改userId
	 * @param oldUserId
	 * @param newUserId
	 */
	void updateUserId(long oldUserId, long newUserId);

}
