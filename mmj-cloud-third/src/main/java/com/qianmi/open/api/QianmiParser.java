package com.qianmi.open.api;

/**
 * 响应解释器接口。响应格式目前只支持JSON
 */
public interface QianmiParser<T extends QianmiResponse> {

	/**
	 * 把响应字符串解释成相应的领域对象。
	 * 
	 * @param rsp 响应字符串
	 * @return 领域对象
	 */
	public T parse(String rsp) throws ApiException;

	/**
	 * 获取响应类类型。
	 */
	public Class<T> getResponseClass() throws ApiException;

}
