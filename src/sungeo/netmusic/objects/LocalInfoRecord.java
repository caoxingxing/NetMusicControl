package sungeo.netmusic.objects;

import java.io.UnsupportedEncodingException;

import sungeo.netmusic.unit.ParsePacket;


public class LocalInfoRecord implements PackageRecord{
	private String 	mSerial;
	private String 	mIpAddr;
	
	@Override
	public byte[] getRecordBytes() {
		try {
			byte[] ret = null;

			byte[] addr = mIpAddr.getBytes("UTF-8");

			byte[] ser = mSerial.getBytes("UTF-8");

			ret = new byte[addr.length + ser.length + 2];
			int len = addr.length;
			System.arraycopy(addr, 0, ret, 0, addr.length);
			ret[addr.length] = ParsePacket.NETIN_FIELD_SP_BYTE;
			len ++;
			System.arraycopy(ser, 0, ret, len, ser.length);
			ret[ret.length - 1] = ParsePacket.NETIN_FIELD_SP_BYTE;

			return ret;
		} catch (UnsupportedEncodingException ex) {
			return null;
		}
	}

	@Override
	public boolean setRecordBytes(byte[] data) {
		return false;
	}

	public void setIpAddr(String ipAddr) {
		mIpAddr = ipAddr;
	}

	public String getIpAddr() {
		return mIpAddr;
	}

	public void setSerial(String serial) {
		mSerial = serial;
	}

	public String getSerial() {
		return mSerial;
	}

}
