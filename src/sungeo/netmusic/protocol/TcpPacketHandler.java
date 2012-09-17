/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sungeo.netmusic.protocol;

import sungeo.netmusic.data.MainApplication;
import sungeo.netmusic.objects.AlbumDataRecord;
import sungeo.netmusic.objects.LocalInfoRecord;
import sungeo.netmusic.objects.PackageObject;

/**
 *
 * @author caoxingxing
 */
public class TcpPacketHandler {
	private byte[] mBody;

	public PackageObject getPackage(byte cmd) {
		PackageObject po = new PackageObject();
		po.setPackageType(cmd);
		if (cmd == ProtocolCommand.MSG_TYPE_NOTIFY_GATEWAY) {
			LocalInfoRecord gateway = new LocalInfoRecord();
			String ipStr = MainApplication.getInstance().getLocalIpAddress();
			gateway.setIpAddr(ipStr);
			int serial = MainApplication.getInstance().getmConfig().getSelfSerial();
			String strSerial = Integer.toHexString(serial);
			gateway.setSerial(strSerial);
			po.getRecords().addRecord(gateway);
		} else if (cmd == ProtocolCommand.MSG_TYPE_UPDATE_MUSIC) {
			AlbumDataRecord adr = new AlbumDataRecord();
			po.getRecords().addRecord(adr);
		} else if (cmd == ProtocolCommand.MSG_TYPE_QUERY_MUSIC) {
			po.setExecStatus((byte)0);
		} else if (cmd == ProtocolCommand.MSG_TYPE_UDP_BROADCAST) {
			
		} else if (cmd == ProtocolCommand.MSG_TYPE_REQUEST_PLAY) {
		    String url = new String(mBody);
		    boolean flag = MainApplication.getInstance().getmMediaMgr().playByUrl(url);
		    if (flag) {
		        MainApplication.getInstance().getmMsgSender().sendStrMsg(url);
		        po.setExecStatus((byte)0);
		    } else {
		        MainApplication.getInstance().getmMsgSender().sendStrMsg("正在处理上一请求");
		        po.setExecStatus((byte)-1);
		    }
		} 

		return po;
	}

	public void setBody(byte[] body) {
		mBody = body;
	}

	public byte[] getBody() {
		return mBody;
	}
}
