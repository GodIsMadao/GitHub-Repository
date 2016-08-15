package com.jzfy.jfchangesystem.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import android.util.Log;

public class DataUtils 
{

	@SuppressWarnings("unused")
	private char[] getChar(int position) {
		String str = String.valueOf(position);
		if (str.length() == 1) 
		{
			str = "0" + str;
		}
		char[] c = { str.charAt(0), str.charAt(1) };
		return c;
	}

	@SuppressWarnings("unused")
	public static byte[] hexStringTobyte(String hex) 
	{
		int len = hex.length() / 2;
		int mod = hex.length() % 2;

		if(0 != mod)
		{
			len += 1;
		}
		
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();
		String temp = "";
		for (int i = 0; i < len; i++) 
		{
			int pos = i * 2;
			result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
			temp += result[i] + ",";
		}
		
		if(0 != mod)
		{
			result[len] = (byte)(toByte(achar[len * 2]));
		}

		return result;
	}

	public static int toByte(char c) 
	{
		byte b = (byte) "0123456789ABCDEF".indexOf(c);
		return b;
	}


	public static String toHexString(byte[] b) 
	{
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < b.length; i++) 
		{
			buffer.append(toHexString1(b[i]));
		}
		return buffer.toString();
	}

	public static String toHexString1(byte b) 
	{
		String s = Integer.toHexString(b & 0xFF);
		if (s.length() == 1) 
		{
			return "0" + s;
		} 
		else 
		{
			return s;
		}
	}

	public static String hexStr2Str(String hexStr) 
	{
		String str = "0123456789ABCDEF";
		char[] hexs = hexStr.toCharArray();
		byte[] bytes = new byte[hexStr.length() / 2];
		int n;
		for (int i = 0; i < bytes.length; i++) 
		{
			n = str.indexOf(hexs[2 * i]) * 16;
			n += str.indexOf(hexs[2 * i + 1]);
			bytes[i] = (byte) (n & 0xff);
		}
		return new String(bytes);
	}

	public static String str2Hexstr(String str) 
	{
		char[] chars = "0123456789ABCDEF".toCharArray();
		StringBuilder sb = new StringBuilder("");
		byte[] bs = str.getBytes();
		int bit;
		for (int i = 0; i < bs.length; i++) 
		{
			bit = (bs[i] & 0x0f0) >> 4;
			sb.append(chars[bit]);
			bit = bs[i] & 0x0f;
			sb.append(chars[bit]);
		}
		return sb.toString();
	}

	public static String byte2Hexstr(byte b) 
	{
		String temp = Integer.toHexString(0xFF & b);
		if (temp.length() < 2) 
		{
			temp = "0" + temp;
		}
		temp = temp.toUpperCase();
		return temp;
	}

	public static String str2Hexstr(String str, int size) 
	{
		byte[] byteStr = str.getBytes();
		byte[] temp = new byte[size];
		System.arraycopy(byteStr, 0, temp, 0, byteStr.length);
		temp[size - 1] = (byte) byteStr.length;
		String hexStr = toHexString(temp);
		return hexStr;
	}

	public static String[] hexStr2StrArray(String str) 
	{
		int len = 32;
		int size = str.length() % len == 0 ? str.length() / len : str.length()
				/ len + 1;
		String[] strs = new String[size];
		for (int i = 0; i < size; i++) 
		{
			if (i == size - 1) 
			{
				String temp = str.substring(i * len);
				for (int j = 0; j < len - temp.length(); j++) 
				{
					temp = temp + "0";
				}
				strs[i] = temp;
			} 
			else 
			{
				strs[i] = str.substring(i * len, (i + 1) * len);
			}
		}
		return strs;
	}


	public static byte[] compress(byte[] data) throws IOException 
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(out);
		gzip.write(data);
		gzip.close();
		return out.toByteArray();
	}

	public static byte[] uncompress(byte[] data) throws IOException 
	{

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		GZIPInputStream gunzip = new GZIPInputStream(in);
		byte[] buffer = new byte[256];
		int n;
		while ((n = gunzip.read(buffer)) >= 0) 
		{
			out.write(buffer, 0, n);
		}
		return out.toByteArray();
	}

	public static byte[] short2byte(short s) 
	{
		byte[] size = new byte[2];
		size[0] = (byte) (s >>> 8);
		short temp = (short) (s << 8);
		size[1] = (byte) (temp >>> 8);

		return size;
	}

	public static short[] hexStr2short(String hexStr) 
	{
		byte[] data = hexStringTobyte(hexStr);
		short[] size = new short[4];
		for (int i = 0; i < size.length; i++) 
		{
			size[i] = getShort(data[i * 2], data[i * 2 + 1]);
		}
		return size;
	}

	public static short getShort(byte b1, byte b2) {
		short temp = 0;
		temp |= (b1 & 0xff);
		temp <<= 8;
		temp |= (b2 & 0xff);
		return temp;
	}
	
	public static short xCalcCrc(byte[] buffer, int len)
	{
		Log.i("cy", "Enter function DataUtils-xCalcCrc()");
		short crc = 0;
		int j = 0;
		while(0 != len--)
		{
			crc ^= (buffer[j] & 0xFF);
			crc &= 0xFF;
			j++;
			for(int i=0; i<8; i++)
			{
				if(0 != (crc & 0x01))
				{
					crc = (byte)((crc >> 1) ^ 0x8C);
					crc &= 0xFF;
				}
				else
				{
					crc >>= 1;
					crc &= 0xFF;
				}
				
			}	
		}
		return crc;
	}
	
	public static short iso7816CalcLRC(byte[] buffer, int len)
	{
		Log.i("cy", "Enter function DataUtils-iso7816CalcLRC()");
		int lenBuf = buffer.length;
		if(0 == lenBuf || null == buffer)
		{
			return -1;
		}
		short lrc = 0;
		for(int i=0; i < len; i++)
		{
			lrc ^= buffer[i];
			lrc &= 0xFF;
		}	
		return lrc;
	}
	
	public static byte[] getFirstCmd(byte[] buffer, int len)
	{
		Log.i("cy", "Enter function DataUtils-getFirstCmd()");
		int lrc = DataUtils.iso7816CalcLRC(buffer, len);	
		byte[] cmd = new byte[len + 1];
		System.arraycopy(buffer, 0, cmd, 0, len);
		cmd[len] = (byte)lrc;
		String toLogStr = DataUtils.toHexString(cmd);
		Log.i("cy", "To get first cmd " + toLogStr);
		return cmd;
	}
	
	public static byte[] getCmd(byte[] buffer, int len, int modId)
	{
		Log.i("cy", "Enter function DataUtils-getCmd()");
		int crc = DataUtils.xCalcCrc(buffer, len);
		byte[] cmdHead = {(byte)0xCA, (byte)0xDF, (byte)modId};
		byte[] cmdLength = {0x00, (byte)len};
		byte[] cmd = new byte[3 + 2 + len + 1 + 1];
		System.arraycopy(cmdHead, 0, cmd, 0, 3);
		System.arraycopy(cmdLength, 0, cmd, 3, 2);
		System.arraycopy(buffer, 0, cmd, 5, len);
		cmd[5 + len] = (byte)crc;
		cmd[5 + len + 1] = (byte)0xE3;
		String toLogStr = DataUtils.toHexString(cmd);
		Log.i("cy", "To get cmd " + toLogStr);
		return cmd;
	}
	
	public static boolean isContactless = false;

}
