package sungeo.netmusic.broadcastreceiver;

import sungeo.netmusic.data.MainApplication;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class HeadsetBroadcastReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		int state = intent.getIntExtra("state", 4);
		char address = MainApplication.getInstance().getmConfig().getSelfAddr();
		if(state == 0){
			//耳机拔出
			char powAddr = MainApplication.getInstance().getmConfig().getPowAddr();
			if (address != 0 && powAddr != 0) {
				//ClientSocket.startConnectGateway(ClientSocket.CLOSE_CTRL_POWER);
			} else {
				//UIGlobal.showStringMsg("无法与电源控制模块通信");
			}
		}else if(state == 1){
			//耳机插入
			//UIGlobal.showStringMsg("耳机插入了");
		}else {
			//其他情况
		}
	}

}
