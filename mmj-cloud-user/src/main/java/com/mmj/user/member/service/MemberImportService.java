package com.mmj.user.member.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.common.model.MobileCode;
import com.mmj.user.member.model.MemberImport;

/**
 * <p>
 * 第三方导入会员的记录表 服务类
 * </p>
 *
 * @author shenfuding
 * @since 2019-08-12
 */
public interface MemberImportService extends IService<MemberImport> {
	
	/**
	 * 从会员导入记录表中查询用户绑定的手机号
	 * @returnt
	 */
	String bindInfo();

	void sendValidateCode(String mobile);
	
	void validate(MobileCode mobileCode);
	
	/**
	 * 获取导入会员的消费金额，该消费金额是第三方平台的历史消费金额
	 * @param userId
	 * @return
	 */
	double getImportMemberConsumptionAmount(long userId);
	
}
