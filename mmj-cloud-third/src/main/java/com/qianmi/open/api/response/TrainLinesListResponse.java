package com.qianmi.open.api.response;

import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.domain.elife.Trainline;
import com.qianmi.open.api.tool.mapping.ApiField;
import com.qianmi.open.api.tool.mapping.ApiListField;

import java.util.List;

/**
 * API: qianmi.elife.train.lines.list response.
 *
 * @author auto
 * @since 2.0
 */
public class TrainLinesListResponse extends QianmiResponse {

	private static final long serialVersionUID = 1L;

	/** 
	 * 火车票详情信息列表
	 */
	@ApiListField("trainlines")
	@ApiField("trainline")
	private List<Trainline> trainlines;

	public void setTrainlines(List<Trainline> trainlines) {
		this.trainlines = trainlines;
	}
	public List<Trainline> getTrainlines( ) {
		return this.trainlines;
	}

}
