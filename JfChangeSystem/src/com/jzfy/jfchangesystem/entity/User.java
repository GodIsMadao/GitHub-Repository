package com.jzfy.jfchangesystem.entity;

public class User {
	private int id;// id
	private int status;// 登录状态
	private long restJf;// 剩余积分
	private int cardNum;// 卡号
	private int idcardNum;// 身份证号
	private String uname;// 用户名字

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getRestJf() {
		return restJf;
	}

	public void setRestJf(long restJf) {
		this.restJf = restJf;
	}

	public int getCardNum() {
		return cardNum;
	}

	public void setCardNum(int cardNum) {
		this.cardNum = cardNum;
	}

	public int getIdcardNum() {
		return idcardNum;
	}

	public void setIdcardNum(int idcardNum) {
		this.idcardNum = idcardNum;
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public User(int status, long restJf, int cardNum, String uname) {
		super();
		this.status = status;
		this.restJf = restJf;
		this.cardNum = cardNum;
		this.uname = uname;
	}

}
