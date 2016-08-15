package com.jzfy.jfchangesystem.entity;

import java.util.List;

public class Cart {
//	private UserInfo userInfo;
	
	private int id;
	private String billno;
	private int single_count;//单品数量
	private long price;//单品总价格
	private int count;//库存
	private String brand;
	private String producer;
	private String name;
	private long single_price;//单品价格
	private String image;
	
//	public UserInfo getUserInfo() {
//		return userInfo;
//	}
//	public void setUserInfo(UserInfo userInfo) {
//		this.userInfo = userInfo;
//	}
	
	
	
	
	public int getSingle_count() {
		return single_count;
	}
	public String getBillno() {
		return billno;
	}
	public void setBillno(String billno) {
		this.billno = billno;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setSingle_count(int single_count) {
		this.single_count = single_count;
	}
	public long getPrice() {
		return price;
	}
	public void setPrice(long price) {
		this.price = price;
	}
	public Cart() {
		super();
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getProducer() {
		return producer;
	}
	public void setProducer(String producer) {
		this.producer = producer;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getSingle_price() {
		return single_price;
	}
	public void setSingle_price(long single_price) {
		this.single_price = single_price;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public Cart(int id,int single_count, long price, int count, String brand,
			String producer, String name, long single_price, String image) {
		super();
		this.id=id;
		this.single_count = single_count;
		this.price = price;
		this.count = count;
		this.brand = brand;
		this.producer = producer;
		this.name = name;
		this.single_price = single_price;
		this.image = image;
	}
	
	
}
