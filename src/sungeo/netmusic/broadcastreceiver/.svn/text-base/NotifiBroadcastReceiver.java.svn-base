package sungeo.netmusic.broadcastreceiver;

import sungeo.netmusic.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.RemoteViews;

public class NotifiBroadcastReceiver extends BroadcastReceiver {
	private int 					mNotiId			=	19172439;
	private Context 				mContext		=	null;
	private NotificationManager 	mNotiMgr		=	null;
	private Notification 			mNotification	=	null;
	private NotifiBroadcastReceiver mReceiver;
	
	public final static int SHOW_NOTIFICATION 			= 0x3010;
	public final static int REFRESH_DOWNLOAD_PROGRESS 	= 0x3011;
	public final static int CANEL_NOTIFICATION 			= 0x3012;

	public NotifiBroadcastReceiver(Context c){
		mContext=c;
		mReceiver=this;
	}

	//注册
	public void registerAction(String action){
		IntentFilter filter=new IntentFilter();
		filter.addAction(action);
		mContext.registerReceiver(mReceiver, filter);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String msg=intent.getStringExtra("notifi_msg");
		int id=intent.getIntExtra("notifi_type", 0);
		if(intent.getAction().equals("com.notification.sendMsg")){
			initNotifi(context);

			if (id == NotifiBroadcastReceiver.SHOW_NOTIFICATION) {
				showNotifi();
			} else if (id == NotifiBroadcastReceiver.REFRESH_DOWNLOAD_PROGRESS) {
				changeCurDownFileName(msg);
			} else if (id == NotifiBroadcastReceiver.CANEL_NOTIFICATION) {
				canelNotifi();
			}
		}
		msg = null;
	}

	private void changeCurDownFileName(String name) {
		if (name == null || mNotification == null || mNotiMgr == null) {
			return;
		}

		mNotification.contentView.setTextViewText(R.id.cursongname, name);
		mNotiMgr.notify(mNotiId, mNotification);
	}

	private void showNotifi() {
		if (mNotiMgr == null) {
			return;
		}

		mNotiMgr.notify(mNotiId, mNotification);
	}

	private void canelNotifi() {
		if (mNotiMgr == null) {
			return;
		}

		mNotiMgr.cancel(mNotiId);
	}

	private void initNotifi(Context context) {
		if (mNotiMgr == null) {
			mNotiMgr = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		}

		if (mNotification == null) {

			mNotification = new Notification(R.drawable.download,"正在下载歌曲",System.currentTimeMillis());

			mNotification.flags = Notification.FLAG_AUTO_CANCEL;
			mNotification.contentView = new RemoteViews(context.getPackageName(),R.layout.progressbar); 
			//使用notification.xml文件作VIEW
			mNotification.contentView.setProgressBar(R.id.downloadProgressBar, 100,0, false);
			//设置进度条，最大值 为100,当前值为0，最后一个参数为true时显示条纹
			//（就是在Android Market下载软件，点击下载但还没获取到目标大小时的状态）
			Intent notificationIntent = new Intent(context,context.getClass()); 
			PendingIntent contentIntent = PendingIntent.getActivity(context,0,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT); 
			mNotification.contentIntent = contentIntent; 
		}
	}
}
