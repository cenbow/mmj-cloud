package com.mmj.user.manager.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.mmj.user.manager.model.CutUser;
import java.util.List;

/**
 * <p>
 * 用户砍价表 Mapper 接口
 * </p>
 *
 * @author KK
 * @since 2019-06-15
 */
public interface CutUserMapper extends BaseMapper<CutUser> {
    /**
     * 查询用户的砍价记录列表
     *
     * @param cutUser
     * @return
     */
    List<CutUser> selectByUserId(CutUser cutUser);
}
