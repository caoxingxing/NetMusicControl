package sungeo.netmusic.activity;

import java.util.Timer;
import java.util.TimerTask;

import sungeo.netmusic.data.MainApplication;
import sungeo.netmusic.manager.MediaMgr;
import sungeo.netmusic.netbase.ClientSocket;
import sungeo.netmusic.unit.MsgSender;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.Toast;

import com.iflytek.speech.SpeechError;
import com.iflytek.speech.SynthesizerPlayer;
import com.iflytek.speech.SynthesizerPlayerListener;
import com.mobclick.android.MobclickAgent;

/**
 * 所有activity的基类，是个抽象类，需要子类实现 refreshUI函数
 * 负责处理一些子类用到的变量的初始化，及消息分发
 *
 */
public abstract class BaseActivity extends Activity{
	protected Handler 			mMsgHandler;
	protected MainApplication	mMainApp;
	private   AlertDialog		mDialog;
	private   Toast 			mToast 		 = null;
	private   boolean 			mConfirmExit = false; 
	
	public static BaseActivity  sCurActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mMainApp = MainApplication.getInstance();

		initMsgHandler();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		char addr = mMainApp.getmConfig().getSelfAddr();
		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			mMainApp.getmMediaMgr().volumeDec();
			int volume = mMainApp.getmMediaMgr().getCurMusicVolume();
			if (volume == 0) {
				if (!MainApplication.isSilent) {
					mMainApp.getmMediaMgr().setMute(true);
				}

				if (addr != 0) {
				    byte[] params = mMainApp.getmMediaMgr().getSyncParams(MediaMgr.STATE_SILENT);
					//同步状态，静音
					mMainApp.getmClientSocket().startConnectGateway(ClientSocket.SYNC_STATE, params);
				}
			}
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			mMainApp.getmMediaMgr().volumeAdd();
			if (MainApplication.isSilent) {
				mMainApp.getmMediaMgr().setMute(false);
			}
			int volume = mMainApp.getmMediaMgr().getCurMusicVolume();
			
			if (volume != 0 && addr != 0) {
			    byte[] params = mMainApp.getmMediaMgr().getSyncParams(MediaMgr.STATE_EXIT_SILENT);
				//同步状态，退出静音
				mMainApp.getmClientSocket().startConnectGateway(ClientSocket.SYNC_STATE, params);
			}
			
