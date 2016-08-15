package com.jzfy.jfchangesystem.setting;

public class Constant {
	public interface MESSAGE_WHAT {

		/**
		 * received mqtt success
		 */
		public static final int MQTT_MSG_RECEIVED_SUCCESS = 1;

		/**
		 * mqtt server connection success
		 */
		public static final int MQTT_CONN_SUCCESS = 0;

		/**
		 * mqtt server connection failed
		 */
		public static final int MQTT_CONN_FAILED = 999;
	}

	public interface APP_INFO {
		/**
		 * 配置标示
		 */
		public static final String SHAREDPREFERENCES_NAME = "com.jzfy.jfchangesystem";
	}
}
