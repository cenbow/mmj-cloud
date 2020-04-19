package com.mmj.user.member.service;

import java.util.Map;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.common.model.UserMerge;
import com.mmj.user.member.dto.SaveUserMemberDto;
import com.mmj.user.member.model.UserMember;
import com.mmj.user.member.model.Vo.DegradeVo;

/**
 * <p>
 * 会员表 服务类
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-11
 */
public interface UserMemberService extends IService<UserMember> {
	
	/**
	 * 保存会员信息，此方法内会同步更新shop_account表is_member标识
	 * @param entity
	 * @return 返回生成的会员ID，-1表示生成会员失败，成功则返回正确的会员ID
	 */
	int save(SaveUserMemberDto entity);
	
	/**
	 * 根据用户ID查询会员信息
	 * @param userId
	 * @return
	 */
	UserMember queryUserMemberInfoByUserId(Long userId);
	
	/**
	 * 会员降级，由会员变成非会员
	 * @param degradeVo
	 * @return
	 */
	boolean degrade(DegradeVo degradeVo);
	
	/**
	 * 查询会员总数量
	 * @return
	 */
	int queryMemberTotalCount();
	
	/**
     * 获取个人中心的会员信息
     * @return
     */
    Map<String, Object> queryUserMemberInfoForUC();

	/**
	 * 直接购买会员
	 * @return
	 */
	Map<String, String> buy(String appType);

	/**
	 * 修改userId
	 * @param userMerge
	 */
	void updateUserID(UserMerge userMerge);

}
