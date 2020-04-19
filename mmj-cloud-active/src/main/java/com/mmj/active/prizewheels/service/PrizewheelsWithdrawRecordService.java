package com.mmj.active.prizewheels.service;

import com.mmj.active.prizewheels.model.PrizewheelsWithdrawRecord;
import com.baomidou.mybatisplus.service.IService;

/**
 * <p>
 * 转盘活动 - 用户提现记录表 服务类
 * </p>
 *
 * @author shenfuding
 * @since 2019-06-26
 */
public interface PrizewheelsWithdrawRecordService extends IService<PrizewheelsWithdrawRecord> {
	
	void updateUserId(long oldUserId, long newUserId);

}