			return true;
		}

		return false;
	}

	@Override
	public void onResume() {
		super.onResume();

		sCurActivity = this;
		mConfirmExit = false;
        clearToast();
        
		MobclickAgent.onResume(this);
	}

	private void initMsgHandler() {
		mMsgHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case MsgSender.MSG_PARSE_PB_CATEGORY_FINISH:
				case MsgSender.MSG_PARSE_PB_LOCAL_FINISH:
				case MsgSender.MSG_PARSE_PB_VERSION_FINISH:
				case MsgSender.MSG_PARSE_PB_ALBUM_FINISH:
				case MsgSender.MSG_PARSE_PB_SONG_FINISH:
				case MsgSender.MSG_START_PARSE_PB:
				case MsgSender.MSG_PARSE_PB_ERROR:
				case MsgSender.MSG_SUBMIT_PB_ALBUM_FINISH:
				case MsgSender.MSG_INITSINGLE_ALBUM:
				case MsgSender.MSG_DOWNLOAD_ING:
				case MsgSender.MSG_REFRESH_PLAYSTATE:
				case MsgSender.MSG_AUTOPLAY:
				case MsgSender.MSG_COPY_ERROR:
				case MsgSender.MSG_STOP_COPY:
				case MsgSender.MSG_COPY_PROGRESS:
				case MsgSender.MSG_UDP_BROADCAST_RECIVE:{
					refreshUi(msg);
					break;
				}
				case MsgSender.MSG_ALARM_RING: {
					String alarm = (String)msg.obj;
					if (alarm == null) {
						break;
					}
					
					startIflytekTtsCheck(alarm);
					break;
				}
				case MsgSender.MSG_STRING:{
					showToastMsg(msg);
					break;
				}
				case MsgSender.MSG_SHOWWIFI_DIALOG:
					showDialogWifi();
					break;
				case MsgSender.MSG_CANCEL_WIFI_DIALOG:
					cancelDialog();
					break;
				default:
					break;
				}
			}
		};      
	}
	
	public void onPause() { 
		super.onPause(); 
		
		MobclickAgent.onPause(this); 
	}
	
	public Handler getMsgHandler(){
	    return mMsgHandler;
	}
	 
	public void onExitApp(){
		if(mConfirmExit == true){
			clearToast();
			finish();
		}else{
			startExitCheck();
		}    		           
	}

	private void startIflytekTtsCheck(final String alarm) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				if (mMainApp.ismIflytekTts()) {
					alarmSpeaker(alarm);
				} else {
					mMainApp.setmIflytekTts(true);
				}
			}}, 1000);
	}
	
	private void startExitCheck(){
		showToastStr("再按一次退出程序");
		mConfirmExit = true;
		Timer timer = new Timer();
		timer.schedule(new TimerTask(){

			@Override
			public void run() {
				mConfirmExit = false;
				clearToast();
			}

		}, 3000);
	}

	public void clearToast(){
		if(mToast != null){
			mToast.cancel();
		}
	}

	private void showToastMsg(Message msg) {
		Bundle bundle = msg.getData();
		if (null == bundle) {
			return;
		}

		showToastStr(bundle.getString("message"));
	}
	
	protected void showToastStr(String str) {
		if (str == null) {
			return;
		}
		StringBuffer strBuf = new StringBuffer();
		strBuf.append(str);
		strBuf.append(" ");
		if (mToast == null) {
			mToast = Toast.makeText(mMainApp, strBuf.toString(), Toast.LENGTH_LONG);
		} else {
			mToast.cancel();
		}

		mToast.setText(str);
		
		mToast.show();
	}
	
	protected void showAboutDialog() {
		AlertDialog.Builder aboutDialog = null;
		String version = " ";
		String appName = " ";

		aboutDialog = new AlertDialog.Builder(this);
		version = mMainApp.getAppVersionName();
		appName = mMainApp.getAppName();

		aboutDialog.setTitle("欢迎使用" + appName + "_" + version);
		aboutDialog.setMessage("轻轻一“点”，享受生活。");

		aboutDialog.setNegativeButton("确定", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();
			}
		});

		aboutDialog.show();
	}
	
	private void cancelDialog() {
		if (mDialog == null) {
			return;
		}
		
		if (mDialog.isShowing()) {
			mDialog.cancel();
			mDialog = null;
		}
	}
	
	private void showDialogWifi() {
		boolean flag = mMainApp.checkWifiwork();

		if (!flag) {
			showWifiDialog("WIFI未连接！", "请开启WIFI网络连接");
		}
	}    

	private void showWifiDialog(String str, String msg) {
		if (mDialog != null) {
			return;
		}
		Builder b = new AlertDialog.Builder(this).setTitle(str).setMessage(msg);
		b.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
				mDialog.cancel();
				mDialog = null;
				Intent mIntent = new Intent("/");
				ComponentName comp = new ComponentName("com.android.settings", "com.android.settings.WirelessSettings");
				mIntent.setComponent(comp);
				mIntent.setAction("android.intent.action.VIEW");
				startActivity(mIntent);
			}
		}).setNeutralButton("取消", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.cancel();
				mDialog.cancel();
				mDialog = null;
			}
		});
		mDialog = b.create();
		mDialog.show();
	}

	private void alarmSpeaker(String alarm) {
		// 后台方式
		SynthesizerPlayer player = SynthesizerPlayer.createSynthesizerPlayer(this,"appid=4ed2ec63");
		SynthesizerPlayerListener playerListener = new SynthesizerPlayerListener() {

			@Override
			public void onBufferPercent(int arg0, int arg1, int arg2) {
			}

			@Override
			public void onEnd(SpeechError arg0) {
				if (mMainApp.getmMediaMgr().isTtsPause()) {
					mMainApp.getmMediaMgr().setTtsPause(false);
					mMainApp.getmMediaMgr().start();
				} else {
					/*OperaGpio15 ls = new OperaGpio15();
					boolean resp = ls.openGpio15();
					mMainApp.setmGpioIsOpen(resp);*/
				}
			}

			@Override
			public void onPlayBegin() {
			}

			@Override
			public void onPlayPaused() {
			}

			@Override
			public void onPlayPercent(int arg0, int arg1, int arg2) {
			}

			@Override
			public void onPlayResumed() {
			}

		};

		player.setVoiceName("vixy");
		player.playText(alarm,"ent=vivi21,bft=5",playerListener);
	}
	
	public abstract void refreshUi(Message msg);
}
