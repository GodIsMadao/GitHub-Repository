package com.jzfy.jfchangesystem.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Set;
import java.util.Vector;

import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.gprinter.aidl.GpService;
import com.gprinter.command.EscCommand;
import com.gprinter.command.GpCom;
import com.gprinter.command.LabelCommand;
import com.gprinter.command.EscCommand.ENABLE;
import com.gprinter.command.EscCommand.FONT;
import com.gprinter.command.EscCommand.HRI_POSITION;
import com.gprinter.command.EscCommand.JUSTIFICATION;
import com.gprinter.io.GpDevice;
import com.gprinter.io.utils.GpUtils;
import com.gprinter.service.GpPrintService;
import com.jzfy.jfchangesystem.R;
import com.jzfy.jfchangesystem.R.id;
import com.jzfy.jfchangesystem.R.layout;
import com.jzfy.jfchangesystem.entity.ResponseType;
import com.jzfy.jfchangesystem.logic.BluetoothChatService;
import com.jzfy.jfchangesystem.setting.ApplicationEx;
import com.jzfy.jfchangesystem.util.ShareHelper;
import com.jzfy.jfchangesystem.util.ToastUtil;
import com.jzfy.jfchangesystem.util.Util;
import com.jzfy.jfchangesystem.util.VolleySingleton;
//import com.jzfy.jfchangesystem.entity.Product;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;

import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost.TabSpec;

public class MainActivity extends FragmentActivity implements OnClickListener {

	
	
	private BluetoothAdapter mBtAdapter;
	private BluetoothChatService chatService2;
	private ApplicationEx application;
	private int printerhandle=0;
	private GpService mGpService = null;
	
	
	public static final String CONNECT_STATUS = "connect.status";
	private static final String DEBUG_TAG = "MainActivity";
	private PrinterServiceConnection conn = null;
	private int mPrinterIndex = 0;
	private int mTotalCopies = 0;
	private static final int MAIN_QUERY_PRINTER_STATUS = 0xfe;
	
	private boolean isExit = false;
	private static String TAG = "Activity Log";
	private Fragment contentFragment;
	private int status;

	private Fragment market;
	private Fragment lucky;
	private Fragment setting;
	private Fragment cart;

	private ImageView img_market;
	private ImageView img_shoppingcart;
	private ImageView img_lucky;
	private ImageView img_setting;

