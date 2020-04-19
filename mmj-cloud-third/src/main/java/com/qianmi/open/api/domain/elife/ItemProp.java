package com.qianmi.open.api.domain.elife;

import com.qianmi.open.api.QianmiObject;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * 标准商品基础属性值
 *
 * @author auto
 * @since 2.0
 */
public class ItemProp extends QianmiObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 属性PID
	 */
	@ApiField("pid")
	private String pid;

	/**
	 * 属性分类标志
	 */
	@ApiField("type")
	private String type;

	/**
	 * 属性分类描述
	 */
	@ApiField("typeDesc")
	private String typeDesc;

	/**
	 * v编号
	 */
	@ApiField("vid")
	private String vid;

	/**
	 * 属性key
	 */
	@ApiField("vkey")
	private String vkey;

	/**
	 * 属性名称
	 */
	@ApiField("vname")
	private String vname;

	public String getPid() {
		return this.pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getType() {
		return this.type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public String getTypeDesc() {
		return this.typeDesc;
	}
	public void setTypeDesc(String typeDesc) {
		this.typeDesc = typeDesc;
	}

	public String getVid() {
		return this.vid;
	}
	public void setVid(String vid) {
		this.vid = vid;
	}

	public String getVkey() {
		return this.vkey;
	}
	public void setVkey(String vkey) {
		this.vkey = vkey;
	}

	public String getVname() {
		return this.vname;
	}
	public void setVname(String vname) {
		this.vname = vname;
	}

}