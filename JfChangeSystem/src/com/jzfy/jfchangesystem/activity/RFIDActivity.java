package com.jzfy.jfchangesystem.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.jzfy.jfchangesystem.R;
import com.jzfy.jfchangesystem.asynctask.AsyncM1Card;
import com.jzfy.jfchangesystem.asynctask.AsyncM1Card.OnReadAtPositionListener;
import com.jzfy.jfchangesystem.asynctask.AsyncM1Card.OnReadCardNumListener;
import com.jzfy.jfchangesystem.asynctask.AsyncM1Card.OnWriteAtPositionListener;
import com.jzfy.jfchangesystem.logic.M1CardAPI;
import com.jzfy.jfchangesystem.setting.ApplicationEx;
import com.jzfy.jfchangesystem.util.ToastUtil;

public class RFIDActivity extends Activity implements OnClickListener {
	private ProgressDialog progressDialog;

	private ApplicationEx application;

	private AsyncM1Card reader;

	private TextView tv;

	private Button readCardNum;
	private Button writePositionButton;
	private Button readPositionButton;
	private Button changeButton;

	private EditText writePositionEditText;
	private EditText writePositionDataEditText;
	private EditText writePasswordEditText;

	private EditText readPositionEditText;
	private TextView readPositionText;
	private EditText readPasswordEditText;

	private Spinner spinner1;
	private ArrayAdapter<String> adapter1;

	private Spinner spinner2;
	private ArrayAdapter<String> adapter2;

