package com.mmj.user.manager.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.user.manager.mapper.CutUserMapper;
import com.mmj.user.manager.model.CutUser;
import com.mmj.user.manager.service.CutUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户砍价表 服务实现类
 * </p>
 *
 * @author KK
 * @since 2019-06-15
 */
@Service
public class CutUserServiceImpl extends ServiceImpl<CutUserMapper, CutUser> implements CutUserService {
    @Autowired
    private CutUserMapper cutUserMapper;

    @Override
    public List<CutUser> selectByUserId(CutUser cutUser) {
        return cutUserMapper.selectByUserId(cutUser);
    }
}
