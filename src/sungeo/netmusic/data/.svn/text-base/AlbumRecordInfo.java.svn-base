package sungeo.netmusic.data;

import java.io.UnsupportedEncodingException;

import sungeo.netmusic.unit.ParsePacket;

public class AlbumRecordInfo{
	private byte		mPlayMode = -1;
	private int 		mPosition;//第几个专辑
	private int 		mSongCount;
	private int 		mCurPlayIndex = 0;
	private boolean 	mIsEmpty;
	private String 		mAlbumName;
	private String[] 	mSongName;
	private String[] 	mSongUrl;
	private String[] 	mSinger;

	/**
	 * 得到专辑的字节流，向网关同步数据时用到
	 * 按照协议格式，生成字节流
	 * @return
	 */
	public byte[] getAlbumSongBytes() {
		int len = 0;
		int sPos = 0;
		byte[] bytes = new byte[64*1024];

		String strPos = String.valueOf(mPosition);
		byte[] posByt = getBytes(strPos);
		len = posByt.length;
		System.arraycopy(posByt, 0, bytes, 0, len);
		bytes[len] = ParsePacket.NETIN_FIELD_SP_BYTE;
		sPos = len + 1;
		len ++;

		if (mAlbumName != null) {
			byte[] nameByt = getNameBytes(mAlbumName);
			len += nameByt.length;
			System.arraycopy(nameByt, 0, bytes, sPos, nameByt.length);
			bytes[len] = ParsePacket.NETIN_FIELD_SP_BYTE;
			sPos = len + 1;
			len ++;
		}

		for (int i = 0; i < mSongCount; i ++) {
			String strId = String.valueOf(i);
			byte[] idByt = getBytes(strId);
			len += idByt.length;
			System.arraycopy(idByt, 0, bytes, sPos, idByt.length);

			bytes[len] = ParsePacket.NETIN_FIELD_SP_BYTE;
			sPos = len + 1;
			len ++;

			if (mSongName[i] == null) {
				continue;
			}
			byte[] sNameBy = getNameBytes(mSongName[i]);
			len += sNameBy.length;
			System.arraycopy(sNameBy, 0, bytes, sPos, sNameBy.length);
			bytes[len] = ParsePacket.NETIN_FIELD_SP_BYTE;
			sPos = len + 1;
			len ++;
		}

		byte[] result = new byte[len];
		System.arraycopy(bytes, 0, result, 0, len);

		return result;
	}

	//得到专辑的字节流，保存专辑信息时要用到
	public byte[] getAlbumBytes() {
		int len = 0;
		int sPos = 0;
		byte[] bytes = new byte[64*1024];

		String strPos = String.valueOf(mPosition);
		byte[] posByt = getBytes(strPos);
		len = posByt.length;
		System.arraycopy(posByt, 0, bytes, 0, len);
		bytes[len] = ParsePacket.NETIN_RECORD_SP_BYTE;
		sPos = len + 1;
		len ++;

		if (mAlbumName != null) {
			byte[] nameByt = getBytes(mAlbumName);
			len += nameByt.length;
			System.arraycopy(nameByt, 0, bytes, sPos, nameByt.length);
			bytes[len] = ParsePacket.NETIN_RECORD_SP_BYTE;
			sPos = len + 1;
			len ++;
		}

		String couStr = String.valueOf(getSongCount());
		byte[] couByt = getBytes(couStr);
		len += couByt.length;
		System.arraycopy(couByt, 0, bytes, sPos, couByt.length);
		bytes[len] = ParsePacket.NETIN_RECORD_SP_BYTE;
		sPos = len + 1;
		len ++;

		int count = 0;
		if (mSongName != null) {
			count = mSongName.length;
		} else {
			mSongCount = 0;
		}
		
		int realCount = 0;
		
		for (int i = 0; i < count; i ++) {
			byte[] sNameBy = getBytes(mSongName[i]);
			if (sNameBy == null) {
				continue;
			}
			len += sNameBy.length;
			System.arraycopy(sNameBy, 0, bytes, sPos, sNameBy.length);
			bytes[len] = ParsePacket.NETIN_FIELD_SP_BYTE;
			sPos = len + 1;
			len ++;
			realCount ++;
		}

		//得到实际的歌曲数目，因为在下载过程中，可能有的没有下载完成，这里要保持 mSongCount与实际的歌曲数目相同
		mSongCount = realCount;
		
		if (mSongCount != 0) {
			bytes[len] = ParsePacket.NETIN_RECORD_SP_BYTE;
			sPos = len + 1;
			len ++;
		}

		if (mSinger != null) {
			for (int i = 0; i < mSongCount; i ++) {
				byte[] sSingerByte = getBytes(mSinger[i]);
				if (sSingerByte == null) {
					continue;
				}
				len += sSingerByte.length;
				System.arraycopy(sSingerByte, 0, bytes, sPos, sSingerByte.length);
				bytes[len] = ParsePacket.NETIN_FIELD_SP_BYTE;
				sPos = len + 1;
				len ++;
			}
		}

		if (mSongCount != 0) {
			bytes[len] = ParsePacket.NETIN_RECORD_SP_BYTE;
			sPos = len + 1;
			len ++;
		}

		return bytes;
	}

