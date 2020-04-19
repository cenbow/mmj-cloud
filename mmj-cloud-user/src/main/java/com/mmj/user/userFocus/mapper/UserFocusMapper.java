package com.mmj.user.userFocus.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.user.userFocus.model.UserFocus;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户关注公众号记录 Mapper 接口
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-16
 */
public interface UserFocusMapper extends BaseMapper<UserFocus> {

    List<UserFocus> queryList(Page<UserFocus> page, @Param("module") Integer module, @Param("type") Integer type);
}
