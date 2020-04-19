package com.mmj.active.threeSaleTenner.mapper;

import com.mmj.active.threeSaleTenner.model.ThreeSaleFission;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 十元三件红包裂变 Mapper 接口
 * </p>
 *
 * @author dashu
 * @since 2019-07-11
 */
public interface ThreeSaleFissionMapper extends BaseMapper<ThreeSaleFission> {

    Integer updateInvalid();

    void updateFromUserId(@Param("oldUserId") long oldUserId, @Param("newUserId") long newUserId);

    void updateToUserId(@Param("oldUserId") long oldUserId, @Param("newUserId") long newUserId);
}
