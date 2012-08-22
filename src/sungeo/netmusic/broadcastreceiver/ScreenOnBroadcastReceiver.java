package sungeo.netmusic.broadcastreceiver;

import sungeo.netmusic.data.MainApplication;
import android.app.KeyguardManager.KeyguardLock;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScreenOnBroadcastReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		KeyguardLock keyguradlock = MainApplication.getInstance().getmKeyguradLock();
		keyguradlock.disableKeyguard();

		keyguradlock = null;
		
		if (!MainApplication.getInstance().isCallPhone()) {
			//从待机中唤醒后，播放最后一次播放的歌曲
			MainApplication.getInstance().getmMediaMgr().start();
		} else {
			MainApplication.getInstance().setCallPhone(false);
		}
	}

}
