package sungeo.netmusic.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import sungeo.netmusic.R;
import sungeo.netmusic.data.AlbumInfoDb;
import sungeo.netmusic.data.AlbumRecordInfo;
import sungeo.netmusic.manager.MediaMgr;
import sungeo.netmusic.unit.MsgSender;
import sungeo.netmusic.unit.SongAdapter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class SongListActivity extends BaseActivity {
	private int 	 		mCurPlay;
	private ListView 		mSongList;
	private SongAdapter 	mSongAdapter;
	private Button 			mSinModeBtn;
	private Button			mOrdModeBtn;
	private Button			mRadModeBtn;
	private Button         mSingleModeBtn;
	private Button 			mDelSongBtn;
	private List<String> 	mListItem;
	private MenuItem 		mMenuItem = null;
	private AlbumRecordInfo mCurAlbum;
	
	private static final int ITEM_DEL	  = 0;
	private static final int CTRL_BTN_NUM = 5;
	
	private Button[] 	mCtrlBtn = new Button[CTRL_BTN_NUM];
	private int[] 		mLayoutId = {R.id.volume_addbtn,
									 R.id.ctrl_prebtn,
									 R.id.ctrl_playbtn,
									 R.id.ctrl_nextbtn,
									 R.id.volume_decbtn};

	private OnClickListener mCtrlListener = new OnClickListener() {
		@Override
		public void onClick(View btn) {
			int id = btn.getId();
			respondEvent(id);
		}
	};
	private OnClickListener mModeBtnListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			respondBtnEvent(v.getId());
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		loadViewByOrientation();

		int curAlbumPos = mMainApp.getmCurShowAlbumId();
		mCurAlbum = (AlbumRecordInfo)mMainApp.getmAllAlbum().get(curAlbumPos - 1);
		mCurPlay = mCurAlbum.getCurPlayIndex();

		init();
	}

	public void onConfigurationChanged(Configuration newConfig) {   
		super.onConfigurationChanged(newConfig);   
		loadViewByOrientation();
		init();
	} 

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			mMainApp.setmCurShowAlbumId(0);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean ret = true;

		int orientation = getResources().getConfiguration().orientation;
		if (orientation == Configuration.ORIENTATION_PORTRAIT && 
				mMainApp.getmCurShowAlbumId() == AlbumInfoDb.SIXTH_ALBUM_ID) {
			menu.add(0, ITEM_DEL, ITEM_DEL, "删除");
			mDelSongBtn = (Button) findViewById(R.id.music_delbtn);
			mDelSongBtn.setVisibility(View.GONE);
		}

		return ret;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean ret = true;
		int orientation = getResources().getConfiguration().orientation;
		if (orientation	== Configuration.ORIENTATION_PORTRAIT && 
				mMainApp.getmCurShowAlbumId() == AlbumInfoDb.SIXTH_ALBUM_ID) {
			int id = item.getItemId();
			if (id == ITEM_DEL) {
				mMenuItem = item;
				musicListDel();
			}
		}

		return ret;
	}

	public void onDestroy() {
		super.onDestroy();
		mListItem.clear();
		mSongList = null;
		mListItem = null;
		mCurAlbum = null;
		mMenuItem = null;
		mSongAdapter = null;
		mSongList = null;
		
		mSinModeBtn = null;
		mOrdModeBtn = null;
		mRadModeBtn = null;
		mDelSongBtn = null;
		mSingleModeBtn = null;
		mCtrlBtn = null;
	}
	
	private void loadViewByOrientation() {
		if (getResources().getConfiguration().orientation 
				== Configuration.ORIENTATION_LANDSCAPE) {   
			setContentView(R.layout.songlist);
		} else if (getResources().getConfiguration().orientation 
				== Configuration.ORIENTATION_PORTRAIT) {   
			setContentView(R.layout.songlist_pro);
		}   
	}

	private void init() {

		initAlbumTitle();

		initBackBtn();

		initCtrlBtn();
		initModeBtn();

		initPlayMode();

		initSongList();

		initDelBtn();
	}

	private void initDelBtn() {
		mDelSongBtn = (Button) findViewById(R.id.music_delbtn);

		mDelSongBtn.setBackgroundResource(R.drawable.deletebtn);
		if (getResources().getConfiguration().orientation 
				== Configuration.ORIENTATION_PORTRAIT) {
			mDelSongBtn.setVisibility(View.GONE);
			return;
		}

		if (mMainApp.getmCurShowAlbumId() == AlbumInfoDb.SIXTH_ALBUM_ID) {
			mDelSongBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					musicListDel();
				}
			});
		} else {
			mDelSongBtn.setVisibility(View.GONE);
		}
	}

	private void musicListDel() {
		if (mSongAdapter == null) {
			return;
		}
		if(mSongAdapter.ismDelSong()){
			mSongAdapter.setmDelSong(false);
			mSongAdapter.notifyDataSetChanged();
			mDelSongBtn.setBackgroundResource(R.drawable.deletebtn);
			setMenuItem("删除");
		} else {
			mSongAdapter.setmDelSong(true);
			mDelSongBtn.setBackgroundResource(R.drawable.cancelbtn);
			setMenuItem("取消");
		}
		mSongAdapter.notifyDataSetChanged();
	}

	private void setMenuItem(String str) {
		if (mMenuItem == null || str == null) {
			return;
		}
		
		mMenuItem.setTitle(str);
	}
	
	private void initAlbumTitle() {
		TextView title = (TextView) findViewById(R.id.album_title_text);

		title.setText(mCurAlbum.getAlbumName());
	}

	private void initModeBtn() {
		mSinModeBtn = (Button) findViewById(R.id.playsmode_btn);
		mOrdModeBtn = (Button) findViewById(R.id.playomode_btn);
		mRadModeBtn = (Button) findViewById(R.id.playrmode_btn);

		mSinModeBtn.setOnClickListener(mModeBtnListener);
		mOrdModeBtn.setOnClickListener(mModeBtnListener);
		mRadModeBtn.setOnClickListener(mModeBtnListener);
		mSingleModeBtn = (Button) findViewById(R.id.playssmode_btn);
		if (mSingleModeBtn != null) {
		    mSingleModeBtn.setOnClickListener(mModeBtnListener);
		}
	}

	private void initCtrlBtn() {
		int len = mCtrlBtn.length;
		for (int i = 0; i < len; i++) {
			mCtrlBtn[i] = (Button) findViewById(mLayoutId[i]);
			mCtrlBtn[i].setOnClickListener(mCtrlListener);
		}

		refreshPlayIcon();
	}

	private void respondEvent(int id) {
		int count = mCurAlbum.getSongCount();
		switch (id) {
		case R.id.volume_addbtn: {
			mMainApp.getmMediaMgr().volumeAdd();
			break;
		}
		case R.id.volume_decbtn: {
			mMainApp.getmMediaMgr().volumeDec();
			break;
		}
		case R.id.ctrl_nextbtn:
			if (count == 0) {
				mMainApp.getmMsgSender().sendStrMsg("当前专辑为空，请下载");
				return;
			}
			playNext();
			break;
		case R.id.ctrl_prebtn:
			if (count == 0) {
				mMainApp.getmMsgSender().sendStrMsg("当前专辑为空，请下载");
				return;
			}
			playPre();
			break;
		case R.id.ctrl_playbtn:
			if (count == 0) {
				mMainApp.getmMsgSender().sendStrMsg("当前专辑为空，请下载");
				return;
			}
			playOrPause();
			break;
		default:
			break;
		}
	}

	private void playOrPause() {
		int albumId = mMainApp.getmCurShowAlbumId();
		if (albumId == mMainApp.getmCurPlayAlbumId()) {
			mMainApp.getmMediaMgr().pause(true);
		} else {
			mMainApp.getmMediaMgr().localPlay(mMainApp.getmCurShowAlbumId(), mCurPlay, true);
			mCurPlay = mCurAlbum.getCurPlayIndex();
		}

		refreshPlayIcon();
	}

	private void playRandom() {
		Random random = new Random();
		int count = 0;
		String[] songName = mCurAlbum.getSongName();
		if (songName != null) {
			count = songName.length;
		}
		if (count == 0 || count == 1) {
			return;
		}
		int id = random.nextInt(count);
		if (id == mCurPlay) {
			id = random.nextInt(count);
		}

		playSel(id);
	}

	private void playSel(int pos) {
		int albumId = mMainApp.getmCurShowAlbumId();

		if (albumId == mMainApp.getmCurPlayAlbumId()) {//同一专辑的播放和暂停
			if (mCurAlbum.getCurPlayIndex() == pos) {
				mMainApp.getmMediaMgr().pause(true);
			} else {
				mMainApp.getmMediaMgr().localPlay(albumId, pos, true);
			}
		} else {//专辑的播放与暂停切换
			mMainApp.getmMediaMgr().localPlay(albumId, pos, true);
		}
		
		mCurPlay = mCurAlbum.getCurPlayIndex();
	}

	private void refreshPlayIcon() {
		boolean ret = false;

		if (mMainApp.getmCurPlayAlbumId() != mMainApp.getmCurShowAlbumId()) {
			ret = false;
		} else {
			ret = mMainApp.getmMediaMgr().isPlaying();
		}

		if (ret) {
			mCtrlBtn[2].setBackgroundResource(R.drawable.pausepanel);
		} else {
			mCtrlBtn[2].setBackgroundResource(R.drawable.playpanel);
		}
	}

	private void playNext() {
		mMainApp.getmMediaMgr().localPlayNext();

		mCurPlay = mMainApp.getmCurSondIndex();
		setSelectItem(mCurPlay);
		refreshPlayIcon();
	}

	private void playPre() {
		mMainApp.getmMediaMgr().localPlayPre();
		mCurPlay = mMainApp.getmCurSondIndex();
		setSelectItem(mCurPlay);
		refreshPlayIcon();
	}

	private void initBackBtn() {
		Button backBtn = (Button) findViewById(R.id.backmain_btn);
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void initPlayMode() {
		if (mCurAlbum.isEmpty()) {
			return;
		}

		mOrdModeBtn.setBackgroundResource(R.drawable.repeat_normal);
		mRadModeBtn.setBackgroundResource(R.drawable.random_normal);
		mSinModeBtn.setBackgroundResource(R.drawable.repeatone_normal);
		if (mSingleModeBtn != null) {
		    mSingleModeBtn.setBackgroundResource(R.drawable.single_normal);
		}

		byte mode = mCurAlbum.getPlayMode();
		switch (mode) {
		case MediaMgr.PLAY_MODE_ORDER:
			mOrdModeBtn.setBackgroundResource(R.drawable.repeat_clicked);
			break;
		case MediaMgr.PLAY_MODE_RANDOM:
			mRadModeBtn.setBackgroundResource(R.drawable.random_clicked);
			break;
		case MediaMgr.PLAY_MODE_SINGLE:
			mSinModeBtn.setBackgroundResource(R.drawable.repeatone_clicked);
			break;
		case MediaMgr.PLAY_MODE_SINGLE_END:
		    if (mSingleModeBtn != null) {
		        mSingleModeBtn.setBackgroundResource(R.drawable.single_clicked);
		    }
		    break;
		default:
			mOrdModeBtn.setBackgroundResource(R.drawable.repeat_clicked);
			break;
		}
	}

	private void respondBtnEvent(int checkedId) {
		if (mCurAlbum.isEmpty()) {
			return;
		}

		switch (checkedId) {
		case R.id.playomode_btn: {
			mCurAlbum.setPlayMode(MediaMgr.PLAY_MODE_ORDER);
			mOrdModeBtn.setBackgroundResource(R.drawable.repeat_clicked);
			mRadModeBtn.setBackgroundResource(R.drawable.random_normal);
			mSinModeBtn.setBackgroundResource(R.drawable.repeatone_normal);
			if (mSingleModeBtn != null) {
			    mSingleModeBtn.setBackgroundResource(R.drawable.single_normal);
			}
			if (mMainApp.getmCurPlayAlbumId() == mMainApp.getmCurShowAlbumId()) {
				mMainApp.getmMediaMgr().setPlayMode(MediaMgr.PLAY_MODE_ORDER);
				mMainApp.getmMediaMgr().computeNextSongId();
			}
			
			//此处必须有空格，否则在c8500手机上，会显示不全四个字，具体原因尚不明确
			mMainApp.getmMsgSender().sendStrMsg("循环播放");		
		}
		break;
		case R.id.playsmode_btn: {
			mCurAlbum.setPlayMode(MediaMgr.PLAY_MODE_SINGLE);
			mOrdModeBtn.setBackgroundResource(R.drawable.repeat_normal);
			mRadModeBtn.setBackgroundResource(R.drawable.random_normal);
			mSinModeBtn.setBackgroundResource(R.drawable.repeatone_clicked);
			if (mSingleModeBtn != null) {
			    mSingleModeBtn.setBackgroundResource(R.drawable.single_normal);
			}
			if (mMainApp.getmCurPlayAlbumId() == mMainApp.getmCurShowAlbumId()) {
				mMainApp.getmMediaMgr().setPlayMode(MediaMgr.PLAY_MODE_SINGLE);
				mMainApp.getmMediaMgr().computeNextSongId();
			}
			
			mMainApp.getmMsgSender().sendStrMsg("单曲循环");
		}
		break;
		case R.id.playrmode_btn: {
			mCurAlbum.setPlayMode(MediaMgr.PLAY_MODE_RANDOM);
			mOrdModeBtn.setBackgroundResource(R.drawable.repeat_normal);
			mRadModeBtn.setBackgroundResource(R.drawable.random_clicked);
			mSinModeBtn.setBackgroundResource(R.drawable.repeatone_normal);
			if (mSingleModeBtn != null) {
			    mSingleModeBtn.setBackgroundResource(R.drawable.single_normal);
			}
			mMainApp.getmMsgSender().sendStrMsg("随机播放");
			if (mMainApp.getmCurPlayAlbumId() == 0) {
				playRandom();
			} else if (mMainApp.getmCurPlayAlbumId() == mMainApp.getmCurShowAlbumId()) {
				mMainApp.getmMediaMgr().setPlayMode(MediaMgr.PLAY_MODE_RANDOM);
				playRandom();
				mMainApp.getmMediaMgr().computeNextSongId();
			}
		}
		break;
		case R.id.playssmode_btn: {
		    mCurAlbum.setPlayMode(MediaMgr.PLAY_MODE_SINGLE_END);
            mOrdModeBtn.setBackgroundResource(R.drawable.repeat_normal);
            mRadModeBtn.setBackgroundResource(R.drawable.random_normal);
            mSinModeBtn.setBackgroundResource(R.drawable.repeatone_normal);
            if (mSingleModeBtn != null) {
                mSingleModeBtn.setBackgroundResource(R.drawable.single_clicked);
            }
            mMainApp.getmMsgSender().sendStrMsg("单曲播放");
            if (mMainApp.getmCurPlayAlbumId() == mMainApp.getmCurShowAlbumId()) {
                mMainApp.getmMediaMgr().setPlayMode(MediaMgr.PLAY_MODE_SINGLE_END);
            }
		}
		break;
		default:
			break;
		}
	}

	private void initSongList() {
		mSongList = (ListView) findViewById(R.id.listView1);

		String[] songName = mCurAlbum.getSongName();
		// 生成动态数组，加入数据
		mListItem = new ArrayList<String>();
		int musicCount = 0;
		if (songName != null) {
			musicCount = songName.length;
		}
		for (int i = 0; i < musicCount; i++) {
			if (songName[i] == null) {
				continue;
			}
			int pos = songName[i].lastIndexOf(".");
			if (pos == -1) {
				mListItem.add(songName[i]);
			} else {
				mListItem.add(songName[i].substring(0, pos));
			}
		}

		String[] singer = mCurAlbum.getSinger();
		List<String> singerList = new ArrayList<String>();
		int singerNum = 0;
		if (singer != null) {
			singerNum = singer.length;
		}
		for (int i = 0; i < singerNum; i ++) {
			if (singer[i] == null) {
				continue;
			}

			singerList.add(singer[i]);
		}

		if (musicCount != 0) {
			mSongAdapter = new SongAdapter(this, mListItem);

			if (singerNum != 0 && singerList != null && singerList.size() != 0) {
				mSongAdapter.setSinger(singerList);
			}

			// 添加并且显示
			mSongList.setAdapter(mSongAdapter);
			addSongListEvent();
			setSelectItem(mCurPlay);
		} else {
			mSongList.setAdapter(null);
		}
	}

	private void setSelectItem(int position) {
		int count = mSongAdapter.getCount();
		if (position < 0 || position >= count) {
			return;
		}
		mSongList.setSelection(position);
		mSongAdapter.setSelectItem(position);
		mSongAdapter.notifyDataSetInvalidated();
	}

	private void removeBlackBG(View view) {
		int len = mSongList.getChildCount();

		for (int i = 0; i < len; i ++) {
			RelativeLayout v = (RelativeLayout)mSongList.getChildAt(i);

			if (v != view) {
				v.setBackgroundColor(Color.TRANSPARENT);
			}
		}
	}

	private void addSongListEvent() {
		mSongList.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					removeBlackBG(v);
				} else if (event.getAction() == MotionEvent.ACTION_CANCEL
						|| event.getAction() == MotionEvent.ACTION_UP) {
					removeBlackBG(null);
				}
				return false;
			}});

		mSongList.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long rowid) {
				if (position < 0) {
					return;
				}

				mSongAdapter.setSelectItem(position);
				mSongAdapter.notifyDataSetInvalidated();

				if(mSongAdapter.ismDelSong()){
					if (mMainApp.getmCurPlayAlbumId() == AlbumInfoDb.SIXTH_ALBUM_ID 
						&& mCurPlay == position) {
						mMainApp.getmMsgSender().sendStrMsg("不能删除正在播放的歌曲");
						return;
					}
					String musicName = mCurAlbum.getSongName()[position];
					if(mMainApp.getmAlbumMgr().deleteSong(musicName)) {
						mListItem.remove(position);
						mSongAdapter.notifyDataSetChanged(); 
					}			
				} else {
					playSel(position);
				}			
			}
		});
	}

	@Override
	public void refreshUi(Message msg) {
		if (msg == null) {
			return;
		}

		int type = msg.what;
		switch (type) {
		case MsgSender.MSG_AUTOPLAY:
			if (mMainApp.getmCurPlayAlbumId() == mMainApp.getmCurShowAlbumId()) {
				int curId = msg.arg1;
				setSelectItem(curId);
			}
			break;
		case MsgSender.MSG_INITSINGLE_ALBUM: {
			int id = msg.arg1;
			if (id == mMainApp.getmCurShowAlbumId()) {
				init();
			}
		}
		break;
		case MsgSender.MSG_REFRESH_PLAYSTATE:
			refreshPlayIcon();
			break;
		default:
			break;
		}
	}
}
