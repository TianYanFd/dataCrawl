package com.nstars.model;

import java.sql.Timestamp;

public class ProxyServer {
	//物理主键
	private int id;
	//IP
	private String host;
	//端口号
	private Integer port;
	//类型
	private String telecomType;
	//归属地
	private String attribution;
	//入库时间
	private Timestamp loadTime;
	//成功次数
	private Integer sucCount;
	//失败次数
	private Integer failCount;
	//是否有效 0-无效，1-有效
	private Integer isEffective;
	//最近更新时间
	private Timestamp updateTime;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	public String getTelecomType() {
		return telecomType;
	}
	public void setTelecomType(String telecomType) {
		this.telecomType = telecomType;
	}
	public String getAttribution() {
		return attribution;
	}
	public void setAttribution(String attribution) {
		this.attribution = attribution;
	}
	public Timestamp getLoadTime() {
		return loadTime;
	}
	public void setLoadTime(Timestamp loadTime) {
		this.loadTime = loadTime;
	}
	public Integer getSucCount() {
		return sucCount;
	}
	public void setSucCount(Integer sucCount) {
		this.sucCount = sucCount;
	}
	public Integer getFailCount() {
		return failCount;
	}
	public void setFailCount(Integer failCount) {
		this.failCount = failCount;
	}
	/**
	 * @return the isEffective
	 */
	public Integer getIsEffective() {
		return isEffective;
	}
	/**
	 * @param isEffective the isEffective to set
	 */
	public void setIsEffective(Integer isEffective) {
		this.isEffective = isEffective;
	}
	/**
	 * @return the updateTime
	 */
	public Timestamp getUpdateTime() {
		return updateTime;
	}
	/**
	 * @param updateTime the updateTime to set
	 */
	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProxyServer [id=");
		builder.append(id);
		builder.append(", host=");
		builder.append(host);
		builder.append(", port=");
		builder.append(port);
		builder.append(", telecomType=");
		builder.append(telecomType);
		builder.append(", attribution=");
		builder.append(attribution);
		builder.append(", loadTime=");
		builder.append(loadTime);
		builder.append(", sucCount=");
		builder.append(sucCount);
		builder.append(", failCount=");
		builder.append(failCount);
		builder.append(", isEffective=");
		builder.append(isEffective);
		builder.append(", updateTime=");
		builder.append(updateTime);
		builder.append(']');
		return builder.toString();
	}
	
}
