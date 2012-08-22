package sungeo.netmusic.download;

import java.io.File;
import java.util.List;

import com.mobclick.android.MobclickAgent;

import sungeo.netmusic.data.AlbumRecordInfo;
import sungeo.netmusic.data.MainApplication;
import sungeo.netmusic.netbase.ClientSocket;
import sungeo.netmusic.unit.MsgSender;
import android.os.StatFs;

public class DownloadMgr {
	private int 				mAlbumIndex 	= 0;
	private int 				mCurIndex 		= 0;
	private int 				mSongCount 		= 0;
	private int 				mAllSongCount 	= 0;
	private int 				mCurDownSongId 	= 0;
	private int 				mPosition;
	private int 				mReDownCounter 	= 0;
	private boolean 			mDeleteAlbum 	= false;
	private boolean 			mDeleteSong 	= false;
	private boolean 			mDownload 		= false;
	private boolean 			mIsDowning 		= false;
	private boolean 			mIsError 		= false;//标识下载过程是否有错，有错就不保存版本号
	private String 				mFullPath;
	private String 				mAlbumName;
	private String 				mDownFileName;
	private String 				mDownloadDir;
	private AlbumRecordInfo[] 	mAlbumInfo;
	private DownloadThread 		mDownThread;
	private MainApplication 	mMainApp;
	private final static int	MAX_REDOWNLOAD_NUM	= 3;

	public DownloadMgr() {
		mMainApp = MainApplication.getInstance();
		initAllValue();
	}

	public void initAllValue() {
		setAlbum(mMainApp.getmDownAlbum()); 
		mAllSongCount = 0;
		int len = 0;
		if (mAlbumInfo != null) {
			len = mAlbumInfo.length;
		}
		for (int i = 0; i < len; i ++) {
			if (mAlbumInfo[i] == null) {
				continue;
			}
			mAllSongCount += mAlbumInfo[i].getSongCount();
		}

		if (mAllSongCount != 0) {
			mCurDownSongId = 1;
		}
	}

	public void startDownloadAlbum() {
		if (getAlbum() == null || mAlbumIndex < 0) {
			mAlbumIndex = 0;
			return;
		}

		int len = getAlbum().length;
		if (mAlbumIndex >= len) {//全部专辑下载完成
			finishDownload();
			return;
		}

		if (getAlbum()[mAlbumIndex] == null) {
			return;
		}

		initToDownloadAlbumInfo();

		if (mDownloadDir == null) {
			//说明存储卡不存在或已经被挂载到电脑上，暂时不可用
			mIsError = true;
			finishDownload();
			return;
		} else if (mDownloadDir.equals("")) {
			downloadNextAlbum();
			return;
		}

		if (!checkSdSize()) {
			mMainApp.getmMsgSender().sendStrMsg("SD卡空间不足，无法完成下载！");
			mIsError = true;
			finishDownload();
			return;
		}

		if (mDownload && mIsDowning) {
			if (mMainApp.getmConfig().getSelfAddr() != 0) {
				//地址码为0,说明还没有加入系统，不必同步数据
				mMainApp.getmClientSocket().startConnectGateway(ClientSocket.NOTIFI_UPDATE_DATA);
			}

			mDownload = false;
		}

		mIsDowning = isDownloadAlbum();

		boolean flag = createAlbum();

		if (!flag) {
			downloadNextAlbum();
			return;
		}

		startDownloadSong();
	}

	private boolean checkSdSize() {
		File path = new File(mMainApp.getExtStoDir()); 
		StatFs stat = new StatFs(path.getPath()); 
		/*块大小*/
		long blockSize = stat.getBlockSize(); 
		/*块数量*/
		long availableBlocks = stat.getAvailableBlocks();
		/* 返回bit大小值*/
		long size = (availableBlocks * blockSize)/1024 /1024;

		if (size < 10) {
			//剩余空间小于5M，说明空间不足
			return false;
		}

		return true;

		//(availableBlocks * blockSize)/1024;      //KIB 单位


		//(availableBlocks * blockSize)/1024 /1024;  //MIB单位


		//(availableBlocks * blockSize)/1024/1024/1024;  //GB单位
	}

