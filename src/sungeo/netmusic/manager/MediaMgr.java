package sungeo.netmusic.manager;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


import sungeo.netmusic.activity.BaseActivity;
import sungeo.netmusic.data.AlbumRecordInfo;
import sungeo.netmusic.data.MainApplication;
import sungeo.netmusic.gpio.OperaGpio15;
import sungeo.netmusic.netbase.ClientSocket;
import android.app.Service;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;

public class MediaMgr {
	public static final byte	PLAY_MODE_ORDER		= 0x30;
	public static final byte 	PLAY_MODE_RANDOM	= 0x31;
	public static final byte	PLAY_MODE_SINGLE	= 0x32;
	
	public static final byte STATE_PLAY = 0x01;
	public static final byte STATE_PAUSE = 0x02;
	public static final byte STATE_STOP = 0x00;
	public static final byte STATE_VOLUME_ADD = 0x03;
	public static final byte STATE_VOLUME_DEC = 0x04;
	public static final byte STATE_NEXT = 0x05;
	public static final byte STATE_PRE = 0x06;
	public static final byte STATE_SILENT = 0x07;
	public static final byte STATE_EXIT_SILENT = 0x08;
	public static final byte STATE_EFFERT = 0x09;

	private int 				mPlayMode;
	private int 				mErrorPlay 			= -1;
	private int					mErrorIndex			= -1;
	private boolean 			mTtsPause 			= false;
	private boolean 			mIsRecord 			= false;
	private Timer				mDelayErrorPlayTimer= null;
	private MediaPlayer 		mAudioCtrl;
	private AudioManager 		mAudioManager		= null; //音频
	private AlbumRecordInfo 	mCurAlbum;
	private MainApplication 	mMainApp;

	public MediaMgr() {
		mMainApp = MainApplication.getInstance();
		mAudioCtrl = new MediaPlayer();
		mAudioManager=(AudioManager)mMainApp.getSystemService(Service.AUDIO_SERVICE);
	}

	public void initMediaPlayer() {
		if (mAudioCtrl == null) {
			mAudioCtrl = new MediaPlayer();
		}
	}
	
	private void initAllIden() {
		mMainApp.setmCurPlayAlbumId(0);
		mCurAlbum = null;
	}

	/**
	 * 重新获得当前播放的歌曲ID，当删除歌曲时用到，根据当前播放的歌曲名称来获得ID，
	 * 在删除歌曲时，当前播放的歌曲是不能删除的
	 */
	public void initCurPlayIndex() {
		if (mMainApp.getmCurShowAlbumId() != 0) {
			mCurAlbum = mMainApp.getmAllAlbum().get(mMainApp.getmCurShowAlbumId() - 1);
		}

		if (mMainApp.getmLastPlaySongName() == null) {
			return;
		}

		int count = 0;
		if (mCurAlbum.getSongName() != null) {
			count = mCurAlbum.getSongName().length;
		}

		for (int i = 0; i < count; i ++) {
			if (mCurAlbum.getSongName()[i] == null) {
				continue;
			}

			if (mCurAlbum.getSongName()[i].equals(mMainApp.getmLastPlaySongName())) {
				mCurAlbum.setCurPlayIndex(i);
				mMainApp.setmCurSondIndex(i);
				break;
			}
		}
	}

	public void computeNextSongId() {
		int tmp_PlayId;
		int curIndex = mMainApp.getmCurSondIndex();
		if (mMainApp.getmCurShowAlbumId() != 0) {
			mCurAlbum = mMainApp.getmAllAlbum().get(mMainApp.getmCurShowAlbumId() - 1);
		}

		int count = mCurAlbum.getSongCount();
		switch (mPlayMode) {
		case MediaMgr.PLAY_MODE_ORDER: {
			if(curIndex == count - 1) {
				tmp_PlayId = 0;
			}
			else {
				tmp_PlayId = curIndex + 1;
			}
			mMainApp.setmNextSongIndex(tmp_PlayId);
		}
		break;
		case MediaMgr.PLAY_MODE_RANDOM: {
			if(count > 0) {
				Random random = new Random();
				int id = random.nextInt(count);
				if (id == curIndex) {
					id = random.nextInt(count);
				}
				mMainApp.setmNextSongIndex(id);
				random = null;
			}
		}
		break;
		case MediaMgr.PLAY_MODE_SINGLE: {
			mMainApp.setmNextSongIndex(curIndex);
		}
		break;
		default:
			break;
		}
	}

