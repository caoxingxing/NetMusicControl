/*
 * 协议基类
 */

package sungeo.netmusic.protocol;

/**
 * 协议基类
 * @author maoyu
 */
public abstract class BaseProtocol implements ProtocolInfo{
	private byte 	mFuncWord;
	private char 	mFrameHead = (char)0xAA;
	private char 	mUserCode;
	private char 	mTargetAddress;
	private char 	mSourceAddress;
	private char 	mSsAddress;
	private char 	mFrameLength;
	private byte[] 	mParams;

	/**
	 * 获取发送命令请求字节序
	 * @return 字节序
	 */
	public abstract byte[] getRequestBytes();

	/**
	 * 将接收到的数据包转换为类中的成员
	 * @param data 字节序
	 */
	@Override
	public abstract boolean bytesToInformation(byte[] data);

	/**
	 * 将类成员信息转换为字节数组
	 * @return 字节数组
	 */
	@Override
	public abstract byte[] informationToBytes();

	/**
	 * @return the funcWord
	 */
	public byte getFuncWord() {
		return mFuncWord;
	}

	/**
	 * @param funcWord the funcWord to set
	 */
	public void setFuncWord(byte funcWord) {
		mFuncWord = funcWord;
	}

	/**
	 * @return the frameHead
	 */
	public char getFrameHead() {
		return mFrameHead;
	}

	/**
	 * @param frameHead the frameHead to set
	 */
	public void setFrameHead(char frameHead) {
		mFrameHead = frameHead;
	}

	/**
	 * @return the userCode
	 */
	public char getUserCode() {
		return mUserCode;
	}

	/**
	 * @param userCode the userCode to set
	 */
	public void setUserCode(char userCode) {
		mUserCode = userCode;
	}

	/**
	 * @return the targetAddress
	 */
	public char getTargetAddress() {
		return mTargetAddress;
	}

	/**
	 * @param targetAddress the targetAddress to set
	 */
	public void setTargetAddress(char targetAddress) {
		mTargetAddress = targetAddress;
	}

	/**
	 * @return the sourceAddress
	 */
	public char getSourceAddress() {
		return mSourceAddress;
	}

	/**
	 * @param sourceAddress the sourceAddress to set
	 */
	public void setSourceAddress(char sourceAddress) {
		mSourceAddress = sourceAddress;
	}

	/**
	 * @return the ssAddress
	 */
	public char getSsAddress() {
		return mSsAddress;
	}

	/**
	 * @param ssAddress the ssAddress to set
	 */
	public void setSsAddress(char ssAddress) {
		mSsAddress = ssAddress;
	}

	/**
	 * @return the frameLength
	 */
	public char getFrameLength() {
		return mFrameLength;
	}

	/**
	 * 检查校验和
	 * @param data
	 * @return 正确返回true
	 */
	public static boolean checkSum(byte[] data){
		byte sum = 0;
		for(int i = 1; i < data.length - 2; i++)
			sum += data[i];

		return sum == data[data.length-2];
	}

	/**
	 * 写入协议帧的校验和字段
	 *
	 * @param data 应该为填充所有数据后的数组
	 *
	 */
	public static void writeSumCheckWord(byte[] data)
	{
		byte sum=0;
		for(int i = 1; i < data.length - 2; i++)
			sum += data[i];

		data[data.length-2] = sum;
	}

	/**
	 * 获取帧的命令字
	 * @param data
	 * @return
	 */
	public static byte getFuncWord(byte[] data)
	{
		return data[8];
	}

	/**
	 * 获取命令帧中的参数字节数组
	 * @param data
	 * @return
	 */
	public  byte[] getParamBytes(byte[] data)
	{
		byte len = (byte)(data[7]-1);
		if(len >= 0)
		{
			mParams = new byte[len];
			System.arraycopy(data, 9, mParams, 0, len);
			return mParams;
		}
		else
		{
			return null;
		}
	}

	public byte[] getParamBytes()
	{
		return mParams;
	}

	/**
	 * 检查是否为有效数据帧
	 * @param data
	 * @return
	 */
	public boolean isVaildFrameData(byte[] data)
	{
		if (data.length >= ProtocolInfo.FIXED_BYTE_LENGTH)
		{
			char head = (char) ((data[0]) & 0xFF);
			char cauda = (char) (data[data.length - 1] & 0xFF);
			if ((head == mFrameHead) && (cauda == mFrameHead))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}

	/**
	 * 分解协议字段<br>
	 * 如：帧头、功能字等
	 * @param data
	 */
	public void decodeProtocolInfo(byte[] data)
	{
		setFrameHead((char)data[0]);

		char sh = (char)((data[1]<<8)|(0xff&data[2]));
		setUserCode(sh);

		sh = (char)((data[3]<<8)|(0xff&data[4]));
		setTargetAddress(sh);

		sh = (char)((data[5]<<8)|(0xff&data[6]));
		setSourceAddress(sh);

		mFrameLength= (char)data[7];

		setFuncWord(data[8]);

		byte len=(byte)(data[7]-1);
		if(len>=0)
		{
			mParams=new byte[len];
			System.arraycopy(data, 9, mParams, 0, len);
		}
		sh=(char)((data[data.length-4]<<8)|(0xff&data[data.length-3]));
		setSsAddress(sh);
	}
}
