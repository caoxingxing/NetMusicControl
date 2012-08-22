/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sungeo.netmusic.protocol;

import sungeo.netmusic.data.MainApplication;
import sungeo.netmusic.unit.ParsePacket;

/**
 * 无线信号处理类
 * @author caoxingxing
 */
public class CommPackDeal extends BaseProtocol{
	private byte 	mLenth;
	private byte[] 	mParma;

	public CommPackDeal() {
		char selfAddr = MainApplication.getInstance().getmConfig().getSelfAddr();
		char usercode = MainApplication.getInstance().getmConfig().getUsercode();
		setUserCode(usercode);
		setSourceAddress(selfAddr);
	}

	public void setLenth(byte len) {
		mLenth = len;
		mLenth++;
	}

	public void setParam(byte[] param) {
		mParma = param;
	}

	@Override
	public byte[] getRequestBytes() {
		byte[] ret = new byte[13 + mParma.length];

		ret[0]=(byte)getFrameHead();
		
		ParsePacket parsePacket = new ParsePacket();
		
		parsePacket.charToBigEndianBytes(getUserCode(),ret,1);

		parsePacket.charToBigEndianBytes(getTargetAddress(),ret,3);

		parsePacket.charToBigEndianBytes(getSourceAddress(), ret, 5);

		ret[7]=mLenth;   //长度

		ret[8]=(byte)getFuncWord();

		System.arraycopy(mParma, 0, ret, 9, mParma.length);

		char selfAddr = MainApplication.getInstance().getmConfig().getSelfAddr();
		parsePacket.charToBigEndianBytes(selfAddr, ret, mParma.length + 9);
		ret[ret.length-1]=ret[0];

		BaseProtocol.writeSumCheckWord(ret);

		return ret;
	}

	@Override
	public boolean bytesToInformation(byte[] data) {
		return false;
	}

	@Override
	public byte[] informationToBytes() {
		return new byte[0];
	}
}
