package sungeo.netmusic.activity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import sungeo.netmusic.R;
import sungeo.netmusic.data.AllDataDb;
import sungeo.netmusic.netbase.ParsePbPackage;
import sungeo.netmusic.protocol.AlbumSendPackage.UserDevAlbum;
import sungeo.netmusic.protocol.AlbumSendPackage.UserDevAlbum.AlbumPackage;
import sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat;
import sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat.AlbumInfo;
import sungeo.netmusic.protocol.CategoriesInfo.CategoryList;
import sungeo.netmusic.protocol.CategoriesInfo.CategoryList.CategoryInfo;
import sungeo.netmusic.protocol.DataVersion.Version;
import sungeo.netmusic.protocol.SongsInfo.MusicInfo;
import sungeo.netmusic.protocol.SongsInfo.MusicInfoOfSingleCat;
import sungeo.netmusic.protocol.SongsInfo.MusicInfo.SongInfo;
import sungeo.netmusic.unit.ArrayListAdapter;
import sungeo.netmusic.unit.MsgSender;
import sungeo.netmusic.unit.MultipleAdapter;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class LocalDownloadActivity extends BaseActivity{
	private    int							mCurCatSel;
	private    int 						mCurAlbumId;
	private    int 						mSubAlbumNum;
	private 	int						mServerVersion;
	private    boolean				mSubFinish;
	private 	ListView 				mCateList;
	private 	ListView 				mAlbumInfoList;
	private 	ListView 				mSongInfoList;
	private 	Button	   				mReverseBtn;
	private 	Button	   				mAllSelBtn;
	private 	Button    				mConfirmBtn;
	private   AlertDialog				mSubDialog;
	private   ProgressDialog 		mProgressDialog;
	private 	MultipleAdapter 	mSongAdapter;
	private    ArrayListAdapter	mAlbumAdapter, mCategoryAdapter;
	private    String[]			mLocalAlbumName;
	private 	AllDataDb 			mAllDataDb = new AllDataDb();
	private    ParsePbPackage mParsePb;
	private 	UserDevAlbum 		mUda;
	private    ArrayList<AlbumInfo> 		mCurAlbumArray 		= 	new ArrayList<AlbumInfo>();
	private    ArrayList<SongInfo> 		mCurSongArray 		= 	new ArrayList<SongInfo>();
	private 	ArrayList<CategoryInfo> 	mCurCategoryArray =  new ArrayList<CategoryInfo>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.local_download);

		initAllControl();

		int serial = mMainApp.getmConfig().getSelfSerial();
		if (serial == 0) {
			mMainApp.getmMsgSender().sendStrMsg("请先设置序例号!");
			return;
		}
		mParsePb = new ParsePbPackage();
		mSubFinish = true;

		setBtnListenerEvent();
		
		mAllDataDb.initFileName();
		mParsePb.setmAllDataDb(mAllDataDb);
		mParsePb.startQuery(ParsePbPackage.TYPE_DATA_VERSION);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			int serial = mMainApp.getmConfig().getSelfSerial();
			if (serial == 0) {
				finish();
				return true;
			}
			if (mSubFinish) {
				finish();
			} else {
				mMainApp.getmMsgSender().sendStrMsg("正在提交信息，请稍候返回……");
			}
			
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void initAllControl() {
		mCateList = (ListView) findViewById(R.id.category_list);
		mAlbumInfoList = (ListView) findViewById(R.id.album_info_list);
		mSongInfoList = (ListView) findViewById(R.id.song_info_list);
		mReverseBtn = (Button) findViewById(R.id.reverse_select_btn);
		mAllSelBtn = (Button) findViewById(R.id.all_select_btn);
		mConfirmBtn = (Button) findViewById(R.id.confirm_btn);
	}
	
	private void showBtn() {
		mReverseBtn.setVisibility(View.VISIBLE);
		mAllSelBtn.setVisibility(View.VISIBLE);
		mConfirmBtn.setVisibility(View.VISIBLE);
	}

	private void setBtnListenerEvent() {
		mReverseBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mSongAdapter == null) {
					return;
				}
				mSongAdapter.reverseCheck();
				mSongAdapter.notifyDataSetChanged();
			}});

		mAllSelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mSongAdapter == null) {
					return;
				}
				mSongAdapter.allCheck();
				mSongAdapter.notifyDataSetChanged();
			}});

		mConfirmBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onSubmit();
			}});
	}

	private void initCatList(String[] data, int[] catId) {
		mCategoryAdapter = new ArrayListAdapter(this, data);
		mCateList.setAdapter(mCategoryAdapter);
		mCateList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long arg3) {
				mCategoryAdapter.setSelected(position);
				mCurCatSel = position;

				showAlbumName(position);
			}});
		mCategoryAdapter.setSelected(0);
		mAllDataDb.generateFileName(catId);
		mAllDataDb.saveCatId(catId);
		mParsePb.startQuery(ParsePbPackage.TYPE_LOCAL_ALBUM);
	}

	private boolean[] getSongSelected(int count) {
		boolean[] ret = null;
		if (count > 0) {
			ret = new boolean[count];
		} else {
			return null;
		}

		if (mUda != null) {
			int size = mUda.getAlbumPakCount();
			AlbumPackage ap = null;
			String songId = null;
			for (int i = 0; i < size; i ++) {
				ap = mUda.getAlbumPak(i);
				if (mCurAlbumId == Integer.valueOf(ap.getAlbumId())) {
					songId = ap.getSongId();
					break;
				}
			}

			if (songId != null) {
				String[] id = songId.split(",");
				int sId = 0;
				int len = mCurSongArray.size();
				SongInfo songInfo = null;
				size = id.length;

				for (int i = 0; i < len; i ++) {
					songInfo = (SongInfo) mCurSongArray.get(i);
					size = id.length;
					for (int j = 0; j < size; j ++) {
						try {
							sId = Integer.valueOf(id[j]);
						} catch (NumberFormatException e) {
							e.printStackTrace();
						}

						if (songInfo.getSongId() == sId) {
							ret[i] = true;
						}
					}
				}
			}
		}
		return ret;
	}

	private void initSongList(String[] eleName, String[] singerName) {
		int count = 0;
		if (eleName != null) {
			count = eleName.length;
		}
		boolean[] ret = getSongSelected(count);

		if (ret == null) {
			mSongInfoList.setAdapter(null);
			return;
		}

		if (mSongAdapter == null) {
			mSongAdapter = new MultipleAdapter(this, eleName, singerName);
			mSongAdapter.setmSel(ret);
			mSongInfoList.setAdapter(mSongAdapter);
		} else {
			mSongAdapter.setmSel(ret);
			mSongAdapter.setmName(eleName);
			mSongAdapter.setmSinger(singerName);
			mSongAdapter.notifyDataSetChanged();
		}

		mSongInfoList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int index,
					long arg3) {
				mSongAdapter.reverseCheck(index);
				mSongAdapter.notifyDataSetChanged();
			}});
	}

	private void initAlbumList(String[] albumName) {
		if (mAlbumAdapter != null) {
			if (albumName != null && albumName.length > 0) {
				mCurAlbumId = ((AlbumInfo)mCurAlbumArray.get(0)).getAlbumId();
				showSongName();
			}

			mAlbumAdapter.setmElementName(albumName);
			mAlbumAdapter.notifyDataSetChanged();
			mAlbumAdapter.setSelected(0);

			return;
		}

		mAlbumAdapter = new ArrayListAdapter(this, albumName);
		mAlbumInfoList.setAdapter(mAlbumAdapter);
		mAlbumInfoList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mAlbumAdapter.setSelected(position);
				mCurAlbumId = ((AlbumInfo)mCurAlbumArray.get(position)).getAlbumId();
				showSongName();
			}});
		mCurAlbumId = ((AlbumInfo)mCurAlbumArray.get(0)).getAlbumId();
	}

	private void onSubmit() {
		if (mUda == null) {
			return;
		}

		boolean flag = false;
		int size = mUda.getAlbumPakCount();
		AlbumPackage ap = null;
		for (int i = 0; i < size; i ++) {
			ap = mUda.getAlbumPak(i);
			if (mCurAlbumId == Integer.valueOf(ap.getAlbumId())) {
				mSubAlbumNum = Integer.valueOf(ap.getAlbumNum());
				flag = true;
				break;
			}
		}

		if (flag) {
			doSubmit();
		} else {
			if (!mSongAdapter.hasSelected()) {
				mMainApp.getmMsgSender().sendStrMsg("请选择歌曲……");
				return;
			}
			showSubDialog();
		}
	}

	public String stringToHexString(String strPart) {
		String hexString = "";
		for (int i = 0; i < strPart.length(); i++) {
			int ch = (int) strPart.charAt(i);
			String strHex = Integer.toHexString(ch);
			hexString = hexString + strHex;
		}
		return hexString;
	}

	private void doSubmit() {
		AlbumPackage.Builder asp = AlbumPackage.newBuilder();
		asp.setAlbumId(mCurAlbumId);
		asp.setAlbumNum(String.valueOf(mSubAlbumNum));
		String strSerial = Integer.toHexString(mMainApp.getmConfig().getSelfSerial());
		asp.setDevSerial(strSerial);
		asp.setSongCount(mSongAdapter.getSelCount());
		boolean[] ret = mSongAdapter.getmSel();
		int len = ret.length;
		StringBuffer songIdStr = new StringBuffer();
		for (int i = 0; i < len; i ++) {
			if (ret[i]) {
				int songId = ((SongInfo)mCurSongArray.get(i)).getSongId();
				songIdStr.append(songId);
				songIdStr.append(",");
			}
		}
		songIdStr.deleteCharAt(songIdStr.length() - 1);
		asp.setSongId(songIdStr.toString());

		ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
		try {
			asp.build().writeTo(baos);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		mSubFinish = false;
		
		mParsePb.setmBaos(baos);
		mParsePb.submitData();
	}

	private void checkVersion(int sversion) {
		FileInputStream fis = mAllDataDb.getDataVersionFis();
		mServerVersion = sversion;
		int version = 0;
		if (fis != null) {
			Version.Builder dataVersion = null;
			try {
				dataVersion = Version.parseFrom(fis).toBuilder();
				fis.close();
				version = dataVersion.getDataVersion();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (version != sversion) {
			//先清除所有数据，再保存版本，然后再开始查询分类
			mAllDataDb.clearAllData();
		}

		mParsePb.startQuery(ParsePbPackage.TYPE_CATEGORY);
	}

	private void saveDataVersion() {
		Version.Builder dataVersion = Version.newBuilder();

		FileOutputStream fos = mAllDataDb.getDataVersionFos();

		dataVersion.setDataVersion(mServerVersion);
		try {
			dataVersion.build().writeTo(fos);
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initCatData() {
		if (mCateList.getAdapter() != null) {
			return;
		}
		mCateList.post(new Runnable() {

			@Override
			public void run() {
				showCategoryName();
			}});
	}

	private void initLocalAlbumData() {
		if (mAlbumInfoList.getAdapter() != null) {
			return;
		}

		mParsePb.startQuery(ParsePbPackage.TYPE_ALBUM);
	}

	private void initAlbumData() {
		updateDefaultAlbumName();
		mParsePb.startQuery(ParsePbPackage.TYPE_SONG);
	}

	private void initSongData() {
		if (mSongInfoList.getAdapter() != null) {
			return;
		}
		mSongInfoList.post(new Runnable() {
			@Override
			public void run() {
				showSongName();
			}
		});
	}

	private void showCategoryName() {
		FileInputStream fis = mAllDataDb.getCatFis();

		if (fis == null) {
			Log.d("函数showCategoryName出错", "mAllDataDb.getCatFis返回null");
			return;
		}

		CategoryList catList = null;
		mCurCategoryArray.clear();
		try {
			catList = CategoryList.parseFrom(fis);
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (catList == null) {
			return;
		}
		final String[] data;
		final int[] catId;
		int count = catList.getCatInfoCount();
		if (count <= 0) {
			return;
		}

		CategoryInfo catInfo = null;
		data = new String[count + 1];
		catId = new int[count + 1];
		data[0] = "本地专辑";
		catId[0] = 0;
		for (int i = 1; i < count + 1; i ++) {
			catInfo = catList.getCatInfo(i - 1);
			mCurCategoryArray.add(catInfo);
			data[i] = catInfo.getCategoryName();
			catId[i] = catInfo.getCatId();
		}
		initCatList(data, catId);
	}

	private void updateDefaultAlbumName() {
		String[] data = null;
		FileInputStream fis = null;
		if (mUda == null) {
			fis = mAllDataDb.getLocalAlbumFis();
			if (fis == null) {
				Log.d("函数updateDefaultAlbumName()出错", "mAllDataDb.getLocalAlbumFis()返回为null");
				return;
			}

			try {
				mUda = UserDevAlbum.parseFrom(fis);
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		int count = mUda.getAlbumPakCount();
		data = new String[count];
		boolean flag = false;

		int size = mAllDataDb.getCatCount();
		int length = 0;
		AlbumListOfSingleCat alosc = null;
		AlbumInfo album = null;
		AlbumPackage ap = null;
		AlbumInfo.Builder albumInfo = null;

		mCurAlbumArray.clear();

		if (mLocalAlbumName == null) {
			mLocalAlbumName = new String[count];
		}

		for (int i = 0; i < count; i ++) {
			ap = mUda.getAlbumPak(i);

			flag = false;
			for (int j = 0; j < size; j ++) {
				fis = mAllDataDb.getAlbumFisById(j);

				if (fis == null) {
					Log.d("mAllDataDb.getAlbumFisById(j)返回为null", "函数updateDefaultAlbumName()出错,j值为："+j);
					continue;
				}
				try {
					alosc = AlbumListOfSingleCat.parseFrom(fis);
					fis.close();
					fis = null;
				} catch (IOException e) {
					e.printStackTrace();
				}

				length = alosc.getAlbumInfoCount();

				for (int k = 0; k < length; k ++) {
					album = alosc.getAlbumInfo(k);

					if (album.getAlbumId() == Integer.valueOf(ap.getAlbumId())) {
						albumInfo = AlbumInfo.newBuilder();
						albumInfo.setAlbumId(Integer.valueOf(ap.getAlbumId()));
						albumInfo.setAlbumName(album.getAlbumName());
						data[i] = album.getAlbumName();
						mLocalAlbumName[i] = data[i];
						mCurAlbumArray.add(album);
						flag = true;
						break;
					}
				}

				if (flag) {
					break;
				}
			}
		}

		initAlbumList(data);
	}

	private void showAlbumName(int index) {
		String[] data = null;
		mCurAlbumArray.clear();
		if (index == 0) {
			updateDefaultAlbumName();
			return;
		} else {
			FileInputStream fis = mAllDataDb.getAlbumFisById(index - 1);

			if (fis == null) {
				mMainApp.getmMsgSender().sendStrMsg("正在更新……");
				Log.d("showAlbumName函数出错", "mAllDataDb.getAlbumFisById返回为null，index为：" + index);
				return;
			}
			AlbumListOfSingleCat alos = null;
			try {
				alos = AlbumListOfSingleCat.parseFrom(fis);
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (alos == null) {
				mMainApp.getmMsgSender().sendStrMsg("没有专辑呦");
				return;
			}
			int count = alos.getAlbumInfoCount();
			AlbumInfo albumInfo = null;
			data = new String[count];
			for (int i = 0; i < count; i ++) {
				albumInfo = alos.getAlbumInfo(i);
				mCurAlbumArray.add(albumInfo);
				data[i] = albumInfo.getAlbumName();
			}
		}

		initAlbumList(data);
	}

	private void showSongName() {
		mCurSongArray.clear();
		int count = 0;
		boolean flag = false;
		MusicInfo mi = null;
		FileInputStream fis = null;
		MusicInfoOfSingleCat miosc = null;

		int len = 1;
		if (mCurCatSel == 0) {
			len = mAllDataDb.getCatCount();
		} 

		int autoValue = 0;

		for (int i = 0; i < len; i++) {
			if (len == 1) {
				autoValue = mCurCatSel - 1;
			} else {
				autoValue = i;
			}

			fis = mAllDataDb.getSongFisById(autoValue);

			if (fis == null) {
				Log.d("函数showSongName出错", "mAllDataDb.getSongFisById返回null，autoValue值为："+autoValue);
				continue;
			}

			try {
				miosc = MusicInfoOfSingleCat.parseFrom(fis);
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (miosc == null) {
				mMainApp.getmMsgSender().sendStrMsg("没有歌曲呦");
				return;
			}
			count = miosc.getMusicInfoCount();

			flag = false;
			for (int j = 0; j < count; j ++) {
				mi = miosc.getMusicInfo(j);
				if (mi.getAblumId() == mCurAlbumId) {
					flag = true;
					break;
				}
			}

			if (flag) {
				break;
			}
		}

		if (mi == null) {
			initSongList(null, null);
			return;
		}
		count = mi.getSongInfoCount();
		String[] songName = new String[count];
		String[] singerName = new String[count];
		for (int j = 0; j < count; j ++) {
			SongInfo si = mi.getSongInfo(j);
			mCurSongArray.add(si);
			songName[j] = si.getSongName();
			singerName[j] = si.getSongSinger();
		}

		initSongList(songName, singerName);
	}

	private void showSubDialog() {
		if (mSubDialog != null) {
			mSubDialog.show();
			return;
		}
		Builder b = new AlertDialog.Builder(this).setTitle("选择要替换的专辑");

		mSubDialog = b.create();
		ListView listView = new ListView(mSubDialog.getContext());

		String[] name = null;
		if (mLocalAlbumName == null) {
			return;
		}
		
		int len = mLocalAlbumName.length;
		name = new String[len];
		for (int i = 0; i < len; i ++) {
			name[i] = (i + 1) + ". " + mLocalAlbumName[i];
		}

		listView.setAdapter(new ArrayAdapter<String>(mSubDialog.getContext(), android.R.layout.simple_expandable_list_item_1, name));
		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
				mSubDialog.dismiss();
				mSubDialog = null;
				mSubAlbumNum = index + 1;

				doSubmit();
			}
		});
		mSubDialog.setView(listView);

		mSubDialog.show();
	}

	private void updateLocalPb(Object obj) {
		boolean ret = mAllDataDb.removeLocalAlbumInfo();

		if (!ret || obj == null) {
			return;
		}

		mMainApp.getmMsgSender().sendStrMsg("提交成功！");

		String str = (String)obj;

		byte[] buffer = null;
		
		try {
			buffer = str.getBytes("ISO8859_1");
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
		}
		
		if (buffer == null) {
			mMainApp.getmMsgSender().sendStrMsg("保存失败！");
			return;
		}
		
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
		AlbumPackage ap = null;
		try {
			ap = AlbumPackage.parseFrom(bais);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		if (ap == null) {
			return;
		}

		int albumId = ap.getAlbumId();
		String albumNum = ap.getAlbumNum();
		int songCount = ap.getSongCount();
		String songId = ap.getSongId();
		String strSerial = Integer.toHexString(mMainApp.getmConfig().getSelfSerial());

		AlbumPackage.Builder apb;
		UserDevAlbum.Builder uda = UserDevAlbum.newBuilder();
		int count = mUda.getAlbumPakCount();
		for (int i = 0; i < count; i ++) {
			apb = mUda.getAlbumPak(i).toBuilder();
			if (apb.getAlbumNum().equals(albumNum)) {
				apb.clear();
				apb.setAlbumId(albumId);
				apb.setAlbumNum(albumNum);
				apb.setDevSerial(strSerial);
				apb.setSongCount(songCount);
				apb.setSongId(songId);
				updateLocalValue(i, albumId);
			}
			uda.addAlbumPak(apb);
		}

		FileOutputStream fos = null;
		fos = mAllDataDb.getLocalAlbumFos();
		if (fos == null) {
			return;
		}

		try {
			uda.build().writeTo(fos);
			uda = null;
			fos.close();
			fos = null;
		} catch (IOException e) {
			e.printStackTrace();
		}

		FileInputStream fis = null;
		fis = mAllDataDb.getLocalAlbumFis();
		if (fis == null) {
			return;
		}

		try {
			mUda.toBuilder().clear();
			mUda = UserDevAlbum.parseFrom(fis);
			fis.close();
			fis = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		mSubFinish = true;
	}
	
	private void updateLocalValue(int index, int albumId) {
		if (mLocalAlbumName == null) {
			return;
		}
		
		int len = mLocalAlbumName.length;
		if (index >= len || index < 0) {
			return;
		}
		
		boolean flag = false;
		String albumName = null;
		AlbumListOfSingleCat alosc = null;
		AlbumInfo album = null;
		FileInputStream fis = null;
		
		int size = mAllDataDb.getCatCount();
		
		for (int i = 0; i < size; i ++) {
			flag = false;
			fis = mAllDataDb.getAlbumFisById(i);
			
			if (fis == null) {
				Log.d("mAllDataDb.getAlbumFisById(j)返回为null", "函数updateLocalValue()出错,j值为："+i);
				continue;
			}
			try {
				alosc = AlbumListOfSingleCat.parseFrom(fis);
				fis.close();
				fis = null;
			} catch (IOException e) {
				e.printStackTrace();
			}

			len = alosc.getAlbumInfoCount();

			for (int j = 0; j < len; j ++) {
				album = alosc.getAlbumInfo(j);

				if (album.getAlbumId() == albumId) {
					albumName = album.getAlbumName();
					flag = true;
					break;
				}
			}
			
			if (flag) {
				break;
			}
		}
		
		mLocalAlbumName[index] = albumName;
	}

	private void cancelProgressDialog() {
		if (mProgressDialog == null) {
			return;
		}

		mProgressDialog.cancel();
		mProgressDialog = null;
	}

	private void showProgressDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			return;
		}
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(this); //实例化 
		}
		
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); //设置进度条风格，风格为圆形，旋转的 
		mProgressDialog.setTitle("互联网背景音乐"); //设置ProgressDialog 标题 
		mProgressDialog.setMessage("正在加载数据……"); //设置ProgressDialog 提示信息 
		mProgressDialog.setIndeterminate(false); //设置ProgressDialog 的进度条是否不明确 
		mProgressDialog.setCancelable(false); //设置ProgressDialog 是否可以按退回按键取消 
		mProgressDialog.show(); //让ProgressDialog显示
	}

	@Override
	public void refreshUi(Message msg) {
		if (msg == null) {
			return;
		}
		int type = msg.what;

		switch (type) {
		case MsgSender.MSG_PARSE_PB_CATEGORY_FINISH:
			initCatData();
			break;
		case MsgSender.MSG_PARSE_PB_LOCAL_FINISH:
			initLocalAlbumData();
			break;
		case MsgSender.MSG_PARSE_PB_ALBUM_FINISH:
			initAlbumData();
			break;
		case MsgSender.MSG_PARSE_PB_SONG_FINISH:
			saveDataVersion();
			initSongData();
			cancelProgressDialog();
			showBtn();
			break;
		case MsgSender.MSG_PARSE_PB_VERSION_FINISH:
			checkVersion(msg.arg1);
			break;
		case MsgSender.MSG_SUBMIT_PB_ALBUM_FINISH:
			updateLocalPb(msg.obj);
			break;
		case MsgSender.MSG_START_PARSE_PB:
			showProgressDialog();
			break;
		case MsgSender.MSG_PARSE_PB_ERROR:
			cancelProgressDialog();
			mMainApp.getmMsgSender().sendStrMsg("网络异常");
			finish();
			break;
		default:
			break;
		}
	}
}
