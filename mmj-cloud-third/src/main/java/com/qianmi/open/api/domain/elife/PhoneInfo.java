package com.qianmi.open.api.domain.elife;

import com.qianmi.open.api.QianmiObject;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * 手机或固话详情
 *
 * @author auto
 * @since 2.0
 */
public class PhoneInfo extends QianmiObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 所在市名称
	 */
	@ApiField("city")
	private String city;

	/**
	 * 详情，根据查询模板不同，返回信息有差异，可能包含用户名,余额，套餐等信息
	 */
	@ApiField("detail")
	private String detail;

	/**
	 * 运营商名称
	 */
	@ApiField("operator")
	private String operator;

	/**
	 * 号码
	 */
	@ApiField("phoneNo")
	private String phoneNo;

	/**
	 * 所在省名称
	 */
	@ApiField("province")
	private String province;

	public String getCity() {
		return this.city;
	}
	public void setCity(String city) {
		this.city = city;
	}

	public String getDetail() {
		return this.detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getOperator() {
		return this.operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getPhoneNo() {
		return this.phoneNo;
	}
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	public String getProvince() {
		return this.province;
	}
	public void setProvince(String province) {
		this.province = province;
	}

}