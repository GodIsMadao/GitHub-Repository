package com.jzfy.jfchangesystem.setting;

import java.util.List;

import com.google.code.microlog4android.Logger;
import com.google.code.microlog4android.LoggerFactory;
import com.google.code.microlog4android.appender.FileAppender;
import com.google.code.microlog4android.config.PropertyConfigurator;
import com.jzfy.jfchangesystem.R;
import com.jzfy.jfchangesystem.activity.ConnectActivity;
import com.jzfy.jfchangesystem.entity.Cart;
import com.jzfy.jfchangesystem.entity.Product;
import com.jzfy.jfchangesystem.entity.UserInfo;
import com.jzfy.jfchangesystem.logic.BluetoothChatService;
import com.jzfy.jfchangesystem.logic.BluetoothChatService.OnConnectListener;
import com.jzfy.jfchangesystem.setting.Constant.APP_INFO;
import com.jzfy.jfchangesystem.util.ToastUtil;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

public class ApplicationEx extends Application  implements BluetoothChatService.OnConnectListener{

	private static final int CONNECT_SUCCESS = 1;
	private static final int CONNECT_FAIL = 2;
	private static final int CONNECT_LOST = 3;
	
	
	private int CONNECT_STATE;
	private String PEOPLEID;
	private String bufferString;
	
	private BluetoothChatService chatService = null;

	private boolean isConnect;

	private HandlerThread handlerThread;

	private final Logger logger = LoggerFactory.getLogger();
	
	
	private static final String LOG_TAG = ApplicationEx.class.getSimpleName();
	private static ApplicationEx app = null;
	private int ADMIN_STATUS;// 当前状态是否是柜员在登陆
	private int USER_STATUS;// 当前状态是否是用户再登陆
	private AppConfig appConfig = null;
	// private boolean flag = false;
	private List<Cart> carts;
	private UserInfo userInfo;

	
	
	public int getCONNECT_STATE() {
		return CONNECT_STATE;
	}

	public void setCONNECT_STATE(int cONNECT_STATE) {
		CONNECT_STATE = cONNECT_STATE;
	}

	public static ApplicationEx getApp() {
		return app;
	}

	public int getADMIN_STATUS() {
		return ADMIN_STATUS;
	}

	public int getUSER_STATUS() {
		return USER_STATUS;
	}

	public void setUSER_STATUS(int uSER_STATUS) {
		USER_STATUS = uSER_STATUS;
	}

	
	public String getBufferString() {
		return bufferString;
	}

	public void setBufferString(String bufferString) {
		this.bufferString = bufferString;
	}

	public void setADMIN_STATUS(int aDMIN_STATUS) {
		ADMIN_STATUS = aDMIN_STATUS;
	}

	public static void setApp(ApplicationEx app) {
		ApplicationEx.app = app;
	}

	public List<Cart> getCarts() {
		return carts;
	}

	public void setCarts(List<Cart> carts) {
		this.carts = carts;
	}

	public AppConfig getAppConfig() {
		return appConfig;
	}

	public void setAppConfig(AppConfig appConfig) {
		this.appConfig = appConfig;
	}

	
	public String getPEOPLEID() {
		return PEOPLEID;
	}

