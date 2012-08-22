package sungeo.netmusic.broadcastreceiver;

import sungeo.netmusic.data.MainApplication;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

public class PhoneStateBroadcastReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		//如果是来电
		TelephonyManager tm = 
			(TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);                        
		switch (tm.getCallState()) {
		case TelephonyManager.CALL_STATE_RINGING:
			//标识当前是来电
			MainApplication.getInstance().setCallPhone(true);
			//Log.i(TAG, "RINGING :"+ incoming_number);
			break;
		case TelephonyManager.CALL_STATE_OFFHOOK:                       
			//Log.i(TAG, "incoming ACCEPT :"+ incoming_number);
			break;
		case TelephonyManager.CALL_STATE_IDLE:                                
			//Log.i(TAG, "incoming IDLE");                                
			break;
		} 
	}

}
