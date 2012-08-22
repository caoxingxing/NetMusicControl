package sungeo.netmusic.netbase;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import sungeo.netmusic.data.MainApplication;
import sungeo.netmusic.objects.PackageObject;
import sungeo.netmusic.protocol.ProtocolCommand;
import sungeo.netmusic.protocol.RadioFreqPacketHandler;
import sungeo.netmusic.protocol.TcpPacketHandler;
import sungeo.netmusic.unit.ActionListener;
import sungeo.netmusic.unit.OverTimeAdapter;
import sungeo.netmusic.unit.ParsePacket;


public class SocketReciveThread implements Runnable, ActionListener{
	private Socket 				mClientSocket;
	private boolean 			mInitiative 	= false;
	private boolean 			mConnected 		= false;
	private DataInputStream 	mDataInStr;
	private DataOutputStream 	mDataOutStr;
	private final int 			CONNECT_TYPE 	= 0x8160;
	private OverTimeAdapter 	mTimeoutTimer;// ���Ϳ������ݵĳ�ʱ��ʱ��

	public SocketReciveThread(Socket socket, boolean flag) {
		mClientSocket = socket;
		mInitiative = flag;

		if (!mInitiative) {
			startTimeoutTimer(CONNECT_TYPE);
		}
		if (mClientSocket != null) {
			try {
				mDataInStr = new DataInputStream(mClientSocket.getInputStream());
				mDataOutStr = new DataOutputStream(mClientSocket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void sendDataToGateway() {
		TcpPacketHandler packet = new TcpPacketHandler();
		PackageObject obj = packet.getPackage(ProtocolCommand.MSG_TYPE_UPDATE_MUSIC);
		if (obj != null) {
			send(obj.getPackageBytes(), ProtocolCommand.MSG_TYPE_UPDATE_MUSIC);
			/*if (ret) {
				UIGlobal.showStringMsg("��������ͬ���ɹ�");
			}else {
				UIGlobal.showStringMsg("����ʧ��");
			}*/
		}
	}

	public void sendOpenPowerToGateway() {
		RadioFreqPacketHandler packet = new RadioFreqPacketHandler();
		char addr = MainApplication.getInstance().getmConfig().getPowAddr();
		char selfAddr = MainApplication.getInstance().getmConfig().getSelfAddr();
		char usercode = MainApplication.getInstance().getmConfig().getUsercode();
		packet.setTargetAddress(addr);
		packet.setUsercode(usercode);
		packet.setAddress(selfAddr);
		PackageObject obj = packet.getRfSendPacket(ProtocolCommand.CMD_POWER_ON);
		byte[] subtype = new byte[16];
		subtype[0] = 1;
		obj.setUsername(subtype);
		if (obj != null) {
			send(obj.getPackageBytes(), ProtocolCommand.CMD_POWER_ON);
		}
	}

	public void sendClosePowerToGateway() {
		RadioFreqPacketHandler packet = new RadioFreqPacketHandler();
		char addr = MainApplication.getInstance().getmConfig().getPowAddr();
		char selfAddr = MainApplication.getInstance().getmConfig().getSelfAddr();
		char usercode = MainApplication.getInstance().getmConfig().getUsercode();
		packet.setTargetAddress(addr);
		packet.setUsercode(usercode);
		packet.setAddress(selfAddr);
		PackageObject obj = packet.getRfSendPacket(ProtocolCommand.CMD_POWER_OFF);
		byte[] subtype = new byte[16];
		subtype[0] = 1;
		obj.setUsername(subtype);
		if (obj != null) {
			send(obj.getPackageBytes(), ProtocolCommand.CMD_POWER_OFF);
		}
	}

	public void sendStateToGateway(byte[] param) {
		RadioFreqPacketHandler packet = new RadioFreqPacketHandler();
		char selfAddr = MainApplication.getInstance().getmConfig().getSelfAddr();
		char usercode = MainApplication.getInstance().getmConfig().getUsercode();
		packet.setAddress(selfAddr);
		packet.setUsercode(usercode);
		packet.setTargetAddress(ProtocolCommand.ADDR_BROADCAST);
		byte[] params = new byte[1];
		params[0] = param[0];
		packet.setParams(params);
		PackageObject obj = packet.getRfSendPacket(ProtocolCommand.CMD_STATE_SYNC);
		byte[] subtype = new byte[16];
		subtype[0] = 1;
		obj.setUsername(subtype);
		if (obj != null) {
			send(obj.getPackageBytes());
		}
	}

	public void sendInfoToGateway() {
		if (!MainApplication.getInstance().ismNotifiSucess()) {
			//�����ر���
			TcpPacketHandler packet = new TcpPacketHandler();
			PackageObject obj = packet.getPackage(ProtocolCommand.MSG_TYPE_NOTIFY_GATEWAY);
			if (obj != null) {
				send(obj.getPackageBytes(), ProtocolCommand.MSG_TYPE_NOTIFY_GATEWAY);
			}
		}
	}

	@Override
	public void run() {
		int ret = 0; // ����֡
		int totalLen = 0; // ����
		int recvedLen = 0;
		byte[] buf;
		PackageObject obj;
		try {
			byte[] lenBytes = new byte[2];
			if (mClientSocket == null) {
				return;
			}
			mConnected = mClientSocket.isConnected();
			while (mConnected) {

				if (mDataInStr == null) {
					break;
				}
				//��ȡ���ݳ���֡
				ret = mDataInStr.read(lenBytes);
				if(-1 == ret){
					// ���ӱ��ر�
					break;
				} else if (ret != 2) {
					continue;
				}

				//����
				totalLen = getTotalLen(lenBytes);
				if (totalLen <= 0) {
					continue;
				}

				//��ʼ�����ջ���
				buf = new byte[totalLen];
				System.arraycopy(lenBytes, 0, buf, 0, 2);

				recvedLen = 2;
				while(recvedLen < totalLen){
					if (mDataInStr == null) {
						break;
					}
					ret = mDataInStr.read(buf, recvedLen, totalLen - recvedLen);
					if (0 > ret) {                            
						break;
					}

					recvedLen += ret;                        
				}

				if(recvedLen < totalLen){
					// ���ӹر�
					break;
				}                                  

				//����Э�齫�����е����ݲ��
				obj = decodePackage(buf);
				if (obj == null) {
					continue;
				}


				if (obj.getPackageType() == ProtocolCommand.MSG_TYPE_NOTIFY_GATEWAY) {
					//UIGlobal.showStringMsg("�յ�IP����");
				}
				/*if (obj.getPackageType() == ProtocolCommand.MSG_TYPE_UPDATE_MUSIC) {
					UIGlobal.showStringMsg("�յ�ͬ�����ݻ���");
				}*/
				if (mTimeoutTimer != null && mTimeoutTimer.getPlus() == obj.getPackageType()) {
					//UIGlobal.showStringMsg("�յ�IP�������رն�ʱ��");
					//���͵����ݱ�����
					stopTimeoutTimer();
				}


				// ֪ͨǰ����̨��������������
				boolean result = onReceive(obj);
				if (result) {
					//�������Ҫ�Ļ����������ӶϿ�
					disconnect();
				}

				if (mClientSocket == null) {
					mConnected = false;
				} else {
					mConnected = mClientSocket.isConnected();
				}
			}

			// �������ݳ����쳣���Ͽ�����
			disconnect();
		} catch (IOException e) {
			disconnect();
		}
	}

	private boolean onReceive(PackageObject obj) {
		boolean flag = false;
		ClientHandleMsg clientMsg = new ClientHandleMsg();
		clientMsg.handleMsg(obj);
		flag = clientMsg.isWorked();
		if (clientMsg.isSync() && mInitiative) {
			//����·ģ��ʱ�����ػ��Ȼظ�09,�ٻظ�81,�˴���Ϊ�����յ�81
			return false;
		}
		//����������������أ����յ������󷵻�true���Ա�Ͽ�����
		if (mInitiative) {
			return true;
		}

		if (obj.getPackageType() == ProtocolCommand.MSG_TYPE_UPDATE_MUSIC) {
			if (obj.getExecStatus() != 0) {
				disconnect();
				MainApplication.getInstance().getmClientSocket().startConnectGateway(ClientSocket.NOTIFI_UPDATE_DATA);
			}
			return true;
		}
		//�����������ص����ӣ������������򷢻������򲻻��������ж�ʱ������Ƿ�Ͽ����ӣ��������û�жϿ�����ʱ��Ͽ�����
		if (obj.getPackageType() != ProtocolCommand.MSG_TYPE_NOTIFY_GATEWAY) {
			PackageObject po = clientMsg.getReplyPackage();
			if (po != null) {
				send(po.getResponsePackageBytes());
			}
		} else {
			MainApplication.getInstance().setmNotifiSucess(flag);
			
			if (!flag) {
				flag = true;//���ڹرյ�ǰ���ӣ���ʱ������¸����ӣ�֪ͨ����
			}
		}
		return flag;
	}

	/**
	 * ������Ӧ��
	 */


	/**
	 * �Խ��յ������ݽ��н��
	 * @param data ʵ������
	 * @return ����ɹ����ؽ�������ɵ�Э�����
	 */
	protected PackageObject decodePackage(byte[] data) {
		int pkgLen = getTotalLen(data);
		if (pkgLen == data.length) { //data�п��ܴ��ڶ�����ݰ�
			byte resType = data[2];
			byte[] userName = new byte[16];
			System.arraycopy(data, 3, userName, 0, 16);
			byte resStatus = -2;
			int packetHeadLen = 0;
			if (mInitiative) {
				packetHeadLen = ParsePacket.NETPAKCAGE_HEAD_RESPONSE_LENGTH;
			} else {
				packetHeadLen = ParsePacket.NETPAKCAGE_HEAD_REQUEST_LENGTH;
			}
			if (data.length >= packetHeadLen && mInitiative) {
				resStatus = data[packetHeadLen - 1];
			}
			if (pkgLen > packetHeadLen) {
				//�а���İ�
				int bodyLen = pkgLen - packetHeadLen;
				byte[] body = new byte[bodyLen];
				System.arraycopy(data, packetHeadLen, body, 0, bodyLen);
				//�ֽ��
				PackageObject po = new PackageObject();

				//����������
				po.setPackageData(data);

				po.setPackLen(pkgLen);
				po.setUsername(userName);
				po.setExecStatus(resStatus);
				po.setPackageType(resType);

				//��������
				po.setPackageBytes(body);

				return po;
			} else {
				//ֻ�а�ͷ�İ��Ĵ���
				//�ֽ��
				PackageObject po = new PackageObject();
				po.setPackLen(pkgLen);
				po.setUsername(userName);
				po.setExecStatus(resStatus);
				po.setPackageType(resType);

				//����������
				po.setPackageData(data);

				return po;
			}
		} else {
			//������������
			return null;
		}
	}

	private int getTotalLen(byte[] lenBytes) {
		return ((lenBytes[0] & 255) << 8) | (lenBytes[1] & 255);
	}

	/**
	 * �Ͽ�����
	 */
	public void disconnect() {
		if (mTimeoutTimer != null) {
			mTimeoutTimer.cancel();
		}

		if (mClientSocket != null) {
			try {
				closeOutStream();
				closeInputStream();
				closeSocket();              
			} catch (IOException ex) {

			}
		}
	}

	/**
	 * �������ݣ�Ϊ��Ӧ�������󣬷�����ɺ�ֱ�ӶϿ����ӣ����õȴ���ʱ
	 */
	public boolean send(byte[] buf) {
		if(mDataOutStr == null){
			return false;
		}

		try {
			//��������
			mDataOutStr.write(buf);//����һ������ִ��I/O����
			mDataOutStr.flush();//����ִ��I/O����
			return true;
		} catch (IOException ex) {
			return false;
		}     
	}

	/**
	 * ��������
	 * @param buf
	 * @int plus��Ϊ��ʱ����ָ��Ĺؼ���,����������Ϊ�ؼ��ֵ�ʱ����Ҫ��0x8000���л����㣬�Ա��pcЭ������
	 * @return
	 */
	public boolean send(byte[] buf, int plus) {
		startTimeoutTimer(plus);   

		return send(buf);
	}

	private void closeInputStream() throws IOException {
		if (mDataInStr != null) {
			mDataInStr.close();
			mDataInStr = null;
		}
	}

	private void closeOutStream() throws IOException {
		// WARNING �����߳��Ƿ��soc.close����������Ҫ����
		//�ر������������
		if (mDataOutStr != null) {
			mDataOutStr.close();
			mDataOutStr = null;
		}

		if (mTimeoutTimer != null && mTimeoutTimer.isRunning()) {
			mTimeoutTimer.cancel();
		}
	}

	private void closeSocket() throws IOException {
		//�ر�socket
		mClientSocket.close();
		mClientSocket = null;
	}

	/**
	 * ������ͨͨѶ��ʱ��ʱ��
	 */
	private void startTimeoutTimer(int plus) {
		if (mTimeoutTimer == null) {
			mTimeoutTimer = new OverTimeAdapter();
		}
		long waitTime;

		if (plus == CONNECT_TYPE) {
			waitTime = 5 * 1000;
		} else if (plus == ProtocolCommand.CMD_POWER_ON){
			//����ȴ�10s����Ϊ�����������·ģ��ʱ��ϳ�
			waitTime = 10 * 1000;
		} else {
			waitTime = 6 * 1000;
		}

		mTimeoutTimer.setActionListener(this);
		mTimeoutTimer.setWaitTime(waitTime);
		mTimeoutTimer.setPlus(plus);
		mTimeoutTimer.start();
	}

	private void stopTimeoutTimer() {
		if (mTimeoutTimer != null) {
			mTimeoutTimer.cancel();
			mTimeoutTimer = null;
		}
	}

	@Override
	public void overTimePerformed(int plus) {
		stopTimeoutTimer();
		if (plus == ProtocolCommand.MSG_TYPE_NOTIFY_GATEWAY) {
			if (!MainApplication.getInstance().ismNotifiSucess()) {
				int serial = MainApplication.getInstance().getmConfig().getSelfSerial();
				if (serial != 0) {
					MainApplication.getInstance().getmMsgSender().sendStrMsg("�ȴ����ػ�����ʱ");
					disconnect();
					MainApplication.getInstance().getmClientSocket().startConnectGateway(ClientSocket.NOTIFI_GATEWAY_IP);
				}
			} else {
				disconnect();
			}
		} else if (plus == CONNECT_TYPE) {
			//����������ӳ�ʱû�жϿ����ɱ������ֶϿ�
			disconnect();
		} else if (plus == ProtocolCommand.CMD_POWER_OFF) {
			MainApplication.getInstance().getmMsgSender().sendStrMsg("�ȴ��ػ�����ʱ");
		} else if (plus == ProtocolCommand.CMD_POWER_ON) {
			//�ȴ���������ʱʱ�������ط��͹������Ϊ���������Ѿ��������ض����У���ʱ���ؿ���������·ģ��һֱ����
			//��ʱ����б�����Ϣ���򽫱�����Ϣ���
			if (MainApplication.getInstance().getmHostAlarmName() != null) {
				MainApplication.getInstance().setmHostAlarmName(null);
			}
			//ClientSocket.startConnectGateway(ClientSocket.CLOSE_CTRL_POWER);
			MainApplication.getInstance().getmMsgSender().sendStrMsg("�ȴ���������ʱ");
		} else if (plus == ProtocolCommand.MSG_TYPE_UPDATE_MUSIC) {
			MainApplication.getInstance().getmMsgSender().sendStrMsg("�ȴ��������ݻ�����ʱ");
			disconnect();
			MainApplication.getInstance().getmClientSocket().startConnectGateway(ClientSocket.NOTIFI_UPDATE_DATA);
		}
	}
}