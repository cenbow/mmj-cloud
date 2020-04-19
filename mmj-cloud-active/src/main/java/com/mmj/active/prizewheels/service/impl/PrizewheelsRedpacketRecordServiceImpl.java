package com.mmj.active.prizewheels.service.impl;

import com.mmj.active.prizewheels.model.PrizewheelsRedpacketRecord;
import com.mmj.active.prizewheels.mapper.PrizewheelsRedpacketRecordMapper;
import com.mmj.active.prizewheels.service.PrizewheelsRedpacketRecordService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 转盘活动 - 用户红包变更记录表 服务实现类
 * </p>
 *
 * @author shenfuding
 * @since 2019-06-26
 */
@Service
public class PrizewheelsRedpacketRecordServiceImpl extends ServiceImpl<PrizewheelsRedpacketRecordMapper, PrizewheelsRedpacketRecord> implements PrizewheelsRedpacketRecordService {

	@Autowired
	private PrizewheelsRedpacketRecordMapper mapper;
	
	@Override
	public void updateUserId(long oldUserId, long newUserId) {
		mapper.updateUserId(oldUserId, newUserId);
	}

}