	/*
	 * 网络连接断开时，做回收
	 */
	public void intermitDownload() {
		StringBuffer strBuf = new StringBuffer();
		int serial = mMainApp.getmConfig().getSelfSerial();
		strBuf.append(Integer.toHexString(serial));
		strBuf.append("由于网络断开，下载被中断");
		MobclickAgent.onEvent(MainApplication.getInstance(), "download_intermit", strBuf.toString());
		mMainApp.getmDownAlbum().clear();
		mMainApp.setmDownMgr(null);
		
		mMainApp.setmCurDownloadAlbumId(-1);
		mMainApp.getmMsgSender().sendInitSingleAlbum(mPosition);
		mMainApp.getmMsgSender().sendCanelToRecMsg();
	}
	
	private void finishDownload() {
		mMainApp.getmDownAlbum().clear();
		mMainApp.setmDownMgr(null);
		if (!mIsError) {
			mMainApp.getmConfig().saveVersion(mMainApp.getmServerVersion());
		}

		mMainApp.setmCurDownloadAlbumId(-1);
		mMainApp.getmMsgSender().sendInitSingleAlbum(mPosition);
		
		mMainApp.getmMsgSender().sendCanelToRecMsg();

		//保存完版本同时连接网关并发送版本信息

		if (mMainApp.getmConfig().getSelfAddr() != 0 && !mIsError) {
			mMainApp.getmMsgSender().sendStrMsg("下载完成，开始同步数据");
			mMainApp.getmClientSocket().startConnectGateway(ClientSocket.NOTIFI_UPDATE_DATA);
		}

		StringBuffer strBuf = new StringBuffer();
		int serial = mMainApp.getmConfig().getSelfSerial();
		String serStr = Integer.toHexString(serial);
		strBuf.append(serStr);
		
		if (!mIsError) {
			strBuf.append("下载完成，没有错误");
			MobclickAgent.onEvent(MainApplication.getInstance(), "download_finish", strBuf.toString());
			//当出现error时，说明SD卡已经满或不可用，此时不用再打开版本检查器
			mMainApp.getmVersionMgr().startCheckVersion();//下载完成后，重新打开查询版本计时器
		} else {
			strBuf.append("下载完成，但下载过程有错误（空间不足引起的）");
			MobclickAgent.onEvent(MainApplication.getInstance(), "download_finish", strBuf.toString());
		}
	}

	private void initToDownloadAlbumInfo() {
		mSongCount = getAlbum()[mAlbumIndex].getSongCount();
		mAlbumName = getAlbum()[mAlbumIndex].getAlbumName();
		mPosition = getAlbum()[mAlbumIndex].getPosition();

		mMainApp.setmCurDownloadAlbumId(mPosition);
		mMainApp.getmMsgSender().sendMsg(MsgSender.MSG_DOWNLOAD_ING);

		if (mMainApp.getExtStoDir() == null) {
			mMainApp.getmMsgSender().sendStrMsg("SD卡不可用");
			mDownloadDir = null;
			return;
		}

		if (mAlbumName != null && !mAlbumName.equals("")) {
			// 获取SD卡目录  
			mDownloadDir = mMainApp.getExtStoDir() + "/" + mAlbumName + "/";
		} else {
			mDownloadDir = null;
		}
		
		if (mAlbumName != null && (mAlbumName.equals("") ||
			mAlbumName.equals(" "))) {
			mDownloadDir = "";
		}
	}

	private boolean createAlbum() {
		boolean flag = false;
		checkAlbum();
		if (mAlbumName.equals("") && mSongCount == 0) {
			flag = true;
		}

		File file = new File(mDownloadDir);  
		//创建下载目录  
		if (!file.exists()) {
			flag = file.mkdirs();  
			if (!flag) {
				mMainApp.getmMsgSender().sendStrMsg("创建目录失败");
				mIsError = true;
			}
		} else {
			//如果存在，检查歌曲是否相同，不同则更新（删除旧的，添加新的）
			checkSong();
			flag = true;
		}
		return flag;
	}

