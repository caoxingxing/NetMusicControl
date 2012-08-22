/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sungeo.netmusic.protocol;

import sungeo.netmusic.data.MainApplication;
import sungeo.netmusic.objects.PackageObject;
import sungeo.netmusic.objects.RadioFreqRecord;

/**
 *
 * @author Administrator
 */
public class RadioFreqPacketHandler {
	private char 		mUserCode;
	private char 		mAddress;
	private char 		mTargetAddress 	= ProtocolCommand.ADDR_TRANS_CENTER;
	private byte[] 		mParams;
	private boolean 	mSucess 		= false;

	public PackageObject getRfSendPacket(byte cmd) {
		PackageObject obj = new PackageObject();

		CommPackDeal cpd;
		cpd = new CommPackDeal();
		cpd.setTargetAddress(mTargetAddress);
		cpd.setFuncWord(cmd);
		cpd.setSsAddress(MainApplication.getInstance().getmConfig().getSelfAddr());
		byte[] param = null;
		param = getSendParam(cmd);
		cpd.setLenth((byte) (param.length));
		cpd.setParam(param);
		byte[] tmp = cpd.getRequestBytes();

		RadioFreqRecord rfr = new RadioFreqRecord(cpd);
		rfr.setRecordBytes(tmp);

		obj.setPackageType(ProtocolCommand.MSG_TYPE_WIRELESS_PROTOCOL);
		obj.getRecords().addRecord(rfr);

		return obj;
	}

	private byte[] getSendParam(byte cmd) {
		byte[] param = null;
		switch (cmd) {
		case ProtocolCommand.CMD_POWER_OFF:
			param = new byte[1];
			param[0] = 15;
			break;
		case ProtocolCommand.CMD_POWER_ON:
			param = new byte[1];
			param[0] = 15;
			break;
		case ProtocolCommand.CMD_STATE_SYNC:
			param = mParams;
			break;
		default:
			break;
		}

		if (param == null) {
			param = new byte[1];
			param[0] = 0;
		}
		return param;
	}

	public PackageObject getRfReplyPacket(byte cmd) {
		PackageObject obj = new PackageObject();

		CommPackDeal cpd;
		cpd = new CommPackDeal();
		cpd.setTargetAddress(mTargetAddress);
		cpd.setFuncWord((byte)(cmd + ProtocolCommand.CMD_REPLY_BASIC));
		byte[] param = null;
		param = getReplyParam(cmd);

		char selfAddr = MainApplication.getInstance().getmConfig().getSelfAddr();
		char usercode = MainApplication.getInstance().getmConfig().getUsercode();
		
		cpd.setSsAddress(selfAddr);
		cpd.setSourceAddress(selfAddr);
		cpd.setUserCode(usercode);

		cpd.setLenth((byte) (param.length));
		cpd.setParam(param);
		byte[] tmp = cpd.getRequestBytes();

		RadioFreqRecord rfr = new RadioFreqRecord(cpd);
		rfr.setRecordBytes(tmp);

		obj.setPackageType(ProtocolCommand.MSG_TYPE_WIRELESS_PROTOCOL);
		obj.getRecords().addRecord(rfr);

		if (mSucess) {
			obj.setExecStatus((byte)0x0);
		} else {
			obj.setExecStatus((byte)0xff);
		}
		return obj;
	}