	private boolean isSameSong(int albumId, int songIndex) {
		boolean flag = false;
		String name = mMainApp.getmLastPlaySongName();
		String[] songName = null;
		if (mCurAlbum != null) {
			songName = mCurAlbum.getSongName();
		} else {
			return flag;
		}

		int count = 0;
		if (songName != null) {
			count = songName.length;
		} else {
			return flag;
		}

		if (songIndex < 0 || songIndex >= count) {
			return flag;
		}

		flag = albumId == mMainApp.getmLastPlayAlbumId() 
		&& songIndex == mCurAlbum.getCurPlayIndex()
		&& name != null && songName != null
		&& name.equals(songName[songIndex]);
		return flag;
	}

	public boolean localPlay(String path, boolean isFinal) {
		if (isPlaying()) {
			pause(false);
			setTtsPause(true);
		}

		return play(path, false);
	}
	
	public boolean localPlay(int albumId, int songIndex, boolean sync) {
		if (mMainApp.getExtStoDir() == null) {
			mMainApp.getmMsgSender().sendStrMsg("请检查SD卡是否存在");
			return false;
		}

		int size = 0;
		size = mMainApp.getmAllAlbum().size();
		if (albumId <= 0 || albumId > size) {
			return false;
		}
		mCurAlbum = (AlbumRecordInfo)mMainApp.getmAllAlbum().get(albumId - 1);

		String[] songName = null;
		if (mCurAlbum != null) {
			songName = mCurAlbum.getSongName();
		}

		int count = 0;
		if (songName != null) {
			count = songName.length;
		}

		if (songIndex < 0 || songIndex >= count) {
			return false;
		}

		setPlayMode(mCurAlbum.getPlayMode());
		if (mErrorIndex == -1) {
			setmErrorIndex(songIndex);
		}

		boolean flag = false;
		if (isSameSong(albumId, songIndex)) {
			flag = play(sync);
		}

		if (!flag) {
			StringBuffer fullName = new StringBuffer();
			fullName.append(mMainApp.getExtStoDir());
			fullName.append("/");
			fullName.append(mCurAlbum.getAlbumName());
			fullName.append("/");
			fullName.append(songName[songIndex]);
			songName = null;
			flag = isSongExist(fullName.toString());
			if (flag) {
				flag = play(fullName.toString(), sync);
			}
		}

		if (flag) {
			clearToast();
			mMainApp.setmCurSondIndex(songIndex);
			mCurAlbum.setCurPlayIndex(songIndex);
			mMainApp.setmCurPlayAlbumId(albumId);
			/*MainApplication.getInstance().getmMsgSender().setDebugLevel(6);
			MainApplication.getInstance().getmMsgSender().OutputLogToSD("保存的setmCurPlayAlbumId值为："+String.valueOf(albumId));
			MainApplication.getInstance().getmMsgSender().setDebugLevel(1);*/
			mMainApp.setmLastPlayAlbumId(albumId);

			//播放成功，初始化mErrorPlay
			setmErrorPlay(-1);
			setmErrorIndex(-1);
			cancelDelayErrorTimer();
			// 根据播放模式，设置下首歌曲播放ID
			computeNextSongId();

			mMainApp.getmMsgSender().sendRefreshPlayState();
			mMainApp.getmMsgSender().sendAutoPlay(songIndex);
		}
		return flag;
	}

