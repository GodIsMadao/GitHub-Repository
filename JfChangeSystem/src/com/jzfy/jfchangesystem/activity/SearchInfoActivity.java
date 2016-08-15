package com.jzfy.jfchangesystem.activity;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jzfy.jfchangesystem.R;
import com.jzfy.jfchangesystem.entity.UserInfo;
import com.jzfy.jfchangesystem.setting.ApplicationEx;
import com.jzfy.jfchangesystem.util.ShareHelper;
import com.jzfy.jfchangesystem.util.VolleySingleton;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SearchInfoActivity extends Activity implements OnClickListener {

	private static final int RESULT_RETURN = 1;
	private Button btn_search_return;
	private Button btn_search_confirm;
	private SharedPreferences sp;
	
	private EditText et_card_search;
	private EditText et_IDcard_search;
	private TextView tv_search_count_time;
	private int market_overtime;
	private int login_overtime;
	
	private String token = "";
	private String clientNo = "";
	private Button btn_admin_logout;
	private TextView tv_card_jf; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_info);
		btn_search_confirm=(Button) findViewById(R.id.btn_search_confirm);
		btn_search_return=(Button) findViewById(R.id.btn_search_return);
//		tv_search_count_time=(TextView) findViewById(R.id.tv_search_count_time);
		tv_card_jf=(TextView) findViewById(R.id.tv_card_jf);
		et_IDcard_search=(EditText) findViewById(R.id.et_IDcard_search);
		et_card_search=(EditText) findViewById(R.id.et_card_search);
		btn_admin_logout=(Button) findViewById(R.id.btn_admin_logout);
		btn_admin_logout.setOnClickListener(this);
		sp = SearchInfoActivity.this.getSharedPreferences("mysp_setting",
				Context.MODE_PRIVATE);
		market_overtime = Integer.parseInt(sp.getString("market_overtime", "0"));
		login_overtime=Integer.parseInt(sp.getString("market_overtime", "0"));
		btn_search_return.setOnClickListener(this);
		btn_search_confirm.setOnClickListener(this);
	}

	/* 点击返回 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			sh=new ShareHelper(LoginActivity.this);
//			sh.save("", 0);
			Intent intent=new Intent(SearchInfoActivity.this,LoginActivity.class);
			setResult(RESULT_RETURN);
//			startActivity(intent);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.search_info, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_search_confirm:
			String cardNoString=et_card_search.getText().toString();
			String clientId=et_IDcard_search.getText().toString();
			Toast.makeText(SearchInfoActivity.this, "点击了查询积分按钮"+clientId, Toast.LENGTH_SHORT).show();
			if(!cardNoString.equals("")){
				
			}
			else if(!clientId.equals("")){
			JsonObjectRequest jRequest=new JsonObjectRequest("http://115.236.69.226:8764/DJF-Pad/pad/login.action?clientId="+clientId, null, 
					new Listener<JSONObject>(
							) {

								@Override
								public void onResponse(JSONObject response) {
									int result = 0;
									Log.d("TAG", response.toString());
									try {
										result = response.getInt("result");
					
									} catch (JSONException e) {
										e.printStackTrace();
									}
					
										if (result==200) {
											try {
												clientNo=response.getString("clientNo");
												Log.i("clientNo",clientNo);
											} catch (JSONException e) {
												e.printStackTrace();
											}
											try {
												token=response.getString("token");
												Log.i("TOKEN",token);
											} catch (JSONException e) {
												e.printStackTrace();
											}
											JsonObjectRequest jr = new JsonObjectRequest(
													"http://115.236.69.226:8764/DJF-Pad/pad/userPointGet.action?"
															+ "clientNo=" + clientNo + "&token=" + token, null,
													new Listener<JSONObject>() {
														@Override
														public void onResponse(JSONObject response) {
															Log.e("SHOW RESPONSE6",response.toString());
															int result = 0;
															try {
																result = response.getInt("result");

															} catch (JSONException e) {
																e.printStackTrace();
															}
															if (result == 200) {
																try {
																	Toast.makeText(SearchInfoActivity.this,
																			response.getString("msg"),
																			Toast.LENGTH_SHORT).show();
																} catch (JSONException e) {
																	e.printStackTrace();
																}
																tv_card_jf.setVisibility(View.VISIBLE);
																try {
																	tv_card_jf.setText("尊敬的"+response.getString("username")+",您当前剩余"+response.getString("point")+"积分");
																} catch (JSONException e) {
																	e.printStackTrace();
																}
															} else if (result == 300) {
																try {
																	Toast.makeText(SearchInfoActivity.this,
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
											VolleySingleton.getVolleySingleton(SearchInfoActivity.this).addToRequestQueue(jr);
										} else if(result==300){
											try {
												Toast.makeText(getApplicationContext(), response.getString("msg"),
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
			VolleySingleton.getVolleySingleton(SearchInfoActivity.this)
			.addToRequestQueue(jRequest);
			}
			et_IDcard_search.setText("");
			et_card_search.setText("");
			break;
		case R.id.btn_search_return:
			Toast.makeText(SearchInfoActivity.this, "点击了查询返回按钮", Toast.LENGTH_SHORT).show();
			Intent intent=new Intent(SearchInfoActivity.this, LoginActivity.class);
//			intent.putExtra("result", "ADMIN RETURN");
			setResult(1,intent);
//			SearchInfoActivity.this.startActivity(intent);
			finish();
			break;
		case R.id.btn_admin_logout:
			Toast.makeText(SearchInfoActivity.this, "点击了柜员退出按钮", Toast.LENGTH_SHORT).show();
			ApplicationEx.getApp().setADMIN_STATUS(0);
			Intent intent2=new Intent(SearchInfoActivity.this, LoginActivity.class);
//			intent2.putExtra("result", "ADMIN LOGOUT");
			setResult(3,intent2);
//			SearchInfoActivity.this.startActivity(intent2);
			finish();
			break;
		default:
			break;
		}
	}
}
