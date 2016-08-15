package com.jzfy.jfchangesystem.entity;

import java.io.Serializable;

import android.R.integer;
import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {
	private int id;
	private int count;
	private String brand;
	private String producer;
	private String name;
	private long price;
	private String image;

	private String barcode;
	private String msg;
	private String unit;
	private int alarmcount;
	private String remark;
	private int status;

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public int getAlarmcount() {
		return alarmcount;
	}

	public void setAlarmcount(int alarmcount) {
		this.alarmcount = alarmcount;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getname() {
		return name;
	}

	public void setname(String name) {
		this.name = name;
	}

	public long getprice() {
		return price;
	}

	public void setprice(long price) {
		this.price = price;
	}

	public String getimage() {
		return image;
	}

	public void setimage(String image) {
		this.image = image;
	}

	public Product(int count, String name, long price, String image) {
		super();
		// this.id = id;
		this.count = count;
		this.name = name;
		this.price = price;
		this.image = image;
	}

	public Product(int count, String brand, String producer, String name,
			long price, String image) {
		super();
		this.count = count;
		this.brand = brand;
		this.producer = producer;
		this.name = name;
		this.price = price;
		this.image = image;
	}

	public Product() {
		super();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(count);
		out.writeString(brand);
		out.writeString(producer);
		out.writeString(name);
		out.writeLong(price);
		out.writeString(image);
	}

	public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
		public Product createFromParcel(Parcel in) {
			return new Product(in);
		}

		public Product[] newArray(int size) {
			return new Product[size];
		}
	};

	private Product(Parcel in) {
		count = in.readInt();
		brand = in.readString();
		producer = in.readString();
		name = in.readString();
		price = in.readLong();
		image = in.readString();
	}

}
