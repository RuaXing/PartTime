package com.fmt.parttime.entity;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;



/**
 * 通知客户端的消息类
 * @author Administrator
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Message implements Serializable {
	
	private int statuId;//标识
	private String msg;//内容
	private String data;//附带数据
	
	
	public Message() {
		super();
	}
	public Message(int statuId, String msg, String data) {
		super();
		this.statuId = statuId;
		this.msg = msg;
		this.data = data;
	}
	public int getStatuId() {
		return statuId;
	}
	public void setStatuId(int statuId) {
		this.statuId = statuId;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	
	

}
