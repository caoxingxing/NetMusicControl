package sungeo.netmusic.data;

import android.content.Context;
import android.content.SharedPreferences;

public class ConfigPreferences {
	public static final int 	BG_MIN_SERIAL 	   = 0x62000001;
	public static final int 	BG_MAX_SERIAL 	   = 0x62ffffff;
	public static final String SELF_SERIAL_KEY     = "netmusicserial";
	public static final String SELF_ADDRESS_KEY    = "netmusicaddress";
	public static final String GATEWAY_IP_KEY      = "gatewayipaddress";
	public static final String POWER_ADDRESS_KEY   = "poweraddress";
	public static final String VERSION_KEY         = "dataversion";
	public static final String SYSTEM_USERCODE_KEY = "systemusercode";

	private MainApplication mMainApp;

	public ConfigPreferences() {
		mMainApp = MainApplication.getInstance();
	}

	public void savePowAddr(char address) {
		SharedPreferences myShare = mMainApp.getSharedPreferences(mMainApp.getPackageName(), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = myShare.edit();

		editor.putInt(POWER_ADDRESS_KEY, address);
		editor.commit();
	}

	public char getPowAddr() {
		SharedPreferences myShare = mMainApp.getSharedPreferences(mMainApp.getPackageName(), Context.MODE_PRIVATE);

		return (char)myShare.getInt(POWER_ADDRESS_KEY, 0);
	}

	public void saveSelfAddr(char address) {
		SharedPreferences myShare = mMainApp.getSharedPreferences(mMainApp.getPackageName(), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = myShare.edit();

		editor.putInt(SELF_ADDRESS_KEY, address);
		editor.commit();
	}

	public char getSelfAddr() {
		SharedPreferences myShare = mMainApp.getSharedPreferences(mMainApp.getPackageName(), Context.MODE_PRIVATE);

		return (char)myShare.getInt(SELF_ADDRESS_KEY, 0);
	}

	public void saveUsercode(char usercode) {
		SharedPreferences myShare = mMainApp.getSharedPreferences(mMainApp.getPackageName(), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = myShare.edit();

		editor.putInt(SYSTEM_USERCODE_KEY, usercode);
		editor.commit();
	}

	public char getUsercode() {
		SharedPreferences myShare = mMainApp.getSharedPreferences(mMainApp.getPackageName(), Context.MODE_PRIVATE);

		return (char)myShare.getInt(SYSTEM_USERCODE_KEY, 0);
	}

	public void saveGatewayIp(String ipAddress) {
		SharedPreferences myShare = mMainApp.getSharedPreferences(mMainApp.getPackageName(), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = myShare.edit();

		editor.putString(GATEWAY_IP_KEY, ipAddress);
		editor.commit();
	}

	public String getGatewayIp() {
		SharedPreferences myShare = mMainApp.getSharedPreferences(mMainApp.getPackageName(), Context.MODE_PRIVATE);

		return myShare.getString(GATEWAY_IP_KEY, "192.168.1.11");
	}

	public void saveSelfSerial(int serial) {
		SharedPreferences myShare = mMainApp.getSharedPreferences(mMainApp.getPackageName(), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = myShare.edit();

		editor.putInt(SELF_SERIAL_KEY, serial);
		editor.commit();
	}

	public int getSelfSerial() {
		SharedPreferences myShare = mMainApp.getSharedPreferences(mMainApp.getPackageName(), Context.MODE_PRIVATE);

		int serial = myShare.getInt(SELF_SERIAL_KEY, 0);
		return serial;
	}

	public void saveVersion(String version) {
		SharedPreferences myShare = mMainApp.getSharedPreferences(mMainApp.getPackageName(), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = myShare.edit();

		editor.putString(VERSION_KEY, version);
		editor.commit();
	}

	public String getVersion() {
		SharedPreferences myShare = mMainApp.getSharedPreferences(mMainApp.getPackageName(), Context.MODE_PRIVATE);

		return myShare.getString(VERSION_KEY, "0.0.0.0.0.0");
	}
}
