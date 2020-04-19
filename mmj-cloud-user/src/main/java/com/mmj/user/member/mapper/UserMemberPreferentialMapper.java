package com.mmj.user.member.mapper;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.mmj.user.member.model.UserMemberPreferential;
import com.mmj.user.member.model.Vo.EconomizeMoneyVo;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zhangyicao
 * @since 2019-07-11
 */
@Repository
public interface UserMemberPreferentialMapper extends BaseMapper<UserMemberPreferential> {

    List<EconomizeMoneyVo> selectEconomizeMoney(Long userid);
}
