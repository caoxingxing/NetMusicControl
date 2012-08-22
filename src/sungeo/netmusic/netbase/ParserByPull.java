package sungeo.netmusic.netbase;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import sungeo.netmusic.data.AlbumRecordInfo;
import sungeo.netmusic.data.MainApplication;
import sungeo.netmusic.download.DownloadMgr;
import android.util.Log;

public class ParserByPull {
	private String 				mSerial;
	private int 				mCount;
	private int 				mIndex 				= 0;
	private int 				mCounter 			= 0;
	private int 				mCurAlbumId 		= 0;
	private String 				mVersion			= new String();
	private boolean 			mQueryAlbumFinish 	= false;
	private String   			mAlbumName;
	private String[] 			mSongUrl;
	private String[] 			mSongName;
	private String[] 			mSinger;
	private ArrayList<Integer> 	mAlbumId 			= new ArrayList<Integer>();
	//����XmlPullParser������XML�ļ�    
	public void parserXml(InputStream inStream) throws Throwable {    
		XmlPullParserFactory pullFactory = null;
		XmlPullParser parser = null;    
		int eventType = XmlPullParser.END_DOCUMENT;
		//========����XmlPullParser,�����ַ�ʽ=======  
		//��ʽһ:ʹ�ù�����XmlPullParserFactory 
		pullFactory = XmlPullParserFactory.newInstance();
		//��ʽ��:ʹ��Android�ṩ��ʵ�ù�����android.util.Xml  
		//XmlPullParser parser = Xml.newPullParser();
		parser = pullFactory.newPullParser();
		parser.setInput(inStream, "UTF-8");
		eventType = parser.getEventType();

		//ֻҪ�����ĵ������¼�����һֱѭ��    
		while(eventType != XmlPullParser.END_DOCUMENT) {    
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT://������ʼ�ĵ��¼�           
				break;    
			case XmlPullParser.START_TAG://������ʼԪ���¼�        
				String name = parser.getName();
				if ("device".equals(name)) {
					mVersion = parser.getAttributeValue(1);
					checkVersion();
				} else if ("album".equals(name)) {
					mQueryAlbumFinish = true;
					mAlbumName = parser.getAttributeValue(0);
					mCount = Integer.valueOf(parser.getAttributeValue(1));
					if (mCount > 0) {
						mSongUrl = new String[mCount];
						mSinger = new String[mCount];
						mSongName = new String[mCount];
					} else if (mCount == 0) {
						querySingleFinish();
					}
				} else if ("music".equals(name)) {
					if (mSongUrl != null && mCounter < mCount) {
						mSongUrl[mCounter] = parser.getAttributeValue(1);
						String songName = parser.getAttributeValue(0);
						setSongName(songName);
						mSinger[mCounter] = parser.getAttributeValue(2);
						mCounter ++;
					}
				}
				break;    
			case XmlPullParser.END_TAG://��������Ԫ���¼�     
				if (mCounter < mCount) {
					break;
				}
				if("device".equals(parser.getName())){    
					startQuaryAlbum();
				} else if ("album".equals(parser.getName())) {
					querySingleFinish();
				}
				break;    
			default:    
				break;    
			}    
			eventType = parser.next();
		}     
	}    

	private void querySingleFinish() {
		if (!mQueryAlbumFinish) {
			return;
		}
		saveSingleAlbum();
		initAllValue();
		int len = mAlbumId.size();
		if (mIndex == len - 1) {
			clearValue();
			startDownAlbum();
		} else {
			queryNextAlbum();
		}
	}
	
	private void startDownAlbum() {
		if (MainApplication.getInstance().checkNetwork()) {
			MainApplication.getInstance().getmMsgSender().sendShowToRecMsg();

			MainApplication.getInstance().setmDownMgr(new DownloadMgr());

			MainApplication.getInstance().getmDownMgr().startDownloadAlbum();

		} else {
			MainApplication.getInstance().getmMsgSender().sendShowWifiMsg();
		}
	}

	private void saveSingleAlbum() {
		AlbumRecordInfo album = new AlbumRecordInfo();
		album.setAlbumName(mAlbumName);
		album.setPosition(mCurAlbumId);
		album.setSongCount(mCount);
		album.setSongName(mSongName);
		album.setSongUrl(mSongUrl);
		album.setSinger(mSinger);
		MainApplication.getInstance().getmDownAlbum().add(album);
	}

	private void initAllValue() {
		mAlbumName  = null;
		mCurAlbumId = 0;
		mCount      = 0;
		mSongName   = null;
		mSongUrl    = null;
		mSinger     = null;
	}
	
	private void clearValue() {
		mQueryAlbumFinish = false;
		mIndex = 0;
		if (mAlbumId != null) {
			mAlbumId.clear();
		}
	}

	private void queryNextAlbum() {
		mQueryAlbumFinish = false;
		mIndex ++;
		startQuaryAlbum();
	}

	private void setSongName(String name) {
		if (name == null) {
			return;
		}
		StringBuffer str = new StringBuffer();
		str.append(name);
		str.append(getMusicFormat(mSongUrl[mCounter]));
		mSongName[mCounter] = str.toString();
	}

	private String getMusicFormat(String url) {
		int pos = url.lastIndexOf(".");
		String format = "";
		if (pos == -1) {
			format = url;
		} else {
			format = url.substring(pos);
		}

		return format;
	}

	private void startQuaryAlbum() {
		mCounter = 0;
		int len = mAlbumId.size();
		if (mIndex < 0 || mIndex >= len) {
			return;
		}

		int id = mAlbumId.get(mIndex);

		if (id != 0 && id != mCurAlbumId) {
			StringBuffer strId = new StringBuffer();
			strId.append(id);
			mCurAlbumId = id;
			queryAlbum(strId.toString());
			return;
		}
	}

	private void checkVersion() {
		if (mVersion == null) {
			return;
		}

		mAlbumId.clear();
		//String localVer = UIGlobal.getLocalVersion();
		String localVer = MainApplication.getInstance().getmConfig().getVersion();
		//1������á�.����Ϊ�ָ��Ļ�������������д����String.split("\\."),����������ȷ�ķָ�����������String.split(".");
		//2������á�|����Ϊ�ָ��Ļ�������������д����String.split("\\|"),����������ȷ�ķָ�����������String.split("|");
		//��.���͡�|������ת���ַ�������ü�"\\";
		//3�������һ���ַ������ж���ָ����������á�|����Ϊ���ַ������磺��account=? and uu =? or n=?��,���������ָ�������������String.split("and|or");
		String[] serverVersion = mVersion.split("\\.");

		String[] localVersion = localVer.split("\\.");
		localVer = null;

		int serLen = serverVersion.length;
		int locLen = localVersion.length;
		if (serLen != locLen) {
			return;
		}

		int count = 0;
		for (int i = 0; i < locLen; i ++) {
			if (i == locLen - 1) {//���һ��ר���Ǳ��صģ�����Ҫ�Ա�
				continue;
			}
			if (!serverVersion[i].equals(localVersion[i])) {
				count ++;
				mAlbumId.add(i + 1);
			}
		}

		if (count != 0) {
			//�رղ�ѯ�汾��ʱ������������ɺ󣬻����´�
			MainApplication.getInstance().getmVersionMgr().stopCheckVersion();
			MainApplication.getInstance().getmDownAlbum().clear();
		}

		serverVersion = null;
		localVersion = null;
		MainApplication.getInstance().setmServerVersion(mVersion);
	}

	public boolean quary(String urlStr) {
		try {
			URL url = new URL(urlStr);

			try {
				parserXml(url.openStream());
			} catch (Throwable e) {
				e.printStackTrace();
			}

			urlStr = null;

			Log.d("ִ�в�ѯ�汾", "��ѯ����");
			System.gc();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean quaryVersion() {
		StringBuffer str = new StringBuffer();
		//str.append("http://192.168.10.13/Service.asmx/GetVersion?serial=");
		str.append("http://music.sungeo.cn:8002/Service.asmx/GetVersion?serial=");
		str.append(mSerial);
		return quary(str.toString());
	}

	public boolean queryAlbum(String id) {
		StringBuffer str = new StringBuffer();
		//str.append("http://192.168.10.13/Service.asmx/DownloadMusics?serial=");
		str.append("http://music.sungeo.cn:8002/Service.asmx/DownloadMusics?serial=");
		str.append(mSerial);
		str.append("&AlbumNum=");
		str.append(id);
		return quary(str.toString());
	}

	public void setmSerial(String mSerial) {
		this.mSerial = mSerial;
	}

	public String getmSerial() {
		return mSerial;
	}
}
