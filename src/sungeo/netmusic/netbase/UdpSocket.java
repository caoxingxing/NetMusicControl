package sungeo.netmusic.netbase;

import sungeo.netmusic.data.MainApplication;
import sungeo.netmusic.objects.PackageObject;
import sungeo.netmusic.protocol.ProtocolCommand;
import sungeo.netmusic.protocol.TcpPacketHandler;
import sungeo.netmusic.unit.MsgSender;
import sungeo.netmusic.unit.ParsePacket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UdpSocket {

	private final static int sUdpPort = 6020;
	private DatagramSocket mUdpSocket = null;

	public boolean sendUdpPacket() {
		TcpPacketHandler packet = new TcpPacketHandler();
		PackageObject obj = packet
				.getPackage(ProtocolCommand.MSG_TYPE_UDP_BROADCAST);
		obj.setUsername();
		InetAddress local = null;
		DatagramPacket p = null;

		int msg_length = obj.getPackageBytes().length;

		byte[] messages = obj.getPackageBytes();

		try {
			mUdpSocket = new DatagramSocket();
			mUdpSocket.setBroadcast(true);

			local = InetAddress.getByName("255.255.255.255");
			p = new DatagramPacket(messages, msg_length);

			p.setAddress(local);
			p.setPort(sUdpPort);

			mUdpSocket.setSoTimeout(2000);
			mUdpSocket.send(p);
			byte[] data = new byte[64];
			DatagramPacket udpPacket = new DatagramPacket(data, 64);

			mUdpSocket.receive(udpPacket);
			mUdpSocket.close();
			mUdpSocket.disconnect();
			// 根据协议将缓存中的数据拆包
			boolean flag = decodePackage(data);
			if (flag)
				MainApplication.getInstance().getmMsgSender().sendMsg(MsgSender.MSG_UDP_BROADCAST_RECIVE);
			return flag;
		} catch (SocketException e) {
			// DatagramSocket
			e.printStackTrace();
			mUdpSocket.close();
			return false;
		} catch (UnknownHostException e) {
			// InetAddress
			e.printStackTrace();
			mUdpSocket.close();
			return false;
		} catch (IOException e) {
			// send
			e.printStackTrace();
			mUdpSocket.close();
			return false;
		}
	}

	protected boolean decodePackage(byte[] data) {
		int pkgLen = getTotalLen(data);
		byte resType = data[2];
		byte[] userName = new byte[16];
		System.arraycopy(data, 3, userName, 0, 16);
		byte resStatus = -2;
		int packetHeadLen = 0;

		packetHeadLen = ParsePacket.NETPAKCAGE_HEAD_RESPONSE_LENGTH;

		if (data.length >= packetHeadLen) {
			resStatus = data[packetHeadLen - 1];
		}
		// 有包体的包
		int bodyLen = pkgLen - packetHeadLen;
		byte[] body = new byte[bodyLen];
		System.arraycopy(data, packetHeadLen, body, 0, bodyLen);
		// 分解包
		PackageObject po = new PackageObject();

		// 保存整个包
		po.setPackageData(data);

		po.setPackLen(pkgLen);
		po.setUsername(userName);
		po.setExecStatus(resStatus);
		po.setPackageType(resType);

		// 处理包体
		return po.setPackageBytes(body);
	}

	private int getTotalLen(byte[] lenBytes) {
		return ((lenBytes[0] & 255) << 8) | (lenBytes[1] & 255);
	}
}
