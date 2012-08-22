package sungeo.netmusic.netbase;

import sungeo.netmusic.data.MainApplication;
import sungeo.netmusic.objects.PackageObject;
import sungeo.netmusic.objects.RadioFreqRecord;
import sungeo.netmusic.objects.SecurityRecord;
import sungeo.netmusic.protocol.CommPackDeal;
import sungeo.netmusic.protocol.ProtocolCommand;
import sungeo.netmusic.protocol.RadioFreqPacketHandler;
import sungeo.netmusic.protocol.TcpPacketHandler;

public class ClientHandleMsg {
	private PackageObject 	mReplyPackage;
	private boolean 		mWorked = false;
	private boolean 		mIsSync = false;
	
	public void handleMsg(PackageObject obj) {
		if (obj == null) {
			return;
		}
		byte type = obj.getPackageType();
		switch (type) {
		case ProtocolCommand.MSG_TYPE_WIRELESS_PROTOCOL:{
			haneleWirelessProtocol(obj);
		}
		break;
		case ProtocolCommand.MSG_TYPE_NOTIFY_GATEWAY:
			if (obj.getExecStatus() == 0) {
				setWorked(true);//�յ�����˵���ɹ�����������
			} else {
				setWorked(false);
			}
			setSync(false);
			break;
		case ProtocolCommand.MSG_TYPE_QUERY_MUSIC:
			handleQueryMusic(type);
			setSync(false);
			break;
		case ProtocolCommand.MSG_TYPE_UPDATE_MUSIC: {
			if (obj.getExecStatus() == 0) {
				setWorked(true);
			} else {
				setWorked(false);
			}
			setSync(false);
		}
		break;
		case ProtocolCommand.MSG_TYPE_HOST_ALARM: {
			/**
			 * �յ�������Ϣ���ȴ���·ģ�飬�򿪳ɹ�����֪ͨTTS���񲥷�
			 * ����·ģ��ʧ�ܣ���ִ����������
			 */

			setSync(false);
			
			String name = " ";
			
			int count = obj.getRecords().getSize();
			for (int i = 0; i < count; i++) {
				SecurityRecord hair = (SecurityRecord) obj.getRecords().getRecordByIndex(i);
				name = hair.getName();
			}

			MainApplication.getInstance().setmHostAlarmName(name);
			MainApplication.getInstance().getmMsgSender().sendStrMsg("̽ͷ����Ϊ��"+ name);
			MainApplication.getInstance().getmMsgSender().sendHostAlarm();
		}
		break;
		default:
			break;
		}

	}

	private boolean haneleWirelessProtocol(PackageObject obj) {
		boolean flag = false;
		CommPackDeal cpd = getCommPack(obj);
		if (cpd == null) {
			return false;
		}

		char devAddr = cpd.getSsAddress();
		char address = cpd.getTargetAddress();
		char usercode = cpd.getUserCode();
		byte cmd = cpd.getFuncWord();
		byte[] params = cpd.getParamBytes();

		switch (cmd) {
		case ProtocolCommand.CMD_REPLY_POWER_ON: {
			//��ʼ����ʱҲ���յ��˻��������Ǵ�ʱ���±���ΪNULL������Ҳ���Ქ������
			if (MainApplication.getInstance().getmHostAlarmName() != null) {
				//����Ǵ���·ģ��ɹ��������������TTS�����͹㲥
				MainApplication.getInstance().getmMsgSender().sendHostAlarm();
				return true;
			}
			setSync(false);
			break;
		}
		case ProtocolCommand.CMD_PLUS:
		case ProtocolCommand.CMD_MINUS:
		case ProtocolCommand.CMD_PLAY_NEXT:
		case ProtocolCommand.CMD_PLAY_PREV:
		case ProtocolCommand.CMD_POWER_OFF:
		case ProtocolCommand.CMD_POWER_ON:
		case ProtocolCommand.CMD_VOLUME_ADJUST:
			handleCtrl(devAddr, cmd, params);
			setSync(false);
			break;
		case ProtocolCommand.CMD_READ_STATE:
			handleQueryState(usercode, address, cmd);
			setSync(false);
			break;
		case ProtocolCommand.CMD_READ_SOFT_VERSION:
			handleQuerySoftVersion(usercode, address, cmd);
			setSync(false);
			break;
		case ProtocolCommand.CMD_SET_USERCODE_ADDRESS:
			handleSetBgMsg(usercode, address, cmd);
			setSync(false);
			break;
		case ProtocolCommand.CMD_STATE_SYNC:
			setSync(true);
			break;
		default:
			break;
		}

		return flag;
	}
	
	private boolean handleQueryMusic(byte cmd) {
		boolean flag = false;
		if (MainApplication.getInstance().getmConfig().getSelfAddr() != 0) {
			MainApplication.getInstance().getmClientSocket().startConnectGateway(ClientSocket.NOTIFI_UPDATE_DATA);
		}
		TcpPacketHandler tcpPacket = new TcpPacketHandler();
		mReplyPackage = tcpPacket.getPackage(cmd);
		return flag;
	}
	
	private boolean handleQuerySoftVersion(char usercode, char addr, byte cmd) {//tcp
		boolean flag = false;
		RadioFreqPacketHandler packet = new RadioFreqPacketHandler();
		packet.setAddress(addr);
		packet.setUsercode(usercode);
		mReplyPackage = packet.getRfReplyPacket(cmd);
		return flag;
	}

	private boolean handleSetBgMsg(char usercode, char addr, byte cmd) {//����
		boolean flag = false;
		RadioFreqPacketHandler packet = new RadioFreqPacketHandler();
		packet.setAddress(addr);
		packet.setUsercode(usercode);
		mReplyPackage = packet.getRfReplyPacket(cmd);
		return flag;
	}

	private boolean handleQueryState(char usercode, char addr, byte cmd) {//����
		boolean flag = false;
		RadioFreqPacketHandler packet = new RadioFreqPacketHandler();
		packet.setAddress(addr);
		packet.setUsercode(usercode);
		mReplyPackage = packet.getRfReplyPacket(cmd);
		return flag;
	}

	private boolean handleCtrl(char address, byte cmd, byte[] params) {//����
		boolean flag = false;
		RadioFreqPacketHandler packet = new RadioFreqPacketHandler();
		packet.setParams(params);
		mReplyPackage = packet.getRfReplyPacket(cmd);
		return flag;
	}

	private CommPackDeal getCommPack(PackageObject obj) {
		RadioFreqRecord rfr = (RadioFreqRecord) obj.getRecords().getRecordByIndex(0);
		if (rfr == null) {
			return null;
		}
		return (CommPackDeal) rfr.getBaseProtocal();
	}

	public void setWorked(boolean worked) {
		mWorked = worked;
	}

	public boolean isWorked() {
		return mWorked;
	}

	public void setReplyPackage(PackageObject replyPackage) {
		mReplyPackage = replyPackage;
	}

	public PackageObject getReplyPackage() {
		return mReplyPackage;
	}

	public void setSync(boolean isSync) {
		mIsSync = isSync;
	}

	public boolean isSync() {
		return mIsSync;
	}
}