	private boolean isDownloadAlbum() {
		if (getAlbum() == null) {
			return false;
		}

		int len = getAlbum().length;
		if (mAlbumIndex < 0 || mAlbumIndex >= len) {
			mAlbumIndex = 0;
			return false;
		}

		String[] songName = getAlbum()[mAlbumIndex].getSongName();

		return mMainApp.getmAlbumMgr().isDownloadSong(mPosition, songName);
	}

	private void checkSong() {

		if (getAlbum() == null) {
			return;
		}
		int len = getAlbum().length;
		if (mAlbumIndex < 0 || mAlbumIndex >= len) {
			mAlbumIndex = 0;
			return;
		}
		String[] songName = getAlbum()[mAlbumIndex].getSongName();

		mMainApp.getmAlbumMgr().initAlbumDirStr(mAlbumName);

		if (mDeleteAlbum) {
			mDeleteSong = mMainApp.getmAlbumMgr().checkSong(mAlbumName, songName);
		} else {
			mDeleteSong = mMainApp.getmAlbumMgr().checkSong(mPosition, songName);
		}
	}

	private void deleteSong() {
		boolean ret = mMainApp.getmAlbumMgr().deleteSong();
		if (ret) {
			//UIGlobal.showStringMsg("删除旧的歌曲成功");
		} else {
			//UIGlobal.showStringMsg("删除旧的歌曲失败");
		}
	}

	private void checkAlbum() {
		mDeleteAlbum = mMainApp.getmAlbumMgr().isExistOnPos(mPosition, mAlbumName);
	}

	private void deleteAlbum() {
		mMainApp.getmAlbumMgr().deleteAlbum();
	}

	private void downloadNextAlbum() {
		mAlbumIndex ++;
		startDownloadAlbum();
	}

	private void saveAlbum() {
		if (mMainApp == null || getAlbum() == null) {
			return;
		}

		int len = getAlbum().length;
		if (mAlbumIndex < 0 || mAlbumIndex >= len) {
			mAlbumIndex = 0;
			return;
		}

		if (getAlbum()[mAlbumIndex] == null) {
			return;
		}

		if (!mAlbumName.equals("") && mMainApp.checkNetwork()) {
			updateMediaCache();
			byte[] data = mAlbumInfo[mAlbumIndex].getAlbumBytes();
			mMainApp.saveDataToDb(mPosition, data);
		}

		updateOldAlbum();

		mMainApp.getmMsgSender().sendInitSingleAlbum(mPosition);

		//下完一个专辑就保存一个版本号，以便在下载过程中，同步网关数据
		if (mMainApp.checkNetwork()) {
			mMainApp.getmMsgSender().printLog("貌似网络正常，保存数据版本");
			mMainApp.saveVersion(mPosition - 1);
		}

		mCurIndex = 0;
		
		mReDownCounter = 0;		//重新下载计数器也要清0

		downloadNextAlbum();
	}

	/**
	 * 更新完一个专辑后，同步更新MediaManager缓存，否则可能会出现播放已经不存在的歌曲的情况
	 */
	private void updateMediaCache() {
		AlbumRecordInfo album = mMainApp.getCurAlbum();
		if (mDeleteAlbum && album != null) {
			String tmpName = album.getAlbumName();
			String delName = mMainApp.getmAlbumMgr().getDeleteAlbumName();

			if (tmpName != null && delName != null && tmpName.equals(delName)) {
				mMainApp.getmMediaMgr().stop();
				return;
			}
		}

		if (mDeleteSong && album != null) {
			String tmpName = album.getAlbumName();
			String name = mMainApp.getmLastPlaySongName();
			if (tmpName != null && tmpName.equals(mAlbumName)
					&& name != null) {
				int len = 0;
				if (mMainApp.getmAlbumMgr().getDeleteName() != null){
					len = mMainApp.getmAlbumMgr().getDeleteName().length;
				}

				for (int i = 0; i < len; i ++) {
					if (mMainApp.getmAlbumMgr().getDeleteName()[i] == null) {
						continue;
					}

					if (mMainApp.getmAlbumMgr().getDeleteName()[i].equals(name)) {
						mMainApp.getmMediaMgr().stop();
						return;
					}
				}

			}
		}
	}

