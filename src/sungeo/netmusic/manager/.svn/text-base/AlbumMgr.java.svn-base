package sungeo.netmusic.manager;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import sungeo.netmusic.data.AlbumInfoDb;
import sungeo.netmusic.data.AlbumRecordInfo;
import sungeo.netmusic.data.MainApplication;
import sungeo.netmusic.netbase.ClientSocket;

/**
 * 专辑管理类，实现专辑（实际上是文件夹），歌曲文件的操作
 * 操作同时要更新数据库信息
 * @author Administrator
 *
 */
public class AlbumMgr {
	private Timer 			mQueryAudioTimer;
	private File 			mLocalAlbumDir;
	private String 			mAlbumDir;
	private String 			mDeleteAlbumName;
	private String[] 		mDeleteName;
	private MainApplication mMainApp;
	public static final int MAX_SONG_NUM = 150;

	public AlbumMgr() {
		mMainApp = MainApplication.getInstance();
		
		setmLocalAlbumDir();
	}

	private boolean setmLocalAlbumDir() {
		if (mMainApp.getExtStoDir() == null) {
			mMainApp.getmMsgSender().sendStrMsg("SD卡不可用");
			return false;
		}
		mLocalAlbumDir = new File(mMainApp.getExtStoDir() + "/" 
				  + AlbumInfoDb.LOCAL_ALBUM_NAME + "/");
		return true;
	}
	
	public void initAlbumDirStr(String name) {
		if (name == null) {
			return;
		}

		if (mMainApp.getExtStoDir() == null) {
			mMainApp.getmMsgSender().sendStrMsg("SD卡不存在");
			return;
		}
		mAlbumDir = mMainApp.getExtStoDir() + "/" + name + "/";
	}

