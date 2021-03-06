/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sungeo.netmusic.netbase;

import java.io.IOException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import sungeo.netmusic.data.MainApplication;

/**
 *
 * @author caoxingxing
 */
public class ClientSocket{
	private Thread 				mReceiveThread 		= null;
	private Socket 				mClientSocket 		= null;
	private byte[] 				mParam;
	private int 				mMsgType = 0;
	private final static int 	sLocalServerPort 	= 6018;	//连接网关固定端口
	public final static int 	NOTIFI_GATEWAY_IP 	= 0x3000;
	public final static int 	SYNC_STATE 			= 0x3001;
	public final static int 	OPEN_CTRL_POWER 	= 0x3003;
	public final static int 	CLOSE_CTRL_POWER	= 0x3004;
	public final static int 	NOTIFI_UPDATE_DATA	= 0x3005;

	public void startConnectGateway(int flag, byte[] param) {
		if (MainApplication.getInstance().checkNetwork()) {
			openConnectThread(flag, param);
		}
	}
	public void startConnectGateway(int flag) {
		if (MainApplication.getInstance().checkNetwork()) {
			openConnectThread(flag);
		}
	}

	private void openConnectThread(int flag, byte[] param) {
		mParam = param;
		mMsgType = flag;
		ConnectThread ct = new ConnectThread();
		Thread thread = new Thread(ct);
		thread.start();
	}

	private void openConnectThread(int flag) {
		mMsgType = flag;
		ConnectThread ct = new ConnectThread();
		Thread thread = new Thread(ct);
		thread.start();
	}

	/**
	 * 建立连接线程
	 */
	class ConnectThread implements Runnable {

		@Override
		public void run() {
			connectServer();
		}
	}

	public void connectServer() {
		try {
			String gatewayIp = MainApplication.getInstance().getmConfig().getGatewayIp();
			//建立socket
			mClientSocket = new Socket(gatewayIp, sLocalServerPort);

			//启动接受线程
			SocketReciveThread recive = new SocketReciveThread(mClientSocket, true);

			if (mMsgType == ClientSocket.NOTIFI_GATEWAY_IP) {
				mReceiveThread = new Thread(recive);
				recive.sendInfoToGateway();
				mReceiveThread.start();
			} else if (mMsgType == ClientSocket.SYNC_STATE) {
				//状态同步，不用等待回馈，发完就关闭连接
				recive.sendStateToGateway(mParam);
				recive.disconnect();
				disconnect();
			} else if (mMsgType == ClientSocket.OPEN_CTRL_POWER) {
				mReceiveThread = new Thread(recive);
				recive.sendOpenPowerToGateway();
				mReceiveThread.start();
			} else if (mMsgType == ClientSocket.CLOSE_CTRL_POWER) {
				mReceiveThread = new Thread(recive);
				recive.sendClosePowerToGateway();
				mReceiveThread.start();
			} else if (mMsgType == ClientSocket.NOTIFI_UPDATE_DATA) {
				mReceiveThread = new Thread(recive);
				recive.sendDataToGateway();
				mReceiveThread.start();
			}
		} catch (Exception e) {
			delayConnect();
		}
	}

	/**
	 * 如果网关没有上电，每次连接失败就会马上又重新连接，如次会消耗CPU，所以延时1S进行重连
	 */
	private void delayConnect() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask(){

			@Override
			public void run() {
				//主动连接网关失败，重新连接
				startConnectGateway(mMsgType);
			}}, 2000);
	}

	private void disconnect() {
		if (mClientSocket != null) {
			try {
				mClientSocket.close();
				mClientSocket = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void setMsgType(int msgType) {
		mMsgType = msgType;
	}

	public int getMsgType() {
		return mMsgType;
	}

	public void setParam(byte[] param) {
		mParam = param;
	}

	public byte[] getParam() {
		return mParam;
	}
}
