package com.jzfy.jfchangesystem.activity;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jzfy.jfchangesystem.R;
import com.jzfy.jfchangesystem.asynctask.AsynMagStripeCard;
import com.jzfy.jfchangesystem.asynctask.AsyncParseSFZ;
import com.jzfy.jfchangesystem.asynctask.AsynMagStripeCard.OnReadCardListener;
import com.jzfy.jfchangesystem.asynctask.AsyncParseSFZ.OnReadSFZListener;
import com.jzfy.jfchangesystem.entity.UserInfo;
import com.jzfy.jfchangesystem.logic.BluetoothChatService;
import com.jzfy.jfchangesystem.logic.ParseSFZAPI;
import com.jzfy.jfchangesystem.logic.ParseSFZAPI.People;
import com.jzfy.jfchangesystem.setting.ApplicationEx;
import com.jzfy.jfchangesystem.util.DataUtils;
import com.jzfy.jfchangesystem.util.ShareHelper;
import com.jzfy.jfchangesystem.util.ToastUtil;
import com.jzfy.jfchangesystem.util.VolleySingleton;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint({ "InflateParams", "HandlerLeak" })
public class LoginActivity extends Activity implements OnClickListener {

	protected static final int ADMIN_LOGIN_CODE = 3;

	
	private static final int READ_SFZ_SUCCESSS=1;
	private static final int READ_CARD_SUCCESSS=2;
	private static final int RETRUN_MAIN=3;


	private static final int RESULT_RETURN = 1;


	private static final int RESULT_ADMIN_RETURN = 4;
	
	
	private boolean isCircleRead = false;

	ViewPager pager = null;
	PagerTabStrip tabStrip = null;
	ArrayList<View> viewContainter = new ArrayList<View>();
	ArrayList<String> titleContainer = new ArrayList<String>();
	private ProgressDialog progressDialog;
	private ApplicationEx application;
	private AsyncParseSFZ asyncParseSFZ;

	private int readTime = 0;
	private int readFailureTime = 0;
	private int readSuccessTime = 0;

	private ShareHelper sh;

	private String peopleid;

	private View view1, view2, view3;
	
	private BluetoothAdapter mBtAdapter;
	private BluetoothChatService chatService1;
	private BluetoothChatService chatService2;
	/*
	 * 
	 * 分割线----------------------------------------------
	 */
	private Handler handler1 = new Handler();
	private Handler handler2 = new Handler();
	private Button btn_return;
	
	
	private SharedPreferences sPreferences;

	private Button btn_admin_login_submit;
	private SharedPreferences sp;
	private SharedPreferences sp1;
	private LinearLayout layout;
	private LinearLayout admin_layout1;
	private LinearLayout admin_layout2;
	// 后期不需要点击textview即可刷卡，测试为了触发点击事件
	private TextView tv_login_card;
	private TextView tv_login_Idcard;

	private AsynMagStripeCard asynMagStripeCard;
	int market_overtime;
	int login_overtime;

	private int recLen;
	private RelativeLayout setting_layout;
	private String userName;
	private String password;
	private ShareHelper sHelper;
	private Editor editor;
	private String port = "8080";
	private String IP;
	private String branchIdString;

	private String TAG = "http";
	private EditText admin_username = null;
	private EditText admin_password = null;

//	final Handler handler1 = new Handler() {
//
//		public void handleMessage(Message msg) { // handle message
//			switch (msg.what) {
//			case 1:
//				recLen--;
//
//				if (recLen > 0) {
//					Log.e("RECLEN", recLen + "");
//					Message message = handler1.obtainMessage(1);
//					handler1.sendMessageDelayed(message, 1000); // send message
//
//				} else {
//					Toast.makeText(LoginActivity.this, "登录超时",
//							Toast.LENGTH_SHORT).show();
//					Intent intent = new Intent(LoginActivity.this,
//							MainActivity.class);
//					LoginActivity.this.startActivity(intent);
//				}
//			}
//
//			super.handleMessage(msg);
//		}
//	};


