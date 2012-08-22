package sungeo.netmusic.manager;

import java.util.Timer;
import java.util.TimerTask;

import com.mobclick.android.MobclickAgent;

import sungeo.netmusic.data.ConfigPreferences;
import sungeo.netmusic.data.MainApplication;
import sungeo.netmusic.netbase.ParserByPull;

public class VersionMgr {
	private Timer mCheckTimer;
	private boolean mRunning = false;
	private ParserByPull mQuery;
	private long mSpTime = 10000;

	public VersionMgr() {
		mQuery = new ParserByPull();
	}
	public void stopCheckVersion() {
		//¹Ø±Õ¶¨Ê±Æ÷
		mRunning = false;
		if (mCheckTimer != null) {
			mCheckTimer.cancel();
			mCheckTimer = null;
		}
	}

	public void startCheckVersion() {
		if (mCheckTimer != null) {
			mCheckTimer.cancel();
			mCheckTimer = null;
		}

		mRunning = true;
		
		mCheckTimer = new Timer();
		long waittime = 1000;
		String onLineParams = MobclickAgent.getConfigParams(MainApplication.getInstance(), "sp_time");
		if (onLineParams != null && !onLineParams.equals("")) {
			mSpTime = Integer.valueOf(onLineParams);
		}
		
		mCheckTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				MobclickAgent.updateOnlineConfig(MainApplication.getInstance());
				String params = MobclickAgent.getConfigParams(MainApplication.getInstance(), "sp_time");
				if (params != null && !params.equals("")) {
					long param = Integer.valueOf(params);
					if (param != mSpTime) {
						stopCheckVersion();
						startCheckVersion();
					}
				}
				checkVersion();
			}
		}, waittime, mSpTime);
	}

	private void checkVersion() {
		boolean flag = MainApplication.getInstance().checkNetwork();
		if (!flag) {
			return;
		}
		int serial = MainApplication.getInstance().getmConfig().getSelfSerial();
		String strSerial = Integer.toHexString(serial);
		int ser = Integer.valueOf(strSerial, 16);
		if (ser > ConfigPreferences.BG_MAX_SERIAL || ser < ConfigPreferences.BG_MIN_SERIAL) {
			return;
		}
		mQuery.setmSerial(strSerial);
		mQuery.quaryVersion();
		strSerial = null;
	}

	public void setmRunning(boolean mRunning) {
		this.mRunning = mRunning;
	}

	public boolean ismRunning() {
		return mRunning;
	}
}
