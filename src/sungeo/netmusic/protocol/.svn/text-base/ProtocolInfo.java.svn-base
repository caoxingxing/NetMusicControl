/*
 * 协议信息接口
 */

package sungeo.netmusic.protocol;

/**
 * 协议信息接口
 * @author maoyu
 */
public interface ProtocolInfo {
	/**
	 * 每帧协议中固定标识的总长度
	 */
	int FIXED_BYTE_LENGTH=13;
	/**
	 * 将信息转换为协议字节序
	 * @return 字节序
	 */
	byte[] informationToBytes();

	/**
	 * 将字节需转换为信息
	 * @param data
	 * @return 转换成功返回true，转换失败或校验和校验失败均返回false
	 */
	boolean bytesToInformation(byte[] data);
}