	private void clearToast() {
		if (BaseActivity.sCurActivity == null) {
			return;
		}

		BaseActivity.sCurActivity.clearToast();
	}

	private boolean play(boolean sync) {
		if (mMainApp.ismGpioIsOpen() && mMainApp.IsDeviceModel()) {
			//加判断的原因是为了防止在不同专辑间切换播放时，不频繁操作GPIO口
			closeGpio15();
		}
		
		try {
			mAudioCtrl.start();
		} catch (IllegalStateException e) {
			return false;
		}
		MainApplication.isPlaying = true;
		MainApplication.isPause = false;

		if (sync && isPlaying()) {
			//同步状态
			mMainApp.getmClientSocket().startConnectGateway(ClientSocket.SYNC_STATE, getSyncParams(STATE_PLAY));
		}

		return true;
	}

	/**
	 * 
	 * @param path
	 * @param sync 是否发送状态同步命令，因为播放录音也会用这个接口
	 */
	public boolean play(String path, boolean sync) {
		boolean flag = false;
		mAudioCtrl.reset();
		mAudioCtrl.release();
		mAudioCtrl = null;
		mAudioCtrl = new MediaPlayer();

		try {
			mAudioCtrl.setDataSource(path);
			mMainApp.setmLastPlaySongName(path);
			mAudioCtrl.prepare();
			boolean ret = sync && !mIsRecord && mMainApp.getmConfig().getSelfAddr() != 0;
			flag = play(ret);
		} catch (IllegalArgumentException e) {
			delayErrorPlayNext(sync);
			mMainApp.getmMsgSender().sendStrMsg("播放出错，尝试播放下一首");
		} catch (IllegalStateException e) {
			delayErrorPlayNext(sync);
			mMainApp.getmMsgSender().sendStrMsg("播放出错，尝试播放下一首");
		} catch (IOException e) {
			delayErrorPlayNext(sync);
			mMainApp.getmMsgSender().sendStrMsg("播放出错，尝试播放下一首");
		}

		addMediaPlayerEvent();
		return flag;
	}

	private boolean isSongExist(String path) {
		if (path == null) {
			return false;
		}
		File file = new File(path);
		if (!file.exists()) {
			mMainApp.getmMsgSender().sendStrMsg("歌曲不存在，请重新下载");
			return false;
		}

		return true;
	}

