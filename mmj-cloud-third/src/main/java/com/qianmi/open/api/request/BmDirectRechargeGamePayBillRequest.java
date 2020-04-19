package com.qianmi.open.api.request;

import com.qianmi.open.api.ApiRuleException;
import com.qianmi.open.api.QianmiRequest;
import com.qianmi.open.api.response.BmDirectRechargeGamePayBillResponse;
import com.qianmi.open.api.tool.util.QianmiHashMap;
import com.qianmi.open.api.tool.util.RequestCheckUtils;

import java.util.Map;

/**
 * API: bm.elife.directRecharge.game.payBill request
 *
 * @author auto
 * @since 1.0
 */
public class BmDirectRechargeGamePayBillRequest implements QianmiRequest<BmDirectRechargeGamePayBillResponse> {

    private Map<String, String> headerMap = new QianmiHashMap();
	private QianmiHashMap udfParams; // add user-defined text parameters
	private Long timestamp;

	/** 
	 * 回调地址
	 */
	private String callback;

	/** 
	 * 游戏区
	 */
	private String gameArea;

	/** 
	 * 游戏服
	 */
	private String gameServer;

	/** 
	 * 标准商品编号
	 */
	private String itemId;

	/** 
	 * 购买数量
	 */
	private String itemNum;

	/** 
	 * 外部订单号
	 */
	private String outerTid;

	/** 
	 * 游戏帐号(如果是魔兽世界则为战网帐号)
	 */
	private String rechargeAccount;

	/** 
	 * 游戏帐号2(目前只有魔兽世界充值用到，当为魔兽世界商品时，如果不填则为战网下第一个帐号充值)
	 */
	private String rechargeAccount2;

	/** 
	 * Q币资源对应的IP
	 */
	private String rechargeIp;

	public void setCallback(String callback) {
		this.callback = callback;
	}
	public String getCallback() {
		return this.callback;
	}

	public void setGameArea(String gameArea) {
		this.gameArea = gameArea;
	}
	public String getGameArea() {
		return this.gameArea;
	}

	public void setGameServer(String gameServer) {
		this.gameServer = gameServer;
	}
	public String getGameServer() {
		return this.gameServer;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public String getItemId() {
		return this.itemId;
	}

	public void setItemNum(String itemNum) {
		this.itemNum = itemNum;
	}
	public String getItemNum() {
		return this.itemNum;
	}

	public void setOuterTid(String outerTid) {
		this.outerTid = outerTid;
	}
	public String getOuterTid() {
		return this.outerTid;
	}

	public void setRechargeAccount(String rechargeAccount) {
		this.rechargeAccount = rechargeAccount;
	}
	public String getRechargeAccount() {
		return this.rechargeAccount;
	}

	public void setRechargeAccount2(String rechargeAccount2) {
		this.rechargeAccount2 = rechargeAccount2;
	}
	public String getRechargeAccount2() {
		return this.rechargeAccount2;
	}

	public void setRechargeIp(String rechargeIp) {
		this.rechargeIp = rechargeIp;
	}
	public String getRechargeIp() {
		return this.rechargeIp;
	}

    public Long getTimestamp() {
    	return this.timestamp;
    }
    public void setTimestamp(Long timestamp) {
    	this.timestamp = timestamp;
    }

	public String getApiMethodName() {
		return "bm.elife.directRecharge.game.payBill";
	}

	public Map<String, String> getTextParams() {
		QianmiHashMap txtParams = new QianmiHashMap();
		txtParams.put("callback", this.callback);
		txtParams.put("gameArea", this.gameArea);
		txtParams.put("gameServer", this.gameServer);
		txtParams.put("itemId", this.itemId);
		txtParams.put("itemNum", this.itemNum);
		txtParams.put("outerTid", this.outerTid);
		txtParams.put("rechargeAccount", this.rechargeAccount);
		txtParams.put("rechargeAccount2", this.rechargeAccount2);
		txtParams.put("rechargeIp", this.rechargeIp);
		if(udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public void putOtherTextParam(String key, String value) {
		if(this.udfParams == null) {
			this.udfParams = new QianmiHashMap();
		}
		this.udfParams.put(key, value);
	}

	public Class<BmDirectRechargeGamePayBillResponse> getResponseClass() {
		return BmDirectRechargeGamePayBillResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(itemId, "itemId");
		RequestCheckUtils.checkNotEmpty(itemNum, "itemNum");
		RequestCheckUtils.checkNotEmpty(rechargeAccount, "rechargeAccount");
    }

	public Map<String, String> getHeaderMap() {
        return headerMap;
    }
}