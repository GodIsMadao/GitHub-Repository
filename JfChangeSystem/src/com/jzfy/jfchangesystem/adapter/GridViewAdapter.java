package com.jzfy.jfchangesystem.adapter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.jzfy.jfchangesystem.R;
import com.jzfy.jfchangesystem.activity.MainActivity;
import com.jzfy.jfchangesystem.entity.Product;
import com.jzfy.jfchangesystem.util.BitmapCache;
import com.jzfy.jfchangesystem.util.VolleySingleton;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class GridViewAdapter extends BaseAdapter {
	// private List<HashMap<String, Product>> list;
	// private Product product;
	private List<Product> plist;
	// private LayoutInflater layoutInflater;
	private Context mContext;
	private Button btn_add_cart;// 加入购物车
	// private Product product;
	private PopupWindow popupView;
	private LayoutInflater mInflater;
	private static ImageLoader imageLoader;// 图片缓存器

	public GridViewAdapter(ArrayList<Product> plist, Context mContext) {
		super();
		this.plist = plist;
		this.mContext = mContext;
	}
	
	@Override
	public int getCount() {
		return plist.size();
	}

	@Override
	public Object getItem(int position) {
		return plist.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.market_content_show, parent, false);
			holder = new ViewHolder();
			holder.img_icon = (ImageView) convertView
					.findViewById(R.id.img_market_product);
			holder.tv_pname = (TextView) convertView
					.findViewById(R.id.tv_pname);
			holder.tv_jfprice = (TextView) convertView
					.findViewById(R.id.tv_jfprice);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		RequestQueue mQueue = VolleySingleton.getVolleySingleton(mContext)
				.getRequestQueue();
		ImageLoader imageLoader = VolleySingleton.getVolleySingleton(mContext)
				.getImageLoader();
		ImageListener listener = ImageLoader.getImageListener(holder.img_icon,
				R.drawable.picture_bg_loding_3, R.drawable.picture_bg_loding_3);
		imageLoader.get("http://115.236.69.226:8764/DJF-Pad/"
				+ plist.get(position).getimage(), listener);
		
		// VolleySingleton.getVolleySingleton(mContext).addToRequestQueue();
//		Log.i("image", plist.get(position).getimage());
		// +plist.get(position).getimage()
		holder.tv_jfprice.setText(plist.get(position).getprice() + "积分");
		holder.tv_pname.setText(plist.get(position).getname());
		return convertView;
	}

	static class ViewHolder {
		ImageView img_icon;
		Button btn_add;
		TextView tv_pname;
		TextView tv_brand;
		TextView tv_producer;
		TextView tv_count;
		TextView tv_jfprice;
	}

}
