package com.qianmi.open.api;


/**
 * 千米开放平台客户端。
 */
public interface OpenClient {
	
	/**
	 * 执行API请求。
	 * @param <T>
	 * @param request 具体的请求
	 * @return
	 * @throws ApiException
	 */
	public <T extends QianmiResponse> T execute(QianmiRequest<T> request) throws ApiException;
	/**
	 * 执行API请求。
	 * @param <T>
	 * @param request 具体的请求
	 * @param accessToken 用户会话授权码
	 * @return
	 * @throws ApiException
	 */
	public <T extends QianmiResponse> T execute(QianmiRequest<T> request, String accessToken) throws ApiException;
}
