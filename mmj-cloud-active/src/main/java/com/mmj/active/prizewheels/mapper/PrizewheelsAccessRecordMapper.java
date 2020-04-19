package com.mmj.active.prizewheels.mapper;

import org.apache.ibatis.annotations.Param;

import com.mmj.active.prizewheels.model.PrizewheelsAccessRecord;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * <p>
 * 用户访问转盘活动的时间记录表 Mapper 接口
 * </p>
 *
 * @author shenfuding
 * @since 2019-06-27
 */
public interface PrizewheelsAccessRecordMapper extends BaseMapper<PrizewheelsAccessRecord> {
	
	void updateUserId(@Param("oldUserId")long oldUserId, @Param("newUserId")long newUserId);

}
