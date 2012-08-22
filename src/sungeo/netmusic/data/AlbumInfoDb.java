package sungeo.netmusic.data;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;

public class AlbumInfoDb {
	public  final static int 	FIRST_ALBUM_ID     = 1;
	public  final static int 	SECOND_ALBUM_ID    = 2;
	public  final static int 	THIRD_ALBUM_ID     = 3;
	public  final static int 	FOURTH_ALBUM_ID    = 4;
	public  final static int 	FIFITHS_ALBUM_ID   = 5;
	public  final static int 	SIXTH_ALBUM_ID	   = 6;
	public 	final static String LOCAL_ALBUM_NAME   = "用户专辑";
	private final static String sFirstAlbumDbName  = "first_album";
	private final static String sSecondAlbumDbName = "second_album";
	private final static String sThirdAlbumDbName  = "third_album";
	private final static String sFourthAlbumDbName = "fourth_album";
	private final static String sFifthsAlbumDbName = "fifths_album";
	private final static String sSixthAlbumDbName  = "sixth_album";

	private MainApplication mMainApp;

	public AlbumInfoDb() {
		mMainApp = MainApplication.getInstance();
	}
	/**
	 *
	 * @param type
	 * @param data
	 */
	public void saveData(int type, byte[] data) {
		switch (type) {
		case FIRST_ALBUM_ID:
			saveData(sFirstAlbumDbName, data);
			break;
		case SECOND_ALBUM_ID:
			saveData(sSecondAlbumDbName, data);
			break;
		case THIRD_ALBUM_ID:
			saveData(sThirdAlbumDbName, data);
			break;
		case FOURTH_ALBUM_ID:
			saveData(sFourthAlbumDbName, data);
			break;
		case FIFITHS_ALBUM_ID:
			saveData(sFifthsAlbumDbName, data);
			break;
		case SIXTH_ALBUM_ID:
			saveData(sSixthAlbumDbName, data);
			break;
		default:
			break;
		}
	}

	/**
	 * 保存数据到文件中
	 * @param name  文件名称
	 * @param data  要保存的数据
	 */
	private void saveData(String name, byte[] data) {
		FileOutputStream os = null;

		if (data == null) {
			return;
		}
		try {
			os = mMainApp.openFileOutput(name, Context.MODE_PRIVATE);
			int len = data.length;
			byte[] bodyLen = new byte[4];
			bodyLen[3] = (byte)len;
			bodyLen[2] = (byte)(len >> 8);
			bodyLen[1] = (byte)(len >> 16);
			bodyLen[0] = (byte)(len >> 24);
			os.write(bodyLen);
			os.write(data);
		} catch (FileNotFoundException e) {
			mMainApp.getmMsgSender().showStringMsg(name+"文件不存在", 1);
		} catch (IOException e) {
			mMainApp.getmMsgSender().showStringMsg("打开文件出错", 1);
		} finally {
			try {
				if (os != null) {
					os.close();
				}

			} catch (IOException e) {
				mMainApp.getmMsgSender().showStringMsg("关闭打开文件出错", 1);
			}
		}
	}

	public byte[] getData(int type) {
		byte[] tmp = null;
		switch (type) {
		case FIRST_ALBUM_ID:
			tmp = getDbData(sFirstAlbumDbName);
			break;
		case SECOND_ALBUM_ID:
			tmp = getDbData(sSecondAlbumDbName);
			break;
		case THIRD_ALBUM_ID:
			tmp = getDbData(sThirdAlbumDbName);
			break;
		case FOURTH_ALBUM_ID:
			tmp = getDbData(sFourthAlbumDbName);
			break;
		case FIFITHS_ALBUM_ID:
			tmp = getDbData(sFifthsAlbumDbName);
			break;
		case SIXTH_ALBUM_ID:
			tmp = getDbData(sSixthAlbumDbName);
		default:
			break;
		}
		return tmp;
	}
	private byte[] getDbData(String name) {
		byte[] data = null;
		FileInputStream is = null;
		try {
			is = mMainApp.openFileInput(name);
			byte[] bodyLen = new byte[4];
			int len = is.read(bodyLen);
			if (len != 4) {
				mMainApp.getmMsgSender().showStringMsg("读取的数据包长度不正确", 1);
			}
			len = (((bodyLen[0] & 255) << 24) | ((bodyLen[1] & 255) << 16) | ((bodyLen[2] & 255) << 8) | (bodyLen[3] & 255));
			data = new byte[len];
			int length = is.read(data);
			if (len != length) {
				mMainApp.getmMsgSender().showStringMsg("从数据文件中读取数据不正确", 1);
			}
		} catch (FileNotFoundException e) {
			mMainApp.getmMsgSender().showStringMsg(name+"文件不存在", 1);
		} catch (IOException e) {
			mMainApp.getmMsgSender().showStringMsg("打开文件出错", 1);
		} finally {
			try {
				if (is != null) {
					is.close();
				}

			} catch (IOException e) {
				mMainApp.getmMsgSender().showStringMsg("关闭打开文件出错", 1);
			}
		}
		return data;
	}
}
