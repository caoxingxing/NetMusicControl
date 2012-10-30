package sungeo.netmusic.download;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import com.mobclick.android.MobclickAgent;

import sungeo.netmusic.data.MainApplication;
import android.net.http.AndroidHttpClient;
import android.os.MemoryFile;
import android.util.Log;


public class DownloadThread extends Thread {
	private int 			mDownloadPercent 	= 0;
	private int 			mDownloadSpeed 		= 0;
	private int 			mUsedTime			= 0;
	private int 			mFileSize;
	private int 			mDownloadedSize;
	private long 			mStartTime;
	private long 			mCurTime;
	private String 			mUrlStr;
	private String 			mFileName;
	private String 			mSavePath;
	private MemoryFile mMemFile;
	private byte[] 			mBuffer = new byte[BUFFER_SIZE];
	private MainApplication mMainApp;
	private static final int BUFFER_SIZE 		= 1024 * 64;	

	public DownloadThread() {
		mMainApp = MainApplication.getInstance();
	}
	//��URL,����·��,�������������졣
	public void initValue(String URL, String savePath, String fileName) {
		mUrlStr = utf8UrlEncode(URL);

		mSavePath = savePath;
		mFileName = fileName; 
	}
	/**
	 * Utf8URL����
	 * @param s
	 * @return
	 */
	public String utf8UrlEncode(String text) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c >= 0 && c <= 255) {
				result.append(c);
			}else {
				byte[] b = new byte[0];
				try {
					b = Character.toString(c).getBytes("UTF-8");
				}catch (Exception ex) {
				}
				for (int j = 0; j < b.length; j++) {
					int k = b[j];
					if (k < 0) k += 256;
					result.append("%" + Integer.toHexString(k).toUpperCase());
				}
			}
		}
		return result.toString();
	}

	@Override
	public void run() {
		//download();
		clientDownload();
	}

	private boolean saveToFile() {
		boolean flag = false;
		File file = null;
		file = new File(mSavePath + mFileName);

		if (file.exists()) {
			file.delete();
		}

		try {
			flag = file.createNewFile();
			if (!flag) {
				return flag;
			}

			FileOutputStream os = new FileOutputStream(file);
			InputStream stream = mMemFile.getInputStream();
			int curPos = 0;
			int endPos = mMemFile.length();
			while(curPos < endPos) {
				int len = stream.read(mBuffer, 0, BUFFER_SIZE);
				if (len == -1) {
					break;
				}

				os.write(mBuffer, 0, len);

				curPos += len;

				mDownloadedSize = curPos;
			}

			stream.close();
			os.close();
			mMemFile.close();
			flag = true;
		} catch (IOException e) {
			mMemFile.close();
			e.printStackTrace();
		}

		long fileSize = file.length();
		if (fileSize == 0 || mDownloadedSize != mFileSize) {
			return false;
		}

		return flag;
	}

	private boolean saveToMemFile(InputStream inputStream) {
		boolean flag = false;
		if (inputStream == null) {
			return flag;
		}
		try {
			mMemFile = new MemoryFile(null, mFileSize);
			BufferedInputStream bis = new BufferedInputStream(inputStream);
			int total = 0;
			while (total < mFileSize) {
				int len = bis.read(mBuffer, 0, BUFFER_SIZE);

				if (len == -1) {
					break;
				}

				mMemFile.writeBytes(mBuffer, 0, total, len);
				total += len;
			}
			bis.close();
			flag = true;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return flag;
	}

	private void clientDownload() {
		AndroidHttpClient client = AndroidHttpClient.newInstance("sungeo");
		HttpGet getRequest = new HttpGet(mUrlStr);
		try {
			mStartTime = System.currentTimeMillis();
			HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				downloadError(false);
				client.close();
				delayReDownload();
				mMainApp.getmMsgSender().sendStrMsg("�����û����Ӧ");
				return;
			} else {
				setDownloadedSize(0);
			}

			final HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream inputStream = null;

				try {
					mFileSize = (int)entity.getContentLength();
					if (mFileSize <= 0) {
						client.close();
						delayReDownload();
						downloadError(true);
						mMainApp.getmMsgSender().sendStrMsg("��ȡ�ļ���Сʧ��");
						return;
					}
					inputStream = entity.getContent();
					if (!saveToMemFile(inputStream)) {
						client.close();
						delayReDownload();
						return;
					}
					inputStream.close();

					if (!saveToFile()) {
						client.close();
						delayReDownload();
						return;
					}
					//�������صİٷֱ�
					if (mFileSize <= 0) {
						mFileSize = 1;
					}

					client.close();
					client = null;

					//��ȡ��ǰʱ�䣬����ƽ�������ٶ�
					mCurTime = System.currentTimeMillis();
					mUsedTime =(int)((mCurTime-mStartTime)/1000);
					if(mUsedTime==0)
						mUsedTime =1;
					mDownloadSpeed = (getDownloadedSize()/mUsedTime)/1024;
					downloadNextSong();
				} catch (IOException e) {
					uploadLog(e);
					delayReDownload();
				} finally {
					if (inputStream != null) {
						inputStream.close();
					}
					entity.consumeContent();
				}
			}
		} catch (Exception e) {
			getRequest.abort();
			uploadLog(e);
			delayReDownload();
		} finally {
			if (client != null) {
				client.close();
			}
		}
	}

	private void uploadLog(Exception e) {
		StringBuffer strBuf = new StringBuffer();
		int serial = MainApplication.getInstance().getmConfig().getSelfSerial();
		String serStr = Integer.toHexString(serial);
		strBuf.append(serStr);
		strBuf.append("����Ϊ��");
		strBuf.append(e.toString());
		MobclickAgent.onEvent(MainApplication.getInstance(), "download_thread_error", strBuf.toString());
	}

	private void download() {
		byte[] buf = new byte[BUFFER_SIZE];
		try {
			mStartTime = System.currentTimeMillis();
			URL url = new URL(mUrlStr);

			//mMainApp.getmMsgSender().printLog("׼�����ظ�����" + mFileName);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5*1000);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
			conn.setRequestProperty("Accept-Language", "zh-CN");
			conn.setRequestProperty("Referer", mUrlStr); 
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.connect();

			if (conn.getResponseCode()==200) {
				mFileSize = conn.getContentLength();//������Ӧ��ȡ�ļ���С
				setDownloadedSize(0);
				if (mFileSize <= 0) {
					conn.disconnect();
					delayReDownload();

					downloadError(true);
					//MainApplication.getInstance().getmMsgSender().printLog("��ȡ�ļ���Сʧ��");
					//MainApplication.getInstance().getmMsgSender().printLog("����URLΪ��"+mUrlStr);
					mMainApp.getmMsgSender().sendStrMsg("��ȡ�ļ���Сʧ��");
					return;
				}
			} else {
				downloadError(false);
				conn.disconnect();
				delayReDownload();
				//MainApplication.getInstance().getmMsgSender().printLog("�����û����Ӧ");
				//MainApplication.getInstance().getmMsgSender().printLog("����URLΪ��"+mUrlStr);
				mMainApp.getmMsgSender().sendStrMsg("�����û����Ӧ");
				return;
			}

			//startTime = System.currentTimeMillis();
			//��ȡ��ʼ���ص�ʱ�䣬���������ٶȡ�
			InputStream inStream = conn.getInputStream();
			MemoryFile memFile = new MemoryFile(null, mFileSize);
			BufferedInputStream bis = new BufferedInputStream(inStream);
			int total = 0;
			while (total < mFileSize) {
				int len = bis.read(buf, 0, BUFFER_SIZE);

				if (len == -1) {
					break;
				}

				memFile.writeBytes(buf, 0, total, len);
				total += len;
			}
			bis.close();

			File file = null;
			file = new File(mSavePath + mFileName);

			if (file.exists()) {
				file.delete();
			}

			boolean result = file.createNewFile();
			if (result) {
				//MainApplication.getInstance().getmMsgSender().printLog("���������ļ��ɹ�");
			} else {
				//MainApplication.getInstance().getmMsgSender().printLog("���������ļ�ʧ��");
				//MainApplication.getInstance().getmMsgSender().printLog("����URLΪ��"+mUrlStr);
			}
			FileOutputStream os = new FileOutputStream(file);
			InputStream stream = memFile.getInputStream();
			int curPos = 0;
			int endPos = memFile.length();
			while(curPos < endPos) {
				int len = stream.read(buf, 0, BUFFER_SIZE);
				if (len == -1) {
					break;
				}
				os.write(buf);

				curPos += len;

				mDownloadedSize = curPos;
			}

			stream.close();
			os.close();
			memFile.close();

			long fileSize = file.length();
			if (fileSize == 0 || mDownloadedSize != mFileSize) {
				conn.disconnect();
				//MainApplication.getInstance().getmMsgSender().printLog("������СΪ0,��������");
				//MainApplication.getInstance().getmMsgSender().printLog("����URLΪ��"+mUrlStr);
				delayReDownload();
				return;
			}

			//�������صİٷֱ�
			if (mFileSize <= 0) {
				mFileSize = 1;
			}

			conn.disconnect();
			conn = null;

			//��ȡ��ǰʱ�䣬����ƽ�������ٶ�
			mCurTime = System.currentTimeMillis();
			mUsedTime =(int)((mCurTime-mStartTime)/1000);
			if(mUsedTime==0)
				mUsedTime =1;
			mDownloadSpeed = (getDownloadedSize()/mUsedTime)/1024;
			//MainApplication.getInstance().getmMsgSender().printLog("���سɹ�����ʼ������һ��");
			downloadNextSong();
		} catch (IOException e) {
			//MainApplication.getInstance().getmMsgSender().printLog("�����̳߳�����������");
			//MainApplication.getInstance().getmMsgSender().printLog("����URLΪ��"+mUrlStr);
			Log.d("���������߳��쳣��", e.toString());
			uploadLog(e);
			delayReDownload();
		}
	}

	/*
	 * 1.��ʱ2S���������أ���Ϊ�����ع����������жϣ�
	 * ��ʱ�������̻߳����꣬
	 * ����ʱ����Ͽ�����Ϣ���ܻ�û�й�����
	 * Ϊ�˱���������⣬�������̳߳����쳣ʱ�ȵ�2S���ټ�������������
	 * Ȼ���ٽ�����һ������
	 * 2.��һ��ԭ���ǣ�ֱ�ӵ���reDownload�����´������̣߳�����ʱ��ǰ�߳̿��ܻ�û�н���
	 * ����������жϵ�����£����ܻ�˲��򿪺ö������̣߳����ı���Ĵ����ڴ�
	 */
	private void delayReDownload() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				reDownload();
			}}, 2000);
	}

	private void reDownload() {
		if (mMainApp.getmDownMgr() == null) {
			return;
		}

		if (!mMainApp.checkNetwork()) {
			mMainApp.getmDownMgr().intermitDownload();
			return;
		}
		mMainApp.getmDownMgr().reDownload();
	}

	private void downloadNextSong() {
		if (mMainApp.getmDownMgr() == null) {
			return;
		}

		mMainApp.getmDownMgr().downloadNextSong();
	}

	//��ȡ���ذٷֱ�
	public int getDownloadPercent(){

		return mDownloadPercent;
	}

	//��ȡ�����ٶ�
	public int getDownloadSpeed(){
		return mDownloadSpeed;
	}

	public void setDownloadedSize(int downloadedSize) {
		mDownloadedSize = downloadedSize;
	}

	public int getDownloadedSize() {
		return mDownloadedSize;
	}

	public void setFileSize(int fileSize) {
		mFileSize = fileSize;
	}

	public int getFileSize() {
		return mFileSize;
	}

	private void downloadError(boolean flag) {
		StringBuffer strBuf = new StringBuffer();
		int serial = MainApplication.getInstance().getmConfig().getSelfSerial();
		String serStr = Integer.toHexString(serial);
		strBuf.append(serStr);
		if (flag) {
			strBuf.append("�ļ���С��ȡʧ��");
		} else {
			strBuf.append("HTTP��Ӧ����");
		}

		MobclickAgent.onEvent(MainApplication.getInstance(), "download_error", strBuf.toString());
	}
}
