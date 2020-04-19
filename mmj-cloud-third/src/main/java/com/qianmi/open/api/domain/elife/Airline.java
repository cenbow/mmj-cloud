package com.qianmi.open.api.domain.elife;

import com.qianmi.open.api.QianmiObject;
import com.qianmi.open.api.tool.mapping.ApiField;
import com.qianmi.open.api.tool.mapping.ApiListField;

import java.util.List;

/**
 * 航班信息
 *
 * @author auto
 * @since 2.0
 */
public class Airline extends QianmiObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 成人机建费
	 */
	@ApiField("adultAirportTax")
	private Double adultAirportTax;

	/**
	 * 成人燃油费
	 */
	@ApiField("adultFuelTax")
	private Double adultFuelTax;

	/**
	 * 航班舱位信息
	 */
	@ApiListField("airSeats")
	@ApiField("airSeat")
	private List<AirSeat> airSeats;

	/**
	 * 降落时间
	 */
	@ApiField("arriTime")
	private String arriTime;

	/**
	 * Y舱价格，未打折的价格，该价格在前台不需要用
	 */
	@ApiField("basePrice")
	private Double basePrice;

	/**
	 * 起飞时间
	 */
	@ApiField("depTime")
	private String depTime;

	/**
	 * 抵达城市(机场)
	 */
	@ApiField("dstCity")
	private String dstCity;

	/**
	 * 抵达城市名称
	 */
	@ApiField("dstCityName")
	private String dstCityName;

	/**
	 * 航空公司二字码
	 */
	@ApiField("flightCompanyCode")
	private String flightCompanyCode;

	/**
	 * 航空公司的名称
	 */
	@ApiField("flightCompanyName")
	private String flightCompanyName;

	/**
	 * 航班号
	 */
	@ApiField("flightNo")
	private String flightNo;

	/**
	 * 起始城市(机场)
	 */
	@ApiField("orgCity")
	private String orgCity;

	/**
	 * 起始城市名称
	 */
	@ApiField("orgCityName")
	private String orgCityName;

	/**
	 * 机型
	 */
	@ApiField("planeType")
	private String planeType;

	public Double getAdultAirportTax() {
		return this.adultAirportTax;
	}
	public void setAdultAirportTax(Double adultAirportTax) {
		this.adultAirportTax = adultAirportTax;
	}

	public Double getAdultFuelTax() {
		return this.adultFuelTax;
	}
	public void setAdultFuelTax(Double adultFuelTax) {
		this.adultFuelTax = adultFuelTax;
	}

	public List<AirSeat> getAirSeats() {
		return this.airSeats;
	}
	public void setAirSeats(List<AirSeat> airSeats) {
		this.airSeats = airSeats;
	}

	public String getArriTime() {
		return this.arriTime;
	}
	public void setArriTime(String arriTime) {
		this.arriTime = arriTime;
	}

	public Double getBasePrice() {
		return this.basePrice;
	}
	public void setBasePrice(Double basePrice) {
		this.basePrice = basePrice;
	}

	public String getDepTime() {
		return this.depTime;
	}
	public void setDepTime(String depTime) {
		this.depTime = depTime;
	}

	public String getDstCity() {
		return this.dstCity;
	}
	public void setDstCity(String dstCity) {
		this.dstCity = dstCity;
	}

	public String getDstCityName() {
		return this.dstCityName;
	}
	public void setDstCityName(String dstCityName) {
		this.dstCityName = dstCityName;
	}

	public String getFlightCompanyCode() {
		return this.flightCompanyCode;
	}
	public void setFlightCompanyCode(String flightCompanyCode) {
		this.flightCompanyCode = flightCompanyCode;
	}

	public String getFlightCompanyName() {
		return this.flightCompanyName;
	}
	public void setFlightCompanyName(String flightCompanyName) {
		this.flightCompanyName = flightCompanyName;
	}

	public String getFlightNo() {
		return this.flightNo;
	}
	public void setFlightNo(String flightNo) {
		this.flightNo = flightNo;
	}

	public String getOrgCity() {
		return this.orgCity;
	}
	public void setOrgCity(String orgCity) {
		this.orgCity = orgCity;
	}

	public String getOrgCityName() {
		return this.orgCityName;
	}
	public void setOrgCityName(String orgCityName) {
		this.orgCityName = orgCityName;
	}

	public String getPlaneType() {
		return this.planeType;
	}
	public void setPlaneType(String planeType) {
		this.planeType = planeType;
	}

}