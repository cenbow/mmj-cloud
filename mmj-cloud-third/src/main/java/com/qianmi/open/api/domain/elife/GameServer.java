package com.qianmi.open.api.domain.elife;

import com.qianmi.open.api.QianmiObject;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * 游戏服
 *
 * @author auto
 * @since 2.0
 */
public class GameServer extends QianmiObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 游戏服ID
	 */
	@ApiField("serverId")
	private String serverId;

	/**
	 * 游戏服名称
	 */
	@ApiField("serverName")
	private String serverName;

	public String getServerId() {
		return this.serverId;
	}
	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public String getServerName() {
		return this.serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

}