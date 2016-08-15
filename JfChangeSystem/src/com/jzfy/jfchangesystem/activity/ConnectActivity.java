package com.jzfy.jfchangesystem.activity;


import com.jzfy.jfchangesystem.R;
import com.jzfy.jfchangesystem.logic.BluetoothChatService;
import com.jzfy.jfchangesystem.setting.ApplicationEx;
import com.jzfy.jfchangesystem.util.ShareHelper;
import com.jzfy.jfchangesystem.util.ToastUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class ConnectActivity extends Activity 
{
	public static final String CONNECT_RESULT = "connect_result";
	private static final int REQUEST_CONNECT_DEVICE = 1;
	
	public static final int CONNECTY_SUCCESS = 100;
	public static final int CONNECTY_FAIL = 101;
	
	private Button btnConnect;
	
	private BluetoothAdapter bluetoothAdapter;
	private BluetoothChatService chatService = null;
	private ConnectBroadcast broadcast;
	
	private ApplicationEx application;
	private ProgressDialog progressDialog;
	
	private class ConnectBroadcast extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent) 
		{
			Log.i("cy", "Enter function ConnectActivity-ConnectBroadcast-onReceive()");
			
			String action = intent.getAction();
			Log.i("cy", "action=" + action);
			if(CONNECT_RESULT.equals(action))
			{
				int result = intent.getIntExtra("result", 0);
				if(result==1)
				{
					ConnectActivity.this.finish();
				}
				cancleProgressDialog();
			}
		}
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		Log.i("cy", "Enter function ConnectActivity-onCreate()");
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connect);
		
		btnConnect = (Button) findViewById(R.id.connectBluetooth);
		btnConnect.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				Log.i("cy", "Enter function ConnectActivity-onClick()");
				if (!bluetoothAdapter.isEnabled()) 
				{
					Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
					startActivityForResult(enableBtIntent, 0);
					return;
				}
				
				Intent serverIntent = new Intent(ConnectActivity.this, DeviceListActivity.class);
				startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
			}
		});
		
		initData();
		broadcast = new ConnectBroadcast();
		IntentFilter filter = new IntentFilter(CONNECT_RESULT);
		registerReceiver(broadcast, filter);
	}
	
	@Override
	protected void onDestroy() 
	{
		Log.i("cy", "Enter function ConnectActivity-onDestroy()");
		unregisterReceiver(broadcast);
		super.onDestroy();
	}

	public void initData() 
	{
		Log.i("cy", "Enter function ConnectActivity-initData()");
		
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter == null) 
		{
			ToastUtil.showToast(this, R.string.no_bluetooth);
			finish();
			return;
		}

		if (!bluetoothAdapter.isEnabled()) 
		{
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, 0);
		}
		
		application = (ApplicationEx) this.getApplicationContext();
		chatService = new BluetoothChatService(application);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		Log.i("cy", "Enter function ConnectActivity-onActivityResult()");
		
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) 
		{
		case REQUEST_CONNECT_DEVICE:
			if (resultCode == RESULT_OK) 
			{
				String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
				BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
				showProgressDialog(R.string.connecting_bluetooth);
				chatService.connect(device);
				ShareHelper shareHelper=new ShareHelper(ConnectActivity.this);
				Log.e("DEVICE ADDRESS", device.getAddress());
				shareHelper.save_cardaddress(device.getAddress());
				application.setChatService(chatService);
			} 
			else if (resultCode == RESULT_CANCELED) 
			{
				ToastUtil.showToast(this, R.string.disable_bluetooth);
			}
			
			break;
			
		default:
			break;
		}
	}

	@SuppressWarnings("unused")
	private void showProgressDialog(String message) 
	{
		Log.i("cy", "Enter function ConnectActivity-showProgressDialog(String)");
		
		if (progressDialog == null) 
		{
			progressDialog = new ProgressDialog(this);
		}
		
		progressDialog.setMessage(message);
		
		if (!progressDialog.isShowing()) 
		{
			progressDialog.show();
		}
	}

	private void showProgressDialog(int resId) 
	{
		Log.i("cy", "Enter function ConnectActivity-showProgressDialog(int)");
		
		if (progressDialog == null) 
		{
			progressDialog = new ProgressDialog(this);
		}
		
		progressDialog.setMessage(getResources().getString(resId));
		
		if (!progressDialog.isShowing()) 
		{
			progressDialog.show();
		}
	}

	private void cancleProgressDialog() 
	{
		Log.i("cy", "Enter function ConnectActivity-cancleProgressDialog()");
		
		if (progressDialog != null && progressDialog.isShowing()) 
		{
			progressDialog.cancel();
		}
	}
	
}
