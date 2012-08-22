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
	//采用XmlPullParser来解析XML文件    
	public void parserXml(InputStream inStream) throws Throwable {    
		XmlPullParserFactory pullFactory = null;
		XmlPullParser parser = null;    
		int eventType = XmlPullParser.END_DOCUMENT;
		//========创建XmlPullParser,有两种方式=======  
		//方式一:使用工厂类XmlPullParserFactory 
		pullFactory = XmlPullParserFactory.newInstance();
		//方式二:使用Android提供的实用工具类android.util.Xml  
		//XmlPullParser parser = Xml.newPullParser();
		parser = pullFactory.newPullParser();
		parser.setInput(inStream, "UTF-8");
		eventType = parser.getEventType();

		//只要不是文档结束事件，就一直循环    
		while(eventType != XmlPullParser.END_DOCUMENT) {    
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT://触发开始文档事件           
				break;    
			case XmlPullParser.START_TAG://触发开始元素事件        
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
			case XmlPullParser.END_TAG://触发结束元素事件     
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
		//1、如果用“.”作为分隔的话，必须是如下写法：String.split("\\."),这样才能正确的分隔开，不能用String.split(".");
		//2、如果用“|”作为分隔的话，必须是如下写法：String.split("\\|"),这样才能正确的分隔开，不能用String.split("|");
		//“.”和“|”都是转义字符，必须得加"\\";
		//3、如果在一个字符串中有多个分隔符，可以用“|”作为连字符，比如：“account=? and uu =? or n=?”,把三个都分隔出来，可以用String.split("and|or");
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
			if (i == locLen - 1) {//最后一个专辑是本地的，不需要对比
				continue;
			}
			if (!serverVersion[i].equals(localVersion[i])) {
				count ++;
				mAlbumId.add(i + 1);
			}
		}

		if (count != 0) {
			//关闭查询版本计时器，在下载完成后，会重新打开
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

			Log.d("执行查询版本", "查询结束");
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
