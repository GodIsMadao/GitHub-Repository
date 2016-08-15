package com.jzfy.jfchangesystem.activity;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import pl.droidsonroids.gif.GifImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
//import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.jzfy.jfchangesystem.R;
import com.jzfy.jfchangesystem.adapter.GridViewAdapter;
import com.jzfy.jfchangesystem.entity.Cart;
import com.jzfy.jfchangesystem.entity.Product;
import com.jzfy.jfchangesystem.entity.Product;
import com.jzfy.jfchangesystem.entity.ResponseType;
import com.jzfy.jfchangesystem.entity.User;
import com.jzfy.jfchangesystem.entity.UserInfo;
import com.jzfy.jfchangesystem.logic.VersionAPI;
import com.jzfy.jfchangesystem.setting.ApplicationEx;
import com.jzfy.jfchangesystem.util.ShareHelper;
import com.jzfy.jfchangesystem.util.ToastUtil;
import com.jzfy.jfchangesystem.util.VolleySingleton;
import com.jzfy.jfchangesystem.view.AutoScrollViewPager;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
//import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.yxp.loading.lib.AdhesionLoadingView;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.SystemClock;
import android.print.PrintAttributes.Resolution;
import android.provider.Browser.BookmarkColumns;
import android.provider.ContactsContract.Contacts.Data;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

public class FragmentMarket extends Fragment implements OnClickListener {

	
	private int status = 0;// 判断是否重复状态
	private static final String TAG = "MyLog";
	private static final int SEARCH_JF_CODE = 2;
	protected static final int SUCCESS_GET_CONTACT = 0;
	private static final int REQUEST_CODE=1;
	private static FragmentMarket f;
	
	
	Timer timer = new Timer();  //倒计时
	
	private File cache;

	
	private SharedPreferences sp;
	private ShareHelper sp1;
	AutoScrollViewPager mPosterPager;
	LinearLayout pointsLayout;
	PosterPagerAdapter adAdapter;
	private List<Cart> carts = new ArrayList<Cart>();

	private ShareHelper sh;
	private FragmentManager fm;
	private Context mContext;
	private TextView tv_title;

//	private AdhesionLoadingView loading_view;
	private TextView tView_loading;
	private TextView tv_count_time;
	private TextView tv_name;
	private TextView tv_brand;
	private TextView tv_jfprice;
	private TextView tv_producer;
	private TextView tv_rest;
	private Button btn_addToCart;
	private ImageView img_icon;
	private int user_status; 
	 private Timer mTimer;  
	private Button btn_search_jf;
	private ImageView img_pop_goback;
	private Button btn_login;
	private Button btn_logout;
	private ScrollView sv;
	private GridView gv;
	private ApplicationEx application;
	final List<String> t = new ArrayList<String>();

	//
	
	private String URL;
	private static String IP="115.236.69.226";
	private static String port="8764";
	private static String branchIdString="970000";
	
	
	//
	List<Product> plist = new ArrayList<>();
	GridViewAdapter gvAdapter = null;// 自定义网格视图适配器
	// 循环间隔3秒
	private int interval = 3 * 1000;
	private JsonObjectRequest jr = null;
	// 标记点集合
	private List<ImageView> points = null;
	// 当前轮播位置
	private int currentIndex = 0;

	 private int recLen ;
	
	private String baseURL = null;
	private ResponseType reType;
	private Gson gson = null;
	private List<Product> products;
	public String productString;

	public static FragmentMarket newInstanec() {
		if (f == null) {
			f = new FragmentMarket();
		}

		return f;

	}

	String url[] = { "http://www.zj96596.com/sy/zgg/images/20140115/293.jpg",
			"http://www.zj96596.com/sy/zgg/images/20160113/567.jpg",
			"http://www.zj96596.com/sy/zgg/images/20160113/568.jpg" };

	private OnTouchListener viewPagerOnTouch = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mPosterPager.stopAutoScroll();
				break;

			case MotionEvent.ACTION_MOVE:
				tryAutoScroll();
				break;

