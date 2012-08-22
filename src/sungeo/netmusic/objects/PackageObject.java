/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sungeo.netmusic.objects;

import sungeo.netmusic.protocol.CommPackDeal;
import sungeo.netmusic.protocol.ProtocolCommand;
import sungeo.netmusic.unit.ParsePacket;


/**
 *
 * @author caoxingxing
 */
public class PackageObject {
	private byte 			mExecStatus 		= -1;
	private byte 			mPackageType 		= -1;
	private byte 			mCurPackNo 			= 0;//多包情况下当前包的序号userName第4个字节
	private byte 			mFullPacksNum 		= 0;//完整信息的包的总数userName第3个字节
	private byte[] 			mUsername 			= new byte[16];
	private byte[] 			mPackageBody;
	private byte[] 			mPackageData;
	private int 			mPackLen 			= 0;
	private final byte 		REQUEST_HEAD_LEN 	= 19;
	private final byte 		RESPONSE_HEAD_LEN 	= 20;
	private RecordContainer mRecords 			= new RecordContainer();

	/**
	 * 获取整个包的完整数据
	 * @return
	 */
	public byte[] getPackageData() {
		return mPackageData;
	}

	public static byte getPackageType(byte[] data) {
		if(data == null){
			return -1;
		}

		if (data.length <= 2) {
			return -1;
		}
		return data[2];
	}

	public byte getPackageType() {
		return mPackageType;
	}

	public byte getExecStatus() {
		return mExecStatus;
	}

	/**
	 * 填充包体
	 */
	public void setPackageBody(byte[] body) {
		if (body == null) {
			return;
		}
		mPackageBody = body;
	}

	/**
	 * 获取包中的内容
	 */
	public byte[] getPackageBody() {
		return mPackageBody;
	}

	/**
	 * 获取包中的内容的长度
	 */
	public int getPackageBodyLen() {
		return mPackageBody.length;
	}

	/**
	 * 获取数据包字节序
	 * @return
	 */
	public byte[] getResponsePackageBytes() {

		int bodyLen = 0;
		byte[] body = null;
		if (mRecords.getSize() > 0) {
			body = mRecords.getRecordBytes();
			bodyLen = body.length;
		}

		int pkgLen = 0;
		if (mPackLen == 0) {
			pkgLen = RESPONSE_HEAD_LEN + bodyLen;
		} else {
			pkgLen = mPackLen;
		}
		if (pkgLen > 0xFFFF) {
			return null;
		}

		//数据包长度
		byte[] ret = new byte[pkgLen];
		ret[0] = (byte) (pkgLen >> 8);
		ret[1] = (byte) pkgLen;

		//请求类型
		ret[2] = mPackageType;

		//用户名称
		System.arraycopy(getUsername(), 0, ret, 3, getUsername().length);
		ret[RESPONSE_HEAD_LEN - 1] = 0;

		//内容
		if (bodyLen > 0) {
			System.arraycopy(body, 0, ret, RESPONSE_HEAD_LEN, bodyLen);
		}
		return ret;
	}

	/**
	 * 获取数据包字节序
	 * @return
	 */
	public byte[] getPackageBytes() {

		int bodyLen = 0;
		byte[] body = null;
		if (mRecords.getSize() > 0) {
			body = mRecords.getRecordBytes();
			bodyLen = body.length;
		}

		int pkgLen = 0;
		if (mPackLen == 0) {
			pkgLen = REQUEST_HEAD_LEN + bodyLen;
		} else {
			pkgLen = mPackLen;
		}
		if (pkgLen > 0xFFFF) {
			return null;
		}

		//数据包长度
		byte[] ret = new byte[pkgLen];
		ret[0] = (byte) (pkgLen >> 8);
		ret[1] = (byte) pkgLen;

		//请求类型
		ret[2] = mPackageType;

		//用户名称
		System.arraycopy(getUsername(), 0, ret, 3, getUsername().length);

		//内容
		if (bodyLen > 0) {
			System.arraycopy(body, 0, ret, 19, bodyLen);
		}
		return ret;
	}

	/**
	 * 将网络字节序转换为数据包对象
	 * @param data
	 * @return
	 */
	public boolean setPackageBytes(byte[] data) {
		if(data == null){
			return false;
		}

		mRecords.clear();
		mPackageBody = new byte[data.length];
		System.arraycopy(data, 0, mPackageBody, 0, data.length);

		if (mPackageType == ProtocolCommand.MSG_TYPE_WIRELESS_PROTOCOL) {
			CommPackDeal cpd = splitRadioPacket(data);
			RadioFreqRecord pr = new RadioFreqRecord(cpd);
			mRecords.addRecord(pr);
			return true;
		}

		ParsePacket parsePacket = new ParsePacket();
		//分解出每条记录字节数组
		byte[][] recordBytes = parsePacket.splitBytes(data, ParsePacket.NETIN_RECORD_SP_BYTE);
		if (recordBytes == null) {
			return false;
		}

		for (int i = 0; i < recordBytes.length; i++) {
			PackageRecord pr = null;
			switch (mPackageType) {
			case ProtocolCommand.MSG_TYPE_HOST_ALARM:
				pr = new SecurityRecord();
				break;
			case ProtocolCommand.MSG_TYPE_UDP_BROADCAST:
				pr = new UdpInfoRecord();
				break;
			default:
				break;
			}
			if (pr != null) {
				pr.setRecordBytes(recordBytes[i]);
				mRecords.addRecord(pr);
			}
		}

		return true;
	}

	/**
	 * 返回记录容器
	 * @return 返回记录容器
	 */
	public RecordContainer getRecords() {
		return mRecords;
	}

	private CommPackDeal splitRadioPacket(byte[] data) {
		if(data == null){
			return null;
		}

		CommPackDeal ret = new CommPackDeal();
		int len = data[7] + 12;
		if (len < data.length) {
			byte[] tem = new byte[len];
			System.arraycopy(data, 0, tem, 0, len);
			ret.decodeProtocolInfo(tem);
			return ret;
		} else if (len == data.length) {
			ret.decodeProtocolInfo(data);
			return ret;
		}
		return null;
	}

	public void setUsername() {
		mUsername[0] = 1;
	}
	/**
	 * @param mUsername the username to set
	 */
	public void setUsername(byte[] userName) {
		if(null == userName){
			return;
		}

		if (userName.length == 16) {
			mUsername = userName;
			setInfoIntegrality(userName[3], userName[2]);
		}
	}

	private void setInfoIntegrality(byte no, byte totalNum) {
		mCurPackNo = no;
		mFullPacksNum = totalNum;
	}

	/**
	 * 请求/返回类型
	 * @param packageType
	 */
	public void setPackageType(byte packageType) {
		mPackageType = packageType;
	}

	public void setPackageData(byte[] data) {
		if(data == null){
			return;
		}

		mPackageData = new byte[data.length];
		System.arraycopy(data, 0, mPackageData, 0, data.length);
	}

	/**
	 * @param len the len to set
	 */
	public void setPackLen(int packLen) {
		mPackLen = packLen;
	}

	/**
	 * 执行状态
	 * @param execStatus
	 */
	public void setExecStatus(byte execStatus) {
		mExecStatus = execStatus;
	}

	/**
	 * 判断多包数据是否发送完
	 * 
	 */
	public boolean isInfoIntegral() {
		if (mCurPackNo + 1 == mFullPacksNum || mFullPacksNum == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @return the username
	 */
	public byte[] getUsername() {
		return mUsername;
	}
}
