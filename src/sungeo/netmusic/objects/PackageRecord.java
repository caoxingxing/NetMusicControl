/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sungeo.netmusic.objects;

/**
 * ���ݰ��м�¼�Ļ���
 * @author maoyu
 */
public interface PackageRecord {
	/**
	 * ���
	 * @returnָ���body
	 */
	public byte[] getRecordBytes();

	/**
	 * ���
	 * @param data
	 * @return
	 */
	public boolean setRecordBytes(byte[] data);
}