	private TextView tv_market;
	private TextView tv_shoppingcart;
	private TextView tv_lucky;
	private TextView tv_setting;
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			isExit = false;
		}
	};
	/**
	 * 商城界面布局
	 */
	private View marketLayout;

	/**
	 * 购物车界面布局
	 */
	private View cartLayout;

	/**
	 * 抽奖界面布局
	 */
	private View luckyLayout;

	/**
	 * 设置界面布局
	 */
	private View settingLayout;

	private FragmentManager manager;
	private ResponseType reType;
	private String products;
	private ShareHelper sh;

	
	
	/* 实现连续点击两下退出界面 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			sh = new ShareHelper(MainActivity.this);
			sh.save("", 0);
			exit();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void exit() {
		// if (!isExit) {
		// isExit = true;
		// Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT)
		// .show();
		AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
				.setTitle("退出").setMessage("确认退出么？")
				.setPositiveButton("确认", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						finish();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						return;
					}
				}).create();
		dialog.show();
		// 利用handler延迟发送更改状态信息
		// mHandler.sendEmptyMessageDelayed(0, 2000);
		// } else {
		// finish();
		// }
	}

	class PrinterServiceConnection implements ServiceConnection {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.i("ServiceConnection", "onServiceDisconnected() called");
			try {
				mGpService.closePort(printerhandle);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			mGpService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mGpService = GpService.Stub.asInterface(service);
			SharedPreferences sp=MainActivity.this.getSharedPreferences("mysp_printer_address", Context.MODE_PRIVATE);
			try {
				if(sp!=null){
				printerhandle = mGpService.openPort(0, 4, sp.getString("printer_address", ""), 0);
				}
				
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.e(DEBUG_TAG, "onResume");
	}
	
	
	

	/*在fragment的管理类中，我们要实现这部操作，而他的主要作用是，当D这个activity回传数据到
	这里碎片管理器下面的fragnment中时，往往会经过这个管理器中的onActivityResult的方法。
	*/
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		  /*在这里，我们通过碎片管理器中的Tag，就是每个碎片的名称，来获取对应的fragment*/
        /*然后在碎片中调用重写的onActivityResult方法*/
        market.onActivityResult(requestCode, resultCode, data);
	}




	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			// GpCom.ACTION_DEVICE_REAL_STATUS 为广播的IntentFilter
			if (action.equals(GpCom.ACTION_DEVICE_REAL_STATUS)) {

				// 业务逻辑的请求码，对应哪里查询做什么操作
				int requestCode = intent.getIntExtra(
						GpCom.EXTRA_PRINTER_REQUEST_CODE, -1);
				// 判断请求码，是则进行业务操作
				if (requestCode == MAIN_QUERY_PRINTER_STATUS) {

					int status = intent.getIntExtra(
							GpCom.EXTRA_PRINTER_REAL_STATUS, 16);
					String str;
					if (status == GpCom.STATE_NO_ERR) {
						str = "打印机正常";
					} else {
						str = "打印机 ";
						if ((byte) (status & GpCom.STATE_OFFLINE) > 0) {
							str += "脱机";
						}
						if ((byte) (status & GpCom.STATE_PAPER_ERR) > 0) {
							str += "缺纸";
						}
						if ((byte) (status & GpCom.STATE_COVER_OPEN) > 0) {
							str += "打印机开盖";
						}
						if ((byte) (status & GpCom.STATE_ERR_OCCURS) > 0) {
							str += "打印机出错";
						}
						if ((byte) (status & GpCom.STATE_TIMES_OUT) > 0) {
							str += "查询超时";
						}
					}
					Toast.makeText(getApplicationContext(),
							"打印机：" + mPrinterIndex + " 状态：" + str,
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	};


	private void connection() {
		conn = new PrinterServiceConnection();
		Intent intent = new Intent(this, GpPrintService.class);
		bindService(intent, conn, Context.BIND_AUTO_CREATE); // bindService
	}

	public boolean[] getConnectState() {
		boolean[] state = new boolean[GpPrintService.MAX_PRINTER_CNT];
		for (int i = 0; i < GpPrintService.MAX_PRINTER_CNT; i++) {
			state[i] = false;
		}
		for (int i = 0; i < GpPrintService.MAX_PRINTER_CNT; i++) {
			try {
				if (mGpService.getPrinterConnectStatus(i) == GpDevice.STATE_CONNECTED) {
					state[i] = true;
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return state;
	}

	public void openPortDialogueClicked(View view) {
		if (mGpService == null) {
			Toast.makeText(this, "Print Service is not start, please check it",
					Toast.LENGTH_SHORT).show();
			return;
		}
		Log.d(DEBUG_TAG, "openPortConfigurationDialog ");
		Intent intent = new Intent(this, PrinterConnectDialog.class);
		boolean[] state = getConnectState();
		intent.putExtra(CONNECT_STATUS, state);
		this.startActivity(intent);
	}

	void sendReceipt() {
		EscCommand esc = new EscCommand();
		esc.addPrintAndFeedLines((byte) 3);
		esc.addSelectJustification(JUSTIFICATION.CENTER);// 设置打印居中
		esc.addSelectPrintModes(FONT.FONTA, ENABLE.OFF, ENABLE.ON, ENABLE.ON,
				ENABLE.OFF);// 设置为倍高倍宽
		esc.addText("Sample\n"); // 打印文字
		esc.addPrintAndLineFeed();

		/* 打印文字 */
		esc.addSelectPrintModes(FONT.FONTA, ENABLE.OFF, ENABLE.OFF, ENABLE.OFF,
				ENABLE.OFF);// 取消倍高倍宽
		esc.addSelectJustification(JUSTIFICATION.LEFT);// 设置打印左对齐
		esc.addText("Print text\n"); // 打印文字
		esc.addText("Welcome to use Gprinter!\n"); // 打印文字

		/* 打印繁体中文 需要打印机支持繁体字库 */
		String message = "佳博票據打印機\n";
		// esc.addText(message,"BIG5");
		esc.addText(message, "GB2312");
		esc.addPrintAndLineFeed();

		/* 绝对位置 具体详细信息请查看GP58编程手册 */
		esc.addText("你好啊");
		esc.addSetHorAndVerMotionUnits((byte) 7, (byte) 0);
		esc.addSetAbsolutePrintPosition((short) 6);
		esc.addText("你好啊");
		esc.addSetAbsolutePrintPosition((short) 10);
		esc.addText("你好啊");
		esc.addPrintAndLineFeed();

		/* 打印图片 */
		// esc.addText("Print bitmap!\n"); // 打印文字
		// Bitmap b = BitmapFactory.decodeResource(getResources(),
		// R.drawable.gprinter);
		// esc.addRastBitImage(b, b.getWidth(), 0); // 打印图片

		/* 打印一维条码 */
		// esc.addText("Print code128\n"); // 打印文字
		// esc.addSelectPrintingPositionForHRICharacters(HRI_POSITION.BELOW);//
		// 设置条码可识别字符位置在条码下方
		// esc.addSetBarcodeHeight((byte) 60); // 设置条码高度为60点
		// esc.addSetBarcodeWidth((byte) 1); // 设置条码单元宽度为1
		// esc.addCODE128(esc.genCodeB("Gprinter")); // 打印Code128码
		// esc.addPrintAndLineFeed();

		/*
		 * QRCode命令打印 此命令只在支持QRCode命令打印的机型才能使用。 在不支持二维码指令打印的机型上，则需要发送二维条码图片
		 */
		// esc.addText("Print QRcode\n"); // 打印文字
		// esc.addSelectErrorCorrectionLevelForQRCode((byte)0x31); //设置纠错等级
		// esc.addSelectSizeOfModuleForQRCode((byte)3);//设置qrcode模块大小
		// esc.addStoreQRCodeData("www.gprinter.com.cn");//设置qrcode内容
		// esc.addPrintQRCode();//打印QRCode
		// esc.addPrintAndLineFeed();

		/* 打印文字 */
		esc.addSelectJustification(JUSTIFICATION.CENTER);// 设置打印左对齐
		esc.addText("Completed!\r\n"); // 打印结束
		esc.addGeneratePlus(LabelCommand.FOOT.F5, (byte) 255, (byte) 255);
		// esc.addGeneratePluseAtRealtime(LabelCommand.FOOT.F2, (byte) 8);

		esc.addPrintAndFeedLines((byte) 8);

		Vector<Byte> datas = esc.getCommand(); // 发送数据
		Byte[] Bytes = datas.toArray(new Byte[datas.size()]);
		Log.d("diaonilaomu", datas.toString());
		byte[] bytes = GpUtils.ByteTo_byte(Bytes);
		String sss = Base64.encodeToString(bytes, Base64.DEFAULT);
		int rs;
		try {
			rs = mGpService.sendEscCommand(mPrinterIndex, sss);
			GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rs];
			if (r != GpCom.ERROR_CODE.SUCCESS) {
				Toast.makeText(getApplicationContext(), GpCom.getErrorText(r),
						Toast.LENGTH_SHORT).show();
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void sendReceipt3() {
		EscCommand esc = new EscCommand();
		esc.addText("1234567890\n"); // 打印文字
		Vector<Byte> datas = esc.getCommand(); // 发送数据
		Byte[] Bytes = datas.toArray(new Byte[datas.size()]);
		byte[] bytes = GpUtils.ByteTo_byte(Bytes);
		String str = Base64.encodeToString(bytes, Base64.DEFAULT);
		int rel;
		try {
			rel = mGpService.sendEscCommand(mPrinterIndex, str);
			GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rel];
			if (r != GpCom.ERROR_CODE.SUCCESS) {
				Toast.makeText(getApplicationContext(), GpCom.getErrorText(r),
						Toast.LENGTH_SHORT).show();
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		Log.e(DEBUG_TAG, "onDestroy");
		super.onDestroy();
		if (conn != null) {
			unbindService(conn); // unBindService
		}
		unregisterReceiver(mBroadcastReceiver);
	}

	
	
	/**
	 * 用于对Fragment进行管理
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		// 初始化布局元素
		Log.e("USER STATUS mainactivity", ApplicationEx.getApp()
				.getUSER_STATUS() + "lalal");
		initViews();
		
		initEvents();
		// startService();
		connection();
		Log.e("CONNECTION",conn.toString());
		// 第一次启动时选中第0个tab
		registerReceiver(mBroadcastReceiver, new IntentFilter(
				GpCom.ACTION_DEVICE_REAL_STATUS));
		
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
		
		
		setTabSelection(0);
	}
	

	/**
	 * 在这里获取到每个需要用到的控件的实例，并给它们设置好必要的点击事件。
	 */
	private void initViews() {
		img_market = (ImageView) findViewById(R.id.market_image);
		img_lucky = (ImageView) findViewById(R.id.lucky_image);
		img_setting = (ImageView) findViewById(R.id.setting_image);
		img_shoppingcart = (ImageView) findViewById(R.id.cart_image);

		tv_market = (TextView) findViewById(R.id.market_text);
		tv_lucky = (TextView) findViewById(R.id.lucky_text);
		tv_setting = (TextView) findViewById(R.id.setting_text);
		tv_shoppingcart = (TextView) findViewById(R.id.cart_text);

		marketLayout = findViewById(R.id.market_layout);
		cartLayout = findViewById(R.id.cart_layout);
		luckyLayout = findViewById(R.id.lucky_layout);
		settingLayout = findViewById(R.id.setting_layout);
	}

	private void initEvents() {
		marketLayout.setOnClickListener(this);
		cartLayout.setOnClickListener(this);
		luckyLayout.setOnClickListener(this);
		settingLayout.setOnClickListener(this);
	}

	/**
	 * 将所有的Fragment都置为隐藏状态。
	 * 
	 * @param transaction
	 *            用于对Fragment执行操作的事务
	 */
	private void hideFragments(FragmentTransaction transaction) {
		if (market != null) {
			transaction.hide(market);
		}
		if (lucky != null) {
			transaction.hide(lucky);
		}
		if (cart != null) {
			transaction.hide(cart);
		}
		if (setting != null) {
			transaction.hide(setting);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.market_layout:
			setTabSelection(0);
			break;
		case R.id.cart_layout:
			setTabSelection(1);
			break;
		case R.id.lucky_layout:
			setTabSelection(2);
			break;
		case R.id.setting_layout:
			setTabSelection(3);
			break;
		default:
			break;
		}
	}

	/**
	 * 清除掉所有的选中状态。
	 */
	private void clearSelection() {
		img_market.setImageResource(R.drawable.market_default);
		tv_market.setTextColor(Color.parseColor("#82858b"));
		img_shoppingcart.setImageResource(R.drawable.cart_default);
		tv_shoppingcart.setTextColor(Color.parseColor("#82858b"));
		img_lucky.setImageResource(R.drawable.lucky_default);
		tv_lucky.setTextColor(Color.parseColor("#82858b"));
		img_setting.setImageResource(R.drawable.setting_default);
		tv_setting.setTextColor(Color.parseColor("#82858b"));
	}

	/**
	 * 根据传入的index参数来设置选中的tab页。
	 * 
	 * @param index
	 *            每个tab页对应的下标。0表示消息，1表示联系人，2表示动态，3表示设置。
	 */
	protected void setTabSelection(int index) {
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		Log.i(TAG, "=========setTabSelection=========");
		clearSelection();
		Log.i(TAG, "=========clearSelection=========");
		// // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
		hideFragments(transaction);
		Log.i(TAG, "=========clearSelection=========" + market);
		switch (index) {
		case 0:
			market = manager.findFragmentByTag("TabMarket");
			hideFragments(transaction);
			tv_market.setTextColor(Color.parseColor("#f4c600"));
			img_market.setImageResource(R.drawable.market_press);
			if (market == null) {
				market = new FragmentMarket();
				transaction.add(R.id.main_content, market, "TabMarket");
			} else {
				transaction.show(market);
			}
			Log.i(TAG, "=========TAB MARKET=========");
			break;
		case 1:
			cart = manager.findFragmentByTag("TabCart");
			hideFragments(transaction);
			tv_shoppingcart.setTextColor(Color.parseColor("#f4c600"));
			img_shoppingcart.setImageResource(R.drawable.cart_press);
			// if (cart == null) {
			cart = new FragmentCart();
			transaction.add(R.id.main_content, cart, "TabCart");
			// } else {
			transaction.show(cart);
			// }
			Log.i(TAG, "=========TAB CART=========");
			break;
		case 2:
			lucky = manager.findFragmentByTag("TabLucky");
			hideFragments(transaction);
			tv_lucky.setTextColor(Color.parseColor("#f4c600"));
			img_lucky.setImageResource(R.drawable.lucky_press);
			if (lucky == null) {
				lucky = new FragmentLucky();
				transaction.add(R.id.main_content, lucky, "TabLucky");
			} else {
				transaction.show(lucky);
			}
			Log.i(TAG, "=========TAB LUCKY=========");
			break;
		case 3:
			setting = manager.findFragmentByTag("TabSetting");
			hideFragments(transaction);
			tv_setting.setTextColor(Color.parseColor("#f4c600"));
			img_setting.setImageResource(R.drawable.setting_press);
			setting = new FragmentSetting();
			transaction.add(R.id.main_content, setting, "TabSetting");
			transaction.show(setting);
			Log.i(TAG, "=========TAB SETTING=========");
			break;
		default:
			break;
		}
		transaction.commit();
	}

}
