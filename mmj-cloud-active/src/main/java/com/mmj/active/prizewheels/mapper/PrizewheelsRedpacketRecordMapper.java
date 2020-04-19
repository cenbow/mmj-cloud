package com.mmj.active.prizewheels.mapper;

import org.apache.ibatis.annotations.Param;

import com.mmj.active.prizewheels.model.PrizewheelsRedpacketRecord;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * <p>
 * 转盘活动 - 用户红包变更记录表 Mapper 接口
 * </p>
 *
 * @author shenfuding
 * @since 2019-06-27
 */
public interface PrizewheelsRedpacketRecordMapper extends BaseMapper<PrizewheelsRedpacketRecord> {
	
	void updateUserId(@Param("oldUserId")long oldUserId, @Param("newUserId")long newUserId);

}