	private static final String[] m = { "KEYA", "KEYB" };
	private static final int[] keyType = { M1CardAPI.KEY_A, M1CardAPI.KEY_B };
	private int writeKeyType;
	private int readKeyType;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rfid_activity);
		initView();
		initData();
	}

	private void initView() 
	{
		tv = (TextView) findViewById(R.id.card_num_textview);
		readCardNum = (Button) findViewById(R.id.read_num_button);

		writePositionButton = (Button) findViewById(R.id.write_position_button);
		readPositionButton = (Button) findViewById(R.id.read_position_button);
		changeButton = (Button) findViewById(R.id.change_button);

		writePositionEditText = (EditText) findViewById(R.id.write_position);
		writePositionDataEditText = (EditText) findViewById(R.id.write_position_text);
		writePasswordEditText = (EditText) findViewById(R.id.write_password_text);

		readPositionEditText = (EditText) findViewById(R.id.read_position);
		readPositionText = (TextView) findViewById(R.id.read_position_text);
		readPasswordEditText = (EditText) findViewById(R.id.read_password_text);

		spinner1 = (Spinner) findViewById(R.id.Spinner01);
		adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, m);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner1.setAdapter(adapter1);
		spinner1.setOnItemSelectedListener(new OnItemSelectedListener() 
		{
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) 
			{
				writeKeyType = keyType[position];
				Log.i("cy", "position=" + position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) 
			{
				// TODO Auto-generated method stub

			}

		});

		spinner2 = (Spinner) findViewById(R.id.Spinner02);
		adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, m);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner2.setAdapter(adapter2);
		spinner2.setOnItemSelectedListener(new OnItemSelectedListener() 
		{
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) 
			{
				readKeyType = keyType[position];
				Log.i("cy", "position=" + position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) 
			{
				// TODO Auto-generated method stub

			}

		});

		readCardNum.setOnClickListener(this);

		writePositionButton.setOnClickListener(this);
		readPositionButton.setOnClickListener(this);

		changeButton.setOnClickListener(this);
	}

	private void initData() 
	{
		application = (ApplicationEx) this.getApplicationContext();
		reader = new AsyncM1Card(application.getHandlerThread().getLooper(), application.getChatService());
		reader.setOnReadCardNumListener(new OnReadCardNumListener()
		{
			@Override
			public void onReadCardNumSuccess(String num) 
			{
				tv.setText(num);
			}

			@Override
			public void onReadCardNumFail(int confirmationCode) 
			{
				tv.setText("");
				if (confirmationCode == M1CardAPI.Result.FIND_FAIL) 
				{
					ToastUtil.showToast(RFIDActivity.this, R.string.no_card_with_data);
				} 
				else if (confirmationCode == M1CardAPI.Result.TIME_OUT) 
				{
					ToastUtil.showToast(RFIDActivity.this, R.string.no_card_without_data);
				} 
				else if (confirmationCode == M1CardAPI.Result.OTHER_EXCEPTION) 
				{
					ToastUtil.showToast(RFIDActivity.this, R.string.find_card_exception);
				}
			}
		});

		reader.setOnWriteAtPositionListener(new OnWriteAtPositionListener() 
		{
			@Override
			public void onWriteAtPositionSuccess(String num) 
			{
				cancleProgressDialog();
				tv.setText(num);
				ToastUtil.showToast(RFIDActivity.this, R.string.writing_success);
			}

			@Override
			public void onWriteAtPositionFail(int comfirmationCode) 
			{
				cancleProgressDialog();
				ToastUtil.showToast(RFIDActivity.this, R.string.writing_fail);
			}
		});

		reader.setOnReadAtPositionListener(new OnReadAtPositionListener() 
		{
			@Override
			public void onReadAtPositionSuccess(String num, byte[] data) 
			{
				cancleProgressDialog();
				tv.setText(num);
				if (data != null && data.length != 0) 
				{
					readPositionText.setText(new String(data));
				}

			}

			@Override
			public void onReadAtPositionFail(int comfirmationCode) 
			{
				cancleProgressDialog();
				ToastUtil.showToast(RFIDActivity.this, R.string.reading_fail);
			}
		});
	}

	@SuppressWarnings("unused")
	private void showProgressDialog(String message) 
	{
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(message);
		if (!progressDialog.isShowing()) 
		{
			progressDialog.show();
		}
	}

	private void showProgressDialog(int resId) 
	{
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(getResources().getString(resId));
		progressDialog.show();
	}

	private void cancleProgressDialog() 
	{
		if (progressDialog != null && progressDialog.isShowing()) 
		{
			progressDialog.cancel();
			progressDialog = null;
		}
	}

	@Override
	public void onClick(View v) 
	{
		if (!application.isConnect()) 
		{
			ToastUtil.showToast(this, R.string.bluetooth_no_connect);
			return;
		}
		
		int id = v.getId();
		switch (id) 
		{
		case R.id.read_num_button:
			tv.setText("");
			reader.readCardNum();
			break;
			
		case R.id.write_position_button:
			tv.setText("");
			String writePosition = writePositionEditText.getText().toString();
			String writeData = writePositionDataEditText.getText().toString();
			String writePassword = writePasswordEditText.getText().toString();
			if (TextUtils.isEmpty(writePosition) || TextUtils.isEmpty(writeData))
			{
				ToastUtil.showToast(this, R.string.write_data_toast);
			} 
			else 
			{
				showProgressDialog(R.string.writing_wait);
				if (TextUtils.isEmpty(writePassword)) 
				{
					reader.write(Integer.parseInt(writePosition), writeKeyType, null, writeData.getBytes());
				} 
				else 
				{
					reader.write(Integer.parseInt(writePosition), writeKeyType, writePassword.getBytes(), writeData.getBytes());
				}
				

			}	
			break;
			
		case R.id.read_position_button:
			tv.setText("");
			readPositionText.setText("");
			String readPosition = readPositionEditText.getText().toString();
			String readPassword = readPasswordEditText.getText().toString();
			if (TextUtils.isEmpty(readPosition)) 
			{
				ToastUtil.showToast(this, R.string.read_toast);
			} 
			else 
			{
				showProgressDialog(R.string.reading_wait);
				if (TextUtils.isEmpty(readPassword)) 
				{
					reader.read(Integer.parseInt(readPosition), readKeyType, null);
				} 
				else 
				{
					reader.read(Integer.parseInt(readPosition), readKeyType, readPassword.getBytes());
				}
			}
			break;
			
		case R.id.change_button:
			changePassword();
			break;
			
		default:
			break;
			
		}
		
	}

	private void changePassword() 
	{
		byte[] password = new byte[] 
				{
				// ����A(6�ֽ�)
				//Password A byte (6)
				// 'b', 'b', 'b', 'b', 'b', 'b',
				(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
				(byte) 0xff, (byte) 0xff,
				// ��ȡ����(4�ֽ�),�����޸�
				//Access control (4 bytes), the need to modify
				(byte) 0xff, 0x07, (byte) 0x80, 0x69,
				// ����B(6�ֽ�)
				//Password (6 B bytes)
				(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff,
				(byte) 0xff, (byte) 0xff
		// 'a', 'a', 'a', 'a', 'a','a'
		};
		
		String writePosition = writePositionEditText.getText().toString();
		String writePassword = writePasswordEditText.getText().toString();
		if (TextUtils.isEmpty(writePosition)) 
		{
			ToastUtil.showToast(this, R.string.write_toast);
		} 
		else 
		{
			if (TextUtils.isEmpty(writePassword)) 
			{
				ToastUtil.showToast(this, R.string.write_toast);
				reader.write(Integer.parseInt(writePosition), writeKeyType, null, password);
			} 
			else 
			{
				showProgressDialog(R.string.writing_wait);
				reader.write(Integer.parseInt(writePosition), writeKeyType, writePassword.getBytes(), password);
			}

		}
	}

}
