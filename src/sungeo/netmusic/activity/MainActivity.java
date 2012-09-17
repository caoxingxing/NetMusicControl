package sungeo.netmusic.activity;

import java.util.ArrayList;
import java.util.List;

import sungeo.netmusic.R;
import sungeo.netmusic.data.AlbumRecordInfo;
import sungeo.netmusic.data.MainApplication;
import sungeo.netmusic.unit.MainGridViewAdapter;
import sungeo.netmusic.unit.MsgSender;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.TextView;

import com.mobclick.android.MobclickAgent;
import com.mobclick.android.ReportPolicy;

/**
 * Ӧ�ó���������棬��ʾ����ר�����ƣ���ʵ�ֿ���ר���Ĳ��ŵȹ���
 * @author caoxingxing
 *
 */
public class MainActivity extends BaseActivity{
	private GridView 			myGrid;
	//private Button	 		record_btn;
	private TextView 			mCurSongName;
	private TextView 			mNextSongName;
	private List<String> 		mNameList 		 = new ArrayList<String>();
	private MainGridViewAdapter mMainGridAdapter = null;
	private final static int    ITEM_SETTING	 = 0;
	private final static int    ITEM_ABOUT		 = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadViewByOrientation();

		//Ӧ�ó������ÿ����Ϣ(����������Ϣ���Զ�����Ϣ���˳���Ϣ)ʱ�����������͵�������
		MobclickAgent.setDefaultReportPolicy(MainApplication.getInstance(), ReportPolicy.REALTIME);

		//�Զ�������ʾ
		MobclickAgent.update(this);

		MobclickAgent.onError(this);
		
		MobclickAgent.updateOnlineConfig(this);

		//���ڷ�WIFI�����¸���
		//MobclickAgent.setUpdateOnlyWifi(false);

		testDisplayResolution();
		//����ϵͳ�ģ���Ҫ¼������
		//initRecordBtn();

		mMainApp.registerReceiver();
		mMainApp.getmMediaMgr().initMediaPlayer();
		mMainApp.startAllTimer();

		initSongName();

		initGridView();

