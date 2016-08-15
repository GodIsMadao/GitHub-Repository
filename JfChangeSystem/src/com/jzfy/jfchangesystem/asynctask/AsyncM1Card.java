package com.jzfy.jfchangesystem.asynctask;

import com.jzfy.jfchangesystem.logic.BluetoothChatService;
import com.jzfy.jfchangesystem.logic.M1CardAPI;
import com.jzfy.jfchangesystem.logic.M1CardAPI.Result;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;


public class AsyncM1Card extends Handler 
{
	private static final int READ_CARD_NUM = 1;

	private static final int WRITE_AT_POSITION_DATA = 2;

	private static final int READ_AT_POSITION_DATA = 3;

	private static final String POSITION_KEY = "position";
	private static final String KEY_TYPE_KEY = "keyType";
	private static final String PASSWORD_KEY = "password";
	private static final String DATA_KEY = "data";


	private Handler mWorkerThreadHandler;

	private M1CardAPI reader;

	public AsyncM1Card(Looper looper, BluetoothChatService chatSerivce) 
	{
		mWorkerThreadHandler = createHandler(looper);
		reader = new M1CardAPI(chatSerivce);
	}

	protected Handler createHandler(Looper looper) 
	{
		return new WorkerHandler(looper);
	}

	protected class WorkerHandler extends Handler 
	{
		public WorkerHandler(Looper looper) 
		{
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) 
		{
			switch (msg.what) 
			{
			case READ_CARD_NUM:
				Result result = reader.readCardNum();
				AsyncM1Card.this.obtainMessage(READ_CARD_NUM, result).sendToTarget();
				break;
				
			case WRITE_AT_POSITION_DATA:
				Result writeAtPositionResult = write(msg);
				AsyncM1Card.this.obtainMessage(WRITE_AT_POSITION_DATA, writeAtPositionResult).sendToTarget();
				break;
				
			case READ_AT_POSITION_DATA:
				Result readAtPositionResult = read(msg);
				AsyncM1Card.this.obtainMessage(READ_AT_POSITION_DATA, readAtPositionResult).sendToTarget();
				break;
				
			default:
				break;
				
			}
			
		}
		
	}

	private OnReadCardNumListener onReadCardNumListener;

	private OnReadAtPositionListener onReadAtPositionListener;

	private OnWriteAtPositionListener onWriteAtPositionListener;

	public void setOnReadCardNumListener(OnReadCardNumListener onReadCardNumListener) 
	{
		this.onReadCardNumListener = onReadCardNumListener;
	}

	public void setOnReadAtPositionListener(OnReadAtPositionListener onReadAtPositionListener) 
	{
		this.onReadAtPositionListener = onReadAtPositionListener;
	}

	public void setOnWriteAtPositionListener(OnWriteAtPositionListener onWriteAtPositionListener) 
	{
		this.onWriteAtPositionListener = onWriteAtPositionListener;
	}

	public interface OnReadCardNumListener 
	{
		public void onReadCardNumSuccess(String num);
		public void onReadCardNumFail(int comfirmationCode);
	}

	public interface OnReadAtPositionListener 
	{
		public void onReadAtPositionSuccess(String num, byte[] data);

		/**
		 * 确认码 1: 成功 2：寻卡失败 3：验证失败 4:写卡失败 5：超时 6：其它异常
		 * 
		 * @param comfirmationCode
		 */
		public void onReadAtPositionFail(int comfirmationCode);
	}

	public interface OnWriteAtPositionListener 
	{
		public void onWriteAtPositionSuccess(String num);
		public void onWriteAtPositionFail(int comfirmationCode);
	}

	public void readCardNum() 
	{
		mWorkerThreadHandler.obtainMessage(READ_CARD_NUM).sendToTarget();
	}

