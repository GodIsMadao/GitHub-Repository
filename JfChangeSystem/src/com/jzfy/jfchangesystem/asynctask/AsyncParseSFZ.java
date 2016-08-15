package com.jzfy.jfchangesystem.asynctask;


import com.jzfy.jfchangesystem.logic.BluetoothChatService;
import com.jzfy.jfchangesystem.logic.ParseSFZAPI;
import com.jzfy.jfchangesystem.logic.ParseSFZAPI.People;
import com.jzfy.jfchangesystem.logic.ParseSFZAPI.Result;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class AsyncParseSFZ extends Handler 
{
	private static final int READ_SFZ = 0;
	private static final int READ_SFZ_SUCCESS = 1;
	private static final int READ_SFZ_FAILURE = 2;

	public static final int DATA_SIZE = 1295;
	private ParseSFZAPI parse;
	private Handler workerThreadHandler;

	public interface OnReadSFZListener 
	{
		void onReadSuccess(People people);
		void onReadFail(int confirmationCode);
	}
	
	private OnReadSFZListener onReadSFZListener;

	public void setOnReadSFZListener(OnReadSFZListener onReadSFZListener) 
	{
		Log.i("cy", "Enter function AsyncParseSFZ-setOnReadSFZListener()");
		this.onReadSFZListener = onReadSFZListener;
	}
	
	public AsyncParseSFZ(Looper looper, BluetoothChatService chatSerivce) 
	{
		Log.i("cy", "Enter function AsyncParseSFZ-AsyncParseSFZ()");
		workerThreadHandler = createHandler(looper);
		parse = new ParseSFZAPI(chatSerivce);
	}

	protected Handler createHandler(Looper looper) 
	{
		Log.i("cy", "Enter function AsyncParseSFZ-createHandler()");
		return new WorkerHandler(looper);
	}

	protected class WorkerHandler extends Handler 
	{
		public WorkerHandler(Looper looper) 
		{
			super(looper);
			Log.i("cy", "Enter function AsyncParseSFZ-WorkerHandler-WorkerHandler()");
		}

		@Override
		public void handleMessage(Message msg) 
		{
			Log.i("cy", "Enter function AsyncParseSFZ-WorkerHandler-handleMessage()");
			switch (msg.what) 
			{
			case READ_SFZ:
				Result resultSFZ = parse.read();
				if (Result.SUCCESS == resultSFZ.confirmationCode) 
				{
					AsyncParseSFZ.this.obtainMessage(READ_SFZ_SUCCESS, resultSFZ.resultInfo).sendToTarget();
				} 
				else 
				{
					AsyncParseSFZ.this.obtainMessage(READ_SFZ_FAILURE, resultSFZ.confirmationCode, -1).sendToTarget();
				}
				break;
				
			default:
				break;
				
			}
		}
	}

	public void readSFZ() 
	{
		Log.i("cy", "Enter function AsyncParseSFZ-readSFZ()");
		workerThreadHandler.obtainMessage(READ_SFZ).sendToTarget();
	}

	@Override
	public void handleMessage(Message msg) 
	{
		Log.i("cy", "Enter function AsyncParseSFZ-handleMessage()");
		super.handleMessage(msg);
		switch (msg.what) 
		{
		case READ_SFZ_SUCCESS:
			if (null != onReadSFZListener) 
			{
				onReadSFZListener.onReadSuccess((People) msg.obj);
			}
			break;
			
		case READ_SFZ_FAILURE:
			if (null != onReadSFZListener) 
			{
				onReadSFZListener.onReadFail(msg.arg1);
			}
			break;
			
		default:
			break;
			
		}
	}

}
