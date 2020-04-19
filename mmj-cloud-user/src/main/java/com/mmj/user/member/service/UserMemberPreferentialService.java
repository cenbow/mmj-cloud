package com.mmj.user.member.service;


import com.baomidou.mybatisplus.service.IService;
import com.mmj.user.member.model.UserMemberPreferential;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhangyicao
 * @since 2019-07-11
 */
public interface UserMemberPreferentialService extends IService<UserMemberPreferential> {

    /**
     * 查询会员省钱
     * @return
     */
    Map<String,Object> queryEconomizeMoney();
}