	/**
	 * 更新旧专辑，只在保存已经更新的专辑后使用，用于删除旧专辑或删除旧歌曲
	 */
	private void updateOldAlbum() {
		if (mDeleteAlbum) {
			//一个专辑下载完成后，先保存新的，再删除旧的，保证在更新过程中终端还能控制
			deleteAlbum();
			mDeleteAlbum = false;
		}

		if (mDeleteSong) {
			deleteSong();
			mDeleteSong = false;
		}
	}

	private void startDownloadSong() {
		if (mSongCount != 0) {
			if (mCurIndex < 0) {
				return;
			}

			if (mCurIndex >= mSongCount) {
				saveAlbum();
				return;
			}

			if (getAlbum() == null || mAlbumIndex >= 5) {
				mAlbumIndex = 0;
				return;
			}
			mFullPath = getAlbum()[mAlbumIndex].getSongUrl()[mCurIndex];
			mDownFileName = getAlbum()[mAlbumIndex].getSongName()[mCurIndex];
		} else {
			saveAlbum();
			return;
		}

		if (mFullPath != null && mDownFileName != null) {
			doDownload();
		}
	}

	private void doDownload() {
		if (songIsExist(mDownFileName)) {
			downloadNextSong();
			return;
		}

		StringBuffer str = new StringBuffer();
		str.append("下载进度：");
		str.append(mDownFileName);
		str.append("(");
		str.append(mCurDownSongId);
		str.append("/");
		str.append(mAllSongCount);
		str.append(")");
		mMainApp.getmMsgSender().sendProgressToRecMsg(str.toString());
		//启动文件下载线程
		startDownMainThread();
		mDownload = true;
	}

	private void startDownMainThread() {
		if (mFullPath == null || mDownloadDir == null || mDownFileName == null) {
			return;
		}

		mDownThread = new DownloadThread();

		mDownThread.initValue(mFullPath, mDownloadDir, mDownFileName);
		mDownThread.start();
	}

	public void downloadNextSong() {
		mCurIndex ++;
		
		mReDownCounter = 0;		//下载下一首歌曲时，重新下载的计数器要清0
		
		mCurDownSongId ++;
		startDownloadSong();
	}

	private boolean songIsExist(String songName) {
		boolean ret = false;
		if (songName == null || mDownloadDir == null) {
			return false;
		}
		// 获取SD卡目录  
		File file = new File(mDownloadDir + songName);  
		if (file.exists()) {
			ret = true;
		}
		return ret;
	}

	public void reDownload() {
		//当重新下载前，先检查网络是否连接，如果断开，则中断下载
		if (!mMainApp.checkNetwork()) {
			mMainApp.getmMsgSender().printLog("下载线程发现无网络，中断下载");
			mMainApp.getmMsgSender().sendStrMsg("下载线程发现网络异常，无法完成下载");
			
			intermitDownload();
			return;
		}
		mReDownCounter ++;
		if (mReDownCounter == MAX_REDOWNLOAD_NUM) {
			mReDownCounter = 0;
			getAlbum()[mAlbumIndex].getSongName()[mCurIndex] = null;
			
			StringBuffer strBuf = new StringBuffer();
			int serial = mMainApp.getmConfig().getSelfSerial();
			String serStr = Integer.toHexString(serial);
			strBuf.append(serStr);
			strBuf.append("下载失败的专辑名称为：");
			strBuf.append(mAlbumName);
			strBuf.append(";");
			strBuf.append("下载失败的歌曲名称为：");
			strBuf.append(mDownFileName);
			MobclickAgent.onEvent(MainApplication.getInstance(), "download_three_fail", strBuf.toString());
			//同一首歌曲重试3次，3次后还下载失败，另做处理
			downloadNextSong();
		} else {
			startDownloadSong();
		}
	}

	public void setAlbum(List<AlbumRecordInfo> album) {
		int size = 0;
		if (album != null) {
			size = album.size();
		}

		if (size != 0) {
			mAlbumInfo = new AlbumRecordInfo[size];
		}
		for (int i = 0; i < size; i ++) {
			mAlbumInfo[i] = (AlbumRecordInfo)mMainApp.getmDownAlbum().get(i);
		}
	}

	public AlbumRecordInfo[] getAlbum() {
		return mAlbumInfo;
	}
}