	public UserInfo getUserInfo() {
		SharedPreferences sp = getSharedPreferences("mysp_userinfo",
				Context.MODE_PRIVATE);
		if (sp.contains("empId")) {
			userInfo = new UserInfo();
			userInfo.setEmpId(sp.getInt("id", 0));
			userInfo.setMsg(sp.getString("msg", ""));
			userInfo.setResult(sp.getInt("result", 0));
			userInfo.setClientNo(sp.getString("clientNo", ""));
			userInfo.setToken(sp.getString("token", ""));
			userInfo.setPoint(sp.getString("point", ""));
			userInfo.setUsername(sp.getString("username", ""));
			userInfo.setClientId(sp.getString("clientId", ""));
		}
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
		SharedPreferences sp = getSharedPreferences("object",
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putInt("id", userInfo.getEmpId());
		editor.putString("msg", userInfo.getMsg());
		editor.putString("token", userInfo.getToken());
		editor.putString("clientNo", userInfo.getClientNo());
		editor.putInt("result", userInfo.getResult());
		editor.putString("point", userInfo.getPoint());
		editor.putString("username", userInfo.getUsername());
		editor.putString("clientId", userInfo.getClientId());
		editor.commit();
	}

	

	public void setPEOPLEID(String pEOPLEID) {
		PEOPLEID = pEOPLEID;
	}

	@Override
	public void onCreate() {
		app = this;
		bufferString="";
		PEOPLEID="";
		PropertyConfigurator.getConfigurator(this).configure();
		final FileAppender  fa =  (FileAppender) logger.getAppender(1);  
		fa.setAppend(true); 
		
		logger.debug("**********Enter Myapplication********");
		handlerThread = new HandlerThread("handlerThread");
		handlerThread.start();
		
		
		appConfig = new AppConfig(getApplicationContext(),
				APP_INFO.SHAREDPREFERENCES_NAME);
		
		// 设status默认值为0,未登陆
		ADMIN_STATUS = 0;
		USER_STATUS = 0;
		SharedPreferences sp = getSharedPreferences("object",
				Context.MODE_PRIVATE);
		if (sp.contains("empId")) {
			userInfo = new UserInfo();
			userInfo.setEmpId(sp.getInt("id", 0));
			userInfo.setMsg(sp.getString("msg", ""));
			userInfo.setResult(sp.getInt("result", 0));
			userInfo.setClientNo(sp.getString("clientNo", ""));
			userInfo.setToken(sp.getString("token", ""));
			userInfo.setPoint(sp.getString("point", ""));
			userInfo.setUsername(sp.getString("username", ""));
			userInfo.setClientId(sp.getString("clientId", ""));
		}
		super.onCreate();
	}

	public HandlerThread getHandlerThread() 
	{
		return handlerThread;
	}

	public BluetoothChatService getChatService() 
	{
		return chatService;
	}

	public void setChatService(BluetoothChatService mChatService) 
	{
		this.chatService = mChatService;
	}
	
	public boolean isConnect() 
	{
		return isConnect;
	}

	public void setConnect(boolean isConnect) 
	{
		this.isConnect = isConnect;
		Log.i("cy", "isConnect=" + isConnect);
	}

	@Override
	public void onConnectSuccess() 
	{
		setConnect(true);
		Log.i("cy", "onConnectSuccess");
		mHandler.sendEmptyMessage(CONNECT_SUCCESS);
	}

	@Override
	public void onConnectFail() 
	{
		setConnect(false);
		Log.i("cy", "onConnectFail");
		mHandler.sendEmptyMessage(CONNECT_FAIL);
	}

	@Override
	public void onConnectLost() 
	{
		setConnect(false);
		Log.i("cy", "onConnectLost");
		mHandler.sendEmptyMessage(CONNECT_LOST);
	}
	
	private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) 
		{
			super.handleMessage(msg);
			Intent intent = new Intent(ConnectActivity.CONNECT_RESULT);
			
			switch (msg.what)
			{
			case CONNECT_SUCCESS:
				Log.i("cy", "CONNECT_SUCCESS");
				ToastUtil.showToast(ApplicationEx.this, R.string.connection_success);
				intent.putExtra("result", 1);
				sendBroadcast(intent);
				break;
				
			case CONNECT_FAIL:
				Log.i("cy", "CONNECT_FAIL");
				ToastUtil.showToast(ApplicationEx.this, R.string.connection_fail);
				intent.putExtra("result", 2);
				sendBroadcast(intent);
				break;
				
			case CONNECT_LOST:
				Log.i("cy", "CONNECT_Lost");
				ToastUtil.showToast(ApplicationEx.this, R.string.connection_lost);
				intent.putExtra("result", 3);
				sendBroadcast(intent);
				break;
				
			default:
				break;
			}
		}
		
	};
}
