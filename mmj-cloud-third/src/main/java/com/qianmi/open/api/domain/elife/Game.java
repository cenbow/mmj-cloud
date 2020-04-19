package com.qianmi.open.api.domain.elife;

import com.qianmi.open.api.QianmiObject;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * 游戏
 *
 * @author auto
 * @since 2.0
 */
public class Game extends QianmiObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 游戏ID
	 */
	@ApiField("gameId")
	private String gameId;

	/**
	 * 游戏名称
	 */
	@ApiField("gameName")
	private String gameName;

	/**
	 * 游戏厂商ID
	 */
	@ApiField("manufacturerId")
	private String manufacturerId;

	/**
	 * 游戏厂商名称
	 */
	@ApiField("manufacturerName")
	private String manufacturerName;

	/**
	 * 游戏名称拼音
	 */
	@ApiField("pinyin")
	private String pinyin;

	/**
	 * 关联属性ID
	 */
	@ApiField("relationPropId")
	private String relationPropId;

	/**
	 * 游戏名称首字母拼音
	 */
	@ApiField("simplePinyin")
	private String simplePinyin;

	public String getGameId() {
		return this.gameId;
	}
	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public String getGameName() {
		return this.gameName;
	}
	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	public String getManufacturerId() {
		return this.manufacturerId;
	}
	public void setManufacturerId(String manufacturerId) {
		this.manufacturerId = manufacturerId;
	}

	public String getManufacturerName() {
		return this.manufacturerName;
	}
	public void setManufacturerName(String manufacturerName) {
		this.manufacturerName = manufacturerName;
	}

	public String getPinyin() {
		return this.pinyin;
	}
	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public String getRelationPropId() {
		return this.relationPropId;
	}
	public void setRelationPropId(String relationPropId) {
		this.relationPropId = relationPropId;
	}

	public String getSimplePinyin() {
		return this.simplePinyin;
	}
	public void setSimplePinyin(String simplePinyin) {
		this.simplePinyin = simplePinyin;
	}

}