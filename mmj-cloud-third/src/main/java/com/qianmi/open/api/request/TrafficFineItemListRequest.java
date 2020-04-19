package com.qianmi.open.api.request;

import com.qianmi.open.api.ApiRuleException;
import com.qianmi.open.api.QianmiRequest;
import com.qianmi.open.api.response.TrafficFineItemListResponse;
import com.qianmi.open.api.tool.util.QianmiHashMap;

import java.util.Map;

/**
 * API: qianmi.elife.trafficFine.item.list request
 *
 * @author auto
 * @since 1.0
 */
public class TrafficFineItemListRequest implements QianmiRequest<TrafficFineItemListResponse> {

    private Map<String, String> headerMap = new QianmiHashMap();
	private QianmiHashMap udfParams; // add user-defined text parameters
	private Long timestamp;

	/** 
	 * 品牌名称
	 */
	private String brand;

	/** 
	 * 市属性名称
	 */
	private String city;

	/** 
	 * 市属性V编号
	 */
	private String cityVid;

	/** 
	 * 商品面值名称
	 */
	private String faceName;

	/** 
	 * ^\d{1,6}$
	 */
	private String faceValue;

	/** 
	 * 充值类型 1 直充，2卡密，3慢充，4 实物
	 */
	private String itemChargeType;

	/** 
	 * 标准商品编号
	 */
	private String itemId;

	/** 
	 * 标准商品名称
	 */
	private String itemName;

	/** 
	 * 标准商品类型
	 */
	private String itemType;

	/** 
	 * 页码,0开始
	 */
	private Integer pageNo;

	/** 
	 * 返回条数
	 */
	private Integer pageSize;

	/** 
	 * 标准类目编号
	 */
	private String projectId;

	/** 
	 * 标准类目名称
	 */
	private String projectName;

	/** 
	 * 省属性名称
	 */
	private String province;

	/** 
	 * 省属性V编号
	 */
	private String provinceVid;

	/** 
	 * 充值模板编号字符串组合
	 */
	private String rechargeTplids;

	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getBrand() {
		return this.brand;
	}

	public void setCity(String city) {
		this.city = city;
	}
	public String getCity() {
		return this.city;
	}

	public void setCityVid(String cityVid) {
		this.cityVid = cityVid;
	}
	public String getCityVid() {
		return this.cityVid;
	}

	public void setFaceName(String faceName) {
		this.faceName = faceName;
	}
	public String getFaceName() {
		return this.faceName;
	}

	public void setFaceValue(String faceValue) {
		this.faceValue = faceValue;
	}
	public String getFaceValue() {
		return this.faceValue;
	}

	public void setItemChargeType(String itemChargeType) {
		this.itemChargeType = itemChargeType;
	}
	public String getItemChargeType() {
		return this.itemChargeType;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public String getItemId() {
		return this.itemId;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public String getItemName() {
		return this.itemName;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}
	public String getItemType() {
		return this.itemType;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}
	public Integer getPageNo() {
		return this.pageNo;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public Integer getPageSize() {
		return this.pageSize;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	public String getProjectId() {
		return this.projectId;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getProjectName() {
		return this.projectName;
	}

	public void setProvince(String province) {
		this.province = province;
	}
	public String getProvince() {
		return this.province;
	}

	public void setProvinceVid(String provinceVid) {
		this.provinceVid = provinceVid;
	}
	public String getProvinceVid() {
		return this.provinceVid;
	}

	public void setRechargeTplids(String rechargeTplids) {
		this.rechargeTplids = rechargeTplids;
	}
	public String getRechargeTplids() {
		return this.rechargeTplids;
	}

    public Long getTimestamp() {
    	return this.timestamp;
    }
    public void setTimestamp(Long timestamp) {
    	this.timestamp = timestamp;
    }

	public String getApiMethodName() {
		return "qianmi.elife.trafficFine.item.list";
	}

	public Map<String, String> getTextParams() {
		QianmiHashMap txtParams = new QianmiHashMap();
		txtParams.put("brand", this.brand);
		txtParams.put("city", this.city);
		txtParams.put("cityVid", this.cityVid);
		txtParams.put("faceName", this.faceName);
		txtParams.put("faceValue", this.faceValue);
		txtParams.put("itemChargeType", this.itemChargeType);
		txtParams.put("itemId", this.itemId);
		txtParams.put("itemName", this.itemName);
		txtParams.put("itemType", this.itemType);
		txtParams.put("pageNo", this.pageNo);
		txtParams.put("pageSize", this.pageSize);
		txtParams.put("projectId", this.projectId);
		txtParams.put("projectName", this.projectName);
		txtParams.put("province", this.province);
		txtParams.put("provinceVid", this.provinceVid);
		txtParams.put("rechargeTplids", this.rechargeTplids);
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

	public Class<TrafficFineItemListResponse> getResponseClass() {
		return TrafficFineItemListResponse.class;
	}

	public void check() throws ApiRuleException {
    }

	public Map<String, String> getHeaderMap() {
        return headerMap;
    }
}