	private byte[] getReplyParam(byte cmd) {
		byte[] param = null;
		switch (cmd) {
		case ProtocolCommand.CMD_MINUS:
			if (mParams[0] == 0) {
				setMusicMute(true);
			} else {
				volumeDec();
			}
			param = mParams;
			break;
		case ProtocolCommand.CMD_PLUS:
			if (mParams[0] == 0) {
				setMusicMute(false);
			} else if (mParams[0] == 0x30){//暂停
				close();
				param = mParams;
			} else {
				volumeAdd();
			}
			param = mParams;
			break;
		case ProtocolCommand.CMD_PLAY_NEXT:
			param = new byte[1];
			param[0] = (byte)playNext();
			break;
		case ProtocolCommand.CMD_PLAY_PREV:
			param = new byte[1];
			param[0] = (byte)playPre();
			break;
		case ProtocolCommand.CMD_VOLUME_ADJUST:
			setVolumeByParam();
			param = mParams;
			break;
		case ProtocolCommand.CMD_POWER_OFF:
			close();
			param = new byte[1];
			param[0] = 0;
			break;
		case ProtocolCommand.CMD_POWER_ON:
			MainApplication.getInstance().setmIflytekTts(false);
			playById();
			param = new byte[1];
			param[0] = 1;
			break;
		case ProtocolCommand.CMD_READ_STATE:
			param = getCurStateBytes();
			break;
		case ProtocolCommand.CMD_READ_SOFT_VERSION:
			param = new byte[2];
			String version = MainApplication.getInstance().getAppVersionName();
			String[] str = version.split("\\.");
			param[0] = (byte)Integer.parseInt(str[0]);
			param[1] = (byte)Integer.parseInt(str[1]);
			break;
		case ProtocolCommand.CMD_SET_USERCODE_ADDRESS:
			MainApplication.getInstance().getmConfig().saveUsercode(mUserCode);
			MainApplication.getInstance().getmConfig().saveSelfAddr(mAddress);
			param = new byte[1];
			param[0] = 0;
			break;
		default:
			break;
		}

		if (param == null) {
			param = new byte[1];
			param[0] = 0;
		}
		mSucess = true;
		return param;
	}

	private void close() {
		MainApplication.getInstance().getmMediaMgr().pause(false);
	}

	private int playNext() {
		mSucess = MainApplication.getInstance().getmMediaMgr().remotePlayNext();
		return MainApplication.getInstance().getmCurSondIndex();
	}

	private int playPre() {
		mSucess = MainApplication.getInstance().getmMediaMgr().remotePlayPre();

		return MainApplication.getInstance().getmCurSondIndex();
	}

	private void volumeAdd() {
		MainApplication.getInstance().getmMediaMgr().volumeAdd();
		mSucess = true;
	}

	private void volumeDec() {
		MainApplication.getInstance().getmMediaMgr().volumeDec();
		mSucess = true;
	}

	private void setMusicMute(boolean flag) {
		MainApplication.getInstance().getmMediaMgr().setMute(flag);
		mSucess = true;
	}

	private void playById() {
		if (mParams == null) {
			return;
		}

		int songid = 0;
		int albumid = 0;
		if (mParams.length == 2) {
			albumid = mParams[0];
			songid = mParams[1];
			if (songid <= 0) {
				songid += 256;
			}
		} else if(mParams.length == 1){
			albumid = 1;
			songid = mParams[0];
		}
		if (songid == 0 && albumid == 0) {
			mSucess = false;
			return;
		}
		mSucess = MainApplication.getInstance().getmMediaMgr().remotePlay(albumid, songid - 1);
	}

	private void setVolumeByParam() {
		if (mParams == null) {
			return;
		}

		int volume = mParams[0];
		if (volume < 0x1b || volume > 0x2d) {
			return;
		}

		volume -= 0x1b;
		if (volume > 15) {
			volume = 15;
		}

		MainApplication.getInstance().getmMediaMgr().setVolume(volume);
		mSucess = true;
	}

	private byte[] getCurStateBytes() {
		byte[] param = new byte[6];

		//工作状态
		if (MainApplication.isPlaying) {
			param[0] = 1;
		}

		if (MainApplication.isPause) {
			param[0] = 2;
		}


		//音量
		int volume = MainApplication.getInstance().getmMediaMgr().getCurMusicVolume();

		param[1] = (byte)volume;

		//音效，按照老协议有10种音效，而新的背景音乐没有音效管理，这里存放专辑序号
		param[2] = (byte) MainApplication.getInstance().getmCurPlayAlbumId();
		
		//歌曲数目，老协议最多有255首，新的有6*150首，客户端没有用到，这里存放歌曲序号
		param[3] = (byte) MainApplication.getInstance().getmCurSondIndex();

		//播放模式，老的有6种，新的有3种，暂时客户端没有用到

		//当前播放序号，暂时不用，改用param[3]代替

		return param;
	}

	public void setUsercode(char usercode) {
		mUserCode = usercode;
	}

	public char getUsercode() {
		return mUserCode;
	}

	public void setAddress(char address) {
		mAddress = address;
	}

	public char getAddress() {
		return mAddress;
	}

	public void setParams(byte[] params) {
		mParams = params;
	}

	public byte[] getParams() {
		return mParams;
	}

	public void setTargetAddress(char targetAddress) {
		mTargetAddress = targetAddress;
	}

	public char getTargetAddress() {
		return mTargetAddress;
	}
}
