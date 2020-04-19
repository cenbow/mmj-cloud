package com.mmj.active.prizewheels.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.active.prizewheels.mapper.PrizewheelsCoinsRecordMapper;
import com.mmj.active.prizewheels.model.PrizewheelsCoinsRecord;
import com.mmj.active.prizewheels.service.PrizewheelsCoinsRecordService;

/**
 * <p>
 * 转盘活动 - 用户买买币变更记录表 服务实现类
 * </p>
 *
 * @author shenfuding
 * @since 2019-06-26
 */
@Service
public class PrizewheelsCoinsRecordServiceImpl extends ServiceImpl<PrizewheelsCoinsRecordMapper, PrizewheelsCoinsRecord> implements PrizewheelsCoinsRecordService {
	
	@Autowired
	private PrizewheelsCoinsRecordMapper mapper;

	@Override
	public boolean queryUserCurrentDayHasSign(Long userId) {
		int signCount = mapper.queryUserCurrentDaySignCount(userId);
		return signCount > 0 ? true : false;
	}

	@Override
	public Integer getContinuousSigninDays(Long userid, String endDate) {
		return mapper.getContinuousSigninDays(userid, endDate);
	}

	@Override
	public void updateUserId(long oldUserId, long newUserId) {
		mapper.updateUserId(oldUserId, newUserId);
	}
	
	
}
