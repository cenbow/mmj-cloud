package com.mmj.active.prizewheels.service.impl;

import com.mmj.active.prizewheels.model.PrizewheelsPrizeRecord;
import com.mmj.active.prizewheels.mapper.PrizewheelsPrizeRecordMapper;
import com.mmj.active.prizewheels.service.PrizewheelsPrizeRecordService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 转盘 - 我的奖品记录表 服务实现类
 * </p>
 *
 * @author shenfuding
 * @since 2019-06-26
 */
@Service
public class PrizewheelsPrizeRecordServiceImpl extends ServiceImpl<PrizewheelsPrizeRecordMapper, PrizewheelsPrizeRecord> implements PrizewheelsPrizeRecordService {
	
	@Autowired
	private PrizewheelsPrizeRecordMapper mapper;

	@Override
	public void updateUserId(long oldUserId, long newUserId) {
		mapper.updateUserId(oldUserId, newUserId);
	}

}
