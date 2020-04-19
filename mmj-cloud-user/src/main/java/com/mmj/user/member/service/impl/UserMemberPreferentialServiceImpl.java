package com.mmj.user.member.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.utils.DoubleUtil;
import com.mmj.common.utils.SecurityUserUtil;
import com.mmj.user.member.mapper.UserMemberPreferentialMapper;
import com.mmj.user.member.model.UserMember;
import com.mmj.user.member.model.UserMemberPreferential;
import com.mmj.user.member.model.Vo.EconomizeMoneyVo;
import com.mmj.user.member.service.UserKingLogService;
import com.mmj.user.member.service.UserMemberPreferentialService;
import com.mmj.user.member.service.UserMemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  会员省钱 服务实现类
 * </p>
 *
 * @author zhangyicao
 * @since 2019-07-11
 */
@Service
public class UserMemberPreferentialServiceImpl extends ServiceImpl<UserMemberPreferentialMapper, UserMemberPreferential> implements UserMemberPreferentialService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserMemberPreferentialMapper userMemberPreferentialMapper;
    @Autowired
    private UserMemberService userMemberService;
    @Autowired
    private UserKingLogService userKingLogService;

    /**
     * 查询成为会员后省了多少钱
     * @return
     */
    @Override
    public Map<String,Object> queryEconomizeMoney(){
        Map<String,Object> map = new HashMap<>();
        JwtUserDetails jwtUserDetails = SecurityUserUtil.getUserDetails();
        logger.info("获取会员省钱：{}",jwtUserDetails.getUserId());
        UserMember userMember = userMemberService.queryUserMemberInfoByUserId(jwtUserDetails.getUserId());
        if(userMember == null || !userMember.getActive()){
            logger.info("非会员不能查询节省多少钱，userid：{}",jwtUserDetails.getUserId());
            return map;
        }
        List<EconomizeMoneyVo> economizeMoneyVos = userMemberPreferentialMapper.selectEconomizeMoney(jwtUserDetails.getUserId());
        double amount = 0.00;
        if(!economizeMoneyVos.isEmpty()){
            for(EconomizeMoneyVo economizeMoneyVo : economizeMoneyVos){
                map.put(economizeMoneyVo.getType(), economizeMoneyVo.getAmount());
                amount = DoubleUtil.add(amount, economizeMoneyVo.getAmount(), DoubleUtil.SCALE_3);
            }
        }

        //查询买买金价值
        Double num = userKingLogService.getSumKingNum();
        map.put("mmjCurrency",num);

        map.put("memberType", userMember.getBeMemberType());//成为会员方式

        map.put("amountCount", DoubleUtil.add(amount, num, DoubleUtil.SCALE_3));
        map.put("beMemberTime", com.mmj.common.utils.DateUtils.getDate(userMember.getBeMemberTime(),"yyyy-MM-dd"));
        return map;
    }
}
