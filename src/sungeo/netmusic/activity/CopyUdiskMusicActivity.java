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
	// U盘上的一级文件、目录名列表。
	private String [] 		mUdiskMusics;
	// U盘上选中的一级文件、目录名列表（待拷贝）。
	private List<String> 	mToBeCopiedMusics;	
	// 最终拷贝文件时用到的进度条。
	private final int PROGRESS_DIALOG = 0;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

		setContentView(R.layout.copy_udisk_music);

		// 获取U盘中的背景音乐列表。
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
			mProgressDialog.setMessage("开始拷贝歌曲");		
			mProgressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "退出", new DialogInterface.OnClickListener(){
				// 按下进度条上“退出”按钮，停止拷贝。
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

	// 禁用或启用“拷贝”按钮。
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
			// 不以“.”开头的目录，都可以拷贝。
			if(dir.isDirectory() && !dir.getName().startsWith(".")){				
				return true;
			}else{
				return false;
			}
		}
	}

	// 遍历U盘根目录和一级子目录下所有后缀为“.mp3"的文件，并保存到udiskMusics中。
	private void getUdiskMusics(){
		File udiskDir = new File(MainApplication.getInstance().getmUdiskMountedPath());					

		// 音乐搜索结果路径缓存。
		List<String> mp3Paths = new ArrayList<String>();

		try{
			// 获取根目录下的所有音乐。
			File [] mp3Files = udiskDir.listFiles(new Mp3Filter());
			for(int i = 0; i < mp3Files.length; i ++){
				mp3Paths.add(mp3Files[i].getPath());
			}

			// 逐个获取第一级子目录下的所有音乐。
			File [] firstLevelDirs = udiskDir.listFiles(new DirFilter());
			for(int i = 0; i < firstLevelDirs.length; i ++){				
				mp3Paths.add(firstLevelDirs[i].getPath());
			}
		}catch(SecurityException e){
			Log.e("FILE_ACCESS", e.toString());
		}	

		// 将音乐列表转换成ListView适配器使用的格式。
		mUdiskMusics = new String [mp3Paths.size()];
		for(int i = 0; i < mUdiskMusics.length; i ++){
			mUdiskMusics[i] = mp3Paths.get(i);
		}
	}

	private void countSelectedFile(String path){
		File file = new File(path);
		if(file.isDirectory()){
			// 拷贝整个目录中的Mp3文件到“用户专辑”中。
			File [] mp3Files = file.listFiles(new Mp3Filter());
			for(int i = 0; i < mp3Files.length; i ++){
				mToBeCopiedMusics.add(mp3Files[i].getPath());
			}
		}else{
			// 拷贝单个文件。
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
			mMainApp.getmMsgSender().sendStrMsg("没有找到mp3文件");
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

		// 当前工作状态，用于停止线程执行。
		public void setState(int state) {
			mState = state;
		}

		public void run() {
			mState = STATE_RUNNING;
			int i = 0;
			int total = toBeCopied.size();
			while (mState == STATE_RUNNING && i < total) {            	
				if(doCopy(toBeCopied.get(i))){
					// 发送当前进度消息。
					i++;
					mMainApp.getmMsgSender().sendCopyProgress(MsgSender.MSG_COPY_PROGRESS, i);
				}else{   
					// 发送拷贝出错消息，停止拷贝线程。
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
					mProgressDialog.setMessage("拷贝完成");
				}
			}
		} else if(msgType == MsgSender.MSG_STOP_COPY){                       	  
			removeDialog(PROGRESS_DIALOG);
		} else if(msgType == MsgSender.MSG_COPY_ERROR){
			removeDialog(PROGRESS_DIALOG);
			mMainApp.getmMsgSender().sendStrMsg("SD卡空间不足，请删除一些歌曲后再试！");
		}
	}
}
