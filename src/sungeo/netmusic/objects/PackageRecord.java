/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sungeo.netmusic.objects;

/**
 * 数据包中记录的基类
 * @author maoyu
 */
public interface PackageRecord {
	/**
	 * 打包
	 * @return指令的body
	 */
	public byte[] getRecordBytes();

	/**
	 * 拆包
	 * @param data
	 * @return
	 */
	public boolean setRecordBytes(byte[] data);
}
