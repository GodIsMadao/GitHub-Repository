package com.jzfy.jfchangesystem.adapter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.jzfy.jfchangesystem.R;
import com.jzfy.jfchangesystem.entity.Cart;
import com.jzfy.jfchangesystem.entity.Product;
import com.jzfy.jfchangesystem.util.VolleySingleton;
//import com.jzfy.jfchangesystem.entity.Product;

import android.content.Context;
import android.text.StaticLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class ListViewAdapter extends BaseAdapter {
	private ArrayList<Cart> products;
	private Context mContext;
	// 第一步，设置接口
	private View.OnClickListener onAddNum; // 加商品数量接口
	private View.OnClickListener onSubNum; // 减商品数量接口

	public ListViewAdapter(ArrayList<Cart> plist, Context mContext) {
		super();
		this.products = plist;
		this.mContext = mContext;
	}

	public void setOnAddNum(View.OnClickListener onAddNum) {
		this.onAddNum = onAddNum;
	}

	public void setOnSubNum(View.OnClickListener onSubNum) {
		this.onSubNum = onSubNum;
	}

	@Override
	public int getCount() {
		int ret = 0;
		if (products != null) {
			ret = products.size();
		}
		return ret;

	}

	@Override
	public Object getItem(int position) {
		return products.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.cart_show, parent, false);
			holder = new ViewHolder();

			Log.i("info",
					(convertView.findViewById(R.id.imageview_cart)).toString());
//				holder.btn_delete=(Button) convertView.findViewById(R.id.btn_delete);
				holder.tv_amount_price=(TextView) convertView.findViewById(R.id.tv_amount_price);
				holder.tv_amount_count=(TextView) convertView.findViewById(R.id.tv_amount_count);
				holder.img = (ImageView) convertView
						.findViewById(R.id.imageview_cart);
				holder.tv_name = (TextView) convertView
						.findViewById(R.id.product_name);
				holder.tv_count = (TextView) convertView
						.findViewById(R.id.product_count);
				holder.tv_jf = (TextView) convertView
						.findViewById(R.id.product_jf);
				holder.tv_product_num = (TextView) convertView
						.findViewById(R.id.item_product_num);
				holder.item_btn_add = (ImageButton) convertView
						.findViewById(R.id.item_btn_add);
				holder.item_btn_add.setOnClickListener(onAddNum);
				holder.item_btn_sub = (ImageButton) convertView
						.findViewById(R.id.item_btn_sub);
				holder.item_btn_sub.setOnClickListener(onSubNum);
				convertView.setTag(holder); // 将Holder存储到convertView中

				Log.i("info", "product name" + holder.tv_name);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
		holder.tv_amount_count.setText("数量："+products.get(position).getSingle_count()+"");
		holder.tv_amount_price.setText("总计："+products.get(position).getPrice()+"积分");
		holder.tv_product_num.setText(products.get(position).getSingle_count()+"");
		holder.tv_name.setText(products.get(position).getName());
//		System.out.println(holder.tv_name.getText().toString());
		holder.tv_count.setText("库存:"
				+ products.get(position).getCount());
		holder.tv_jf.setText(products.get(position).getSingle_price()
				+ "积分");
		// 设置Tag，用于判断用户当前点击的哪一个列表项的按钮，解决问题：如何知道你点击的按钮是哪一个列表项中的
		holder.item_btn_add.setTag(position);
		holder.item_btn_sub.setTag(position);
		RequestQueue mQueue = VolleySingleton.getVolleySingleton(mContext)
				.getRequestQueue();
		ImageLoader imageLoader = VolleySingleton.getVolleySingleton(mContext)
				.getImageLoader();
		ImageListener listener = ImageLoader.getImageListener(holder.img,
				R.drawable.picture_bg_loding_3, R.drawable.picture_bg_loding_3);
		imageLoader.get("http://115.236.69.226:8764/DJF-Pad/"
				+ products.get(position).getImage(), listener);
//		convertView.setTag(holder);
		return convertView;
	}

	// 优化BaseAdapter,防止多次find id,把holder存入converView缓存中
	static	class ViewHolder {
		private ImageView img;
		private TextView tv_name;
		private TextView tv_count;// 庫存
		private TextView tv_product_num;// 数量
		private TextView tv_jf;
		// 增减商品数量按钮
		private ImageButton item_btn_add;
		private ImageButton item_btn_sub;
		//每单个商品总数
		private TextView tv_amount_count;
		//每单个商品总价
		private TextView tv_amount_price;
//		private Button btn_delete;
	}
}