			case MotionEvent.ACTION_UP:
				tryAutoScroll();
				break;
			default:
				break;
			}
			return false;
		}
	};

	/***
	 * 尝试开始自动滑动.
	 */
	private void tryAutoScroll() {
		if (adAdapter.dataList.size() > 1) {
			mPosterPager.startAutoScroll();
		} else {
			mPosterPager.stopAutoScroll();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.e(TAG, "========onCreate======");

		super.onCreate(savedInstanceState);

	}

	// 保存数据
	@SuppressWarnings("unchecked")
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		sp = getActivity().getSharedPreferences("mysp_response",
				Context.MODE_WORLD_READABLE);
		savedInstanceState.putString("responseJson",
				sp.getString("responseJson", ""));
		Log.e(TAG, "onSaveInstanceState1");
		super.onSaveInstanceState(savedInstanceState);
		Log.e(TAG,
				"onSaveInstanceState2"
						+ savedInstanceState.getString("responseJson"));
	}

	// 恢复数据
	@Override
	public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		if (savedInstanceState != null) {
			productString = savedInstanceState.getString("responseJson");
		}
		Log.i(TAG, "=======onViewStateRestored======" + savedInstanceState);
	}
	
	 final Handler handler = new Handler(){  
		  
	        public void handleMessage(Message msg){         // handle message   
	            switch (msg.what) {  
	            case 1:  
	                recLen--;  
	                tv_count_time.setText("" + recLen);  
	  
	                if(recLen > 0){  
	                    Message message = handler.obtainMessage(1);  
	                    handler.sendMessageDelayed(message, 1000);      // send message   
	                }else{  
	                    tv_count_time.setVisibility(View.GONE);  
	                }  
	            case 2:
	            	setViewAndButton(btn_login, btn_logout, tv_title, mContext);
	            }  
	  
	            super.handleMessage(msg);  
	        }  
	    };  
	    
	
	      
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container,
			@Nullable final Bundle savedInstanceState) {
		Toast.makeText(getActivity(), "======createView=====",
				Toast.LENGTH_SHORT).show();
		final Date startDate = new Date(System.currentTimeMillis());
		application = (ApplicationEx) getActivity().getApplicationContext();
		mContext = getActivity();
		
		SharedPreferences sPreferences=getActivity().getSharedPreferences("mysp_setting", Context.MODE_PRIVATE);
		
		if(!sPreferences.getString("ip", "").equals("")){
		IP=sPreferences.getString("ip", "");
		port=sPreferences.getString("port", "");
		branchIdString=sPreferences.getString("branchid", "");
		}
		plist = new ArrayList<Product>();
		View view = inflater.inflate(R.layout.market, container, false);
//		loading_view=(AdhesionLoadingView) view.findViewById(R.id.gif_loading);
		tView_loading=(TextView) view.findViewById(R.id.tv_loading);
		/*
		 * 获取json数据
		 */
		Log.i(TAG, "=======查看saveInstanceState======");
		if (savedInstanceState != null) {
			productString = savedInstanceState.getString("responseJson");
			Log.i(TAG, "=========已经有了productString==========");
			Gson gson = new Gson();
			reType = gson.fromJson(productString, ResponseType.class);
			for (Product product : reType.getList()) {
				plist.add(product);
				System.out.println(product.getId());
			}
		} else {
			sh = new ShareHelper(getActivity());
			Log.e(TAG, "========还会经过这里么========");
			jr = new JsonObjectRequest(
					"http://"+IP+":"+port+"/DJF-Pad/pad/giftList.action",
					null, new Response.Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject response) {
							Log.e(TAG, "====GET RESPONSE=======");
							sh.save(response.toString());
							Gson gson = new Gson();
							reType = gson.fromJson(response.toString(),
									ResponseType.class);
							for (Product product : reType.getList()) {
								plist.add(product);
								setListViewHeightBasedOnChildren(gv);
							}
//							loading_view.setVisibility(View.GONE);
							tView_loading.setVisibility(View.GONE);
							Date endDate = new Date(System.currentTimeMillis());
							long diff = endDate.getTime() - startDate.getTime();
							Toast.makeText(getActivity(), "===使用时间====="+diff, Toast.LENGTH_LONG).show();
							//载入时间稳定在2500ms
						}
					}, new ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							Log.e("TAG", error.getMessage(), error);
							Toast.makeText(getActivity(), "系统连接超时，请联系柜台人员", Toast.LENGTH_SHORT).show();
						}
					});
			VolleySingleton.getVolleySingleton(getActivity())
					.addToRequestQueue(jr);
		}

		
		String userJson = getActivity().getIntent().getStringExtra("user");
		
		
		// 显示倒计时
		tv_count_time = (TextView) view.findViewById(R.id.tv_count_time);
		sp = getActivity().getSharedPreferences("mysp_setting",
				Context.MODE_WORLD_READABLE);
		String market_overtime =sp.getString("market_overtime", "0");
		recLen=Integer.parseInt(market_overtime);
		Message message = handler.obtainMessage(1);     // Message   
        handler.sendMessageDelayed(message, 1000);  
		
		
