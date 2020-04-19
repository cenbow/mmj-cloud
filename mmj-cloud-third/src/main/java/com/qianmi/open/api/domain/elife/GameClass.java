package com.qianmi.open.api.domain.elife;

import com.qianmi.open.api.QianmiObject;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * 游戏商品小类
 *
 * @author auto
 * @since 2.0
 */
public class GameClass extends QianmiObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 小类编号
	 */
	@ApiField("classId")
	private String classId;

	/**
	 * 小类名称
	 */
	@ApiField("className")
	private String className;

	/**
	 * 充值地址
	 */
	@ApiField("rechargeAddress")
	private String rechargeAddress;

	public String getClassId() {
		return this.classId;
	}
	public void setClassId(String classId) {
		this.classId = classId;
	}

	public String getClassName() {
		return this.className;
	}
	public void setClassName(String className) {
		this.className = className;
	}

	public String getRechargeAddress() {
		return this.rechargeAddress;
	}
	public void setRechargeAddress(String rechargeAddress) {
		this.rechargeAddress = rechargeAddress;
	}

}