	private void initLayoutView() {
		pager = (ViewPager) this.findViewById(R.id.viewpager);
		tabStrip = (PagerTabStrip) this.findViewById(R.id.tabstrip);
		// 取消tab下面的长横线
		tabStrip.setDrawFullUnderline(false);
		// 设置tab的背景色
		tabStrip.setBackgroundColor(this.getResources().getColor(
				R.color.peachpuff));
		tabStrip.setTextSize(0, 30);
		// 设置当前tab页签的下划线颜色
		tabStrip.setTabIndicatorColor(this.getResources().getColor(R.color.red));
		tabStrip.setTextSpacing(200);
		View view1 = LayoutInflater.from(this)
				.inflate(R.layout.card_view, null);

		View view2 = LayoutInflater.from(this).inflate(R.layout.idcard_view,
				null);

		View view3 = LayoutInflater.from(this).inflate(
				R.layout.admin_login_view, null);
		btn_admin_login_submit = (Button) view3
				.findViewById(R.id.btn_admin_login_submit);
		btn_return=(Button) view3.findViewById(R.id.btn_admin_login_return);
		
		admin_layout1 = (LinearLayout) view3
				.findViewById(R.id.layout_admin_login1);
		admin_layout2 = (LinearLayout) view3
				.findViewById(R.id.layout_admin_login2);
		admin_username = (EditText) view3.findViewById(R.id.et_username);
		admin_password = (EditText) view3.findViewById(R.id.et_password);

		tv_login_card = (TextView) view1.findViewById(R.id.tv_login_helper1);
		tv_login_Idcard = (TextView) view2.findViewById(R.id.tv_login_helper2);
		// viewpager开始添加view
		viewContainter.add(view1);
		viewContainter.add(view2);
		viewContainter.add(view3);
		// 页签项
		titleContainer.add("刷卡");
		titleContainer.add("刷身份证");
		titleContainer.add("柜员登录");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		application = (ApplicationEx) this.getApplicationContext();
		chatService1 = new BluetoothChatService(application);
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBtAdapter == null) 
		{
			ToastUtil.showToast(this, R.string.no_bluetooth);
			finish();
			return;
		}

		if (!mBtAdapter.isEnabled()) 
		{
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, 0);
		}
		
		SharedPreferences spPreferences=LoginActivity.this.getSharedPreferences("mysp_card_address", Context.MODE_PRIVATE);
		if(application.isConnect()!=true&&!spPreferences.getString("card_address", "").equals("")){
			chatService1.connect(mBtAdapter.getRemoteDevice(spPreferences.getString("card_address", "")));
			application.setChatService(chatService1);
			}
		/*
		 * 获取登录倒计时时间
		 */
