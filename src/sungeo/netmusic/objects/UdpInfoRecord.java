package sungeo.netmusic.objects;

import sungeo.netmusic.data.MainApplication;

public class UdpInfoRecord implements PackageRecord {

	@Override
	public byte[] getRecordBytes() {
		return null;
	}

	@Override
	public boolean setRecordBytes(byte[] data) {
		// 解析并保存网关IP
		if (data == null)
			return false;
		if (data.length <= 0)
			return false;

		String ipStr = null;

		ipStr = new String(data);

		if (ipStr.length() < 1)
			return false;
		
		String str = ipStr.substring(ipStr.length() - 1, ipStr.length());

		/**
		 * 将body转换为String类型后，IP地址后会多出个0,收下代码就是为了安全删除结尾的0
		 */
		byte[] tempStr = str.getBytes();
		int tempLen = tempStr.length;
		boolean flag = false;
		for (int i = 0; i < tempLen; i ++) {
			if (tempStr[i] <= 48 || tempStr[i] >= 57)
				continue;
			
			flag = true;
		}
		
		if (!flag)
			ipStr = ipStr.substring(0, ipStr.length() - 1);

		MainApplication mainApp = MainApplication.getInstance();
		mainApp.getmConfig().saveGatewayIp(ipStr);
		return true;
	}

}
