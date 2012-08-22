package sungeo.netmusic.netbase;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import sungeo.netmusic.data.AllDataDb;
import sungeo.netmusic.data.MainApplication;
import sungeo.netmusic.protocol.AlbumSendPackage.UserDevAlbum;
import sungeo.netmusic.protocol.AlbumSendPackage.UserDevAlbum.AlbumPackage;
import sungeo.netmusic.protocol.AlbumsInfo.AlbumListOfSingleCat;
import sungeo.netmusic.protocol.CategoriesInfo.CategoryList;
import sungeo.netmusic.protocol.CategoriesInfo.CategoryList.CategoryInfo;
import sungeo.netmusic.protocol.DataVersion.Version;
import sungeo.netmusic.protocol.SongsInfo.MusicInfoOfSingleCat;
import sungeo.netmusic.unit.MsgSender;
import android.util.Log;

public class ParsePbPackage {
	private 	int[] 						mCategoryId = null;
	private    int 						mVersion;
	private 	AllDataDb 			mAllDataDb;
	private ByteArrayOutputStream mBaos;
	private int 	mDataType;
	public final static int	TYPE_ALBUM = 1;
	public final static int	TYPE_SONG = 2;
	public final static int	TYPE_LOCAL_ALBUM = 3;
	public final static int	TYPE_DATA_VERSION = 4;
	public final static int   TYPE_CATEGORY	= 5;
	private String requestTag = "请求数据：";
	private String sendMsgTag = "发送消息";

	public void submitData() {
		PostDataThread pdt = new PostDataThread();
		Thread thread = new Thread(pdt);
		thread.start();
	}

	public void startQuery(int type) {
		mDataType = type;
		QueryDataThread qat = new QueryDataThread();
		Thread thread = new Thread(qat);
		thread.start();
	}

	class QueryDataThread implements Runnable {

		@Override
		public void run() {
			parseDataByType();
		}
	}

	class PostDataThread implements Runnable {
		@Override
		public void run() {
			postData();
		}
	}

