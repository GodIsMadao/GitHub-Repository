package com.jzfy.jfchangesystem.activity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.gprinter.aidl.GpService;
import com.gprinter.command.EscCommand;
import com.gprinter.command.GpCom;
import com.gprinter.command.LabelCommand;
import com.gprinter.command.EscCommand.ENABLE;
import com.gprinter.command.EscCommand.FONT;
import com.gprinter.command.EscCommand.JUSTIFICATION;
import com.gprinter.io.GpDevice;
import com.gprinter.io.utils.GpUtils;
import com.gprinter.service.GpPrintService;
import com.jzfy.jfchangesystem.R;
import com.jzfy.jfchangesystem.R.layout;
import com.jzfy.jfchangesystem.activity.MainActivity.PrinterServiceConnection;
import com.jzfy.jfchangesystem.adapter.ListViewAdapter;
import com.jzfy.jfchangesystem.entity.Cart;
import com.jzfy.jfchangesystem.entity.Product;
import com.jzfy.jfchangesystem.entity.ResponseType;
import com.jzfy.jfchangesystem.entity.UserInfo;
import com.jzfy.jfchangesystem.setting.ApplicationEx;
//import com.jzfy.jfchangesystem.entity.Product;
import com.jzfy.jfchangesystem.util.ShareHelper;
import com.jzfy.jfchangesystem.util.VolleySingleton;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class FragmentCart extends Fragment implements OnItemClickListener,
		OnClickListener {
	/*
	 * 
	 * 打印相关
	 */

	private String branchName;
	private String clientId;
	private GpService mGpService = null;
	public static final String CONNECT_STATUS = "connect.status";
	private static final String DEBUG_TAG = "MainActivity";
	private PrinterServiceConnection conn = null;
	private int mPrinterIndex = 0;
	private int mTotalCopies = 0;
	private static final int MAIN_QUERY_PRINTER_STATUS = 0xfe;
	/*
	 * 打印相关
	 */

	private int branchid;
	private String token;
	private String clientNo;
	private String json;

	private ListView lv;
	private ArrayList<Cart> plist = null;
	private ListViewAdapter myAdapter = null;
	private SharedPreferences sp;
	private SharedPreferences sp1;
	private ShareHelper sHelper;
	private LinearLayout layout_empty_cart;
	private TextView tv_cart_amount_price;
	private Button btn_delete_all;
	private Button btn_last_count;
	private Button btn_cart_lastcount;
	private ShareHelper sh;
	private long count = 0;
	private int user_status;
	private TextView tv_cart_welcome;

	private String URL;
	private String IP;
	private String port;
	private String branchIdString;
	private String billNo;
	private String occurdate;
	private FragmentManager fm;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fm = getActivity().getFragmentManager();  
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		Toast.makeText(getActivity(), "======createCartView=====",
				Toast.LENGTH_SHORT).show();
		
		user_status = ((ApplicationEx) getActivity().getApplication())
				.getUSER_STATUS();
		Log.e("USER STATUS2", user_status + "lalal");
		Log.e("USER STATUS2 fragment cart", ApplicationEx.getApp()
				.getUSER_STATUS() + "lalal");
		View view = inflater.inflate(R.layout.cart, container, false);
		connection();
		btn_delete_all = (Button) view.findViewById(R.id.btn_delete_all);
		btn_cart_lastcount = (Button) view
				.findViewById(R.id.btn_cart_lastcount);
		btn_last_count = (Button) view.findViewById(R.id.btn_cart_lastcount);
		tv_cart_amount_price = (TextView) view
				.findViewById(R.id.tv_cart_amount_price);
		tv_cart_welcome = (TextView) view.findViewById(R.id.tv_cart_welcome);

		if(ApplicationEx.getApp().getUserInfo()==null){
				tv_cart_welcome.setText("欢迎");
		}
		else{
				tv_cart_welcome.setText("欢您，尊敬的  "
						+ ApplicationEx.getApp().getUserInfo().getUsername());
			}
		btn_cart_lastcount.setOnClickListener(this);
		btn_delete_all.setOnClickListener(this);
		sp = getActivity().getSharedPreferences("mysp_cartJson",
				Context.MODE_PRIVATE);
		layout_empty_cart = (LinearLayout) view
				.findViewById(R.id.layout_empty_cart);
		lv = (ListView) view.findViewById(R.id.listview_cart);
		/*
		 * 加入的应该是购物车对象
		 */
		Gson gson = new Gson();
		if (!sp.getString("cartJson", "").equals("")) {
			plist = gson.fromJson(sp.getString("cartJson", ""),
					new TypeToken<List<Cart>>() {
					}.getType());
			Log.i("mytag", sp.getString("cartJson", ""));
			for (int i = 0; i < plist.size(); i++) {
				Log.i("mytag", plist.get(i).getName());
				count = plist.get(i).getPrice() + count;
			}
			tv_cart_amount_price.setText(count + "积分");
			myAdapter = new ListViewAdapter(plist, getActivity());
			lv.setVisibility(View.VISIBLE);
			btn_delete_all.setVisibility(View.VISIBLE);
			layout_empty_cart.setVisibility(View.GONE);
			btn_cart_lastcount.setVisibility(View.VISIBLE);
			lv.setAdapter(myAdapter);
			myAdapter.setOnAddNum(this);
			myAdapter.setOnSubNum(this);
		} else {
			btn_delete_all.setVisibility(View.INVISIBLE);
			btn_cart_lastcount.setVisibility(View.INVISIBLE);
			lv.setVisibility(View.GONE);
			layout_empty_cart.setVisibility(View.VISIBLE);
		}
		btn_cart_lastcount.setOnClickListener(this);
		lv.setOnItemClickListener(this);
		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Toast.makeText(getActivity(), "你点击了第" + position + "项",
				Toast.LENGTH_SHORT).show();
	}

	void sendReceipt() {
		EscCommand esc = new EscCommand();
		esc.addPrintAndLineFeed();
		
		esc.addSelectJustification(JUSTIFICATION.CENTER);// 设置打印居中
		esc.addSelectPrintModes(FONT.FONTA, ENABLE.OFF, ENABLE.ON, ENABLE.ON,
				ENABLE.OFF);// 设置为倍高倍宽
		esc.addText("" + branchName + "\n"); // 打印文字
		esc.addPrintAndLineFeed();

		/* 打印文字 */
		esc.addSelectPrintModes(FONT.FONTA, ENABLE.OFF, ENABLE.OFF, ENABLE.OFF,
				ENABLE.OFF);// 取消倍高倍宽
		esc.addText("订单号:" + billNo + "\n");
		esc.addSelectJustification(JUSTIFICATION.LEFT);// 设置打印左对齐
		esc.addPrintAndLineFeed();
		esc.addText("礼品");
		esc.addSetHorAndVerMotionUnits((byte) 7, (byte) 0);
		esc.addSetAbsolutePrintPosition((short) 6);
		esc.addText("数量");
		esc.addSetAbsolutePrintPosition((short) 10);
		esc.addText("金额(积分)" + "\n");
		esc.addText("=============================" + "\n");
		for (int i = 0; i < plist.size(); i++) {
			if(plist.get(i).getSingle_count()!=0){
			count = plist.get(i).getPrice() + count;
			esc.addText("" + plist.get(i).getName() + "\n");
			esc.addSetHorAndVerMotionUnits((byte) 7, (byte) 0);
			esc.addSetAbsolutePrintPosition((short) 6);
			esc.addText("x" + plist.get(i).getSingle_count());
			esc.addSetAbsolutePrintPosition((short) 10);
			esc.addText("" + plist.get(i).getPrice());
			esc.addText("\n");
			esc.addPrintAndLineFeed();
			}
		}

		esc.addText("=============================" + "\n");
		esc.addText("用户姓名:"
				+ ApplicationEx.getApp().getUserInfo().getUsername() + "\n");
		if(ApplicationEx.getApp().getPEOPLEID()!=null){
		esc.addText("身份证:" + ApplicationEx.getApp().getPEOPLEID()
				+ "\n");
		}
		if(ApplicationEx.getApp().getBufferString()!=null){
			esc.addText("银行卡:" + ApplicationEx.getApp().getBufferString()
					+ "\n");
		}
		esc.addText("生成时间:" + occurdate + "\n");
		esc.addPrintAndLineFeed();
		esc.addSelectJustification(JUSTIFICATION.CENTER);
		esc.addText("请凭借此小票去柜台领取礼品,如有缺纸,请联系柜员加纸");
		esc.addGeneratePlus(LabelCommand.FOOT.F5, (byte) 255, (byte) 255);
		esc.addPrintAndFeedLines((byte) 5);

		Vector<Byte> datas = esc.getCommand(); // 发送数据
		Byte[] Bytes = datas.toArray(new Byte[datas.size()]);
		byte[] bytes = GpUtils.ByteTo_byte(Bytes);
		String sss = Base64.encodeToString(bytes, Base64.DEFAULT);
		int rs;
		try {
			rs = mGpService.sendEscCommand(mPrinterIndex, sss);
			GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rs];
			if (r != GpCom.ERROR_CODE.SUCCESS) {
				Toast.makeText(getActivity().getApplicationContext(),
						GpCom.getErrorText(r), Toast.LENGTH_SHORT).show();
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	class PrinterServiceConnection implements ServiceConnection {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.i("ServiceConnection", "onServiceDisconnected() called");
			mGpService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mGpService = GpService.Stub.asInterface(service);
		}
	}

	private void connection() {
		conn = new PrinterServiceConnection();
		Intent intent = new Intent(getActivity(), GpPrintService.class);
		getActivity().bindService(intent, conn, Context.BIND_AUTO_CREATE); // bindService
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
				e.printStackTrace();
			}
		}
		return state;
	}

	@Override
	public void onClick(View view) {
		Object tag = view.getTag();
		sh = new ShareHelper(getActivity());
		switch (view.getId()) {
		case R.id.item_btn_add: // 点击添加数量按钮，执行相应的处理
			// 获取 Adapter 中设置的 Tag
			long count1 = 0;
			if (tag != null && tag instanceof Integer) { // 解决问题：如何知道你点击的按钮是哪一个列表项中的，通过Tag的position
				int position = (Integer) tag;
				// 更改集合的数据
				int num = plist.get(position).getSingle_count();
				num++;
				plist.get(position).setSingle_count(num); // 修改集合中商品数量
				/*
				 * 此price为单行list总计单价
				 */
				plist.get(position).setPrice(
						plist.get(position).getSingle_price() * num); // 修改集合中该商品总价
				// 数量*单价
				// 解决问题：点击某个按钮的时候，如果列表项所需的数据改变了，如何更新UI
				myAdapter.notifyDataSetChanged();
				for (int i = 0; i < plist.size(); i++) {
					count1 += plist.get(i).getPrice();
				}
				tv_cart_amount_price.setText(count1 + "积分");
			}
			break;
		case R.id.item_btn_sub: // 点击减少数量按钮 ，执行相应的处理
			long count2 = 0;
			// 获取 Adapter 中设置的 Tag
			if (tag != null && tag instanceof Integer) {
				int position = (Integer) tag;
				// 更改集合的数据
				int num = plist.get(position).getSingle_count();
					//此时count为0
//					try {
//						Thread.sleep(500);
//						plist.remove(position);
//						myAdapter.notifyDataSetChanged();
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
				if (num > 0) {
					num--;
					plist.get(position).setSingle_count(num); // 修改集合中商品数量
					plist.get(position).setPrice(
							plist.get(position).getSingle_price() * num); // 修改集合中该商品总价
					// 数量*单价
					myAdapter.notifyDataSetChanged();
					// Log.i("My count tag", count+"");
				}
				for (int i = 0; i < plist.size(); i++) {
					count2 += plist.get(i).getPrice();
				}
				tv_cart_amount_price.setText(count2 + "积分");
			}
			break;
		case R.id.btn_delete_all:
			sh.clearData();
			sp = getActivity().getSharedPreferences("mysp_cartJson",
					Context.MODE_PRIVATE);
			Log.i("After clear", sp.getString("cartJson", "") + "lalalal");
			tv_cart_amount_price.setText(0 + "积分");
			lv.setVisibility(View.GONE);
			layout_empty_cart.setVisibility(View.VISIBLE);
			break;
		case R.id.btn_cart_lastcount:
			Toast.makeText(getActivity(), "点击了结算", Toast.LENGTH_SHORT).show();
			Log.e("USER STATUS", user_status + "lalal");

			SharedPreferences sPreferences = getActivity()
					.getSharedPreferences("mysp_setting", Context.MODE_PRIVATE);

			IP = sPreferences.getString("ip", "");
			port = sPreferences.getString("port", "");
			branchIdString = sPreferences.getString("branchid", "");
			sp1 = getActivity().getApplicationContext().getSharedPreferences(
					"mysp_userstatus", Context.MODE_PRIVATE);

			final ShareHelper shareHelper = new ShareHelper(getActivity());

			user_status = sp1.getInt("userstatus", 0);
//			user_status == 1 && 
			EscCommand esc = new EscCommand();
			esc.addText("\n"); // 测试是否连接的
			Vector<Byte> datas = esc.getCommand(); // 发送数据
			Byte[] Bytes = datas.toArray(new Byte[datas.size()]);
			byte[] bytes = GpUtils.ByteTo_byte(Bytes);
			String sss = Base64.encodeToString(bytes, Base64.DEFAULT);
			int rs;
			GpCom.ERROR_CODE r = null;
			try {
				rs = mGpService.sendEscCommand(mPrinterIndex, sss);
				r = GpCom.ERROR_CODE.values()[rs];
			} catch (RemoteException e2) {
				e2.printStackTrace();
			}
			if(r != GpCom.ERROR_CODE.SUCCESS){
				Dialog adialog=new AlertDialog.Builder(getActivity()).setTitle("打印机未连接").setMessage("打印机未连接"+GpCom.getErrorText(r)+",请联系柜员手动设置").create();
				adialog.show();
			}
			else if (plist == null||tv_cart_amount_price.getText().equals(0+"积分")) {
				Toast.makeText(getActivity(), "购物车为空，无法结算", Toast.LENGTH_SHORT)
						.show();
				Dialog alertDialog = new AlertDialog.Builder(getActivity()).setTitle("购物车为空").setMessage("购物车为空，无法结算，请检查购物车")
						.setPositiveButton("确定", null).create();
				alertDialog.show();
			}
			else {
				token = ApplicationEx.getApp().getUserInfo().getToken();
				clientNo = ApplicationEx.getApp().getUserInfo().getClientNo();
				// 机构编号需要放在放在全局变量里存起来
				// 订单json
				json = "{\"list\":[";
				for (int i = 0; i < plist.size(); i++) {
					json = json + "{\"id\":" + plist.get(i).getId()
							+ ",\"count\":" + plist.get(i).getSingle_count()
							+ "},";
				}
				json = json.substring(0, json.length() - 1) + "]}";
				// 拼接list json
				Log.i("JSON 字符串", json);
				Log.i("请求串",
						"http://115.236.69.226:8764/DJF-Pad/pad/jfPay.action?"
								+ "clientNo=" + clientNo + "&token=" + token
								+ "&branchId=" + branchIdString + "&json=" + json);
				Dialog alertDialog = new AlertDialog.Builder(getActivity())
						.setTitle("确认支付")
						.setMessage(
								"确认要支付"
										+ tv_cart_amount_price.getText()
												.toString() + "么")
						.setPositiveButton("确认支付",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										JsonObjectRequest jr1 = new JsonObjectRequest(
												"http://"
														+ IP
														+ ":"
														+ port
														+ "/DJF-Pad/pad/jfPay.action?"
														+ "clientNo="
														+ clientNo + "&token="
														+ token + "&branchId="
														+ branchIdString
														+ "&json=" + json,
												null,
												new Listener<JSONObject>() {

													@Override
													public void onResponse(
															JSONObject response) {
														Log.d("TAG",
																response.toString()
																		+ "lalalla");
														int result = 0;
														try {
															result = response
																	.getInt("result");

														} catch (JSONException e) {
															e.printStackTrace();
														}
														if (result == 200) {
															try {
																branchName = response
																		.getString("branchName");
																branchIdString = response
																		.getString("branchId");
																billNo = response
																		.getString("billno");
																occurdate = response
																		.getString("occurdate");

																Toast.makeText(
																		getActivity(),
																		response.getString("msg"),
																		Toast.LENGTH_SHORT)
																		.show();
																shareHelper
																		.save("",
																				0);
																Dialog dialog = new AlertDialog.Builder(
																		getActivity())
																		.setTitle(
																				"打印小票")
																		.setMessage(
																				"请点击下面的打印小票按钮来打印小票，凭借小票去前台换取礼品")
																		.setPositiveButton(
																				"打印小票",
																				new DialogInterface.OnClickListener() {

																					@Override
																					public void onClick(
																							DialogInterface dialog,
																							int which) {
																						try {
																							Log.i("MGP SERVICE",
																									mGpService
																											.getClientID()
																											+ "LALSDSLADLSA");
																							int type = mGpService
																									.getPrinterCommandType(mPrinterIndex);
																							if (type == GpCom.ESC_COMMAND) {
																								sendReceipt();
																								/*
																								 * 
																								 * 打印后退出登录
																								 */
																								SharedPreferences sPreferences = getActivity()
																										.getSharedPreferences(
																												"mysp_setting",
																												Context.MODE_PRIVATE);
																								IP = sPreferences
																										.getString(
																												"ip",
																												"");
																								port = sPreferences
																										.getString(
																												"port",
																												"");
																								branchIdString = sPreferences
																										.getString(
																												"branchid",
																												"");
																								sp1 = getActivity()
																										.getApplicationContext()
																										.getSharedPreferences(
																												"mysp_userstatus",
																												Context.MODE_PRIVATE);

																								sHelper = new ShareHelper(
																										getActivity()
																												.getApplicationContext());
																								Toast.makeText(
																										getActivity(),
																										"点击了退出",
																										Toast.LENGTH_SHORT)
																										.show();
																								String token = ApplicationEx
																										.getApp()
																										.getUserInfo()
																										.getToken();
																								String clientNo = ApplicationEx
																										.getApp()
																										.getUserInfo()
																										.getClientNo();
																								JsonObjectRequest jRequest = new JsonObjectRequest(
																										"http://"
																												+ IP
																												+ ":"
																												+ port
																												+ "/DJF-Pad/pad/logout.action?"
																												+ "clientNo="
																												+ clientNo
																												+ "&token="
																												+ token,
																										null,
																										new Listener<JSONObject>() {

																											@Override
																											public void onResponse(
																													JSONObject response) {
																												int result = 0;
																												Log.d("TAG",
																														response.toString());
																												/*
																												 * 登出
																												 */
																												try {
																													result = response
																															.getInt("result");

																												} catch (JSONException e) {
																													e.printStackTrace();
																												}

																												if (result == 200) {
																													UserInfo userInfo = new UserInfo();
																													sHelper.save(0);
																													try {
																														userInfo.setEmpId(0);
																														userInfo.setMsg(response
																																.getString("msg"));
																														userInfo.setResult(response
																																.getInt("result"));
																														userInfo.setClientNo(response
																																.getString(""));
																														userInfo.setUsername("");
																														userInfo.setClientId("");
																													} catch (JSONException e) {
																														e.printStackTrace();
																													}
																													ApplicationEx
																															.getApp()
																															.setUserInfo(
																																	userInfo);
																													ApplicationEx
																															.getApp()
																															.setUSER_STATUS(
																																	0);
																													ShareHelper shareHelper = new ShareHelper(
																															getActivity());
																													shareHelper
																															.save("",
																																	0);
																													Log.i("MY USERINFO",
																															"用户内码："
																																	+ ApplicationEx
																																			.getApp()
																																			.getUserInfo()
																																			.getClientNo());
																													try {
																														Toast.makeText(
																																getActivity(),
																																response.getString("msg"),
																																Toast.LENGTH_LONG)
																																.show();
																														if(ApplicationEx.getApp().getPEOPLEID()!=null){
																															ApplicationEx.getApp().setPEOPLEID("");
																														}
																														if(ApplicationEx.getApp().getBufferString()!=null){
																															ApplicationEx.getApp().setBufferString("");
																														}
																													} catch (JSONException e) {
																														e.printStackTrace();
																													}
																													Intent intent = new Intent(
																															getActivity(),
																															MainActivity.class);
																													startActivity(intent);
																												} else if (result == 300) {
																													try {
																														Toast.makeText(
																																getActivity(),
																																response.getString("msg"),
																																Toast.LENGTH_LONG)
																																.show();
																													} catch (JSONException e) {
																														e.printStackTrace();
																													}
																													return;
																												}
																											}
																										},
																										new ErrorListener() {

																											@Override
																											public void onErrorResponse(
																													VolleyError error) {
																												Log.e("TAG",
																														error.getMessage(),
																														error);
																											}
																										});
																								VolleySingleton
																										.getVolleySingleton(
																												getActivity())
																										.addToRequestQueue(
																												jRequest);
																							}
																						} catch (RemoteException e1) {
																							e1.printStackTrace();
																						}
																					}
																				})
																		.create();
																dialog.show();
															} catch (JSONException e) {
																e.printStackTrace();
															}
														} else if (result == 300) {
															try {
																Toast.makeText(
																		getActivity(),
																		response.getString("msg"),
																		Toast.LENGTH_SHORT)
																		.show();
															} catch (JSONException e) {
																e.printStackTrace();
															}
														}
													}
												}, new ErrorListener() {

													@Override
													public void onErrorResponse(
															VolleyError error) {
														Log.e("TAG", error
																.getMessage(),
																error);
													}
												});
										VolleySingleton.getVolleySingleton(
												getActivity())
												.addToRequestQueue(jr1);

									}

								})
						.setNegativeButton("返回",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										return;
									}
								}).create();
				alertDialog.show();
				break;
			}
		}
	}
}
