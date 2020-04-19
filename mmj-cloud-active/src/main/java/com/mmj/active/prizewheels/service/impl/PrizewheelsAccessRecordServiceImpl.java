package com.mmj.active.prizewheels.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.active.prizewheels.mapper.PrizewheelsAccessRecordMapper;
import com.mmj.active.prizewheels.model.PrizewheelsAccessRecord;
import com.mmj.active.prizewheels.service.PrizewheelsAccessRecordService;

/**
 * <p>
 * 用户访问转盘活动的时间记录表 服务实现类
 * </p>
 *
 * @author shenfuding
 * @since 2019-06-26
 */
@Service
public class PrizewheelsAccessRecordServiceImpl extends ServiceImpl<PrizewheelsAccessRecordMapper, PrizewheelsAccessRecord> implements PrizewheelsAccessRecordService {
	
	@Autowired
	private PrizewheelsAccessRecordMapper mapper;

	@Override
	public void save(Long userId) {
		PrizewheelsAccessRecord userAccessRecord = this.selectById(userId);
		if(userAccessRecord != null) {
			// 之前已有访问记录，更新访问时间和访问次数
			userAccessRecord.setAccessTime(new Date());
			userAccessRecord.setAccessCount(userAccessRecord.getAccessCount() + 1);
			this.updateById(userAccessRecord);
		} else {
			// 第一次访问转盘
			userAccessRecord = new PrizewheelsAccessRecord();
			userAccessRecord.setUserId(userId);
			userAccessRecord.setAccessTime(new Date());
			userAccessRecord.setAccessCount(1);
			this.insert(userAccessRecord);
		}
	}

	@Override
	public List<PrizewheelsAccessRecord> getLatestActiveUser(Date accessTime) {
		EntityWrapper<PrizewheelsAccessRecord> wrapper = new EntityWrapper<PrizewheelsAccessRecord>();
		wrapper.ge("access_time", accessTime);
		return this.selectList(wrapper);
	}

	@Override
	public void updateUserId(long oldUserId, long newUserId) {
		mapper.updateUserId(oldUserId, newUserId);
	}
	
}
