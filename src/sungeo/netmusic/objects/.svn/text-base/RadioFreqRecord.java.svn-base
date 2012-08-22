/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sungeo.netmusic.objects;

import sungeo.netmusic.protocol.CommPackDeal;


/**
 * 无线命令包记录存储类
 * @author maoyu
 */
public class RadioFreqRecord implements PackageRecord {

	private CommPackDeal mCommPackDeal;

	public RadioFreqRecord(CommPackDeal cpd) {
		mCommPackDeal = cpd;
	}

	@Override
	public byte[] getRecordBytes() {
		if(mCommPackDeal != null) {
			return mCommPackDeal.getRequestBytes();
		} else {
			return null;
		}
	}

	@Override
	public boolean setRecordBytes(byte[] data) {
		if(mCommPackDeal == null) {
			return false;
		} else {
			mCommPackDeal.decodeProtocolInfo(data);
			return true;
		}
	}

	public CommPackDeal getBaseProtocal() {
		return mCommPackDeal;
	}

	public void setBaseProtocal(CommPackDeal baseProtocal) {
		mCommPackDeal = baseProtocal;
	}
}
