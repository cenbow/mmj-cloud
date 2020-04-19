package com.mmj.active.prizewheels.service;

import com.mmj.active.prizewheels.model.PrizewheelsRedpacketRecord;
import com.baomidou.mybatisplus.service.IService;

/**
 * <p>
 * 转盘活动 - 用户红包变更记录表 服务类
 * </p>
 *
 * @author shenfuding
 * @since 2019-06-26
 */
public interface PrizewheelsRedpacketRecordService extends IService<PrizewheelsRedpacketRecord> {
	
	void updateUserId(long oldUserId, long newUserId);

}