	@Override
	public void handleMessage(Message msg) 
	{
		super.handleMessage(msg);
		switch (msg.what) 
		{
		case READ_CARD_NUM:
			Result numResult = (Result) msg.obj;
			if (onReadCardNumListener != null) 
			{
				if (numResult != null && numResult.confirmationCode == Result.SUCCESS) 
				{
					onReadCardNumListener.onReadCardNumSuccess(numResult.num);
				} 
				else 
				{
					onReadCardNumListener.onReadCardNumFail(numResult.confirmationCode);
				}
			}
			break;
			
		case WRITE_AT_POSITION_DATA:
			if (onWriteAtPositionListener != null)
			{
				Result result = (Result) msg.obj;
				if (result != null && result.confirmationCode == Result.SUCCESS) 
				{
					onWriteAtPositionListener.onWriteAtPositionSuccess(result.num);
				} 
				else 
				{
					onWriteAtPositionListener.onWriteAtPositionFail(result.confirmationCode);
				}
			}
			break;
			
		case READ_AT_POSITION_DATA:
			Result readPositionResult = (Result) msg.obj;
			byte[] readPositionData = (byte[]) readPositionResult.resultInfo;
			if (onReadAtPositionListener != null) 
			{
				if (readPositionData != null) 
				{
					onReadAtPositionListener.onReadAtPositionSuccess(readPositionResult.num, readPositionData);
				} 
				else 
				{
					onReadAtPositionListener.onReadAtPositionFail(readPositionResult.confirmationCode);
				}
			}
			break;
			
		default:
			break;
			
		}
		
	}

	public void write(int position, int keyType, byte[] password, byte[] data) 
	{
		Message msg = mWorkerThreadHandler.obtainMessage(WRITE_AT_POSITION_DATA);
		Bundle bundle = new Bundle();
		bundle.putInt(POSITION_KEY, position);
		bundle.putInt(KEY_TYPE_KEY, keyType);
		bundle.putByteArray(PASSWORD_KEY, password);
		bundle.putByteArray(DATA_KEY, data);
		msg.setData(bundle);
		msg.sendToTarget();
	}

	public void read(int position, int keyType, byte[] password)
	{
		Message msg = mWorkerThreadHandler.obtainMessage(READ_AT_POSITION_DATA);
		Bundle bundle = new Bundle();
		bundle.putInt(POSITION_KEY, position);
		bundle.putInt(KEY_TYPE_KEY, keyType);
		bundle.putByteArray(PASSWORD_KEY, password);
		msg.setData(bundle);
		msg.sendToTarget();

	}


	private Result write(Message msg) 
	{
		Bundle writeBundle = msg.getData();
		int position = writeBundle.getInt(POSITION_KEY);
		int keyType = writeBundle.getInt(KEY_TYPE_KEY);
		byte[] password = writeBundle.getByteArray(PASSWORD_KEY);
		byte[] data = writeBundle.getByteArray(DATA_KEY);
		
		Result result = reader.readCardNum();
		if (result.confirmationCode != Result.SUCCESS) 
		{
			return result;
		}

		if (!reader.validatePassword(position, keyType, password)) 
		{
			result.confirmationCode = Result.VALIDATE_FAIL;
			return result;
		}

		boolean writeResult = reader.write(data, position);
		reader.turnOff();
		if (writeResult) 
		{
			result.confirmationCode = Result.SUCCESS;
		} 
		else 
		{
			result.confirmationCode = Result.WRITE_FAIL;
		}
		
		return result;
		
	}


	private Result read(Message msg) 
	{
		Bundle readBundle = msg.getData();
		int position = readBundle.getInt(POSITION_KEY);
		int keyType = readBundle.getInt(KEY_TYPE_KEY);
		byte[] password = readBundle.getByteArray(PASSWORD_KEY);
		
		Result result = reader.readCardNum();
		if (result.confirmationCode != Result.SUCCESS) 
		{
			return result;
		}

		if (!reader.validatePassword(position, keyType, password))
		{
			result.confirmationCode = Result.VALIDATE_FAIL;
			return result;
		}

		byte[] data = reader.read(position);
		reader.turnOff();
		result.confirmationCode = Result.SUCCESS;
		result.resultInfo = data;
		return result;
	}

}
