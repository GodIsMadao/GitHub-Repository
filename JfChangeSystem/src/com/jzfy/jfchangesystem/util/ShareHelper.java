package com.jzfy.jfchangesystem.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jzfy.jfchangesystem.entity.Product;

//import com.jzfy.jfchangesystem.entity.Product;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

public class ShareHelper {

	private Context mContext;

	public ShareHelper(Context mContext) {
		super();
		this.mContext = mContext;
	}

	public ShareHelper() {
		super();
	}

	
	//保存读卡器蓝牙地址
	public void save_cardaddress(String card_address){
		SharedPreferences sp = mContext.getSharedPreferences("mysp_card_address",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString("card_address", card_address);
		editor.commit();
		Toast.makeText(mContext, "信息已写入SharedPreference中", Toast.LENGTH_SHORT)
		.show();
	}
	
	//保存打印机蓝牙地址
	public void save_printaddress(String printer_address){
		SharedPreferences sp = mContext.getSharedPreferences("mysp_printer_address",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString("printer_address", printer_address);
		editor.commit();
		Toast.makeText(mContext, "信息已写入SharedPreference中", Toast.LENGTH_SHORT)
		.show();
	}
	
	
	
	// 定义一个保存数据的方法
	public void save(Product product) {
		SharedPreferences sp = mContext.getSharedPreferences("mysp",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt("id", product.getId());
		editor.putString("name", product.getname());
		editor.putString("brand", product.getBrand());
		editor.putString("producer", product.getProducer());
		editor.putLong("price", product.getprice());
		editor.putInt("count", product.getCount());
		editor.putString("image", product.getimage());
		editor.commit();
		Toast.makeText(mContext, "信息已写入SharedPreference中", Toast.LENGTH_SHORT)
				.show();
	}

	public void save(int userstatus){
		SharedPreferences sp = mContext.getSharedPreferences("mysp_userstatus",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor=sp.edit();
		editor.putInt("userstatus", userstatus);
		editor.commit();
		Toast.makeText(mContext, "userstatus改变", Toast.LENGTH_SHORT)
		.show();
	}
	
	public void save(String cartJson,int userid){
		SharedPreferences sp = mContext.getSharedPreferences("mysp_cartJson",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor=sp.edit();
		editor.putString("cartJson", cartJson);
		editor.putInt("userid", userid);
		editor.commit();
		Toast.makeText(mContext, "cart信息已写入SharedPreference中", Toast.LENGTH_SHORT)
		.show();
	}
	public void save(String responseJson){
		SharedPreferences sp = mContext.getSharedPreferences("mysp_response",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor=sp.edit();
		editor.putString("responseJson", responseJson);
		editor.commit();
		Toast.makeText(mContext, "response信息已写入SharedPreference中", Toast.LENGTH_SHORT)
		.show();
	}
	// 定义一个保存数据的方法
	public void save(String market_overtime, String login_overtime, String ip,
			String branchid,String port) {
		SharedPreferences sp = mContext.getSharedPreferences("mysp_setting",
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString("market_overtime", market_overtime);
		editor.putString("login_overtime", login_overtime);
		editor.putString("ip", ip);
		editor.putString("branchid", branchid);
		editor.putString("port", port);
		editor.commit();
		Toast.makeText(mContext, "信息已写入SharedPreference中", Toast.LENGTH_SHORT)
				.show();
	}
	
	
	 public  void clearData() {
		 SharedPreferences sp = mContext.getSharedPreferences("mysp_cartJson",
					Context.MODE_PRIVATE);
		 SharedPreferences.Editor editor=sp.edit();
		 sp.edit().clear().commit();
		 Toast.makeText(mContext, "已从sharepreference里删除", Toast.LENGTH_SHORT).show();
	 }

	//
	// 定义一个读取SP文件的方法
	public Map<String, Product> read() {
		Map<String, Product> data = new HashMap<String, Product>();
		SharedPreferences sp = mContext.getSharedPreferences("mysp",
				Context.MODE_WORLD_WRITEABLE);
		Product product = new Product(sp.getInt("count", 0), sp.getString(
				"brand", ""), sp.getString("producer", ""), sp.getString(
				"name", ""), sp.getLong("price", 0), sp.getString("image", ""));
		data.put("product", product);
		return data;
	}
}
