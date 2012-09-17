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
	private List<String> 		mRec = new ArrayList<String>();// ���¼���ļ�
	private String 				mTemp = "recaudio_";// ��ʱ�ļ�ǰ׺

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
				/* ȡ�ÿ�ʼִ�е�ʱ�� */
				mStartRecTime = System.currentTimeMillis();
				/* ȡ��SD Card·����Ϊ¼�����ļ�λ�� */
				//myRecAudioDir = Environment.getExternalStorageDirectory();
				/* ����¼���� */
				mRecAudioFile = File.createTempFile(mTemp, ".amr",
						mRecAudioDir);

				mrMgr = new MediaRecorder();
				/* �趨¼����ԴΪ��˷� */
				mrMgr.setAudioSource(MediaRecorder.AudioSource.MIC);
				/* �趨�����ʽ */
				mrMgr.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
				/* �趨��Ƶ��ʽΪEncoder */
				mrMgr.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
				/* �趨��Ƶ����·�� */
				mrMgr.setOutputFile(mRecAudioFile.getAbsolutePath());
				/* ׼����ʼ¼�� */
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
	 * ����¼���ļ�
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
	 * ����¼���ļ�
	 * @param file
	 */
	public void playRecord(File file){
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "audio");
	}

	/**
	 * ��ʾ�б�
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
	 * @param Ҫɾ����¼������
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
