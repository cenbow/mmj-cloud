package com.qianmi.open.api.response;

import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.domain.elife.GameArea;
import com.qianmi.open.api.tool.mapping.ApiField;
import com.qianmi.open.api.tool.mapping.ApiListField;

import java.util.List;

/**
 * API: qianmi.elife.game.areas.list response.
 *
 * @author auto
 * @since 2.0
 */
public class GameAreasListResponse extends QianmiResponse {

	private static final long serialVersionUID = 1L;

	/** 
	 * 游戏区
	 */
	@ApiListField("gameAreas")
	@ApiField("gameArea")
	private List<GameArea> gameAreas;

	public void setGameAreas(List<GameArea> gameAreas) {
		this.gameAreas = gameAreas;
	}
	public List<GameArea> getGameAreas( ) {
		return this.gameAreas;
	}

}
