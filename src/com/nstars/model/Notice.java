package com.nstars.model;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 同业存单公告(发行公告、发行情况公告)
 * */
public class Notice {
	//id
	private int id;
	
	//公告名称
	private String fileName;
	
	//发布日期
	private Date issueDate;
	
	//公告类型 0-发行情况公告，1-发行公告
	private String type;
	
	//入库时间
	private Timestamp loadTime;
	
	//发布日期STR
	private String issueDateStr;
	
	//格式化日期
	DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Date getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Timestamp getLoadTime() {
		return loadTime;
	}

	public void setLoadTime(Timestamp loadTime) {
		this.loadTime = loadTime;
	}
	
	public String getIssueDateStr() {
		return issueDateStr;
	}

	public void setIssueDateStr(String issueDateStr) {
		this.issueDateStr = issueDateStr;
	}

	@Override
	public String toString() {
		return "Notice [fileName=" + fileName + ", issueDate=" + sdf.format(issueDate)
				+ ", type=" + type + ", loadTime=" + loadTime + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Notice other = (Notice) obj;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		if (issueDate == null) {
			if (other.issueDate != null)
				return false;
		} else if (!issueDate.equals(other.issueDate))
			return false;
		return true;
	}
	
	public String toShortString() {
		return "[" + sdf.format(issueDate) + "," + fileName + "]";
	}
	

}
