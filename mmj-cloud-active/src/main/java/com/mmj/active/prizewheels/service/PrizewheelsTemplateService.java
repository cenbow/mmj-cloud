package com.mmj.active.prizewheels.service;

import com.mmj.active.prizewheels.model.PrizewheelsTemplate;
import com.baomidou.mybatisplus.service.IService;

/**
 * <p>
 * 幸运大转盘-活动配置表 服务类
 * </p>
 *
 * @author shenfuding
 * @since 2019-06-26
 */
public interface PrizewheelsTemplateService extends IService<PrizewheelsTemplate> {
	
	/**
	 * 加载转盘活动配置信息
	 * @return
	 */
	PrizewheelsTemplate load();
	
	/**
	 * 转盘活动是否开启
	 * @return
	 */
	boolean queryIsOpen();
	
}
