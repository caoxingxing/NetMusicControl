package sungeo.netmusic.manager;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import sungeo.netmusic.data.MainApplication;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;

public class RecordMgr {
	private MediaRecorder 		mrMgr;
	@SuppressWarnings("unused")
	private long 				mStartRecTime;
	private File 				mRecAudioDir;
	private boolean 			mIsStartRec;
	@SuppressWarnings("unused")
	private int 				mSleepSec;
	private File 				mRecAudioFile;
	private List<String> 		mRec = new ArrayList<String>();// 存放录音文件
	private String 				mTemp = "recaudio_";// 临时文件前缀

	public RecordMgr() {
		String saveDir = Environment.getExternalStorageDirectory() + "/" + "My_Record" + "/";
		mRecAudioDir = new File(saveDir);
		if (!mRecAudioDir.exists()) {  
			mRecAudioDir.mkdirs();  
		} 
	}

	public void stopRecord() {
		if (mrMgr == null) {
			return;
		}

		mrMgr.stop();
		mrMgr.release();
		mrMgr = null;
	}

	public void startRecord() {
		try {
			if (Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED)) {
				/* 取得开始执行的时间 */
				mStartRecTime = System.currentTimeMillis();
				/* 取得SD Card路径做为录音的文件位置 */
				//myRecAudioDir = Environment.getExternalStorageDirectory();
				/* 建立录音档 */
				mRecAudioFile = File.createTempFile(mTemp, ".amr",
						mRecAudioDir);

				mrMgr = new MediaRecorder();
				/* 设定录音来源为麦克风 */
				mrMgr.setAudioSource(MediaRecorder.AudioSource.MIC);
				/* 设定输出格式 */
				mrMgr.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
				/* 设定音频格式为Encoder */
				mrMgr.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
				/* 设定音频保存路径 */
				mrMgr.setOutputFile(mRecAudioFile.getAbsolutePath());
				/* 准备开始录音 */
				mrMgr.prepare();

				mrMgr.start();
				setStartRec(true);
			} else {
				mSleepSec = 1;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 播放录音文件
	 * @param file
	 */
	public void playRecord(int index){
		if (mRec == null) {
			return;
		}

		int len = mRec.size();
		if (index < 0 || index >= len) {
			return;
		}

		String path = mRecAudioDir+File.separator+mRec.get(index)+".amr";
		playRecord(path);
	}

	private void playRecord(String path) {
		//MainApplication.getInstance().getmMediaMgr().setRecord(true);
		MainApplication.getInstance().getmMediaMgr().play(path, true);
	}

	/**
	 * 播放录音文件
	 * @param file
	 */
	public void playRecord(File file){
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "audio");
	}

	/**
	 * 显示列表
	 */
	public List<String> getRecList() {
		File[] f = mRecAudioDir.listFiles(new MusicFilter());
		mRec.clear();
		for (int i = 0; i < f.length; i++) {
			File file = f[i];
			String str = file.getName();
			int pos = str.lastIndexOf(".");
			if (pos == -1) {
				mRec.add(str);
			} else {
				mRec.add(str.substring(0, pos));
			}
		}

		return mRec;
	}

	/**
	 * 
	 * @param 要删除记录的索引
	 */
	public boolean removeRecord(int index) {
		boolean ret = false;
		if (mRec == null) {
			return ret;
		}

		int size = mRec.size();
		if (index < 0 || index >= size) {
			return ret;
		}

		String name = mRec.get(index) + ".amr";
		File f = new File(name);

		mRec.remove(index);

		ret = f.delete();

		return ret;
	}

	public void setStartRec(boolean isStartRec) {
		mIsStartRec = isStartRec;
	}

	public boolean isStartRec() {
		return mIsStartRec;
	}

	class MusicFilter implements FilenameFilter {

		public boolean accept(File dir, String filename) {
			return (filename.endsWith(".amr"));
		}
	}
}