	private byte[] getNameBytes(String str) {
		byte[] bytes = null;
		if (str != null) {
			try {
				bytes = str.getBytes("GBK");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return bytes;
	}

	private byte[] getBytes(String str) {
		byte[] bytes = null;
		if (str != null) {
			try {
				bytes = str.getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return bytes;
	}

	public boolean setAlbumRecords(byte[] data) {
		ParsePacket parsePacket = new ParsePacket();
		int sPos = parsePacket.bytePosition(data,ParsePacket.NETIN_RECORD_SP_BYTE, 0);
		if(sPos == -1)
		{
			return false;
		}

		try{

			byte[] tmpPos = new byte[sPos];
			System.arraycopy(data, 0, tmpPos, 0, sPos);
			mPosition = Integer.parseInt(new String(tmpPos));
			sPos ++;

			int len = parsePacket.bytePosition(data, ParsePacket.NETIN_RECORD_SP_BYTE, sPos);
			if (len == -1) {
				return true;
			}

			//名称
			mAlbumName=new String(data,sPos,len - sPos, "UTF-8");
			sPos = len + 1;

			len = parsePacket.bytePosition(data, ParsePacket.NETIN_RECORD_SP_BYTE, sPos);
			if (len == -1) {
				return true;
			}
			byte[] temCut = new byte[len - sPos];
			System.arraycopy(data, sPos, temCut, 0, len - sPos);
			//歌曲数目
			mSongCount = Integer.parseInt(new String(temCut));
			sPos = len + 1;

			if (mSongCount == 0) {
				mSongName = null;
			}
			len = parsePacket.bytePosition(data, ParsePacket.NETIN_RECORD_SP_BYTE, sPos);
			if (len == -1) {
				return true;
			}
			byte[] temName = new byte[len - sPos];
			System.arraycopy(data, sPos, temName, 0, len - sPos);
			setSongNameBytes(temName);
			sPos = len + 1;

			len = parsePacket.bytePosition(data, ParsePacket.NETIN_RECORD_SP_BYTE, sPos);
			if (len == -1) {
				return true;
			}
			byte[] temSinger = new byte[len - sPos];
			System.arraycopy(data, sPos, temSinger, 0, len - sPos);
			setSingerBytes(temSinger);
			sPos = len + 1;

			return true;
		}
		catch(Exception ex)
		{
			return false;
		}
	}

	private boolean setSingerBytes(byte[] data) {
		if (mSongCount == 0) {
			mSinger = null;
			return false;
		}

		ParsePacket parsePacket = new ParsePacket();
		
		int sPos = 0;
		int len = 0;
		mSinger = new String[mSongCount];
		for (int i = 0; i < mSongCount; i ++) {
			len = parsePacket.bytePosition(data, ParsePacket.NETIN_FIELD_SP_BYTE, sPos);
			if (len == -1) {
				return false;
			}

			try{
				mSinger[i] = new String(data,sPos,len - sPos, "UTF-8");
				sPos = len + 1;
			} catch(Exception ex) {
			}
		}
		return true;
	}

	private boolean setSongNameBytes(byte[] data) {
		if (mSongCount == 0) {
			mSongName = null;
			return false;
		}
		
		ParsePacket parsePacket = new ParsePacket();
		
		int sPos = 0;
		int len = 0;
		mSongName = new String[mSongCount];
		for (int i = 0; i < mSongCount; i ++){

			len = parsePacket.bytePosition(data,ParsePacket.NETIN_FIELD_SP_BYTE, sPos);
			if(len == -1) {
				return false;
			}

			try{
				//歌曲名称
				mSongName[i]=new String(data,sPos,len - sPos, "UTF-8");
				sPos = len + 1;
			} catch(Exception ex) {
			}
		}

		return true;
	}

	/*
	 * 仅在本地专辑为空时用到
	 */
	public void clearInfo() {
		mSongCount = 0;
		mSongName = null;
		mSongUrl = null;
	}
	
	public void setAlbumName(String album_name) {
		mAlbumName = album_name;
	}

	public String getAlbumName() {
		return mAlbumName;
	}

	public void setSongCount(int song_count) {
		mSongCount = song_count;
	}

	public int getSongCount() {
		int realCount = 0;
		int count = 0;
		if (mSongName != null) {
			count = mSongName.length;
		} else {
			mSongCount = 0;
		}
		
		for (int i = 0; i < count; i ++) {
			if (mSongName[i] == null) {
				continue;
			}
			realCount ++;
		}
		
		mSongCount = realCount;
		return mSongCount;
	}

	public void setSongName(String[] song_name) {
		mSongName = song_name;
	}

	public String[] getSongName() {
		return mSongName;
	}

	public void setPosition(int position) {
		mPosition = position;
	}

	public int getPosition() {
		return mPosition;
	}

	public void setSongUrl(String[] song_url) {
		mSongUrl = song_url;
	}

	public String[] getSongUrl() {
		return mSongUrl;
	}

	public void setPlayMode(byte play_mode) {
		mPlayMode = play_mode;
	}

	public byte getPlayMode() {
		return mPlayMode;
	}

	public void setEmpty(boolean isEmpty) {
		mIsEmpty = isEmpty;
	}

	public boolean isEmpty() {
		if (mAlbumName == null && mSongName == null &&
				mSongCount == 0 && mPosition == 0) {
			mIsEmpty = true;
		} else {
			mIsEmpty = false;
		}
		return mIsEmpty;
	}

	public void setSinger(String[] singer) {
		mSinger = singer;
	}

	public String[] getSinger() {
		return mSinger;
	}

	public void setCurPlayIndex(int curPlayIndex) {
		this.mCurPlayIndex = curPlayIndex;
	}

	public int getCurPlayIndex() {
		return mCurPlayIndex;
	}
}
