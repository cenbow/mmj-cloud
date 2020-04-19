package com.qianmi.open.api.response;

import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.domain.elife.CoachLine;
import com.qianmi.open.api.tool.mapping.ApiField;
import com.qianmi.open.api.tool.mapping.ApiListField;

import java.util.List;

/**
 * API: qianmi.elife.coach.lines.list response.
 *
 * @author auto
 * @since 2.0
 */
public class CoachLinesListResponse extends QianmiResponse {

	private static final long serialVersionUID = 1L;

	/** 
	 * 汽车票车次信息
	 */
	@ApiListField("coachLines")
	@ApiField("coachLine")
	private List<CoachLine> coachLines;

	public void setCoachLines(List<CoachLine> coachLines) {
		this.coachLines = coachLines;
	}
	public List<CoachLine> getCoachLines( ) {
		return this.coachLines;
	}

}