//		tv_count_time.setText(market_overtime);

		btn_search_jf = (Button) view.findViewById(R.id.search_jf);
		btn_login = (Button) view.findViewById(R.id.log_in_out);

		if (ApplicationEx.getApp().getADMIN_STATUS() == 0) {
			btn_login.setVisibility(View.VISIBLE);
			btn_search_jf.setVisibility(View.GONE);
		} else {
			btn_login.setVisibility(View.GONE);
			btn_search_jf.setVisibility(View.VISIBLE);
		}

		tv_title = (TextView) view.findViewById(R.id.tv_welcome);
		btn_logout = (Button) view.findViewById(R.id.log_out);
		btn_search_jf.setOnClickListener(this);
		btn_login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Toast.makeText(getActivity(), "点击了登录", Toast.LENGTH_SHORT)
						.show();
				Intent intent = new Intent();
				intent.setClass(getActivity(), LoginActivity.class);
				 //得到新打开Activity关闭后返回的数据
                //第二个参数为请求码，可以根据业务需求自己编号
				getActivity().startActivityForResult(intent, REQUEST_CODE);
//				getActivity().finish();
//				getActivity().startActivity(intent);
				Log.e("LALLALALA","是否经过这里");
			}
		});
		btn_logout.setOnClickListener(this);
		gv = (GridView) view.findViewById(R.id.gv_market);
		Log.i("USER STATUS",ApplicationEx.getApp().getUSER_STATUS()+"LLLL");
		user_status=ApplicationEx.getApp().getUSER_STATUS();
		
		
		final Builder builder = new AlertDialog.Builder(getActivity());
		gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				/*
				 * 
				 * 做个判断，如果登录用户信息不存在，则不显示加入购物车按钮。
				 */
				Log.e(TAG, reType.toString() + "popwindow");
				final View popView = LayoutInflater.from(getActivity())
						.inflate(R.layout.cart_popwindow, null);
				System.out.println(popView.toString());
				btn_addToCart = (Button) popView
						.findViewById(R.id.btn_productinfo_addToCart);
				tv_name = (TextView) popView
						.findViewById(R.id.tv_productinfo_name);
				tv_brand = (TextView) popView
						.findViewById(R.id.tv_productinfo_brand);
				tv_jfprice = (TextView) popView
						.findViewById(R.id.tv_productinfo_jfprice);
				tv_producer = (TextView) popView
						.findViewById(R.id.tv_productinfo_producer);
				tv_rest = (TextView) popView
						.findViewById(R.id.tv_productinfo_rest);
				img_icon = (ImageView) popView
						.findViewById(R.id.img_product_info);
				img_pop_goback = (ImageView) popView
						.findViewById(R.id.img_pop_goback);
				// 点击事件显示弹框页面
				tv_name.setText(plist.get(position).getname());
				tv_brand.setText("品牌：" + plist.get(position).getBrand());
				tv_jfprice.setText(plist.get(position).getprice() + "积分");
				tv_producer.setText("生产商：" + plist.get(position).getProducer());
				tv_rest.setText("库存：" + plist.get(position).getCount());
				RequestQueue mQueue = VolleySingleton.getVolleySingleton(
						getActivity()).getRequestQueue();
				com.android.volley.toolbox.ImageLoader imageLoader = VolleySingleton
						.getVolleySingleton(getActivity()).getImageLoader();
				ImageListener listener = com.android.volley.toolbox.ImageLoader
						.getImageListener(img_icon,
								R.drawable.picture_bg_loding_3,
								R.drawable.picture_bg_loding_3);
				SharedPreferences sPreferences=getActivity().getSharedPreferences("mysp_setting", Context.MODE_PRIVATE);
				
				IP=sPreferences.getString("ip", "");
				port=sPreferences.getString("port", "");
				branchIdString=sPreferences.getString("branchid", "");
				
				imageLoader.get("http://"+IP+":"+port+"/DJF-Pad/"
						+ plist.get(position).getimage(), listener);
				// System.out.println(tv_name.getText());
				img_pop_goback.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						popView.setVisibility(View.GONE);
						View view3 = LayoutInflater.from(getActivity())
								.inflate(R.layout.double_return, null);
						/*
						 * 
						 * 双击返回
						 */
					}
				});
				builder.setView(popView);
				final Dialog dialog=builder.show();
				
				// Log.i("plist4", plist.toString());
				btn_addToCart.setOnClickListener(new OnClickListener() {

					@SuppressWarnings("unused")
					@Override
					public void onClick(View view) {
						Log.i("USER STATUS",ApplicationEx.getApp().getUSER_STATUS()+"LLLL");
						if (ApplicationEx.getApp().getUserInfo()==null){
							Toast.makeText(getActivity(), "请先登录",
									Toast.LENGTH_SHORT).show();
						} else {
							Log.i("USER INFO MA TION ",ApplicationEx.getApp().getUserInfo().getToken());
							ShareHelper sh = new ShareHelper(getActivity());
							SharedPreferences sp = getActivity()
									.getSharedPreferences("mysp_cartJson",
											Context.MODE_PRIVATE);
							Toast.makeText(getActivity(), "点击了加入购物车",
									Toast.LENGTH_SHORT).show();
							Product product = new Product();
							product.setCount(plist.get(position).getCount());
							product.setBrand(plist.get(position).getBrand());
							product.setimage(plist.get(position).getimage());
							product.setprice(plist.get(position).getprice());
							product.setname(plist.get(position).getname());
							product.setProducer(plist.get(position)
									.getProducer());
							/*
							 * 把购物车加入全局变量
							 */
							Product pro = plist.get(position);
							Cart cart = new Cart(pro.getId(),1, pro.getprice(), pro
									.getCount(), pro.getBrand(), pro
									.getProducer(), pro.getname(), pro
									.getprice(), pro.getimage());
							Log.i(TAG,
									cart.getPrice() + "积分"
											+ cart.getSingle_count() + "数量");
							if (sp.getString("cartJson", "").equals("")
									&& carts != null) {
								carts.clear();
							}
							if (carts != null) {
								for (int i = 0; i < carts.size(); i++) {
									Log.i("====判断重复====", carts.get(i)
											.getName() + "lalalla");
									if (cart.getName().equals(
											carts.get(i).getName())) {
										Toast.makeText(getActivity(),
												"购物车已经有该商品", Toast.LENGTH_SHORT)
												.show();
										status++;
									}
								}
							}
							if (status == 0) {
								carts.add(cart);
							}
							status = 0;
							/*
							 * 上述代码利用status判断是否重复加入购物车
							 */
							Log.i("cart-test2", sp.getString("cartJson", ""));
							Gson gson = new Gson();
							String str = gson.toJson(carts);
							Log.i("cart-test1", str);
							sh.save(str, sp.getInt("userId", 0));
							// sh.clearData();
							Log.i("cart-test2", sp.getString("cartJson", ""));
//							View view2 = LayoutInflater.from(getActivity())
//									.inflate(R.layout.add_success, null);
//							builder.setView(view2);
//							Dialog dialog2=builder.show();
//							dialog2.dismiss();
							Toast.makeText(getActivity(), "添加成功", Toast.LENGTH_SHORT).show();
							dialog.dismiss();
						}
					}
				});
			}

		});
		sv = (ScrollView) view.findViewById(R.id.sv_market);

		initImageLoader(getActivity());

