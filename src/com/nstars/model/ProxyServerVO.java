package com.nstars.model;

import java.io.Serializable;

public class ProxyServerVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4771758283664269445L;
	
    private String ip;
    
    private int port;
    
    private boolean isEffective;
    
    private String errMsg;

	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @param ip the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @return the isEffective
	 */
	public boolean isEffective() {
		return isEffective;
	}

	/**
	 * @param isEffective the isEffective to set
	 */
	public void setEffective(boolean isEffective) {
		this.isEffective = isEffective;
	}

	/**
	 * @return the errMsg
	 */
	public String getErrMsg() {
		return errMsg;
	}

	/**
	 * @param errMsg the errMsg to set
	 */
	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

}
