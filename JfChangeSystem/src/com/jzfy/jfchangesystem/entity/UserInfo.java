package com.jzfy.jfchangesystem.entity;

import android.Manifest.permission;

public class UserInfo {
	private int result;
	private String token;
	private String clientNo;
	private String msg;
	private int empId;//登录的ID
	private String username;
	private String point;
	
	private String clientId;
	private String cardNo;
	
	
	public String getClientId() {
		return clientId;
	}


	public void setClientId(String clientId) {
		this.clientId = clientId;
	}



	public String getCardNo() {
		return cardNo;
	}



	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}



	public String getUsername() {
		return username;
	}



	public void setUsername(String username) {
		this.username = username;
	}



	public String getPoint() {
		return point;
	}



	public void setPoint(String point) {
		this.point = point;
	}



	public int getEmpId() {
		return empId;
	}



	public void setEmpId(int empId) {
		this.empId = empId;
	}



	public int getResult() {
		return result;
	}



	public void setResult(int result) {
		this.result = result;
	}



	public String getToken() {
		return token;
	}



	public void setToken(String token) {
		this.token = token;
	}



	public String getClientNo() {
		return clientNo;
	}



	public void setClientNo(String clientNo) {
		this.clientNo = clientNo;
	}



	public String getMsg() {
		return msg;
	}



	public void setMsg(String msg) {
		this.msg = msg;
	}



	public UserInfo() {
		super();
	}
	
	
}