	private void addMediaPlayerEvent() {
		mAudioCtrl.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				if (mIsRecord) {
					mAudioCtrl.reset();
					mIsRecord = false;
				} else {
					playNextByMode();
				}
			}});

		mAudioCtrl.setOnErrorListener(new OnErrorListener() {

			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				MainApplication.isPause = false;
				return true;
			}});
	}

	private void playNextByMode() {
		switch (mPlayMode) {
		case MediaMgr.PLAY_MODE_ORDER: {
			mAudioCtrl.setLooping(true);
			autoPlayNext();
		}
		break;
		case MediaMgr.PLAY_MODE_RANDOM: {
			mAudioCtrl.setLooping(false);

			localPlay(mCurAlbum.getPosition(), mMainApp.getmNextSongIndex(), true);
		}
		break;
		case MediaMgr.PLAY_MODE_SINGLE: {
			mAudioCtrl.setLooping(false);
			localPlay(mCurAlbum.getPosition(), mMainApp.getmCurSondIndex(), true);
		}
		break;
		default: {
			mAudioCtrl.setLooping(true);
			autoPlayNext();
		}
		break;
		}
	}

	private void cancelDelayErrorTimer() {
		if (mDelayErrorPlayTimer != null) {
			mDelayErrorPlayTimer.cancel();
			mDelayErrorPlayTimer = null;
		}
	}

	private void delayErrorPlayNext(final boolean sync) {
		mMainApp.setmCurPlayAlbumId(0);
		mMainApp.getmMsgSender().sendRefreshPlayState();

		//由于调用此函数用到低估递归，考虑到最坏的情况，这里加个延时执行
		if (mDelayErrorPlayTimer != null) {
			mDelayErrorPlayTimer.cancel();
			mDelayErrorPlayTimer = null;
		}
		mDelayErrorPlayTimer = new Timer();

		mDelayErrorPlayTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				mDelayErrorPlayTimer = null;
				errorPlayNextByMode(sync);
			}}, 1000);
	}

	private void errorPlayNextByMode(boolean sync) {
		int count = mCurAlbum.getSongCount();
		if (getmErrorPlay() == -1) {
			setmErrorPlay(getmErrorIndex());
		}

		setmErrorPlay(getmErrorPlay() + 1);
		if (getmErrorPlay() >= count){
			setmErrorPlay(0);
		}

		if (getmErrorPlay() == getmErrorIndex()) {
			mMainApp.getmMsgSender().sendStrMsg("无可播放的歌曲！");
			return;
		}
		localPlay(mCurAlbum.getPosition(), getmErrorPlay(), sync);
	}

	private void autoPlayNext() {
		if (mMainApp.getmCurPlayAlbumId() <= 0) {
			mCurAlbum = mMainApp.getmAllAlbum().get(0);
		}
		if (mCurAlbum == null) {
			if (mMainApp.getmCurPlayAlbumId() > 0) {
				mCurAlbum = mMainApp.getmAllAlbum().get(mMainApp.getmCurPlayAlbumId() - 1);
			}
		} else {
			if (mMainApp.getmCurPlayAlbumId() > 0 &&
					mCurAlbum.getPosition() != mMainApp.getmCurPlayAlbumId()) {
				mCurAlbum = mMainApp.getmAllAlbum().get(mMainApp.getmCurPlayAlbumId() - 1);
			}
		}

		playNext();
	}

	private void playNext() {
		if (mCurAlbum == null) {
			return;
		}
		int index = mCurAlbum.getCurPlayIndex();
		int count = mCurAlbum.getSongCount();
		if (mMainApp.getmCurPlayAlbumId() == mCurAlbum.getPosition()) {
			index ++;
			if (index >= count) {
				index = 0;
			}
		}

		boolean flag = localPlay(mCurAlbum.getPosition(), index, true);

		if (mMainApp.getmConfig().getSelfAddr() != 0 && flag) {
			//同步状态，按照协议参数应该是5,但是为了网关处理方便，这里改为1（也就是播放）
			mMainApp.getmClientSocket().startConnectGateway(ClientSocket.SYNC_STATE, getSyncParams(STATE_PLAY));
		}
	}

	public void localPlayNext() {
		if (mMainApp.getmCurShowAlbumId() <= 0) {
			mCurAlbum = mMainApp.getmAllAlbum().get(0);
		}
		if (mCurAlbum == null) {
			if (mMainApp.getmCurShowAlbumId() > 0) {
				mCurAlbum = mMainApp.getmAllAlbum().get(mMainApp.getmCurShowAlbumId() - 1);
			}
		} else {
			if (mMainApp.getmCurShowAlbumId() > 0 &&
					mCurAlbum.getPosition() != mMainApp.getmCurShowAlbumId()) {
				mCurAlbum = mMainApp.getmAllAlbum().get(mMainApp.getmCurShowAlbumId() - 1);
			}
		}

		playNext();
	}

	public void localPlayPre() {
		mCurAlbum = mMainApp.getmAllAlbum().get(mMainApp.getmCurShowAlbumId() - 1);
		int index = mMainApp.getmCurSondIndex();
		int count = mCurAlbum.getSongCount();
		if (mMainApp.getmCurPlayAlbumId() == mCurAlbum.getPosition()){
			index --;
			if (index < 0) {
				index = count - 1;
			}
		}

		localPlay(mCurAlbum.getPosition(), index, true);

		if (mMainApp.getmConfig().getSelfAddr() != 0) {
			//同步状态，按照协议参数应该是6,但是为了网关处理方便，这里改为1（也就是播放）
			mMainApp.getmClientSocket().startConnectGateway(ClientSocket.SYNC_STATE, getSyncParams(STATE_PLAY));
		}
	}

	public void pause(boolean sync) {
		if (mAudioCtrl.isPlaying()) {
			mAudioCtrl.pause();
			initAllIden();
			MainApplication.isPause = true;
			MainApplication.isPlaying = false;
		}

		if (MainApplication.isPause && !MainApplication.isPlaying) {
			mMainApp.getmMsgSender().sendRefreshPlayState();
		}

		setmErrorPlay(-1);
		setmErrorIndex(-1);
		//关闭GPIO口
		if (mMainApp.IsDeviceModel()) {
		    openGpio15();
		}
		
		//通知网关关闭音箱控制电源,延时5分钟关闭电源
		//startTimer();

		if (mMainApp.getmConfig().getSelfAddr() != 0) {
			//同步状态
			mMainApp.getmClientSocket().startConnectGateway(ClientSocket.SYNC_STATE, getSyncParams(STATE_PAUSE));
		}
	}

	public void stop() {
		if (mAudioCtrl.isPlaying()) {
			mAudioCtrl.stop();
			initAllIden();
			MainApplication.isPause = false;
			MainApplication.isPlaying = false;
		}

		if (!MainApplication.isPause && !MainApplication.isPlaying) {
			mMainApp.getmMsgSender().sendRefreshPlayState();
		}
		setmErrorPlay(-1);
		setmErrorIndex(-1);
		//通知网关关闭音箱控制电源
		if (mMainApp.IsDeviceModel()) {
		    openGpio15();
		}
		
		/*if (mMainApp.getmConfig().getSelfAddr() != 0) {
			ClientSocket.startConnectGateway(ClientSocket.CLOSE_CTRL_POWER);
		}*/

		if (mMainApp.getmConfig().getSelfAddr() != 0) {
			//同步状态
			mMainApp.getmClientSocket().startConnectGateway(ClientSocket.SYNC_STATE, getSyncParams(STATE_PAUSE));
		}
	}

	public void start() {
		if (mAudioCtrl.isPlaying()) {
			return;
		}
		int index = mMainApp.getmCurSondIndex();
		localPlay(mMainApp.getmLastPlayAlbumId(), index, true);
	}

	public void release() {
		if (mAudioCtrl != null) {
			mAudioCtrl.release();
			mAudioCtrl = null;
		}
	}

	public boolean isPlaying() {
		return mAudioCtrl.isPlaying();
	}

	public int getMusicMaxVolume() {
		return mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	}

	public int getCurMusicVolume() {
		return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
	}

	public int volumeAdd() {
		if (MainApplication.isSilent) {
			setMute(false);
		}
		int volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		
		//if (volume < 10) {
			mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, 
				AudioManager.ADJUST_RAISE,
				AudioManager.FLAG_SHOW_UI);    //调高声音
		/*} else {
			mMainApp.getmMsgSender().sendStrMsg("音量已经最大");
		}*/
		
		volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		if (volume != 0 && mMainApp.getmConfig().getSelfAddr() != 0) {
			//同步状态，退出静音
			mMainApp.getmClientSocket().startConnectGateway(ClientSocket.SYNC_STATE, getSyncParams(STATE_EXIT_SILENT));
		}
		return volume;
	}

	public void setMute(boolean mute) {
		mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, mute);

		MainApplication.isSilent = mute;
	}

	public int volumeDec() {
		mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, 
				AudioManager.ADJUST_LOWER, 
				AudioManager.FLAG_SHOW_UI);//调低声音
		int volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		if (volume == 0) {
			if (!MainApplication.isSilent) {
				setMute(true);
			}

			if (mMainApp.getmConfig().getSelfAddr() != 0) {
				//同步状态，静音
				mMainApp.getmClientSocket().startConnectGateway(ClientSocket.SYNC_STATE, getSyncParams(STATE_SILENT));
			}
		}
		return volume;
	}

	public int setVolume(int index) {
		if (index != 0 && MainApplication.isSilent) {
			setMute(false);
		}

		if (index == 0 && !MainApplication.isSilent) {
			setMute(true);
		}
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 
				index, AudioManager.FLAG_SHOW_UI);
		int volume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

		return volume;
	}

	public int getPlayMode() {
		return mPlayMode;
	}

	public void setPlayMode(int playMode) {
		mPlayMode = playMode;
	}

	public void setRecord(boolean isRecord) {
		mIsRecord = isRecord;
	}

	public boolean isRecord() {
		return mIsRecord;
	}

	public boolean remotePlayNext() {
		int albumId = mMainApp.getmCurPlayAlbumId();
		if (albumId == 0) {
			albumId = mMainApp.getmLastPlayAlbumId();
		}

		mCurAlbum = mMainApp.getmAllAlbum().get(albumId - 1);
		int songId = mCurAlbum.getCurPlayIndex();
		if (mMainApp.getmCurPlayAlbumId() == mCurAlbum.getPosition()) {
			songId ++;
			int count = mCurAlbum.getSongCount();
			if (songId >= count) {
				albumId = mMainApp.getNextNotNullAlbumId(albumId);
				mCurAlbum = mMainApp.getmAllAlbum().get(albumId - 1);
				songId = 0;
			}
		}
		return remotePlay(albumId, songId);
	}

	public boolean remotePlayPre() {
		int albumId = mMainApp.getmCurPlayAlbumId();
		if (albumId == 0) {
			albumId = mMainApp.getmLastPlayAlbumId();
		}
		mCurAlbum = mMainApp.getmAllAlbum().get(albumId - 1);
		int songId = mCurAlbum.getCurPlayIndex();
		if (mMainApp.getmCurPlayAlbumId() == mCurAlbum.getPosition()) {
			songId --;
			if (songId < 0) {
				albumId = mMainApp.getPreNotNullAlbumId(albumId);
				mCurAlbum = mMainApp.getmAllAlbum().get(albumId - 1);
				songId = mCurAlbum.getSongCount() - 1;
			}
		}
		return remotePlay(albumId, songId);
	}

	public boolean remotePlay(int albumId, int songId) {
		if (albumId <= 0 || songId < 0) {
			return false;
		}

		return localPlay(albumId, songId, false);
	}

	public void setTtsPause(boolean ttsPause) {
		mTtsPause = ttsPause;
	}

	public boolean isTtsPause() {
		return mTtsPause;
	}

	public void setmErrorIndex(int mErrorIndex) {
		this.mErrorIndex = mErrorIndex;
	}

	public int getmErrorIndex() {
		return mErrorIndex;
	}

	public void setmErrorPlay(int mErrorPlay) {
		this.mErrorPlay = mErrorPlay;
	}

	public int getmErrorPlay() {
		return mErrorPlay;
	}

	public byte[] getSyncParams(byte firstParam) {
	    byte[] params = new byte[3];
	    params[0] = firstParam;
	    params[1] = (byte)mMainApp.getmCurPlayAlbumId();
	    params[2] = (byte)mMainApp.getmCurSondIndex();
	    return params;
	}
	
	public void openGpio15() {
		OperaGpio15 ls = new OperaGpio15();
		boolean resp = ls.openGpio15();
		mMainApp.setmGpioIsOpen(resp);
		ls = null;
	}

	public void closeGpio15() {
		OperaGpio15 ls = new OperaGpio15();
		boolean flag = ls.closeGpio15();
		if (flag) {
			mMainApp.setmGpioIsOpen(false);
		}
		
		ls = null;
	}
}