	private void postData() {
		if (mBaos == null) {
			Log.d("参数没有赋值：", "变量mBaos为null");
			return;
		}
		String url = "http://music.sungeo.cn:8002/localmusicdownloadservice/index.php?m=album&f=downloadMusics&t=proto";
		//String url = "http://192.168.10.110/index.php?m=album&f=downloadMusics&t=proto";
		//String url = "http://192.168.11.110/testAlbum.php?m=album";
		DefaultHttpClient dhc = new DefaultHttpClient();
		HttpPost httpRequest = new HttpPost(url);
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		try {
			String str = mBaos.toString("ISO8859_1");
			params.add(new BasicNameValuePair("album", str));
			mBaos.close();

			MainApplication.getInstance().getmMsgSender().sendStrMsg("正在提交……");
			
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.ISO_8859_1));
			HttpResponse httpResponse = dhc.execute(httpRequest);
			int code = httpResponse.getStatusLine().getStatusCode();
			if (code == 200) {
				MainApplication.getInstance().getmMsgSender().sendPostForm(str);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			httpRequest.abort();
			params.clear();
		}
	}

	private void parseDataByType() {
		String url = null;
		HttpGet request = null;
		HttpResponse response = null;
		HttpEntity entity  = null;
		InputStream is = null;
		FileOutputStream fos = null;
		CategoryList catList = null;
		AlbumListOfSingleCat alos = null;
		MusicInfoOfSingleCat mios = null;
		Version version = null;
		FileInputStream fis = null;
		int len = 1;

		switch (mDataType) {
		case TYPE_CATEGORY:
			Log.d(requestTag, "正在请求分类信息");
			fis = getmAllDataDb().getCatFis();
			url = "http://music.sungeo.cn:8002/localmusicdownloadservice/index.php?m=category&f=getCategorys&t=proto";
			break;
		case TYPE_ALBUM:
			Log.d(requestTag, "正在请求专辑信息");
			len = mCategoryId.length;
			break;
		case TYPE_SONG:
			Log.d(requestTag, "正在请求歌曲信息");
			len = mCategoryId.length;
			break;
		case TYPE_LOCAL_ALBUM:
			Log.d(requestTag, "正在请求本地专辑信息");
			fis = getmAllDataDb().getLocalAlbumFis();
			String strSerial = Integer.toHexString(MainApplication.getInstance().getmConfig().getSelfSerial());
			url = "http://music.sungeo.cn:8002/localmusicdownloadservice/index.php?m=album&f=getUserAlbums&devSerial=" + strSerial + "&t=proto";
			break;
		case TYPE_DATA_VERSION:
			Log.d(requestTag, "正在请求版本信息");
			fis = getmAllDataDb().getDataVersionFis();
			url = "http://music.sungeo.cn:8002/localmusicdownloadservice/index.php?m=dataversion&f=getVersion&t=proto";
			break;
		default:
			break;
		}

		for (int i = 0; i < len; i ++) {
			if (mDataType == TYPE_ALBUM) {
				fis = mAllDataDb.getAlbumFisById(i);
				url = "http://music.sungeo.cn:8002/localmusicdownloadservice/index.php?m=album&f=getAlbums&categoryId=" + mCategoryId[i] + "&t=proto";
			} else if (mDataType == TYPE_SONG) {
				fis = mAllDataDb.getSongFisById(i);
				url = "http://music.sungeo.cn:8002/localmusicdownloadservice/index.php?m=music&f=getMusics&categoryId=" + mCategoryId[i] + "&t=proto";
			}

			if (fis != null) {
				try {
					if (mDataType == TYPE_CATEGORY) {
						CategoryList cat = CategoryList.parseFrom(fis);
						setmCatCount(cat);
					}
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

				/**
				 * 由于背景音乐序例号是可修改的，在修改后会引起显示的本地专辑名称不对
				 * 所以在此每次都查询本地专辑，本地专辑只有五个，信息量比较少，因此不
				 * 影响查询速度
				 */
				if (mDataType != TYPE_DATA_VERSION &&
						mDataType != TYPE_LOCAL_ALBUM) {
					continue;
				}
			}

			request = new HttpGet(url);

			try {
				response = new DefaultHttpClient().execute(request);

				MainApplication.getInstance().getmMsgSender().sendMsg(MsgSender.MSG_START_PARSE_PB);
				
				int code = response.getStatusLine().getStatusCode();
				if (code == 200) {
					entity = response.getEntity();
					is = entity.getContent();

					switch (mDataType) {
					case TYPE_CATEGORY:
						catList = CategoryList.parseFrom(is);
						fos = getmAllDataDb().getCatFos();
						catList.writeTo(fos);
						setmCatCount(catList);
						break;
					case TYPE_ALBUM:
						alos = AlbumListOfSingleCat.parseFrom(is);
						fos = getmAllDataDb().getAlbumFosById(i);
						alos.writeTo(fos);
						break;
					case TYPE_SONG:
						mios = MusicInfoOfSingleCat.parseFrom(is); 
						fos = getmAllDataDb().getSongFosById(i);
						mios.writeTo(fos);
						break;
					case TYPE_LOCAL_ALBUM:
						fos = getmAllDataDb().getLocalAlbumFos();
						compositorByNum(is, fos);
						break;
					case TYPE_DATA_VERSION:
						version = Version.parseFrom(is);
						/*fos = getmAllDataDb().getDataVersionFos();
						version.writeTo(fos);*/
						mVersion = version.getDataVersion();
						break;
					default:
						break;
					}

					if (fos != null) {
						fos.close();
					}

					if (is != null) {
						is.close();
					}
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
				MainApplication.getInstance().getmMsgSender().sendMsg(MsgSender.MSG_PARSE_PB_ERROR);
				return;
			} finally {
				request.abort();

				url = null;
				request = null;
				response = null;
				entity  = null;
				is = null;
				fos = null;
				catList = null;
				alos = null;
				mios = null;
				version = null;
				fis = null;
			}
		}
		notifyUi(0, 0);
	}

	private void notifyUi(int curIndex, int total) {
		if (curIndex != total) {
			return;
		}
		switch (mDataType) {
		case TYPE_CATEGORY:
			Log.d(sendMsgTag, "通知UI分类信息解析完成");
			MainApplication.getInstance().getmMsgSender().sendMsg(MsgSender.MSG_PARSE_PB_CATEGORY_FINISH);
			break;
		case TYPE_ALBUM:
			Log.d(sendMsgTag, "通知UI专辑信息解析完成");
			MainApplication.getInstance().getmMsgSender().sendMsg(MsgSender.MSG_PARSE_PB_ALBUM_FINISH);
			break;
		case TYPE_LOCAL_ALBUM:
			Log.d(sendMsgTag, "通知UI本地专辑信息解析完成");
			MainApplication.getInstance().getmMsgSender().sendMsg(MsgSender.MSG_PARSE_PB_LOCAL_FINISH);
			break;
		case TYPE_SONG:
			Log.d(sendMsgTag, "通知UI歌曲信息解析完成");
			MainApplication.getInstance().getmMsgSender().sendMsg(MsgSender.MSG_PARSE_PB_SONG_FINISH);
			break;
		case TYPE_DATA_VERSION:
			Log.d(sendMsgTag, "通知UI版本信息解析完成");
			MainApplication.getInstance().getmMsgSender().sendQueryVersionFinish(mVersion);
			break;
		default:
			break;
		}
	}

	private void compositorByNum(InputStream is, FileOutputStream fos) {
		if (is == null || fos == null) {
			return;
		}
		UserDevAlbum.Builder uda = null;
		try {
			uda = UserDevAlbum.parseFrom(is).toBuilder();
			is.close();
			is = null;
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (uda == null) {
			return;
		}
		int len = uda.getAlbumPakCount();
		ArrayList<AlbumPackage> listItem = new ArrayList<AlbumPackage>(len);

		AlbumPackage apb = null;
		for (int i = 0; i < len; i ++) {
			apb = uda.getAlbumPak(i);
			listItem.add(apb);
		}
		uda.clear();

		Comparator<AlbumPackage> comparator = new Comparator<AlbumPackage>(){
			public int compare(AlbumPackage ap1, AlbumPackage ap2) {
				//按歌曲NUM排序
				int num1 = -1;
				int num2 = 0;
				try {
					num1 = Integer.valueOf(ap1.getAlbumNum());
					num2 = Integer.valueOf(ap2.getAlbumNum());
				} catch (NumberFormatException e) {
					Log.d("数据有错", "从数据库获得的数据有错");
				}
				
				return num1 - num2;
			}
		};
			  
		//这里就会自动根据规则进行排序
		Collections.sort(listItem,comparator);
		comparator = null;
		  
		for (int i = 0; i < len; i ++) {
			apb = (AlbumPackage) listItem.get(i);
			uda.addAlbumPak(apb);
		}
		listItem.clear();
		listItem = null;
		apb = null;

		try {
			uda.build().writeTo(fos);

			uda = null;
			fos.close();
			fos = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setmCatCount(CategoryList catList) {
		if (catList == null) {
			return;
		}

		int count = catList.getCatInfoCount();
		mCategoryId = new int[count];
		for (int i = 0; i < count; i ++) {
			CategoryInfo ci = catList.getCatInfo(i);
			mCategoryId[i] =  ci.getCatId();
		}
	}

	public void setmCatCount(int[] mCatCount) {
		this.mCategoryId = mCatCount;
	}

	public int[] getmCatCount() {
		return mCategoryId;
	}

	public void setmDataType(int mDataType) {
		this.mDataType = mDataType;
	}

	public int getmDataType() {
		return mDataType;
	}

	public void setmAllDataDb(AllDataDb mAllDataDb) {
		this.mAllDataDb = mAllDataDb;
	}

	public AllDataDb getmAllDataDb() {
		return mAllDataDb;
	}

	public void setmBaos(ByteArrayOutputStream mBaos) {
		this.mBaos = mBaos;
	}

	public ByteArrayOutputStream getmBaos() {
		return mBaos;
	}
}
