package com.jzfy.jfchangesystem.setting;

import java.util.Date;

import com.jzfy.jfchangesystem.setting.Constant.APP_INFO;

import android.content.Context;
import android.content.SharedPreferences;


public class AppConfig {

	// 配置存储文件头
	public static final String CONFIG_NAME = APP_INFO.SHAREDPREFERENCES_NAME;

	// 默认值设置
	private static final String DEFAULT_MQTT_SERVER_IP = "192.168.10.225";
	private static final String DEFAULT_SERVER_IP = "127.0.0.1";
	// private static final String DEFAULT_MQTT_SERVER_USERNAME = "admin";
	// private static final String DEFAULT_MQTT_SERVER_PASSWORD = "password";
	// private static final String DEFAULT_MQTT_TOPIC = "000000";
	private static final String DEFAULT_MQTT_CLIENTID = String
			.valueOf(new Date().getTime());
	private static final String DEFAULT_REQUEST_URL = "http://www.baidu.com";
	private static final int DEFAULT_MQTT_SERVER_DURATION = 60;// 1小时
	private static final int DEFAULT_MQTT_SERVER_REFRESH_RATE = 1;// 1分钟

	public static String branchid;

	// 配置存储字段名称设置
	public static final String KEY_MQTT_SERVER_IP = "mqttip";
	public static final String KEY_SERVER_IP = "ip";
	// public static final String KEY_MQTT_SERVER_USERNAME = "mqttusername";
	// public static final String KEY_MQTT_SERVER_PASSWORD = "mqttpassword";
	// public static final String KEY_MQTT_SERVER_TOPIC = "mqtttopic";
	public static final String KEY_MQTT_SERVER_CLIENTID = "mqttclientid";
	public static final String KEY_REQUEST_URL = "requesturl";
	public static final String KEY_MQTT_SERVER_DURATION = "duration";// 时长
	public static final String KEY_MQTT_SERVER_REFRESH_RATE = "refreshRate";// 刷新频率
	public static final String KEY_MQTT_SERVER_PROTOCOL = "tcp://";
	public static final String KEY_MQTT_SERVER_PORT = ":61613";
	// private change to public
	public SharedPreferences sharedPreferences;
	public SharedPreferences.Editor editor;

	public AppConfig(Context context) {
		this(context, CONFIG_NAME);

	}

	public AppConfig(Context context, String configName) {
		sharedPreferences = context.getSharedPreferences(configName,
				Context.MODE_WORLD_WRITEABLE | Context.MODE_WORLD_READABLE);
		editor = sharedPreferences.edit();
	}

	// 通用方法开始
	public void putSharePreStr(String key, String value) {
		editor.putString(key, value).commit();
	}

	public void putSharePreInt(String key, int value) {
		editor.putInt(key, value).commit();
	}

	public void putSharePreBoolean(String key, boolean value) {
		editor.putBoolean(key, value).commit();
	}

	public void putSharePreLong(String key, long value) {
		editor.putLong(key, value).commit();
	}

	public void putSharePreFloat(String key, float value) {
		editor.putFloat(key, value).commit();
	}

	public String getSharePreStr(String key, String defValue) {
		return sharedPreferences.getString(key, defValue);
	}

	public int getSharePreInt(String key, int defValue) {
		return sharedPreferences.getInt(key, defValue);
	}

	public boolean getSharePreBoolean(String key, boolean defValue) {
		return sharedPreferences.getBoolean(key, defValue);
	}

	public long getSharePreLong(String key, long defValue) {
		return sharedPreferences.getLong(key, defValue);
	}

	public float getSharePreFloat(String key, float defValue) {
		return sharedPreferences.getFloat(key, defValue);
	}

	// 通用方法结束

	// 具体配置设置获取

	public String getMqttClientid() {
		return sharedPreferences.getString(KEY_MQTT_SERVER_CLIENTID,
				DEFAULT_MQTT_CLIENTID);
	}

	public String getMqttServerIP() {
		return sharedPreferences.getString(KEY_MQTT_SERVER_IP,
				DEFAULT_MQTT_SERVER_IP);
	}

	public void setMqttServerIP(String mqttserverip) {
		editor.putString(KEY_MQTT_SERVER_IP, mqttserverip).commit();
	}

	/*
	 * public String getMqttServerUsername() { return
	 * sharedPreferences.getString(KEY_MQTT_SERVER_USERNAME,
	 * DEFAULT_MQTT_SERVER_USERNAME); }
	 * 
	 * public void setMqttServerUsername(String mqttserverusername) {
	 * editor.putString(KEY_MQTT_SERVER_USERNAME, mqttserverusername).commit();
	 * }
	 * 
	 * public String getMqttServerPassword() { return
	 * sharedPreferences.getString(KEY_MQTT_SERVER_PASSWORD,
	 * DEFAULT_MQTT_SERVER_PASSWORD); }
	 * 
	 * public void setMqttServerPassword(String mqttServerPassword) {
	 * editor.putString(KEY_MQTT_SERVER_PASSWORD, mqttServerPassword).commit();
	 * }
	 */

	public String getRequestUrl() {
		return sharedPreferences
				.getString(KEY_REQUEST_URL, DEFAULT_REQUEST_URL);
	}

	public void setRequestUrl(String requestUrl) {
		editor.putString(KEY_REQUEST_URL, requestUrl).commit();
	}

	/*
	 * public String getMqttTopic() { return
	 * sharedPreferences.getString(KEY_MQTT_SERVER_TOPIC, DEFAULT_MQTT_TOPIC); }
	 * 
	 * public void setMqttTopic(String mqtttopic) {
	 * editor.putString(KEY_MQTT_SERVER_TOPIC, mqtttopic).commit(); }
	 */

	public String getServerIP() {
		return sharedPreferences.getString(KEY_SERVER_IP, DEFAULT_SERVER_IP);
	}

	public void setServerIP(String serverip) {
		editor.putString(KEY_SERVER_IP, serverip).commit();
	}

	public int getMqttDuration() {
		return sharedPreferences.getInt(KEY_MQTT_SERVER_DURATION,
				DEFAULT_MQTT_SERVER_DURATION);
	}

	public void setMqttDuration(int mqttDuration) {
		editor.putInt(KEY_MQTT_SERVER_DURATION, mqttDuration).commit();
	}

	public int getMqttRefreshRate() {
		return sharedPreferences.getInt(KEY_MQTT_SERVER_REFRESH_RATE,
				DEFAULT_MQTT_SERVER_REFRESH_RATE);
	}

	public void setMqttRefreshRate(int mqttRefreshRate) {
		editor.putInt(KEY_MQTT_SERVER_REFRESH_RATE, mqttRefreshRate).commit();
	}

}
