package com.jzfy.jfchangesystem.activity;

import java.util.Vector;

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
import com.jzfy.jfchangesystem.setting.ApplicationEx;
import com.jzfy.jfchangesystem.util.ShareHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class FragmentSetting extends Fragment {
	final static private String EDIT_PASSWORD = "31415926";

	private Button btn_confirm;
	private EditText et_password;
	private SharedPreferences sp;
	private EditText et_setting_ip;
	private EditText et_setting_branchid;
	private EditText et_setting_market_counttime;
	private EditText et_setting_login_counttime;
	private EditText et_setting_port;
	private Button btn_connnect_blue;
	private Button btn_confirm_setting;
	private LinearLayout layout_setting;
	private LinearLayout layout_setting_warning;
	private ApplicationEx application;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	


	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.setting, null);
		btn_connnect_blue=(Button) view.findViewById(R.id.btn_connnect_blue);
		application = (ApplicationEx) getActivity().getApplicationContext();
		btn_connnect_blue.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity(), "点击了连接蓝牙", Toast.LENGTH_SHORT).show();
				if (!application.isConnect()) 
				{
					startActivity(new Intent(getActivity(), ConnectActivity.class));
//					getActivity().finish();
					return;
				}
			}
		});
		Log.e("USER STATUS2 fragment setting",ApplicationEx.getApp().getUSER_STATUS()+"lalal");
		layout_setting = (LinearLayout) view.findViewById(R.id.layout_setting);
		layout_setting_warning = (LinearLayout) view
				.findViewById(R.id.layout_setting_warning);
		sp = getActivity().getSharedPreferences("mysp_setting",
				Context.MODE_PRIVATE);
		
		btn_confirm = (Button) view.findViewById(R.id.btn_setting_confirm);
		btn_confirm_setting = (Button) view
				.findViewById(R.id.btn_confirm_setting);
		et_password = (EditText) view.findViewById(R.id.et_setting_password);
		et_setting_ip = (EditText) view.findViewById(R.id.et_setting_ip);
		et_setting_market_counttime = (EditText) view
				.findViewById(R.id.et_setting_market_counttime);
		et_setting_port = (EditText) view.findViewById(R.id.et_setting_port);
		et_setting_login_counttime = (EditText) view
				.findViewById(R.id.et_setting_login_counttime);
		et_setting_branchid = (EditText) view
				.findViewById(R.id.et_setting_branchid);
		// 不是柜员登录
		if (ApplicationEx.getApp().getADMIN_STATUS() == 0) {
			layout_setting.setVisibility(View.GONE);
			layout_setting_warning.setVisibility(View.VISIBLE);
		} else {
			// 是柜员登录
			layout_setting.setVisibility(View.VISIBLE);
			layout_setting_warning.setVisibility(View.GONE);
			et_setting_market_counttime.setText(sp.getString("market_overtime",
					""));
			et_setting_login_counttime.setText(sp.getString("login_overtime",
					""));
			et_setting_ip.setText(sp.getString("ip", ""));
			et_setting_port.setText(sp.getString("port", ""));
			et_setting_branchid.setText(sp.getString("branchid", ""));
		}
		btn_confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// Toast.makeText(getActivity(), "点击了确定密令",
				// Toast.LENGTH_SHORT).show();
				Log.i("密令", et_password.getText() + "sdad");
				System.out.println(et_password.getText().toString()
						.equals(EDIT_PASSWORD));
				if (et_password.getText().toString().equals(EDIT_PASSWORD)) {
					Toast.makeText(getActivity(), "密令正确", Toast.LENGTH_SHORT)
							.show();
					Log.i("password", "密令正确");
					btn_confirm_setting.setVisibility(View.VISIBLE);
				} else {
					Toast.makeText(getActivity(), "密令错误，还有N次机会",
							Toast.LENGTH_SHORT).show();
					Log.i("password", "密令错误，还有N次机会");
				}
			}
		});
		Log.i("visibility:", btn_confirm_setting.getVisibility() + "lalla");
		btn_confirm_setting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				ShareHelper sh = new ShareHelper(getActivity());
				Toast.makeText(getActivity(), "点击了确认设置按钮", Toast.LENGTH_SHORT)
						.show();
				System.out.println("ip地址:    " + et_setting_ip.getText());
				System.out.println("market count time :    "
						+ et_setting_market_counttime.getText());
				System.out.println("login count time:    "
						+ et_setting_login_counttime.getText());

				sh.save(et_setting_market_counttime.getText().toString(),
						et_setting_login_counttime.getText().toString(),
						et_setting_ip.getText().toString(), et_setting_branchid
								.getText().toString(), et_setting_port
								.getText().toString());
				Intent intent = new Intent(getActivity(), MainActivity.class);
				getActivity().startActivity(intent);
				getActivity().finish();
			}
		});
		return view;
	} 

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
