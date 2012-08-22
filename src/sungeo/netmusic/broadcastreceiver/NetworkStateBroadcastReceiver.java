package sungeo.netmusic.broadcastreceiver;

import com.mobclick.android.MobclickAgent;

import sungeo.netmusic.data.MainApplication;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

public class NetworkStateBroadcastReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		
		if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
			ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
			
			NetworkInfo network = connMgr.getActiveNetworkInfo();
			if (network == null || !network.isConnected()) {
				disconnected();
				action = null;
				network = null;
				connMgr = null;
				return;
			}
			
			if (network.getState() == NetworkInfo.State.CONNECTED) {
				connected();
			} else if (network.getState() == NetworkInfo.State.DISCONNECTED) {
				disconnected();
			}
			
			action = null;
			network = null;
			connMgr = null;
		}
	}

	private void connected() {
		MainApplication.getInstance().getmMsgSender().printLog("�����Ѿ���������ServerSocket����");
		MainApplication.getInstance().startNetTimer();
		MainApplication.getInstance().getmMsgSender().sendCancelWifiDialogMsg();
		
		StringBuffer strBuf = new StringBuffer();
		int serial = MainApplication.getInstance().getmConfig().getSelfSerial();
		String serStr = Integer.toHexString(serial);
		strBuf.append(serStr);
		strBuf.append("�����Ѿ�����");

		MobclickAgent.onEvent(MainApplication.getInstance(), "netstate_is_connected", strBuf.toString());
	}
	
	private void disconnected() {
		MainApplication.getInstance().getmMsgSender().printLog("�����Ѿ��Ͽ����ر�ServerSocket����");
		MainApplication.getInstance().stopNetTimer();

		MainApplication.getInstance().getmMsgSender().sendShowWifiMsg();
		
		MainApplication.getInstance().getmMsgSender().printLog("���������磬�ж�����");
		if (MainApplication.getInstance().getmDownMgr() != null) {
			MainApplication.getInstance().getmDownMgr().intermitDownload();
		}
	}
}
