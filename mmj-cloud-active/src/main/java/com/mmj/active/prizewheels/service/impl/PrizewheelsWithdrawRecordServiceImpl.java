package com.mmj.active.prizewheels.service.impl;

import com.mmj.active.prizewheels.model.PrizewheelsWithdrawRecord;
import com.mmj.active.prizewheels.mapper.PrizewheelsWithdrawRecordMapper;
import com.mmj.active.prizewheels.service.PrizewheelsWithdrawRecordService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 转盘活动 - 用户提现记录表 服务实现类
 * </p>
 *
 * @author shenfuding
 * @since 2019-06-26
 */
@Service
public class PrizewheelsWithdrawRecordServiceImpl extends ServiceImpl<PrizewheelsWithdrawRecordMapper, PrizewheelsWithdrawRecord> implements PrizewheelsWithdrawRecordService {

	@Autowired
	private PrizewheelsWithdrawRecordMapper mapper;
	
	@Override
	public void updateUserId(long oldUserId, long newUserId) {
		mapper.updateUserId(oldUserId, newUserId);
	}

}
