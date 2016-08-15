package com.jzfy.jfchangesystem.logic;


import com.jzfy.jfchangesystem.util.DataUtils;

import android.util.Log;

public class MagStripeCardAPI 
{
	public static class Result 
	{
		public static final int SUCCESS = 1;
		public static final int FAILURE = 0;
		public Object resultInfo;
	}
	
	private BluetoothChatService chatService;
	private byte[] buffer = new byte[1024];

	public MagStripeCardAPI(BluetoothChatService chatService)
	{
		Log.i("cy", "Enter function MagStripeCardAPI-MagStripeCardAPI()");
		this.chatService = chatService;
	}
	
	public byte[] readCard()
	{
		Log.i("cy", "Enter function MagStripeCardAPI-readCard()");
		byte[] toCalcCrc = { 0x03, 0x00, 0x00 };
		byte[] cmdReset = DataUtils.getCmd(toCalcCrc, toCalcCrc.length, 0x02);
		chatService.write(cmdReset);
		
		int length = chatService.read(buffer, 4000, 200);
		if(0 == length)
		{
			return null;
		}
		
		byte[] recvData = new byte[length];
		
		System.arraycopy(buffer, 0, recvData, 0, length);
		printlog("MagStripeCardAPI-readCard()", recvData);
		
		if(1 == length && 0xEE == (recvData[0] & 0xFF))
		{
			return null;
		}
		
		return recvData;
	}
	
	private void printlog(String tag, byte[] toLog) 
	{
		Log.i("cy", "Enter function MagStripeCardAPI-printlog()");
		String toLogStr = DataUtils.toHexString(toLog); 
		Log.i("cy", tag + "=" + toLogStr);
	}
	
}
