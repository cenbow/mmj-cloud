package com.mmj.user.member.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.common.model.UserMerge;
import com.mmj.user.member.dto.MyKingExchangeParam;
import com.mmj.user.member.model.KingUser;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * <p>
 * 用户买买金表 服务类
 * </p>
 *
 * @author zhangyicao
 * @since 2019-07-10
 */
public interface KingUserService extends IService<KingUser> {

    @Transactional(rollbackFor = Exception.class)
    void updateUserId(UserMerge userMerge);

    KingUser getByUserId(Long userId);

    Map<String,Object> getMyKing(Long userId);

    Boolean verify(Long userId, Integer count);
    
    Map<String, Object> getMyKingExchangeInfo(MyKingExchangeParam param);
}
