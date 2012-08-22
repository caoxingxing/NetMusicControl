package sungeo.netmusic.data;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.util.Log;

public class AllDataDb {
	private MainApplication mMainApp;
	private final static String categoryFileName = "category";
	private final static String localAlbumInfoName = "localalbuminfo";
	private final static String allDataVersionName = "alldataversion";
	private final static String categoryId = "categoryid";
	private String[] catAlbumFileName = null;
	private String[] catSongFileName = null; 
	private final static String tag = "文件流打开失败";

	public AllDataDb() {
		mMainApp = MainApplication.getInstance();
	}

	public void initFileName() {
		FileInputStream fis = null;
		byte[] buf = null;
		try {
			fis = mMainApp.openFileInput(categoryId);
			
			byte[] bodyLen = new byte[4];
            int len = fis.read(bodyLen);
            
            if (len != 4) {
            	fis.close();
                return;
            }
            len = (((bodyLen[0] & 255) << 24) | ((bodyLen[1] & 255) << 16) | ((bodyLen[2] & 255) << 8) | (bodyLen[3] & 255));
			buf = new byte[len];
			int length = fis.read(buf);
			if (len != length) {
				fis.close();
				return;
			}
			fis.close();
		} catch (FileNotFoundException e) {
			Log.d(tag, e.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (buf == null) {
			return;
		}
		
		String bufStr = new String(buf);
		
		String[] str = bufStr.split(";");
		int len = str.length;
		int[] catId = new int[len];
		for (int i = 0; i < len; i ++) {
			catId[i] = Integer.valueOf(str[i]);
		}
		
		generateFileName(catId);
	}
	
	public boolean generateFileName(int[] catId) {
		if (catId == null) {
			return false;
		}

		int count = catId.length - 1;
		setCatAlbumFileName(new String[count]);
		setCatSongFileName(new String[count]);
		
		//第一个分类为本地专辑
		for (int i = 0; i < count; i ++) {
			getCatAlbumFileName()[i] = "cat_" + "album_" + catId[i + 1];
			getCatSongFileName()[i] = "cat_" + "song_" + catId[i + 1];
		}

		return true;
	}
	
	public void saveCatId(int[] catId) {
		FileOutputStream fos = null;
		
		StringBuffer strId = new StringBuffer();
		
		if (catId == null) {
			return;
		}
		
		int count = catId.length;
		for (int i = 0; i < count; i ++) {
			strId.append(catId[i]);
			strId.append(";");
		}
		
		try {
			fos = mMainApp.openFileOutput(categoryId, Context.MODE_PRIVATE);
			
			byte[] buf = strId.toString().getBytes();
			
			byte[] bodyLen = new byte[4];
			
			int len = buf.length;
            bodyLen[3] = (byte)len;
            bodyLen[2] = (byte)(len >> 8);
            bodyLen[1] = (byte)(len >> 16);
            bodyLen[0] = (byte)(len >> 24);
            
			fos.write(bodyLen);
			fos.write(buf);
			fos.close();
		} catch (FileNotFoundException e) {
			Log.d(tag, e.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public FileInputStream getCatFis() {
		FileInputStream fis = null;
		
		try {
			fis = mMainApp.openFileInput(categoryFileName);
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
			Log.d(tag, e.toString());
		}
		
		return fis;
	}
	
	public FileOutputStream getCatFos() {
		FileOutputStream fos = null;
		
		try {
			fos = mMainApp.openFileOutput(categoryFileName, Context.MODE_PRIVATE);
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
			Log.d(tag, e.toString());
		}
		
		return fos;
	}
	
	public boolean removeLocalAlbumInfo() {
		return mMainApp.deleteFile(localAlbumInfoName);
	}
	
	public FileInputStream getLocalAlbumFis() {
		FileInputStream fis = null;
		
		try {
			fis = mMainApp.openFileInput(localAlbumInfoName);
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
			Log.d(tag, e.toString());
		}
		
		return fis;
	}
	
	public FileOutputStream getLocalAlbumFos() {
		FileOutputStream fos = null;
		
		try {
			fos = mMainApp.openFileOutput(localAlbumInfoName, Context.MODE_PRIVATE);
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
			Log.d(tag, e.toString());
		}
		
		return fos;
	}
	
	public FileInputStream getDataVersionFis() {
		FileInputStream fis = null;
		
		try {
			fis = mMainApp.openFileInput(allDataVersionName);
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
			Log.d(tag, e.toString());
		}
		
		return fis;
	}
	
	public FileOutputStream getDataVersionFos() {
		FileOutputStream fos = null;
		
		try {
			fos = mMainApp.openFileOutput(allDataVersionName, Context.MODE_PRIVATE);
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
			Log.d(tag, e.toString());
		}
		
		return fos;
	}
	
	public FileInputStream getAlbumFisById(int index) {
		FileInputStream fis = null;
		if (getCatAlbumFileName() == null) {
			return fis;
		}
		
		int len = getCatAlbumFileName().length;
		if (index >= len || index < 0) {
			return fis;
		}
		
		try {
			fis = mMainApp.openFileInput(getCatAlbumFileName()[index]);
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
			Log.d(tag, e.toString());
		}
		
		return fis;
	}
	
	public FileOutputStream getAlbumFosById(int index) {
		FileOutputStream fos = null;
		if (getCatAlbumFileName() == null) {
			return fos;
		}

		int len = getCatAlbumFileName().length;
		if (index >= len || index < 0) {
			return fos;
		}
		
		try {
			fos = mMainApp.openFileOutput(getCatAlbumFileName()[index], Context.MODE_PRIVATE);
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
			Log.d(tag, e.toString());
		}
		
		return fos;
	}
	
	public FileInputStream getSongFisById(int index) {
		FileInputStream fis = null;
		
		if (getCatSongFileName() == null) {
			return null;
		}
		
		int len = getCatSongFileName().length;
		if (index >= len || index < 0) {
			return null;
		}
		
		try {
			fis = mMainApp.openFileInput(getCatSongFileName()[index]);
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
			Log.d(tag, e.toString());
		}
		
		return fis;
	}
	
	public FileOutputStream getSongFosById(int index) {
		FileOutputStream fos = null;
		if (getCatSongFileName() == null) {
			return fos;
		}

		int len = getCatSongFileName().length;
		if (index >= len || index < 0) {
			return fos;
		}
		
		try {
			fos = mMainApp.openFileOutput(getCatSongFileName()[index], Context.MODE_PRIVATE);
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
			Log.d(tag, e.toString());
		}
		
		return fos;
	}

	public void clearAllData() {
		mMainApp.deleteFile(categoryFileName);
		mMainApp.deleteFile(localAlbumInfoName);
		
		int len = 0;
		if (getCatAlbumFileName() != null) {
			len = getCatAlbumFileName().length;
			
			for (int i = 0; i < len; i ++) {
				if (getCatAlbumFileName()[i] == null) {
					continue;
				}
				mMainApp.deleteFile(getCatAlbumFileName()[i]);
			}
		}
		
		if (getCatSongFileName() != null) {
			len = getCatSongFileName().length;
			
			for (int i = 0; i < len; i ++) {
				if (getCatSongFileName()[i] == null) {
					continue;
				}
				
				boolean flag = mMainApp.deleteFile(getCatSongFileName()[i]);
				if (flag) {
					//删除成功
				}
			}
		}
		
		mMainApp.deleteFile(categoryId);
	}
	
	public int getCatCount() {
		if (catAlbumFileName == null && catSongFileName == null) {
			return 0;
		}
		
		return catSongFileName.length;
	}
	
	public void setCatAlbumFileName(String[] catAlbumFileName) {
		this.catAlbumFileName = catAlbumFileName;
	}

	public String[] getCatAlbumFileName() {
		return catAlbumFileName;
	}

	public void setCatSongFileName(String[] catSongFileName) {
		this.catSongFileName = catSongFileName;
	}

	public String[] getCatSongFileName() {
		return catSongFileName;
	}
}
