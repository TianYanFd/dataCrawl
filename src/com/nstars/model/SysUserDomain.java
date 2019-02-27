package com.nstars.model;

import java.util.Date;

public class SysUserDomain {
	
	private Long id;
	
	private String name;
	
	private String pwd;
	
	private Date createTime;
	
	private Date updateTime;
	
	private String operator;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SysUserDomain [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", pwd=");
		builder.append(pwd);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", updateTime=");
		builder.append(updateTime);
		builder.append(", operator=");
		builder.append(operator);
		builder.append(']');
		return builder.toString();
	}

}
