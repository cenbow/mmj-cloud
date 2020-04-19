package com.mmj.user.manager.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.user.manager.model.CutUser;
import java.util.List;

/**
 * <p>
 * 用户砍价表 服务类
 * </p>
 *
 * @author KK
 * @since 2019-06-15
 */
public interface CutUserService extends IService<CutUser> {
    /**
     * 查询用户的砍价记录列表
     *
     * @param cutUser
     * @return
     */
    List<CutUser> selectByUserId(CutUser cutUser);
}
