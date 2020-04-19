package com.qianmi.open.api.domain.elife;

import com.qianmi.open.api.QianmiObject;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * 标准商品省属性下市属性信息
 *
 * @author auto
 * @since 2.0
 */
public class City extends QianmiObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 市属性v编号
	 */
	@ApiField("vid")
	private String vid;

	/**
	 * 市属性名称
	 */
	@ApiField("vname")
	private String vname;

	public String getVid() {
		return this.vid;
	}
	public void setVid(String vid) {
		this.vid = vid;
	}

	public String getVname() {
		return this.vname;
	}
	public void setVname(String vname) {
		this.vname = vname;
	}

}