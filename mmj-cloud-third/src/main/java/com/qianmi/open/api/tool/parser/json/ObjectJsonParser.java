package com.qianmi.open.api.tool.parser.json;

import com.qianmi.open.api.ApiException;
import com.qianmi.open.api.QianmiParser;
import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.tool.mapping.Converter;

/**
 * 单个JSON对象解释器。
 */
public class ObjectJsonParser<T extends QianmiResponse> implements QianmiParser<T> {

	private Class<T> clazz;
	private boolean simplify;

	public ObjectJsonParser(Class<T> clazz) {
		this.clazz = clazz;
	}

	public ObjectJsonParser(Class<T> clazz, boolean simplify) {
		this.clazz = clazz;
		this.simplify = simplify;
	}

	public T parse(String rsp) throws ApiException {
		Converter converter;
		if (this.simplify) {
			converter = new SimplifyJsonConverter();
		} else {
			converter = new JsonConverter();
		}
		return converter.toResponse(rsp, clazz);
	}

	public Class<T> getResponseClass() {
		return clazz;
	}

}