//		sp = LoginActivity.this.getSharedPreferences("mysp_setting",
//				Context.MODE_PRIVATE);
//		String login_overtime = sp.getString("login_overtime", "0");
//		recLen = Integer.parseInt(login_overtime);
//		Message message = handler1.obtainMessage(1); // Message
//		handler1.sendMessageDelayed(message, 1000);

		/*
		 * 登录页面Viewpager
		 */
		initLayoutView();

		pager.setAdapter(new PagerAdapter() {

			// viewpager中的组件数量
			@Override
			public int getCount() {
				return viewContainter.size();
			}

			// 滑动切换的时候销毁当前的组件
			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object) {
				((ViewPager) container).removeView(viewContainter.get(position));
			}

			// 每次滑动的时候生成的组件
			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				((ViewPager) container).addView(viewContainter.get(position));
				return viewContainter.get(position);
			}

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getItemPosition(Object object) {
				return super.getItemPosition(object);
			}

			@Override
			public CharSequence getPageTitle(int position) {
				return titleContainer.get(position);
			}
		});

		pager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageScrollStateChanged(int arg0) {
				Log.d(TAG, "--------changed:" + arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				Log.d(TAG, "-------scrolled arg0:" + arg0);
				Log.d(TAG, "-------scrolled arg1:" + arg1);
				Log.d(TAG, "-------scrolled arg2:" + arg2);
			}

			@Override
			public void onPageSelected(int arg0) {
				Log.d(TAG, "------selected:" + arg0);
			}
		});

		/*
		 * 柜员登录
		 */
		sp = LoginActivity.this.getSharedPreferences("mysp",
				Context.MODE_PRIVATE);
		// 初始化页面
		initData();
		// 柜员登录点击事件
		btn_admin_login_submit.setOnClickListener(this);
		// 刷身份证点击事件
		tv_login_Idcard.setOnClickListener(this);
		// 刷银行卡点击事件
		tv_login_card.setOnClickListener(this);
		//柜员返回主菜单
		btn_return.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	/* 点击返回 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// sh=new ShareHelper(LoginActivity.this);
			// sh.save("", 0);
			Intent intent = new Intent(LoginActivity.this, MainActivity.class);
			setResult(RETRUN_MAIN);
//			setResult(RESULT_OK);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	
	
	private void initData() {
		Log.i("cy", "Enter function SFZActivity-initData()");

		application = (ApplicationEx) this.getApplicationContext();
		asyncParseSFZ = new AsyncParseSFZ(application.getHandlerThread()
				.getLooper(), application.getChatService());

		application = (ApplicationEx) this.getApplicationContext();
		asynMagStripeCard = new AsynMagStripeCard(application
				.getHandlerThread().getLooper(), application.getChatService());

		asynMagStripeCard.setOnReadCardListener(new OnReadCardListener() {
			@Override
			public void onReadSuccess(byte[] buffer) {
				// TODO Auto-generated method stub
				cancleProgressDialog();
				Toast.makeText(LoginActivity.this,
						"BUFFER=" + DataUtils.toHexString(buffer),
						Toast.LENGTH_SHORT).show();
				Log.e("BUFFER", "BUFFER=" + DataUtils.toHexString(buffer));
				String bufferString = DataUtils.toHexString(buffer);
				// buffer
				bufferString = bufferString.substring(0,
						bufferString.length() / 2);
				StringBuffer stringBuffer = new StringBuffer();
				for (int i = 0; i < bufferString.length(); i++) {
					if (i % 2 != 0) {
						stringBuffer.append(bufferString.charAt(i));
					}
				}
				bufferString = stringBuffer.toString().substring(1);
				Log.e("BUFFER STRING", bufferString);
				Log.e("PEOPLE ID3", ApplicationEx.getApp().getPEOPLEID()
						+ "lalalalal1");
				ApplicationEx.getApp().setBufferString(bufferString);
				JsonObjectRequest jRequest = new JsonObjectRequest(
						"http://115.236.69.226:8764/DJF-Pad/pad/login.action?cardNo="
								+ bufferString, null,
						new Listener<JSONObject>() {

							@Override
							public void onResponse(JSONObject response) {
								sHelper = new ShareHelper(
										getApplicationContext());
								int result = 0;
								Log.d("TAG", response.toString());
								/*
								 * 把用户信息保存到全局变量里去 传值到首页
								 */
								try {
									result = response.getInt("result");

								} catch (JSONException e) {
									e.printStackTrace();
								}

								if (result == 200) {
									UserInfo userInfo = new UserInfo();
									try {
										Toast.makeText(getApplicationContext(),
												response.getString("msg"),
												Toast.LENGTH_LONG).show();
									} catch (JSONException e1) {
										e1.printStackTrace();
									}
									try {
										sHelper.save(1);
										userInfo.setEmpId(0);
										userInfo.setMsg(response
												.getString("msg"));
										userInfo.setResult(response
												.getInt("result"));
										userInfo.setClientNo(response
												.getString("clientNo"));
										userInfo.setToken(response
												.getString("token"));
									} catch (JSONException e) {
										e.printStackTrace();
									}
									ApplicationEx.getApp().setUSER_STATUS(1);
									ApplicationEx.getApp()
											.setUserInfo(userInfo);
									Log.i("USER STATUS1", ApplicationEx
											.getApp().getUSER_STATUS() + "dsds");
									Log.i("MY USERINFO", "用户内码："
											+ ApplicationEx.getApp()
													.getUserInfo()
													.getClientNo());
									Intent intent = new Intent(
											LoginActivity.this,
											MainActivity.class);
									LoginActivity.this.setResult(READ_CARD_SUCCESSS);
									Log.e("INTENT", "BIAOJI");
									finish();
								} else if (result == 300) {
									try {
										Toast.makeText(getApplicationContext(),
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
				VolleySingleton.getVolleySingleton(LoginActivity.this)
						.addToRequestQueue(jRequest);
				readSuccessTime++;
				refresh2(isCircleRead);
				LoginActivity.this.handler2.removeCallbacks(task2);
			}

			@Override
			public void onReadFail(int confirmationCode) {
				cancleProgressDialog();
				readFailureTime++;
				ToastUtil.showToast(LoginActivity.this,
						"没有发现银行卡!!!");
				refresh2(isCircleRead);
			}
		});
		asyncParseSFZ.setOnReadSFZListener(new OnReadSFZListener() {
			@Override
			public void onReadSuccess(People people) {
				cancleProgressDialog();
				updateInfo(people);
				Toast.makeText(LoginActivity.this,
						"people id " + people.getPeopleIDCode(),
						Toast.LENGTH_SHORT).show();
				Log.e("PEOPLE ID3", ApplicationEx.getApp().getPEOPLEID()
						+ "lalalalal1");
				ApplicationEx.getApp().setPEOPLEID(people.getPeopleIDCode());
				readSuccessTime++;
				/*
				 * 信息可以打印，可以传递登录信息。
				 */
				JsonObjectRequest jRequest = new JsonObjectRequest(
						"http://115.236.69.226:8764/DJF-Pad/pad/login.action?clientId="
								+ people.getPeopleIDCode(), null,
						new Listener<JSONObject>() {

							@Override
							public void onResponse(JSONObject response) {
								sHelper = new ShareHelper(
										getApplicationContext());
								int result = 0;
								Log.d("TAG", response.toString());
								/*
								 * 把用户信息保存到全局变量里去 传值到首页
								 */
								try {
									result = response.getInt("result");

								} catch (JSONException e) {
									e.printStackTrace();
								}

								if (result == 200) {
									UserInfo userInfo = new UserInfo();
									try {
										Toast.makeText(getApplicationContext(),
												response.getString("msg"),
												Toast.LENGTH_LONG).show();
									} catch (JSONException e1) {
										e1.printStackTrace();
									}
									try {
										sHelper.save(1);
										userInfo.setEmpId(0);
										userInfo.setMsg(response
												.getString("msg"));
										userInfo.setResult(response
												.getInt("result"));
										userInfo.setClientNo(response
												.getString("clientNo"));
										userInfo.setToken(response
												.getString("token"));
									} catch (JSONException e) {
										e.printStackTrace();
									}
									ApplicationEx.getApp().setUSER_STATUS(1);
									ApplicationEx.getApp()
											.setUserInfo(userInfo);
									Log.i("USER STATUS1", ApplicationEx
											.getApp().getUSER_STATUS() + "dsds");
									Log.i("MY USERINFO", "用户内码："
											+ ApplicationEx.getApp()
													.getUserInfo()
													.getClientNo());
									Intent intent = new Intent(
											LoginActivity.this,
											MainActivity.class);
									LoginActivity.this.setResult(READ_SFZ_SUCCESSS);
									finish();
								} else if (result == 300) {
									try {
										Toast.makeText(getApplicationContext(),
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
				VolleySingleton.getVolleySingleton(LoginActivity.this)
						.addToRequestQueue(jRequest);
				refresh1(isCircleRead);
				LoginActivity.this.handler1.removeCallbacks(task1);

				Log.e("PEOPLE ID2", ApplicationEx.getApp().getPEOPLEID()
						+ "lalala");
				
			}

			@Override
			public void onReadFail(int confirmationCode) {
				cancleProgressDialog();
				readFailureTime++;
				ToastUtil.showToast(LoginActivity.this,
							"没有发现身份证!!!");
				refresh1(isCircleRead);
			}
		});
	}

	private Runnable task1 = new Runnable() {
		@Override
		public void run() {
			Log.i("cy", "Enter function SFZActivity-Runnable-run()");

			if (!application.isConnect()) {
				Log.i("cy", "Bluetooth is not connected!!!");
				ToastUtil.showToast(LoginActivity.this,
						"Bluetooth is not connected!!!");
				handler1.removeCallbacks(this);
				return;
			}

			Log.i("cy", "Bluetooth  is connected��");
			readTime++;
			showProgressDialog("Now it is reading......");
			asyncParseSFZ.readSFZ();
		}
	};

	private Runnable task2 = new Runnable() {
		@Override
		public void run() {
			Log.i("cy", "Enter function MagStripeCardActivity-Runnable-run()");

			if (!application.isConnect()) {
				Log.i("cy", "Bluetooth is not connected!!!");
				ToastUtil.showToast(LoginActivity.this,
						"Bluetooth is not connected!!!");
				handler2.removeCallbacks(this);
				return;
			}

			Log.i("cy", "Bluetooth  is connected");
			readTime++;
			showProgressDialog("Now it is reading......");
			SystemClock.sleep(1000);
			asynMagStripeCard.readCard();
		}
	};

	private void refresh1(boolean isSequentialRead) {
		Log.i("cy", "Enter function SFZActivity-refresh()");

		if (!isSequentialRead) {
			return;
		}
		handler1.postDelayed(task1, 1000);
		String result = "Total: " + readTime + ", Success: " + readSuccessTime
				+ ", Failure: " + readFailureTime;
		Log.i("cy", "result=" + result);
	}

	private void refresh2(boolean isSequentialRead) {
		Log.i("cy", "Enter function CardActivity-refresh()");

		if (!isSequentialRead) {
			return;
		}
		handler2.postDelayed(task2, 1000);
		String result = "Total: " + readTime + ", Success: " + readSuccessTime
				+ ", Failure: " + readFailureTime;
		Log.i("cy", "result=" + result);
	}

	@Override
	protected void onDestroy() {
		Log.i("cy", "Enter function SFZActivity-onDestroy()");
		isCircleRead = false;
		handler1.removeCallbacks(task1);
		handler2.removeCallbacks(task2);
		super.onDestroy();
	}

	@SuppressWarnings("deprecation")
	private void updateInfo(People people) {
		Log.i("cy", "Enter function SFZActivity-updateInfo()");
		Toast.makeText(LoginActivity.this, "身份证id" + people.getPeopleIDCode(),
				Toast.LENGTH_SHORT).show();
		peopleid = people.getPeopleIDCode();
		ApplicationEx.getApp().setPEOPLEID(peopleid);
	}

	private void showProgressDialog(String message) {
		Log.i("cy", "Enter function SFZActivity-showProgressDialog()");

		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(message);
		if (!progressDialog.isShowing()) {
			progressDialog.show();
		}

	}

	private void cancleProgressDialog() {
		Log.i("cy", "Enter function SFZActivity-cancleProgressDialog()");
		if (progressDialog != null ) {
			progressDialog.cancel();
			progressDialog = null;
		}

	}

	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		 switch (requestCode) {
		case ADMIN_LOGIN_CODE:
			if(resultCode==Activity.RESULT_OK){
				admin_username.setText("");
				admin_password.setText("");
				Log.e("ADMIN LOGOUT","Logout success!");
			}
			else if(resultCode==RESULT_RETURN){
				admin_username.setText("");
				admin_password.setText("");
				Log.e("ADMIN return","return success!");
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_admin_login_submit:
			Toast.makeText(LoginActivity.this, "点击了柜员登录提交", Toast.LENGTH_SHORT)
					.show();
			String employeeid = admin_username.getText().toString();
			String password = admin_password.getText().toString();
			JsonObjectRequest jr = new JsonObjectRequest(
					"http://115.236.69.226:8764/DJF-Pad/pad/employeeAuthori.action?"
							+ "employeeid=" + employeeid + "&password="
							+ password, null, new Listener<JSONObject>() {

						@Override
						public void onResponse(JSONObject response) {
							Log.e("SHOW RESPONSE5", response.toString());
							int result = 0;
							if (ApplicationEx.getApp().getUSER_STATUS() == 1) {
							}
							try {
								result = response.getInt("result");

							} catch (JSONException e) {
								e.printStackTrace();
							}
							if (result == 200) {
								try {
									Toast.makeText(LoginActivity.this,
											response.getString("msg"),
											Toast.LENGTH_SHORT).show();
								} catch (JSONException e) {
									e.printStackTrace();
								}
								// 设柜员登录为1
								ApplicationEx.getApp().setADMIN_STATUS(1);
								Intent intent = new Intent(LoginActivity.this,
										SearchInfoActivity.class);
				                //把返回数据存入Intent
				                intent.putExtra("result", "ADMIN LOGIN");
				                //设置返回数据
								startActivityForResult(intent, ADMIN_LOGIN_CODE);
				                //关闭Activity
//								LoginActivity.this.startActivity(intent);
//								finish();
							} else if (result == 300) {
								try {
									Toast.makeText(LoginActivity.this,
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
			VolleySingleton.getVolleySingleton(LoginActivity.this)
					.addToRequestQueue(jr);
			break;

		case R.id.tv_login_helper2:
			Toast.makeText(LoginActivity.this, "刷身份证", Toast.LENGTH_SHORT)
					.show();
//			单次刷身份证
			// isCircleRead = false;
			// showProgressDialog("Now it is reading......");
			// asyncParseSFZ.readSFZ();
			
//			循环刷身份证
			 isCircleRead = true;
			 readTime = 0;
			 readFailureTime = 0;
			 readSuccessTime = 0;
			 handler1.post(task1);
			 
			break;
		case R.id.tv_login_helper1:
			Toast.makeText(LoginActivity.this, "刷卡", Toast.LENGTH_SHORT).show();
//			单次刷卡
			// isCircleRead = false;
			// showProgressDialog("Now it is reading......");
			// asynMagStripeCard.readCard();
			isCircleRead = true;
			readTime = 0;
			readFailureTime = 0;
			readSuccessTime = 0;
			handler2.post(task2);
			break;
		case R.id.btn_admin_login_return:
			setResult(RESULT_ADMIN_RETURN);
			finish();
			break;
		default:
			break;
		}
	}
}
