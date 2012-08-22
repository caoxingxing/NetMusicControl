package sungeo.netmusic.activity;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import sungeo.netmusic.R;
import sungeo.netmusic.data.AlbumInfoDb;
import sungeo.netmusic.data.MainApplication;
import sungeo.netmusic.unit.MsgSender;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class CopyUdiskMusicActivity extends BaseActivity{

	private ListView 		mMusicList;
	private ProgressDialog 	mProgressDialog;
	private ProgressThread 	mProgressThread;
	// U���ϵ�һ���ļ���Ŀ¼���б�
	private String [] 		mUdiskMusics;
	// U����ѡ�е�һ���ļ���Ŀ¼���б�����������
	private List<String> 	mToBeCopiedMusics;	
	// ���տ����ļ�ʱ�õ��Ľ�������
	private final int PROGRESS_DIALOG = 0;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		setContentView(R.layout.copy_udisk_music);

		// ��ȡU���еı��������б�
		getUdiskMusics();

		initMusicListView();
		initSelectallButton();
		initUnselectallButton();
		initCopyButton();
		initCloseButton();
	}

	protected Dialog onCreateDialog(int id){
		switch(id){
		case PROGRESS_DIALOG:
			mProgressDialog = new ProgressDialog(CopyUdiskMusicActivity.this);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setMessage("��ʼ��������");		
			mProgressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "�˳�", new DialogInterface.OnClickListener(){
				// ���½������ϡ��˳�����ť��ֹͣ������
				public void onClick(DialogInterface dialog, int which){
					mProgressThread.setState(ProgressThread.STATE_DONE);	

					mMainApp.getmMsgSender().sendMsg(MsgSender.MSG_STOP_COPY);
				}
			});
			return mProgressDialog;
		default:
			return null;
		}
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch(id) {
		case PROGRESS_DIALOG:
			mProgressDialog.setMax(mToBeCopiedMusics.size());
			mProgressDialog.setProgress(0);
			mProgressThread = new ProgressThread(mToBeCopiedMusics);
			mProgressThread.start();
		}
	}

	// ���û����á���������ť��
	private void enableCopyButton(boolean flag){
		Button btn = (Button)findViewById(R.id.udisk_music_copy);
		if(btn != null){
			btn.setEnabled(flag);
		}
	}

	class Mp3Filter implements FilenameFilter{
		public boolean accept(File dir, String filename){
			return filename.endsWith(".mp3");
		}
	}

	class DirFilter implements FileFilter{
		public boolean accept(File dir){
			// ���ԡ�.����ͷ��Ŀ¼�������Կ�����
			if(dir.isDirectory() && !dir.getName().startsWith(".")){				
				return true;
			}else{
				return false;
			}
		}
	}

	// ����U�̸�Ŀ¼��һ����Ŀ¼�����к�׺Ϊ��.mp3"���ļ��������浽udiskMusics�С�
	private void getUdiskMusics(){
		File udiskDir = new File(MainApplication.getInstance().getmUdiskMountedPath());					

		// �����������·�����档
		List<String> mp3Paths = new ArrayList<String>();

		try{
			// ��ȡ��Ŀ¼�µ��������֡�
			File [] mp3Files = udiskDir.listFiles(new Mp3Filter());
			for(int i = 0; i < mp3Files.length; i ++){
				mp3Paths.add(mp3Files[i].getPath());
			}

			// �����ȡ��һ����Ŀ¼�µ��������֡�
			File [] firstLevelDirs = udiskDir.listFiles(new DirFilter());
			for(int i = 0; i < firstLevelDirs.length; i ++){				
				mp3Paths.add(firstLevelDirs[i].getPath());
			}
		}catch(SecurityException e){
			Log.e("FILE_ACCESS", e.toString());
		}	

		// �������б�ת����ListView������ʹ�õĸ�ʽ��
		mUdiskMusics = new String [mp3Paths.size()];
		for(int i = 0; i < mUdiskMusics.length; i ++){
			mUdiskMusics[i] = mp3Paths.get(i);
		}
	}

	private void countSelectedFile(String path){
		File file = new File(path);
		if(file.isDirectory()){
			// ��������Ŀ¼�е�Mp3�ļ������û�ר�����С�
			File [] mp3Files = file.listFiles(new Mp3Filter());
			for(int i = 0; i < mp3Files.length; i ++){
				mToBeCopiedMusics.add(mp3Files[i].getPath());
			}
		}else{
			// ���������ļ���
			mToBeCopiedMusics.add(path);
		}
	}

	private void startCopy(){		
		if(mToBeCopiedMusics == null){
			mToBeCopiedMusics = new ArrayList<String>();
		}else{
			mToBeCopiedMusics.clear();
		}		

		SparseBooleanArray checked = mMusicList.getCheckedItemPositions();				
		for(int i = 0; i < mUdiskMusics.length; i ++){
			if(checked.get(i)){
				countSelectedFile(mUdiskMusics[i]);
			}
		}

		final int total = mToBeCopiedMusics.size();
		if(total > 0){			
			showDialog(PROGRESS_DIALOG);			
		}else{
			mMainApp.getmMsgSender().sendStrMsg("û���ҵ�mp3�ļ�");
		}
	}

	private void initMusicListView(){
		mMusicList = (ListView)findViewById(R.id.udisk_music_list_view);
		mMusicList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, mUdiskMusics));
		mMusicList.setItemsCanFocus(false);
		mMusicList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);	
		mMusicList.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int positon, long id){
				boolean hasSelection = false;
				SparseBooleanArray checked = mMusicList.getCheckedItemPositions();				
				for(int i = 0; i < mUdiskMusics.length; i ++){
					if(checked.get(i)){
						hasSelection = true;
						break;
					}
				}

				enableCopyButton(hasSelection);
			}
		});
	};

	private void initSelectallButton(){
		Button btn = (Button)findViewById(R.id.udisk_music_select_all);
		if(btn != null){
			btn.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v){
					final int count = mMusicList.getCount();
					for(int i = 0; i < count;i ++){
						mMusicList.setItemChecked(i, true);
					}

					enableCopyButton(true);
				}
			});
		}		
	}

	private void initUnselectallButton(){
		Button btn = (Button)findViewById(R.id.udisk_music_unselect_all);
		if(btn != null){
			btn.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v){
					final int count = mMusicList.getCount();
					for(int i = 0; i < count; i ++){
						mMusicList.setItemChecked(i, false);
					}

					enableCopyButton(false);
				}
			});
		}
	}

	private void initCopyButton(){
		Button btn = (Button)findViewById(R.id.udisk_music_copy);
		if(btn != null){
			btn.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v){
					startCopy();
				}
			});

			enableCopyButton(false);
		}
	}

	private void initCloseButton(){
		Button btn = (Button)findViewById(R.id.udisk_music_close_panel);
		if(btn != null){
			btn.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v){
					finish();
				}
			});
		}
	}

	private class ProgressThread extends Thread {
		final static int STATE_DONE = 0;
		final static int STATE_RUNNING = 1;
		final static String fixedAlbumPath="/mnt/sdcard/" + AlbumInfoDb.LOCAL_ALBUM_NAME + "/";

		List<String> toBeCopied;
		int mState;

		ProgressThread(List<String> files) {
			toBeCopied = files;
		}

		// ��ǰ����״̬������ֹͣ�߳�ִ�С�
		public void setState(int state) {
			mState = state;
		}

		public void run() {
			mState = STATE_RUNNING;
			int i = 0;
			int total = toBeCopied.size();
			while (mState == STATE_RUNNING && i < total) {            	
				if(doCopy(toBeCopied.get(i))){
					// ���͵�ǰ������Ϣ��
					i++;
					mMainApp.getmMsgSender().sendCopyProgress(MsgSender.MSG_COPY_PROGRESS, i);
				}else{   
					// ���Ϳ���������Ϣ��ֹͣ�����̡߳�
					mMainApp.getmMsgSender().sendMsg(MsgSender.MSG_COPY_ERROR);
					break;
				}									          
			}            
		}

		private boolean doCopy(String srcFilePath){    		
			if(srcFilePath == null){
				return false;
			}

			File srcFile = new File(srcFilePath);
			if(! srcFile.exists()){
				return false;
			}

			String dstFilePath = fixedAlbumPath + srcFile.getName();
			File dstFile = new File(dstFilePath);

			if(! dstFile.exists()){
				try{
					int totalBytes = 0;
					int readBytes;

					InputStream is = new FileInputStream(srcFile);
					FileOutputStream os = new FileOutputStream(dstFile);
					byte [] buffer = new byte[4096];
					while((readBytes = is.read(buffer)) != -1){
						totalBytes += readBytes;
						os.write(buffer, 0, readBytes);
					}
					is.close();
					os.close();
				}catch(Exception e){
					System.out.println(e.toString());
					return false;
				}
			}

			return true;
		}
	}

	@Override
	public void refreshUi(Message msg) {
		int msgType = msg.what;
		final int total = mToBeCopiedMusics.size();
		if (msgType == MsgSender.MSG_COPY_PROGRESS) {
			int current = msg.arg1;
			if(current <= total){
				mProgressDialog.setProgress(current);
				if(current == total){
					mProgressDialog.setMessage("�������");
				}
			}
		} else if(msgType == MsgSender.MSG_STOP_COPY){                       	  
			removeDialog(PROGRESS_DIALOG);
		} else if(msgType == MsgSender.MSG_COPY_ERROR){
			removeDialog(PROGRESS_DIALOG);
			mMainApp.getmMsgSender().sendStrMsg("SD���ռ䲻�㣬��ɾ��һЩ���������ԣ�");
		}
	}
}
