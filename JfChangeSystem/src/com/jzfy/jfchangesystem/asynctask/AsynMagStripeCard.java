package com.jzfy.jfchangesystem.asynctask;

import com.jzfy.jfchangesystem.logic.BluetoothChatService;
import com.jzfy.jfchangesystem.logic.MagStripeCardAPI;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;



public class AsynMagStripeCard extends Handler
{
	private static final int READCARD = 0;
	private static final int READCARD_SUCCESS = 1;
	private static final int READCARD_FAILURE = 2;

	public static final int DATA_SIZE = 40;
	private MagStripeCardAPI magStripeCardAPI;
	private Handler workerThreadHandler;
	
	public AsynMagStripeCard(Looper looper, BluetoothChatService chatSerivce) 
	{
		Log.i("cy", "Enter function AsynMagStripeCard-AsynMagStripeCard()");
		workerThreadHandler = createHandler(looper);
		magStripeCardAPI = new MagStripeCardAPI(chatSerivce);
	}
	
	protected Handler createHandler(Looper looper) 
	{
		Log.i("cy", "Enter function AsynMagStripeCard-createHandler()");
		return new WorkerHandler(looper);
	}
	
	public void readCard() 
	{
		Log.i("cy", "Enter function AsynMagStripeCard-readCard()");
		this.workerThreadHandler.obtainMessage(READCARD).sendToTarget();
	}

	protected class WorkerHandler extends Handler 
	{
		public WorkerHandler(Looper looper) 
		{
			super(looper);
			Log.i("cy", "Enter function AsynMagStripeCard-WorkerHandler-WorkerHandler()");
		}

		@Override
		public void handleMessage(Message msg) 
		{
			Log.i("cy", "Enter function AsynMagStripeCard-WorkerHandler-handleMessage()");
			switch (msg.what) 
			{
			case READCARD:
				byte[] revData = magStripeCardAPI.readCard();
				if (null != revData) 
				{
					AsynMagStripeCard.this.obtainMessage(READCARD_SUCCESS, revData).sendToTarget();
				} 
				else 
				{
					AsynMagStripeCard.this.obtainMessage(READCARD_FAILURE).sendToTarget();
				}
				break;
				
			default:
				break;
				
			}
		}
	}

	public interface OnReadCardListener 
	{
		void onReadSuccess(byte[] buffer);
		void onReadFail(int confirmationCode);
	}
	
	private OnReadCardListener onReadCardListener;

	public void setOnReadCardListener(OnReadCardListener onReadCardListener) 
	{
		Log.i("cy", "Enter function AsynMagStripeCard-setOnReadCardListener()");
		this.onReadCardListener = onReadCardListener;
	}
	
	@Override
	public void handleMessage(Message msg) 
	{
		Log.i("cy", "Enter function AsynMagStripeCard-handleMessage()");
		super.handleMessage(msg);
		switch (msg.what) 
		{
		case READCARD_SUCCESS:
			if (null != onReadCardListener) 
			{
				onReadCardListener.onReadSuccess((byte[])msg.obj);
			}
			break;
			
		case READCARD_FAILURE:
			if (null != onReadCardListener) 
			{
				onReadCardListener.onReadFail(msg.arg1);
			}
			break;
			
		default:
			break;
			
		}
	}
	
}
