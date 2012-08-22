package sungeo.netmusic.objects;

import java.io.UnsupportedEncodingException;

import sungeo.netmusic.data.MainApplication;
import sungeo.netmusic.unit.ParsePacket;

public class AlbumDataRecord implements PackageRecord{
	private int BUFFER_LEN = 64 * 1024;
	@Override
	public byte[] getRecordBytes() {
		int sPos = 0;
		int len = 0;
		byte[] info = new byte[BUFFER_LEN];
		int serial = MainApplication.getInstance().getmConfig().getSelfSerial();
		String strSer = String.valueOf(serial);
		byte[] byteSer = null;
		try {
			byteSer = strSer.getBytes("GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		len = byteSer.length;
		System.arraycopy(byteSer, 0, info, 0, len);
		info[len] = ParsePacket.NETIN_FIELD_SP_BYTE;
		sPos = len + 1;
		len ++;
		
		char address = MainApplication.getInstance().getmConfig().getSelfAddr();
		String add = String.valueOf((int)address);
		byte[] addr = null;
		try {
			addr = add.getBytes("GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		len += addr.length;
		System.arraycopy(addr, 0, info, sPos, addr.length);
		info[len] = ParsePacket.NETIN_FIELD_SP_BYTE;
		sPos = len + 1;
		len ++;

		for (int i = 0; i < 6; i ++) {
			byte[] data = MainApplication.getInstance().getAlbumBytesByPos(i + 1);
			if (data != null) {
				len += data.length;
				if (len >= BUFFER_LEN) {
					return info;
				}
				System.arraycopy(data, 0, info, sPos, data.length);
				info[len] = ParsePacket.NETIN_RECORD_SP_BYTE;
				sPos = len + 1;
				len ++;
				info[len] = ParsePacket.NETIN_FIELD_SP_BYTE;
				sPos = len + 1;
				len ++;
			}
		}

		byte[] result = new byte[len];
		System.arraycopy(info, 0, result, 0, len);
		return result;
	}

	@Override
	public boolean setRecordBytes(byte[] data) {
		return false;
	}
}
