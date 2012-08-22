/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sungeo.netmusic.objects;
import java.util.Vector;

/**
 * 协议包记录容器
 * @author caoxinxing
 */
public class RecordContainer {
	private Vector<PackageRecord> mRecords = new Vector<PackageRecord>();

	public void addRecord(PackageRecord bpr){
		mRecords.addElement(bpr);
	}

	public void remoteRecord(PackageRecord bpr){
		mRecords.removeElement(bpr);
	}

	public void clear(){
		mRecords.removeAllElements();
	}

	public PackageRecord getRecordByIndex(int idx){
		if((idx < 0) || (idx > mRecords.size() - 1))
			return null;
		else
			return mRecords.elementAt(idx);
	}

	public int getSize(){
		return mRecords.size();
	}

	/**
	 * 获取所有记录的字节数据
	 * @return 失败返回null，成功返回字节序
	 */
	public byte[] getRecordBytes(){
		Vector<byte[]> vb = new Vector<byte[]>();
		int sizeCount = 0;
		int recordCount = mRecords.size();
		for(int i = 0; i < recordCount; i++){
			PackageRecord bpr = mRecords.elementAt(i);
			byte[] rd = bpr.getRecordBytes();
			if(rd == null){
				return null;
			}else{
				sizeCount += rd.length;
				vb.addElement(rd);
			}
		}

		if(sizeCount > 0){
			sizeCount += recordCount - 1;  //添加";"占位
			byte[] ret = new byte[sizeCount];
			int destPos = 0;
			for(int i = 0; i < vb.size(); i++){
				byte[] sd = vb.elementAt(i);
				System.arraycopy(sd, 0, ret, destPos, sd.length);
				destPos +=sd.length;
				if(i < vb.size()-1){
					ret[destPos] = (byte)0x3B;    // ; 标识
					destPos++;
				}
			}
			vb.removeAllElements();
			vb = null;
			return ret;
		}else{
			vb.removeAllElements();
			vb = null;
			System.gc();
			return null;
		}
	}
}
