package sungeo.netmusic.unit;

import java.util.Vector;

public class ParsePacket {
	public static final byte NETIN_FIELD_SP_BYTE=(byte)0;
	public static final byte NETIN_RECORD_SP_BYTE=(byte)'|';

	//网络包头长度
	public static final byte NETPAKCAGE_HEAD_RESPONSE_LENGTH = 20;
	public static final byte NETPAKCAGE_HEAD_REQUEST_LENGTH = 19;

	/**
	 * 查找sc在data中出现的索引位置
	 * @param data
	 * @param sc
	 * @return 无法找打返回-1
	 */
	public int bytePosition(byte[] data,byte sc, int top)
	{
		int ret = -1;
		int length = data.length;
		for(int i = top; i < length; i++)
		{
			if(data[i] == sc)
			{
				ret = i;
				break;
			}
		}
		return ret;
	}

	/**
	 * 将字节数组data按照分隔符sp分割成二维字节数组
	 * @param data
	 * @param sp
	 * @return
	 */
	public byte[][] splitBytes(byte[] data,byte sp)
	{
		Vector<byte[]> vb = new Vector<byte[]>();
		int beginPos = 0;
		do{
			int found=-1;
			for(int i=beginPos;i<data.length;i++){
				if(data[i]==sp){
					if(i<data.length-1){
						if(data[i+1]!=sp){
							found=i;
							break;
						}
					}else{
						found=i;
						break;
					}
				}
			}
			if(found==-1){
				if(beginPos<data.length-1){
					byte[] field=new byte[data.length-beginPos];
					System.arraycopy(data, beginPos, field, 0, field.length);
					vb.addElement(field);
				}
				beginPos=0;
			}else{
				byte[] field=new byte[found-beginPos];
				System.arraycopy(data, beginPos, field, 0, field.length);
				vb.addElement(field);
				beginPos=++found;
				if(beginPos>=data.length)
					beginPos=0;
			}
		}while(beginPos>0);

		if(vb.size()>0){
			byte[][] ret=new byte[vb.size()][];
			for(int i=0;i<vb.size();i++){
				ret[i]=vb.elementAt(i);
			}
			return ret;
		}
		else
			return null;
	}

	/**
	 * 无符号整型转换为字节序
	 * @param data 无符号整型
	 * @return 字节序
	 */
	public byte[] charToBytes(char data)
	{
		byte[] ret = new byte[2];
		ret[0] = (byte)((data&0xFF00)>>8);
		ret[1]=(byte)(data&0xFF);
		return ret;
	}

	public char bytesToChar(byte[] data,int beginAt){
		return (char)((data[beginAt]&0xFF)+((data[beginAt+1]&0xFF)<<8));
	}
	
	/**
	 * 转换无符号整型到字节序，并填充到目标字节序中
	 * @param data 无符号整型
	 * @param target 目标字节序
	 * @param fillStart 填充开始位置
	 * 大端字节序
	 */
	public void charToBigEndianBytes (char data,byte[] target,int fillStart)
	{
		byte[] bts = charToBytes(data);
		System.arraycopy(bts, 0, target, fillStart, 2);
	}

	/**
	 * 将指定开始位置字节序中的四个字节转换为整型
	 * @param data 字节序
	 * @param beginAt 开始位置
	 * @return 整型
	 */
	public int bytesToInt(byte[] data,int beginAt){
		return ((data[beginAt]&0xFF) +((data[beginAt+1]&0xFF)<<8)+((data[beginAt+2]&0xFF)<<16)+((data[beginAt+3]&0xFF)<<24));
	}

	public String asciiBytesToString(byte[] data) {
		if ((data == null) || (data.length == 0)) {
			return null;
		}
		char[] chs = new char[data.length];
		for (int i = 0; i < data.length; i++) {
			chs[i] = (char) (0xFF & data[i]);
		}
		return String.valueOf(chs);
	}


	/**
	 * 打印网络数据包，按字节打印
	 * @param buf
	 * @param send
	 */
	public void testDisplaySendData(byte[] buf, boolean send) {
		StringBuffer str = new StringBuffer();
		if (buf == null || buf.length <= 0) {
			return;
		}
		for (int i = 0; i < buf.length; i++) {
			int v = buf[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				str.append(0);
			}
			str.append(hv);
			str.append(" ");
		}

		//MainApplication.getInstance().getmMsgSender().printLog(str.toString());
	}
}
