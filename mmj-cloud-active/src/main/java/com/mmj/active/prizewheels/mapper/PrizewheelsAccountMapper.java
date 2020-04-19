package com.mmj.active.prizewheels.mapper;

import org.apache.ibatis.annotations.Param;

import com.mmj.active.prizewheels.model.PrizewheelsAccount;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * <p>
 * 转盘活动 - 账户表，包含买买币余额、红包余额 Mapper 接口
 * </p>
 *
 * @author shenfuding
 * @since 2019-06-27
 */
public interface PrizewheelsAccountMapper extends BaseMapper<PrizewheelsAccount> {
	
	void updateUserId(@Param("oldUserId")long oldUserId, @Param("newUserId")long newUserId);

}