//		List<HashMap<String, JfGift>> hashMapList = new ArrayList<HashMap<String, JfGift>>();

		Log.i(TAG, ApplicationEx.getApp().getTheme().toString());
		for (int i = 0; i < url.length; i++) {
			list.add(url[i]);
		}
		pointsLayout = (LinearLayout) view
				.findViewById(R.id.activity_marketing_ad_indicator);
		mPosterPager = (AutoScrollViewPager) view
				.findViewById(R.id.activity_marketing_ad_viewpager);
		points = new ArrayList<ImageView>();
		gvAdapter = new GridViewAdapter((ArrayList<Product>) plist,
				getActivity());

		initViewPager();
		initIndicator();
		gv.setAdapter(gvAdapter);
		if (savedInstanceState != null) {
			setListViewHeightBasedOnChildren(gv);
		}
		// 由于加了setListViewHeightBasedOnChildren()方法后，主页默认停留在gridview页面上，需要加自动滚动至顶部
		Log.d(TAG, "======GET RESPONSE OVER=====");
		sv.smoothScrollBy(0, 0);
		Log.i("USERINFO", "========USERINFO============");
		setViewAndButton(btn_login, btn_logout, tv_title, mContext);
		return view;
	}

	// 动态设置button和欢迎view
	// 回去再想想
	//不必要每次创建Activity都会发送一次查询积分请求，改！
	public static void setViewAndButton(final Button btn1, final Button btn2, final TextView tv,
			final Context mContext) {
		SharedPreferences sPreferences=mContext.getSharedPreferences("mysp_setting", Context.MODE_PRIVATE);
		
		IP=sPreferences.getString("ip", "");
		port=sPreferences.getString("port", "");
		branchIdString=sPreferences.getString("branchid", "");
		if (ApplicationEx.getApp().getUserInfo() == null
				&& ApplicationEx.getApp().getUSER_STATUS() == 0) {
			btn1.setVisibility(View.VISIBLE);
			btn2.setVisibility(View.GONE);
		} else if (ApplicationEx.getApp().getUserInfo() != null
				&& ApplicationEx.getApp().getUSER_STATUS() == 1) {
			String token = ApplicationEx.getApp().getUserInfo().getToken();
			String clientNo = ApplicationEx.getApp().getUserInfo()
					.getClientNo();
			Log.i("SHOW RESPONSE1",clientNo+"lalal ");
			Log.i("SHOW RESPONSE2",token+"lalal");
			Log.i("SHOW RESPONSE3","http://115.236.69.226:8764/DJF-Pad/pad/userPointGet.action?"
							+ "clientNo=" + clientNo + "&token=" + token);
			JsonObjectRequest jr = new JsonObjectRequest(
					"http://"+IP+":"+port+"/DJF-Pad/pad/userPointGet.action?"
							+ "clientNo=" + clientNo + "&token=" + token, null,
					new Listener<JSONObject>() {

						@Override
						public void onResponse(JSONObject response) {
							Log.e("SHOW RESPONSE4",response.toString());
							int result = 0;
							UserInfo userInfo = ApplicationEx.getApp().getUserInfo();
							if(ApplicationEx.getApp().getUSER_STATUS()==1){
							Log.i("USER INFO",userInfo.getClientNo()+"lala");
							}
							try {
								result = response.getInt("result");

							} catch (JSONException e) {
								e.printStackTrace();
							}
							if (result == 200) {
								try {
									userInfo.setUsername(response
											.getString("username"));
									userInfo.setResult(response
											.getInt("result"));
									userInfo.setPoint(response
											.getString("point"));
									userInfo.setMsg(response.getString("msg"));
								} catch (JSONException e) {
									e.printStackTrace();
								}
								ApplicationEx.getApp().setUserInfo(userInfo);
								Log.i("MY USERINFO", "用户内码："
										+ ApplicationEx.getApp().getUserInfo()
												.getClientNo() + "lalalalal");
								try {
									Toast.makeText(mContext,
											response.getString("msg"),
											Toast.LENGTH_SHORT).show();
								} catch (JSONException e) {
									e.printStackTrace();
								}
								Log.i("MY USERINFO", "用户内码："
										+ ApplicationEx.getApp().getUserInfo()
												.getClientNo() + "lalalalal");
								tv.setText("欢迎"+ApplicationEx.getApp().getUserInfo().getUsername()+",您剩余" + ApplicationEx.getApp().getUserInfo().getPoint()
										+ "积分");
								btn1.setVisibility(View.GONE);
								btn2.setVisibility(View.VISIBLE);
								ApplicationEx.getApp().setUSER_STATUS(0);
							} else if (result == 300) {
								try {
									Toast.makeText(mContext,
											response.getString("msg"),
											Toast.LENGTH_LONG).show();
								} catch (JSONException e) {
									e.printStackTrace();
								}
								return;
							}
						}

					}, new ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							Log.e("TAG", error.getMessage(), error);
						}
					});
			VolleySingleton.getVolleySingleton(mContext).addToRequestQueue(jr);
			tv.setVisibility(View.VISIBLE);
			if(ApplicationEx.getApp().getUSER_STATUS()==1){
			Log.i("SHOW RESPONSE",ApplicationEx.getApp().getUserInfo().getUsername()+"");
			}
		}
	}

	/**
	 * 动态设置GridView的高度 算上上下间隔40dp,循环加上40dp加每个item的height 可复用到listview
	 * 
	 * @param gridview
	 */
	public static void setListViewHeightBasedOnChildren(GridView gridView) {
		if (gridView == null)
			return;
		GridViewAdapter listAdapter = (GridViewAdapter) gridView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		if (listAdapter.getCount() % 4 == 0) {
			for (int i = 0; i < listAdapter.getCount() / 4; i++) {
				View listItem = listAdapter.getView(i, null, gridView);
				listItem.measure(0, 0);
				totalHeight = listItem.getMeasuredHeight() + totalHeight + 15;
			}
		} else {
			for (int i = 0; i < listAdapter.getCount() / 4 + 1; i++) {
				View listItem = listAdapter.getView(i, null, gridView);
				listItem.measure(0, 0);
				totalHeight += listItem.getMeasuredHeight() + 15;
			}
		}
		ViewGroup.LayoutParams params = gridView.getLayoutParams();
		params.height = totalHeight + (gridView.getHeight());
		gridView.setLayoutParams(params);
	}

	ArrayList<String> list = new ArrayList<String>();

	private void initViewPager() {
		adAdapter = new PosterPagerAdapter(list);

		mPosterPager.setAdapter(adAdapter);
		mPosterPager.setInterval(interval);
		mPosterPager.setScrollDurationFactor(2f);
		mPosterPager.setOnTouchListener(viewPagerOnTouch);
		mPosterPager.setOnPageChangeListener(new PosterPageChange());
		mPosterPager
				.setSlideBorderMode(AutoScrollViewPager.SLIDE_BORDER_MODE_CYCLE);
	}

	private void initIndicator() {
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		lp.setMargins(3, 0, 3, 0);

		points.clear();
		pointsLayout.removeAllViews();

		int count = adAdapter.dataList.size();

		for (int i = 0; i < count; i++) {
			// 添加标记点
			ImageView point = new ImageView(getActivity());

			boolean isCurrentIndex = i == (currentIndex % count);

			point.setBackgroundResource(isCurrentIndex ? R.drawable.icon_n
					: R.drawable.icon_o);

			point.setLayoutParams(lp);

			points.add(point);
			pointsLayout.addView(point);
		}
	}

	class PosterPagerAdapter extends PagerAdapter {
		private List<String> dataList;
		// 是否初始化
		private boolean isInit = false;

		public PosterPagerAdapter(List<String> list) {
			this.dataList = list;
		}

		/**
		 * 对轮播数据排序
		 * 
		 * @param list
		 */
		public void updateCarouselList(List<String> list) {
			if (list == null) {
				return;
			}
			dataList.clear();

			Iterator<String> iterator = list.iterator();
			String urlInfo = null;
			while (iterator.hasNext()) {
				urlInfo = iterator.next();

				dataList.add(urlInfo);

				urlInfo = null;
			}
			// 更新数据后指示器重置.
			initIndicator();

			// 数据少的时候不滑动.
			tryAutoScroll();

			// 设置current position.
			if (dataList.size() > 1 && !isInit) {
				isInit = true;
				mPosterPager.setCurrentItem(dataList.size() * 5000, false);
			}
		}

		@Override
		public int getCount() {
			int size = dataList.size();
			if (size < 2) {
				return size;
			} else {
				return dataList.size() * 50000;
			}
		}

		// 加载之前的默认图片显示
		DisplayImageOptions simpleOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.picture_bg_loding_3)
				.showImageForEmptyUri(R.drawable.picture_bg_loding_3)
				.showImageOnFail(R.drawable.picture_bg_loding_3)
				.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();

		@SuppressLint("NewApi")
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView imageView = new ImageView(getActivity());
			imageView.setAdjustViewBounds(true);
			imageView.setScaleType(ScaleType.CENTER_CROP);

			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
			imageView.setLayoutParams(params);
			imageView.setOnTouchListener(viewPagerOnTouch);

			String info = dataList.get(position % dataList.size());
			// if (info.equals("marketing_curosue_default"))
			// {
			// imageView.setImageResource(R.drawable.marketing_curosue_default);
			// ((ViewPager) container).addView(imageView);
			// }
			// else
			// {
			ImageLoader.getInstance().displayImage(info, imageView,
					simpleOptions);
			((ViewPager) container).addView(imageView);
			//
			imageView.setOnClickListener(new PosterClickListener(1, position
					% dataList.size()));

			return imageView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((ImageView) object);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
	}

	class PosterPageChange implements OnPageChangeListener {
		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int position) {
			if (adAdapter.getCount() > 0) {
				currentIndex = position;
				updateIndicator(position);
			}
		}
	}

	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
        case 1:
        	//result为读身份证成功
            if (resultCode==1||resultCode==2) {
            	setViewAndButton(btn_login, btn_logout, tv_title, mContext);
            }
            //在登录界面使用系统的返回键手动返回（一般是登录失败）
            else if(resultCode==3){
            	//到时候再判断一下是否有data，如果有的话就是柜员登录的返回
            	btn_login.setVisibility(View.VISIBLE);
            	btn_search_jf.setVisibility(View.GONE);
			}
            else if(resultCode==4){
            	btn_login.setVisibility(View.GONE);
    			btn_search_jf.setVisibility(View.VISIBLE);
            }
            break;
        default:
            break;
        }
	}

	private void updateIndicator(int position) {
		for (ImageView view : points) {
			view.setBackgroundResource(R.drawable.icon_o);
		}

		int index = position % adAdapter.dataList.size();
		points.get(index).setBackgroundResource(R.drawable.icon_n);
	}

	class PosterClickListener implements OnClickListener {
		/** 轮播 */
		private static final int CAROUSEL = 1;

		private int position;

		private int type;

		// 防止多次点击
		private boolean isRunning = false;

		public PosterClickListener(int type, int position) {
			this.type = type;
			this.position = position;
		}

		// 图片点击事件，可以设置跳转到某一广告页面
		@Override
		public void onClick(View v) {
			if (isRunning) {
				return;
			}
			isRunning = true;
			if (type == CAROUSEL) {
				mPosterPager.stopAutoScroll();
				Toast.makeText(getActivity(), "--" + position, 0).show();
			}

			mPosterPager.postDelayed(new Runnable() {
				@Override
				public void run() {
					isRunning = false;
				}
			}, 1000);
		}
	}

	/**
	 * 初始化ImageLoader
	 */
	public static void initImageLoader(Context context) {
		File cacheDir = StorageUtils.getOwnCacheDirectory(context, "ab/cache");// 获取到缓存的目录地址
		Log.d("cacheDir", cacheDir.getPath() + "------->" + cacheDir);
		// 创建配置ImageLoader(所有的选项都是可选的,只使用那些你真的想定制)，这个可以设定在APPLACATION里面，设置为全局的配置参数
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context)
				// max width, max height，即保存的每个缓存文件的最大长宽
				.memoryCacheExtraOptions(480, 800)
				// Can slow ImageLoader, use it carefully (Better don't use
				// it)设置缓存的详细信息，最好不要设置这个
				.discCacheExtraOptions(480, 800, null)
				// 线程池内加载的数量
				.threadPoolSize(3)
				// 线程优先级
				.threadPriority(Thread.NORM_PRIORITY - 2)
				/*
				 * When you display an image in a small ImageView and later you
				 * try to display this image (from identical URI) in a larger
				 * ImageView so decoded image of bigger size will be cached in
				 * memory as a previous decoded image of smaller size. So the
				 * default behavior is to allow to cache multiple sizes of one
				 * image in memory. You can deny it by calling this method: so
				 * when some image will be cached in memory then previous cached
				 * size of this image (if it exists) will be removed from memory
				 * cache before.
				 */
				.denyCacheImageMultipleSizesInMemory()

				// You can pass your own memory cache
				// implementation你可以通过自己的内存缓存实现
				// .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 *
				// 1024))
				// .memoryCacheSize(2 * 1024 * 1024)
				// 硬盘缓存50MB
				.diskCacheSize(50 * 1024 * 1024)
				// 将保存的时候的URI名称用MD5
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				// 加密
				.diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
				// 将保存的时候的URI名称用HASHCODE加密
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.diskCacheFileCount(100) // 缓存的File数量
				.diskCache(new UnlimitedDiscCache(cacheDir))// 自定义缓存路径
				// .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				// .imageDownloader(new BaseImageDownloader(context, 5 * 1000,
				// 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);// 全局初始化此配置
	}

	// @Override
	// public void onResume() {
	// super.onResume();
	// if (gvAdapter == null) {
	// gvAdapter = new GridViewAdapter((LinkedList<Product>) plist,
	// getActivity());
	// } else {
	// gvAdapter.notifyDataSetChanged();
	// }
	// gv.setAdapter(gvAdapter);
	//
	// }

	static class ViewHolder {
		ImageView img_icon;
		Button btn_add;
		TextView tv_pname;
		TextView tv_brand;
		TextView tv_producer;
		TextView tv_count;
		TextView tv_jfprice;
	}

	private void refresh() {  
        /*finish(); 
        Intent intent = new Intent(RefreshActivityTest.this, RefreshActivityTest.class); 
        startActivity(intent);*/  
        onCreate(null);  
        }  
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.search_jf:
			Intent intent = new Intent(getActivity(), SearchInfoActivity.class);
