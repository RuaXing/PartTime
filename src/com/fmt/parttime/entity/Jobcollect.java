package com.fmt.parttime.entity; 

import java.io.Serializable;


 /**
  * 工作收藏类
  * @author Administrator
  *
  */
public class Jobcollect implements Serializable{ 
	public int jobCollectID; 
	public String jobCollectDate; 
	public int usersID; 
	public int jobID; 
 
	public Jobcollect(int jobCollectID) { 
		super(); 
		this.jobCollectID = jobCollectID; 
	} 
 
	public Jobcollect() { 
		super(); 
	} 
 
	public int getJobCollectID() { 
		return jobCollectID; 
	} 
 
	public void setJobCollectID(int jobCollectID) { 
		this.jobCollectID = jobCollectID; 
	} 
 
	public String getJobCollectDate() { 
		return jobCollectDate; 
	} 
 
	public void setJobCollectDate(String jobCollectDate) { 
		this.jobCollectDate = jobCollectDate; 
	} 
 
	public int getUsersID() { 
		return usersID; 
	} 
 
	public void setUsersID(int usersID) { 
		this.usersID = usersID; 
	} 
 
	public int getJobID() { 
		return jobID; 
	} 
 
	public void setJobID(int jobID) { 
		this.jobID = jobID; 
	} 
 
 
} 