	public void start() {
		if (mQueryAudioTimer != null) {
			mQueryAudioTimer.cancel();
			mQueryAudioTimer = null;
		}

		mQueryAudioTimer = new Timer();
		long waittime = 1000;
		long spTime = 60000;
		mQueryAudioTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				if(!checkSdExist()) {
					stop();
				} else {
					checkAudioInSd();
				}
			}
		}, waittime, spTime);
	}

	public void stop() {
		if (mQueryAudioTimer != null) {
			mQueryAudioTimer.cancel();
			mQueryAudioTimer = null;
		}
	}

	public void checkAudioInSd() {
		File[] f = null;
		boolean flag = false;
		if (mLocalAlbumDir == null) {
			//在检查本地专辑时，mLocalAlbumDir为null说明程序运行时，就没有SD卡，
			//此时要重新初始化变量mLocalAlbumDir
			flag = setmLocalAlbumDir();
		} else {
			flag = true;
		}
		
		if (!flag) {
			return;
		}
		
		f = mLocalAlbumDir.listFiles(new MusicFilter());

		int len = 0;
		if (f != null) {
			len = f.length;
			if (len == 0) {
				f = null;
				clearLocalAlbumInfo();
				return;
			}
		} else {
			clearLocalAlbumInfo();
			return;
		}
		
		File[] tmpFile = null;
		if (len <= MAX_SONG_NUM) {
			tmpFile = new File[len];
		} else {
			tmpFile = new File[MAX_SONG_NUM];
		}
		int fcount = tmpFile.length;
		for (int i = 0; i < fcount; i ++) {
			tmpFile[i] = f[i];
		}

		f = null;
		
		AlbumRecordInfo album = (AlbumRecordInfo) mMainApp.getmAllAlbum().get(5);
		if (album == null) {
			return;
		}

		int position = album.getPosition();
		
		boolean isChange = false;
		if (position == AlbumInfoDb.SIXTH_ALBUM_ID) {
			isChange = localAlbumIsChange(tmpFile);
		}

		String[] songName = new String[fcount];
		for (int i = 0; i < fcount; i ++) {
			File file = tmpFile[i];
			songName[i] = file.getName();
		}
		tmpFile = null;
		album.setSongCount(fcount);
		album.setSongName(songName);
		album.setSinger(null);

		if (isChange) {
			//通知主界面刷新歌曲数目
			mMainApp.getmMsgSender().sendInitSingleAlbum(AlbumInfoDb.SIXTH_ALBUM_ID);
			//保存专辑，并更新版本号
			saveLocalAlbum();
		}
		
		album = null;
	}

	private void clearLocalAlbumInfo() {
		AlbumRecordInfo album = (AlbumRecordInfo) mMainApp.getmAllAlbum().get(5);
		if (album == null) {
			return;
		}
		
		if (album.getSongCount() == 0) {
			//如果数据库已经为空，不需要重复操作，给网关发信息
			return;
		}
		/*如果f为null，说明没有这个目录或目录下没有歌曲，要同步数据库
		清空专辑，只保留专辑名称*/
		album.clearInfo();
		album.setAlbumName(AlbumInfoDb.LOCAL_ALBUM_NAME);
		if (mMainApp.getmCurPlayAlbumId() == AlbumInfoDb.SIXTH_ALBUM_ID) {
			mMainApp.getmMediaMgr().stop();
		}
		mMainApp.saveDataToDb(AlbumInfoDb.SIXTH_ALBUM_ID, album.getAlbumBytes());
	
		if (mMainApp.getmConfig().getSelfAddr() != 0) {
			mMainApp.getmMsgSender().sendStrMsg("本地专辑更新，同步数据");
			mMainApp.getmClientSocket().startConnectGateway(ClientSocket.NOTIFI_UPDATE_DATA);
		}
	}
	
	private void saveLocalAlbum() {
		AlbumRecordInfo album = (AlbumRecordInfo) mMainApp.getmAllAlbum().get(5);
		if (album == null) {
			return;
		}

		byte[] data = album.getAlbumBytes();
		if (data != null) {
			mMainApp.saveDataToDb(AlbumInfoDb.SIXTH_ALBUM_ID, data);
			if (mMainApp.getmConfig().getSelfAddr() != 0) {
				mMainApp.getmMsgSender().sendStrMsg("本地专辑更新，同步数据");
				mMainApp.getmClientSocket().startConnectGateway(ClientSocket.NOTIFI_UPDATE_DATA);
			}
		}
	}

	private boolean localAlbumIsChange(File[] f) {
		boolean ret = false;
		AlbumRecordInfo album = (AlbumRecordInfo) mMainApp.getmAllAlbum().get(5);
		if (f == null || album == null) {
			return ret;
		}

		int newCount = f.length;
		int oldCount = album.getSongCount();
		if (newCount != oldCount) {
			ret = true;
		} else {//如果歌曲数目相同，再检查歌曲是否相同
			String[] name = new String[newCount];
			for (int i = 0; i < newCount; i ++) {
				name[i] = f[i].getName();
			}

			ret = checkName(album.getSongName(), name);
		}

		return ret;
	}

	private boolean checkSdExist() {
		boolean ret = false;
		if (mMainApp.getExtStoDir() != null) {
			ret = true;
		}
		return ret;
	}

	class MusicFilter implements FilenameFilter {

		public boolean accept(File dir, String filename) {
			boolean ret = filename.endsWith(".mp3") | 
			filename.endsWith(".MP3") |
			filename.endsWith(".wma");
			return ret;
		}
	}

	public boolean isExistOnPos(int position, String name) {
		boolean ret = false;

		int len = mMainApp.getmAllAlbum().size();
		for (int i = 0; i < len; i ++) {
			AlbumRecordInfo album = (AlbumRecordInfo)mMainApp.getmAllAlbum().get(i);
			if (album != null && !album.isEmpty() 
					&& album.getPosition() == position
					&& !album.getAlbumName().equals(name)) {
				setDeleteAlbumName(album.getAlbumName());
				ret = true;
				break;
			}
		}

		return ret;
	}

	private boolean checkName(String[] oldName, String[] newName) {
		boolean ret = false;
		if (oldName == null || newName == null) {
			return ret;
		}

		int len = newName.length;

		for(int i = 0; i < len; i ++){
			boolean flag=false;

			int jLen = oldName.length;

			for(int j = 0; j < jLen; j ++){
				if(newName[i] != null && newName[i].equals(oldName[j])){
					flag=true;
					break;
				}
			}
			if(!flag){
				ret = true;
				break;
			}
		}

		return ret;
	}

	public boolean checkSong(String[] oldName, String[] newName) {
		boolean ret = false;
		if (oldName == null) {
			return ret;
		}

		if (newName == null) {
			setDeleteName(oldName);

			if (getDeleteName() != null) {
				ret = true;
			}

			return ret;
		}
		String[] str1=null;
		String[] str2=null;

		str1 = newName;
		str2 = oldName;

		int len = 0;
		if (str2 != null) {
			len = str2.length;
		}
		String arr3 = null;
		for(int i = 0; i < len; i ++){
			boolean flag=false;

			int jLen = 0;
			if (str1 != null) {
				jLen = str1.length;
			}
			for(int j = 0; j < jLen; j ++){
				if(str2[i] != null && str2[i].equals(str1[j])){
					flag=true;
				}
			}
			if(!flag){
				if (arr3 == null) {
					arr3 = str2[i] + ';';
				} else {
					arr3 += str2[i] + ';';
				}
			}
		}

		if (arr3 == null) {
			return ret;
		}

		setDeleteName(arr3.split(";"));

		if (getDeleteName() != null) {
			ret = true;
		}

		return ret;
	}

	public boolean checkSong(String path, String[] newName) {
		boolean ret = false;
		if (path == null || newName == null) {
			return ret;
		}

		if (mMainApp.getExtStoDir() == null) {
			mMainApp.getmMsgSender().sendStrMsg("SD卡不可用");
			return ret;
		}

		File dir = new File(mMainApp.getExtStoDir() + "/" + path + "/");
		if (!dir.exists()) {
			return false;
		}
		File filelist[] = dir.listFiles(); 
		int listlen = filelist.length; 
		String[] oldName = null;
		if (listlen != 0) {
			oldName = new String[listlen];
		}
		for(int   i = 0; i < listlen; i ++) { 
			oldName[i] = filelist[i].getName();
		}

		ret = checkSong(oldName, newName);
		return ret;
	}

	public boolean checkSong(int position, String[] newName) {
		boolean ret = false;
		String[] oldName = getSongName(position);
		if (oldName == null) {
			return ret;
		}

		ret = checkSong(oldName, newName);

		return ret;
	}

	public boolean isDownloadSong(int position, String[] newName) {
		boolean ret = false;
		String[] oldName = getSongName(position);
		if (oldName == null) {
			return ret;
		}

		ret = checkName(newName, oldName);

		return ret;
	}

	public void deleteAlbum() {
		if (getDeleteAlbumName() == null) {
			return;
		}

		deleteAlbum(getDeleteAlbumName());

		setDeleteAlbumName(null);
	}

	private void deleteAlbum(String name) {
		if (mMainApp.getExtStoDir() == null) {
			mMainApp.getmMsgSender().sendStrMsg("SD卡不可用");
			return;
		}

		File dir = new File(mMainApp.getExtStoDir() + "/" + name + "/");
		if (!dir.exists()) {
			setDeleteAlbumName(null);
			return;
		}
		deleteFolder(dir);
	}

	private void  deleteFolder(File dir) { 
		File filelist[] = dir.listFiles(); 
		int listlen = filelist.length; 
		boolean ret = false;
		for(int   i = 0; i < listlen; i ++) { 
			if(filelist[i].isDirectory()) { 
				deleteFolder(filelist[i]); 
			} else { 
				ret = filelist[i].delete(); 
				if (ret) {
					//UIGlobal.showStringMsg("删除旧专辑中的歌曲成功");
				} else {
					//UIGlobal.showStringMsg("删除旧专辑中的歌曲失败");
				}
			} 
		} 
		ret = dir.delete();//删除当前目录 
		if (ret) {
			//UIGlobal.showStringMsg("删除旧专辑目录成功");
		} else {
			//UIGlobal.showStringMsg("删除旧专辑目录失败");
		}
	}

	/**
	 * 删除歌曲
	 * @param name 歌曲的名称不带路径
	 * 删除成功，并更新内存
	 * 用在歌曲列表界面删除歌曲，此时AlbumManager.curAlbum已经唯一，删除完歌曲后更新此变量
	 * @return
	 */
	public boolean deleteSong(String name) {
		boolean ret = false;

		if (name == null) {
			return ret;
		}
		if (mMainApp.getExtStoDir() == null) {
			mMainApp.getmMsgSender().sendStrMsg("SD卡不可用");
			return ret;
		}

		AlbumRecordInfo curalbum = (AlbumRecordInfo)mMainApp.getmAllAlbum().get(mMainApp.getmCurShowAlbumId() - 1);
		if (curalbum == null) {
			return ret;
		}
		if (mMainApp.getExtStoDir() == null) {
			mMainApp.getmMsgSender().sendStrMsg("SD卡不存在");
			return ret;
		}
		String fileDir = mMainApp.getExtStoDir() + "/" + curalbum.getAlbumName() + "/" + name;

		File file = new File(fileDir);
		ret = file.delete();

		if (!ret) {
			//如果删除失败，不需更新内存
			return ret;
		}

		AlbumRecordInfo album = new AlbumRecordInfo();
		album.setPosition(curalbum.getPosition());
		album.setAlbumName(curalbum.getAlbumName());
		List<String> nameList = new ArrayList<String>();
		String[] songName = curalbum.getSongName();
		int num = 0;
		if (songName != null) {
			num = songName.length;
		}

		for (int i = 0; i < num; i ++) {
			if (songName[i] != null && songName[i].equals(name)) {
				continue;
			}
			nameList.add(songName[i]);
		}

		int size = nameList.size();
		if (size > 0) {
			songName = new String[size];
		} else {
			songName = null;
		}
		for (int i = 0; i < size; i ++) {
			songName[i] = nameList.get(i);
		}
		album.setSongCount(size);
		album.setSongName(songName);

		int position = album.getPosition();
		byte[] data = album.getAlbumBytes();
		mMainApp.saveDataToDb(position, data);
		
		if (mMainApp.getmCurPlayAlbumId() == position) {
			mMainApp.getmMediaMgr().initCurPlayIndex();
			mMainApp.getmMediaMgr().computeNextSongId();
		}

		if (mMainApp.getmConfig().getSelfAddr() != 0) {
			mMainApp.getmMsgSender().sendStrMsg("删除成功，同步数据");
			mMainApp.getmClientSocket().startConnectGateway(ClientSocket.NOTIFI_UPDATE_DATA);
		}

		return ret;
	}

	public boolean deleteSong() {
		boolean ret = false;
		if (getDeleteName() == null) {
			return ret;
		}

		int len = getDeleteName().length;
		for (int i = 0; i < len; i ++) {
			if (getDeleteName()[i] == null) {
				continue;
			}
			File file = new File(mAlbumDir + getDeleteName()[i]);
			ret = file.delete();
		}

		setDeleteName(null);
		return ret;
	}

	private String[] getSongName(int position) {
		String[] name = null;

		AlbumRecordInfo resAlbum = getAlbumByIndex(position);
		if (resAlbum == null) {
			return name;
		}

		name = resAlbum.getSongName();

		return name;
	}

	public AlbumRecordInfo getAlbumByIndex(int index) {
		AlbumRecordInfo resAlbum = null;

		int len = mMainApp.getmAllAlbum().size();
		for (int i = 0; i < len; i ++) {
			AlbumRecordInfo album = (AlbumRecordInfo)mMainApp.getmAllAlbum().get(i);
			if (album != null && !album.isEmpty() 
					&& album.getPosition() == index) {
				resAlbum = album;
				break;
			}
		}

		return resAlbum;
	}

	public void setDeleteName(String[] deleteName) {
		mDeleteName = deleteName;
	}

	public String[] getDeleteName() {
		return mDeleteName;
	}

	public void setDeleteAlbumName(String deleteAlbumName) {
		mDeleteAlbumName = deleteAlbumName;
	}

	public String getDeleteAlbumName() {
		return mDeleteAlbumName;
	}
}