		//registerSensor();
	}

	/** ������Ļ������ز�ͬ��XML�ļ� */
	private void loadViewByOrientation() {
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {   
			setContentView(R.layout.main);
		} else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {   
			setContentView(R.layout.main_pro);
		}   
	}

	@Override
	public void onResume() {
		super.onResume();
		mMainApp.getmKeyguradLock().disableKeyguard();
		mMainApp.setmCurShowAlbumId(0);

		initAlbumName();	

		setCurDownloadIngAlbumName(mMainApp.getmCurDownloadAlbumId() - 1);
		refreshCtrlBtnState();
		refreshSongName();
	}

	public void onConfigurationChanged(Configuration newConfig) {   
		super.onConfigurationChanged(newConfig);   
		initSongName();
		initGridView();

		initAlbumName();	

		setCurDownloadIngAlbumName(mMainApp.getmCurDownloadAlbumId() - 1);

		refreshCtrlBtnState();
		refreshSongName();
	} 

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean ret = true;

		menu.add(0, ITEM_SETTING, ITEM_SETTING, "����");
		menu.add(0, ITEM_ABOUT, ITEM_ABOUT, "����");    

		return ret;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean ret = true;
		int itemId = item.getItemId();

		if (itemId == ITEM_SETTING) {
			Intent showNext = new Intent();
			showNext.setClass(this, SettingActivity.class);
			startActivity(showNext);
		} else if (itemId == ITEM_ABOUT){
			showAboutDialog();
		}
		return ret;
	}

	@TargetApi(5)
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.isLongPress()) {
			//onExitApp();
			Intent showNext = new Intent();
			showNext.setClass(this, LocalDownloadActivity.class);
			startActivity(showNext);
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	public void onStop() {
		super.onStop();
	}
	
	public void onDestroy() {
		BaseActivity.sCurActivity = null;
		super.onDestroy();
		mMainApp.getmMediaMgr().stop();
		mMainApp.getmMediaMgr().release();
		mMainApp.initLastPlaySongName();
		mMainApp.stopAllTimer();
		mMainApp.unRegisterReceiver();
		//mMainApp.mWifiLock.release();
		mMainApp.getmKeyguradLock().reenableKeyguard();
	}

	private void initGridView() {
		myGrid = (GridView) findViewById(R.id.main_gridview);
	}

	private void setCurDownloadIngAlbumName(int id) {
		mMainGridAdapter.setDownId(id);
		mMainGridAdapter.notifyDataSetChanged();
	}

	private void initAlbumName() {
		updateAlbumName();
		if (mMainGridAdapter == null) {
			mMainGridAdapter = new MainGridViewAdapter(this, mNameList);
			myGrid.setAdapter(mMainGridAdapter);
		} else {
			mMainGridAdapter.setDownId(-1);
			mMainGridAdapter.notifyDataSetChanged();
		}
	}

	private void initSongName() {
		mCurSongName = (TextView)findViewById(R.id.curplay_songname);
		mNextSongName = (TextView)findViewById(R.id.nextplay_songname);

		mCurSongName.setText("");
		mNextSongName.setText("");
	}

	private void setCurSongName(String curName) {
		if (curName == null) {
			return;
		}
		StringBuffer str = new StringBuffer();
		int pos = curName.lastIndexOf(".");
		if (pos == -1) {
			str.append(curName);
		} else {
			str.append(curName.substring(0, pos));
		}

		mCurSongName.setText(str.toString());
	}

	private void setNextSongName(String nextName) {
		if (nextName == null) {
			return;
		}
		StringBuffer str = new StringBuffer();
		int pos = nextName.lastIndexOf(".");
		if (pos == -1) {
			str.append(nextName);
		} else {
			str.append(nextName.substring(0, pos));
		}

		mNextSongName.setText(str.toString());
	}

	/*	private void initRecordBtn() {
		record_btn = (Button) findViewById(R.id.btn_record);
		if (record_btn != null) {
			record_btn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent showNext = new Intent();
					showNext.setClass(SungeoEntry.this, RecordManagerActivity.class);
					startActivity(showNext);
				}});
		}
	}*/

	public void respondCtrl(int id) {
		//doSpeak();
		if (id < 0 || id >= MainApplication.ALBUM_NUM) {
			return;
		}

		AlbumRecordInfo album = (AlbumRecordInfo)mMainApp.getmAllAlbum().get(id);
		if (album != null && (album.isEmpty() || album.getSongCount() == 0)) {
			mMainApp.getmMsgSender().sendStrMsg("��ר��Ϊ�գ��������ء�");
			return;
		}

		/*if (mMainApp.getmMediaMgr().isRecord()) {
			mMainApp.getmMsgSender().sendStrMsg("���ڲ���¼��");
			return;
		}*/

		int albumId = mMainApp.getmCurPlayAlbumId();

		if (albumId == id + 1) {//ͬһר���Ĳ��ź���ͣ
			if (mMainApp.getmMediaMgr().isPlaying()) {
				mMainApp.getmMediaMgr().pause(true);
			} else {
				mMainApp.getmMediaMgr().start();
			}
		} else {//ר���Ĳ�������ͣ�л�
			int songIndex = album.getCurPlayIndex();
			mMainApp.getmMediaMgr().localPlay(id + 1, songIndex, true);
			mMainApp.setmCurPlayAlbumId(id + 1);
			mMainApp.getmMsgSender().sendRefreshPlayState();
		}
		album = null;
	}

	public void showSongList(int id) {
		if (id < 0 || id >= MainApplication.ALBUM_NUM) {
			return;
		}
		Intent showNext = new Intent();
		showNext.setClass(this, SongListActivity.class);
		startActivity(showNext);
		mMainApp.setmCurShowAlbumId(id + 1);
	}

	private void updateAlbumName() {
		mNameList.clear();

		int len = mMainApp.getmAllAlbum().size();
		for (int i = 0; i < len; i ++) {
			AlbumRecordInfo album = (AlbumRecordInfo)mMainApp.getmAllAlbum().get(i);
			if (album != null && album.getAlbumName() != null) {
				StringBuffer str = new StringBuffer();
				str.append(album.getAlbumName());
				str.append("(");
				str.append(album.getSongCount());
				str.append(")");
				mNameList.add(str.toString());
			}
		}
	}

	private void refreshSongName() {
		if (checkPlayState()) {
			setSongName();
		} else {
			initSongName();
		}
	}

	private boolean checkPlayState() {
		if (MainApplication.isPlaying) {
			return true;
		}

		return false;
	}

	private void refreshCtrlBtnState() {
		int id = mMainApp.getmCurPlayAlbumId() - 1;
		/*MainApplication.getInstance().getmMsgSender().setDebugLevel(6);
		MainApplication.getInstance().getmMsgSender().OutputLogToSD("refreshCtrlBtnState�����õ���getmCurPlayAlbumId()Ϊ��"+String.valueOf(id+1));
		MainApplication.getInstance().getmMsgSender().setDebugLevel(1);*/
		myGrid.getAdapter();
		if (MainApplication.isPlaying) {
		    mMainGridAdapter.setPlayingId(id);
		} else {
		    mMainGridAdapter.setPlayingId(-1);
		}
		
		mMainGridAdapter.notifyDataSetChanged();
	}

	private void setSongName() {
		setCurSongName(mMainApp.getCurSongName());
		setNextSongName(mMainApp.getNextSongName());
	}

	@Override
	public void refreshUi(Message msg) {
		if (msg == null) {
			return;
		}

		int type = msg.what;
		switch (type) {
		case MsgSender.MSG_REFRESH_PLAYSTATE:
			refreshCtrlBtnState();
			refreshSongName();
			break;
		case MsgSender.MSG_INITSINGLE_ALBUM:	
			initAlbumName();
			refreshSongName();
			break;
		case MsgSender.MSG_DOWNLOAD_ING:
			setCurDownloadIngAlbumName(mMainApp.getmCurDownloadAlbumId() - 1);
			break;
		default:
			break;
		}
	}

	/*	private void registerSensor() {
		SensorManager sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		Sensor proximity = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		if (proximity == null) {
			return;
		}

		//float max = proximity.getMaximumRange();
		// ���崫�����¼�������
		SensorEventListener acceleromererListener = new SensorEventListener() {

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
			}

			@Override
			public void onSensorChanged(SensorEvent event) {
				int len = event.values.length;
				if (len <= 0) {
					return;
				}

				float value = event.values[0];
				//�˴�������ֻ�õ�event.values�ĵ�һ����������Щ�ɱ�ʾ���룬��Щֻ��ʾԶ��
				//Զ��Ϊĳ������������Ϊ0
				if (value == 0.0) {
					if (!mMainApp.getmMediaMgr().isPlaying()) {
						mMainApp.getmMediaMgr().start();
					} else {
						mMainApp.getmMediaMgr().pause(true);
					}
				} else {
					if (mMainApp.getmMediaMgr().isPlaying()) {
						mMainApp.getmMediaMgr().pause(false);
					}
				}
			}

		};

		//�ڴ�������������ע�������
		sm.registerListener(acceleromererListener, proximity, SensorManager.SENSOR_DELAY_NORMAL);
	}*/

	private void testDisplayResolution() {
		DisplayMetrics mMetrics = new DisplayMetrics();

		Display localDisplay = getWindowManager().getDefaultDisplay();
		localDisplay.getMetrics(mMetrics);

		mMainApp.mMetrics = mMetrics;
		float screenWidth = mMetrics.widthPixels * mMetrics.density;
		float screenHeight = mMetrics.heightPixels * mMetrics.density;

		Log.i("this screen width is-----", String.valueOf(screenWidth));
		Log.i("this screen high is-----", String.valueOf(screenHeight));
	}
}