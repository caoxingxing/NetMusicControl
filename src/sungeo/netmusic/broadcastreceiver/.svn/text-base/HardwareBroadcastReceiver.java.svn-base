package sungeo.netmusic.broadcastreceiver;

import sungeo.netmusic.activity.BaseActivity;
import sungeo.netmusic.activity.CopyUdiskMusicActivity;
import sungeo.netmusic.data.MainApplication;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class HardwareBroadcastReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
			mountedHandler(context, intent);
		} else if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
			unmountedHandler(context);
		}
		action = null;
	}

	private void mountedHandler(Context context, Intent intent) {
		// 根据挂载点名称判断是不是U盘。
		String mountPoint = intent.getDataString();
		if(null == mountPoint){
			return;
		}

		if(! mountPoint.matches("file:///mnt/sd[a-z]")){
			// 挂的不是U盘。
			MainApplication.getInstance().getmAlbumMgr().start();
			//MainApplication.getInstance().getmMsgSender().printLog("挂的不是U盘");
			return;
		}else{
			//MainApplication.getInstance().getmMsgSender().printLog("挂的是U盘");
			int len = mountPoint.length();
			String diskName = mountPoint.substring(len - 3, len);
			String partitionName = diskName + "1";
			String prefix = mountPoint.substring(7, len);
			if(prefix.charAt(prefix.length() - 1) != '/'){
				prefix += "/";
			}

			MainApplication.getInstance().setmUdiskMountedPath(prefix + partitionName);
		}

		if(isCopyUdisk()){
			// 界面已经显示。
			return;
		}

		// 显示U盘根目录和第一级子目录下歌曲列表，引导用户拷贝音乐。
		Intent newActivity = new Intent();
		newActivity.setClass(context, CopyUdiskMusicActivity.class);
		newActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(newActivity);
		mountPoint = null;
	}

	private void unmountedHandler(Context context) {
		// 关闭CopyUdiskMusicActivity界面，返回背景音乐界面。
		/*Intent stopIntent = new Intent(CopyUdiskMusicActivity.UDISK_UNMOUNTED_MESSAGE);
		context.sendBroadcast(stopIntent);*/
		//MainApplication.getInstance().getmMsgSender().printLog("SD卡已经卸载");
		MainApplication.getInstance().getmAlbumMgr().stop();
		//关闭正在播放的歌曲
		MainApplication.getInstance().getmMediaMgr().stop();
		if (isCopyUdisk()) {
			BaseActivity.sCurActivity.finish();
		}
	}
	
	private boolean isCopyUdisk() {
		boolean flag = false;
		if (BaseActivity.sCurActivity != null &&
			BaseActivity.sCurActivity.getClass() == CopyUdiskMusicActivity.class){
			flag = true;
		}
		return flag;
	}
}
