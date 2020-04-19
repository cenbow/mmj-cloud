package com.qianmi.open.api.response;

import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.domain.elife.GameClass;
import com.qianmi.open.api.tool.mapping.ApiField;
import com.qianmi.open.api.tool.mapping.ApiListField;

import java.util.List;

/**
 * API: bm.elife.game.classes.list response.
 *
 * @author auto
 * @since 2.0
 */
public class BmGameClassesListResponse extends QianmiResponse {

	private static final long serialVersionUID = 1L;

	/** 
	 * 游戏分类
	 */
	@ApiListField("classes")
	@ApiField("clazz")
	private List<GameClass> classes;

	public void setClasses(List<GameClass> classes) {
		this.classes = classes;
	}
	public List<GameClass> getClasses( ) {
		return this.classes;
	}

}
