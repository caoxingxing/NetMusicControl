package sungeo.netmusic.manager;

import sungeo.netmusic.data.MainApplication;

import java.util.Timer;
import java.util.TimerTask;

public class IpInfoMgr {
	private Timer mCheckTimer;
	private boolean mRunning = false;

	public void startIpInfoTimer() {
		if (mCheckTimer != null) {
			mCheckTimer.cancel();
			mCheckTimer = null;
		}
		mRunning = true;
		mCheckTimer = new Timer();
		long waitTime = 60000;
		long spTime = 60000;
		mCheckTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				sendUdpBroadcast();
			}}, waitTime, spTime);
	}
	
	private void sendUdpBroadcast() {
		if (MainApplication.getInstance().checkNetwork()) {
			MainApplication.getInstance().sendUdpBroadcast();
		} else {
			MainApplication.getInstance().getmMsgSender().sendShowWifiMsg();
		}
	}
	
	public void stopIpInfoTimer() {
		mRunning = false;
		if (mCheckTimer == null) {
			return;
		}
		
		mCheckTimer.cancel();
		mCheckTimer = null;
	}

	public void setmRunning(boolean mRunning) {
		this.mRunning = mRunning;
	}

	public boolean ismRunning() {
		return mRunning;
	}
}
