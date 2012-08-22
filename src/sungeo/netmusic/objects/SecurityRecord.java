/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sungeo.netmusic.objects;

import sungeo.netmusic.unit.ParsePacket;

/**
 *
 * @author caoxingxing
 */
public class SecurityRecord implements PackageRecord{
	private char 	mAddress;
	private byte 	mIndex;
	private String 	mName;
	
	public byte[] getRecordBytes() {
		return null;
	}

	public boolean setRecordBytes(byte[] data) {
		int sPos = 0;
		ParsePacket parsePacket = new ParsePacket();
		parsePacket.bytePosition(data, ParsePacket.NETIN_FIELD_SP_BYTE, 0);

		try {
			int len = parsePacket.bytePosition(data, ParsePacket.NETIN_FIELD_SP_BYTE, sPos);

			byte[] temADD = new byte[len - sPos];
			System.arraycopy(data, sPos, temADD, 0, len - sPos);
			String tmp = new String(temADD);
			mAddress = (char) Integer.parseInt(tmp);
			sPos = len + 1;

			//Ë÷Òý
			len = parsePacket.bytePosition(data, ParsePacket.NETIN_FIELD_SP_BYTE, sPos);
			byte[] temIndex = new byte[len - sPos];
			System.arraycopy(data, sPos, temADD, 0, len - sPos);
			mIndex = temIndex[0];
			sPos = len + 1;

			//Ì½Í·Ãû³Æ
			len = parsePacket.bytePosition(data, ParsePacket.NETIN_FIELD_SP_BYTE, sPos);
			mName = new String(data, sPos, len - sPos, "GBK");

			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * @return the address
	 */
	public char getAddress() {
		return mAddress;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(char address) {
		mAddress = address;
	}

	/**
	 * @return the index
	 */
	public byte getIndex() {
		return mIndex;
	}

	/**
	 * @param index the index to set
	 */
	public void setIndex(byte index) {
		mIndex = index;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return mName;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		mName = name;
	}

}
