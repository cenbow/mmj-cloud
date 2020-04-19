package com.mmj.user.address.mapper;

import com.mmj.user.address.model.BaseUserAddr;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 用户收货地址 Mapper 接口
 * </p>
 *
 * @author dashu
 * @since 2019-07-01
 */
public interface BaseUserAddrMapper extends BaseMapper<BaseUserAddr> {

    void updateUserId(@Param("oldUserId") long oldUserId, @Param("newUserId") long newUserId);
}
