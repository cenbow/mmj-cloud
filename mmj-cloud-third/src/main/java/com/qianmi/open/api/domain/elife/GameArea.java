package com.qianmi.open.api.domain.elife;

import com.qianmi.open.api.QianmiObject;
import com.qianmi.open.api.tool.mapping.ApiField;
import com.qianmi.open.api.tool.mapping.ApiListField;

import java.util.List;

/**
 * 游戏区
 *
 * @author auto
 * @since 2.0
 */
public class GameArea extends QianmiObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 游戏区的ID
	 */
	@ApiField("areaId")
	private String areaId;

	/**
	 * 游戏区的名称
	 */
	@ApiField("areaName")
	private String areaName;

	/**
	 * 游戏服务器列表
	 */
	@ApiListField("gameServers")
	@ApiField("gameServer")
	private List<GameServer> gameServers;

	public String getAreaId() {
		return this.areaId;
	}
	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}

	public String getAreaName() {
		return this.areaName;
	}
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public List<GameServer> getGameServers() {
		return this.gameServers;
	}
	public void setGameServers(List<GameServer> gameServers) {
		this.gameServers = gameServers;
	}

}