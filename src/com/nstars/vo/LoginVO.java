package com.nstars.vo;

import java.io.Serializable;

public class LoginVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7122125282329726965L;
	
	private String name;
	
	private String pwd;
	//0-失败，1-成功
	private Integer status;
	
	private String errMsg;

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

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
	
}