//			getActivity().startActivity(intent);
			getActivity().startActivityForResult(intent, SEARCH_JF_CODE);
			break;
		case R.id.log_out:
			sp1=new ShareHelper(getActivity().getApplicationContext());
			SharedPreferences sPreferences=getActivity().getSharedPreferences("mysp_setting", Context.MODE_PRIVATE);
			IP=sPreferences.getString("ip", "");
			port=sPreferences.getString("port", "");
			branchIdString=sPreferences.getString("branchid", "");
			Toast.makeText(getActivity(), "点击了退出", Toast.LENGTH_SHORT)
					.show();
			String token = ApplicationEx.getApp().getUserInfo().getToken();
			String clientNo = ApplicationEx.getApp().getUserInfo()
					.getClientNo();
			JsonObjectRequest jRequest=new JsonObjectRequest("http://"+IP+":"+port+"/DJF-Pad/pad/logout.action?"+"clientNo="+clientNo+"&token="+token, null, 
					new Listener<JSONObject>(
							) {

								@Override
								public void onResponse(JSONObject response) {
									int result = 0;
									Log.d("TAG", response.toString());
									/*
									 * 登出
									 * */
									try {
										result = response.getInt("result");
					
									} catch (JSONException e) {
										e.printStackTrace();
									}
					
										if (result==200) {
											UserInfo userInfo = new UserInfo();
											sp1.save(0);
											try {
												userInfo.setEmpId(0);
												userInfo.setMsg(response.getString("msg"));
												userInfo.setResult(response.getInt("result"));
												userInfo.setClientNo(response.getString(""));
											} catch (JSONException e) {
												e.printStackTrace();
											}
											ApplicationEx.getApp().setUserInfo(userInfo);
											ApplicationEx.getApp().setUSER_STATUS(0);
											ShareHelper shareHelper=new ShareHelper(getActivity());
											shareHelper.save("",0);
											Log.i("MY USERINFO","用户内码："+ApplicationEx.getApp().getUserInfo().getClientNo());
											try {
												Toast.makeText(getActivity(), response.getString("msg"),
														Toast.LENGTH_LONG).show();
											} catch (JSONException e) {
												e.printStackTrace();
											}
											tv_title.setVisibility(View.GONE);
											btn_login.setVisibility(View.VISIBLE);
											btn_logout.setVisibility(View.GONE);
//											Message message = handler.obtainMessage(2);     // Message   
//									        handler.sendMessageDelayed(message, 1000); 
										} else if(result==300){
											try {
												Toast.makeText(getActivity(), response.getString("msg"),
														Toast.LENGTH_LONG).show();
											} catch (JSONException e) {
												e.printStackTrace();
											}
											return;
										}
								}
					}, new ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							Log.e("TAG", error.getMessage(), error);
						}
					});
			VolleySingleton.getVolleySingleton(getActivity())
			.addToRequestQueue(jRequest);
			break;
		default:
			break;
		}
	}

	
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		user_status=((ApplicationEx)getActivity().getApplication()).getUSER_STATUS();
		Log.e("USER STATUS2",user_status+"lalal");
	}
	
	
	public Runnable task = new Runnable() 
	{
		@Override
		public void run() 
		{
			VersionAPI asyncVersion = new VersionAPI(application.getChatService());
			final int version = asyncVersion.getVersion();
			getActivity().runOnUiThread(new Runnable() 
			{
				@Override
				public void run() 
				{
					ToastUtil.showToast(getActivity(), "version=" + version);
				}
			});
		}
	};
	
}
