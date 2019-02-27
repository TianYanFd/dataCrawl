package com.nstars.model;

import java.math.BigDecimal;
import java.util.Date;

public class BankBasicInfoDomain {
	
	private Long id;
	
	private String fullName;
	
	private Integer type;
	
	private String province;
	
	private BigDecimal totalAssets;
	
	private String assetsTime;

	private Date establishDate;
	
	private String headquarters;
	
	private String shortName;
	
	private String shortNameEng;
	
	private Date createTime;
	
	private Date updateTime;
	
	private String operator;
	
	private String establishDateStr;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public BigDecimal getTotalAssets() {
		return totalAssets;
	}

	public void setTotalAssets(BigDecimal totalAssets) {
		this.totalAssets = totalAssets;
	}

	public String getAssetsTime() {
		return assetsTime;
	}

	public void setAssetsTime(String assetsTime) {
		this.assetsTime = assetsTime;
	}

	public Date getEstablishDate() {
		return establishDate;
	}

	public void setEstablishDate(Date establishDate) {
		this.establishDate = establishDate;
	}

	public String getHeadquarters() {
		return headquarters;
	}

	public void setHeadquarters(String headquarters) {
		this.headquarters = headquarters;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getShortNameEng() {
		return shortNameEng;
	}

	public void setShortNameEng(String shortNameEng) {
		this.shortNameEng = shortNameEng;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getEstablishDateStr() {
		return establishDateStr;
	}

	public void setEstablishDateStr(String establishDateStr) {
		this.establishDateStr = establishDateStr;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BankBasicInfoDomain [id=");
		builder.append(id);
		builder.append(", fullName=");
		builder.append(fullName);
		builder.append(", type=");
		builder.append(type);
		builder.append(", province=");
		builder.append(province);
		builder.append(", totalAssets=");
		builder.append(totalAssets);
		builder.append(", assetsTime=");
		builder.append(assetsTime);
		builder.append(", establishDate=");
		builder.append(establishDate);
		builder.append(", headquarters=");
		builder.append(headquarters);
		builder.append(", shortName=");
		builder.append(shortName);
		builder.append(", shortNameEng=");
		builder.append(shortNameEng);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", updateTime=");
		builder.append(updateTime);
		builder.append(", operator=");
		builder.append(operator);
		builder.append(", establishDateStr=");
		builder.append(establishDateStr);
		builder.append(']');
		return builder.toString();
	}
	
}
