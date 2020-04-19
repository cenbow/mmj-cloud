package com.qianmi.open.api.domain.elife;

import com.qianmi.open.api.QianmiObject;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * 站点详细信息
 *
 * @author auto
 * @since 2.0
 */
public class Station extends QianmiObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 站点代码
	 */
	@ApiField("code")
	private String code;

	/**
	 * 短拼
	 */
	@ApiField("duanpin")
	private String duanpin;

	/**
	 * 中文名
	 */
	@ApiField("name")
	private String name;

	/**
	 * 全拼
	 */
	@ApiField("quanpin")
	private String quanpin;

	public String getCode() {
		return this.code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	public String getDuanpin() {
		return this.duanpin;
	}
	public void setDuanpin(String duanpin) {
		this.duanpin = duanpin;
	}

	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getQuanpin() {
		return this.quanpin;
	}
	public void setQuanpin(String quanpin) {
		this.quanpin = quanpin;
	}

}