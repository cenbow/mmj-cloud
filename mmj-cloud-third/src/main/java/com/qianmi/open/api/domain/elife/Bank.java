package com.qianmi.open.api.domain.elife;

import com.qianmi.open.api.QianmiObject;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * 银行信息
 *
 * @author auto
 * @since 2.0
 */
public class Bank extends QianmiObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 银行编码
	 */
	@ApiField("code")
	private String code;

	/**
	 * 是否热门 Y-热门 N-非热门
	 */
	@ApiField("hot")
	private String hot;

	/**
	 * ID 序号无意义
	 */
	@ApiField("id")
	private String id;

	/**
	 * 银行名称
	 */
	@ApiField("name")
	private String name;

	/**
	 * 银行名称拼音前缀大写
	 */
	@ApiField("prepin")
	private String prepin;

	/**
	 * 银行名称短拼大写
	 */
	@ApiField("shortpin")
	private String shortpin;

	public String getCode() {
		return this.code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	public String getHot() {
		return this.hot;
	}
	public void setHot(String hot) {
		this.hot = hot;
	}

	public String getId() {
		return this.id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getPrepin() {
		return this.prepin;
	}
	public void setPrepin(String prepin) {
		this.prepin = prepin;
	}

	public String getShortpin() {
		return this.shortpin;
	}
	public void setShortpin(String shortpin) {
		this.shortpin = shortpin;
	}

}