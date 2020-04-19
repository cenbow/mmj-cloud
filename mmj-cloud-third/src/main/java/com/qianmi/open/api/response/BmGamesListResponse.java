package com.qianmi.open.api.response;

import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.domain.elife.Game;
import com.qianmi.open.api.tool.mapping.ApiField;
import com.qianmi.open.api.tool.mapping.ApiListField;

import java.util.List;

/**
 * API: bm.elife.games.list response.
 *
 * @author auto
 * @since 2.0
 */
public class BmGamesListResponse extends QianmiResponse {

	private static final long serialVersionUID = 1L;

	/** 
	 * 游戏名称
	 */
	@ApiListField("games")
	@ApiField("game")
	private List<Game> games;

	public void setGames(List<Game> games) {
		this.games = games;
	}
	public List<Game> getGames( ) {
		return this.games;
	}

}
