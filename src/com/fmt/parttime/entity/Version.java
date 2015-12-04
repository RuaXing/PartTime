package com.fmt.parttime.entity; 

import java.io.Serializable;
 

 
public class Version implements Serializable{ 
	public double versionID; 
	public String apkName; 
	public String apkUrl; 
 
	public Version(double versionID) { 
		super(); 
		this.versionID = versionID; 
	} 
 
	public Version() { 
		super(); 
	} 
 
	public double getVersionID() { 
		return versionID; 
	} 
 
	public void setVersionID(int versionID) { 
		this.versionID = versionID; 
	} 
 
	public String getApkName() { 
		return apkName; 
	} 
 
	public void setApkName(String apkName) { 
		this.apkName = apkName; 
	} 
 
	public String getApkUrl() { 
		return apkUrl; 
	} 
 
	public void setApkUrl(String apkUrl) { 
		this.apkUrl = apkUrl; 
	} 
 
 
} 
