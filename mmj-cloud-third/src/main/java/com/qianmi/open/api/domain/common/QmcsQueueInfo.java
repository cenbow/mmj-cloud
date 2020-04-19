package com.qianmi.open.api.domain.common;

import com.qianmi.open.api.QianmiObject;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * 消息队列积压情况
 *
 * @author auto
 * @since 2.0
 */
public class QmcsQueueInfo extends QianmiObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 当前分组下已消费的总条数
	 */
	@ApiField("get_total")
	private Integer getTotal;

	/**
	 * 分组名
	 */
	@ApiField("name")
	private String name;

	/**
	 * 当前分组下的总条数
	 */
	@ApiField("put_total")
	private Integer putTotal;

	public Integer getGetTotal() {
		return this.getTotal;
	}
	public void setGetTotal(Integer getTotal) {
		this.getTotal = getTotal;
	}

	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public Integer getPutTotal() {
		return this.putTotal;
	}
	public void setPutTotal(Integer putTotal) {
		this.putTotal = putTotal;
	}

}