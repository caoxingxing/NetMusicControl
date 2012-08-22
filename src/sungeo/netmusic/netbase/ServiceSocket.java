package sungeo.netmusic.netbase;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import sungeo.netmusic.data.MainApplication;

public class ServiceSocket {
	private ServerThread 		mServerThread 		= null;
	private ServerSocket 		mServer 			= null;
	private ExecutorService 	mExecutorService 	= null;		// �̳߳�
	private boolean				mIsCloseing			= false;	//׼���رռ���
	private boolean 			mIsRunning			= false;
	
	private final static int 	sGatewayServerPort 	= 6019;		//�����������ӹ̶��˿�

	/**
	 * @param args
	 */
	public void startServerSocket() {
		if (!MainApplication.getInstance().ismThreadSwitch()) {
			mServerThread = null;
			if (mExecutorService != null) {
				mExecutorService.shutdown();
			}
			mExecutorService = null;
			MainApplication.getInstance().setmThreadSwitch(true);
			if (mIsRunning) {
				return;
			}
			mServerThread = new ServerThread();
			mIsRunning = true;
			mServerThread.start();
			mIsCloseing = false;
		}
	}

	public void colseServerSocket() {
		/*close��û�������Ĺر�socket����ȴ�240s�Źرգ�
		 * ����˴�ִ��close��������4�������ִ򿪳��򣬻���ִ��
		 * mServer = new ServerSocket(sGatewayServerPort);
		 * ��ʱ���׳�java.net.bindexception:the address is already in use
		 * ���ԣ��˴�����ִ��close��ֻ�ǰ�mServerThread��mExecutorService��null
		 */
		mIsRunning = false;
		MainApplication.getInstance().setmThreadSwitch(false);
		try {
			if (mServer != null) {
				mIsCloseing = true;
				//�� accept() �����е�ǰ�������̶߳������׳� SocketException
				mServer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (mExecutorService != null) {
			mExecutorService.shutdown();
		}
		
		mServer = null;
		mServerThread = null;
		mExecutorService = null;
	}
	
	private void delayOpenServerSocket() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				//��ǰ�ж������Ƿ����ӣ������Ӳ�������
				if (MainApplication.getInstance().checkNetwork()) {
					startServerSocket();
				}
			}}, 2000);
	}
	
	public void setmIsRunning(boolean mIsRunning) {
		this.mIsRunning = mIsRunning;
	}

	public boolean ismIsRunning() {
		return mIsRunning;
	}

	class ServerThread extends Thread {
		@Override
		public synchronized void run() {
			try {
				if (mServer == null) {
					mServer = new ServerSocket(sGatewayServerPort);
				} else {
					boolean isOpen = mServer.isBound() && !mServer.isClosed();
					
					if (!isOpen) {
						mServer = new ServerSocket(sGatewayServerPort);
					}
				}
				
				mExecutorService = Executors.newCachedThreadPool();// ����һ���̳߳�
				while (MainApplication.getInstance().ismThreadSwitch()) {
					//MainApplication.getInstance().getmMsgSender().printLog("��ʼ��������");
					Socket client = mServer.accept();
					
					/**
					 * ������if�ж���Ϊ���ڹرռ����߳�ʱ���ǽ�
					 * MainApplication.getInstance().ismThreadSwitch()��Ϊfalse��
					 * ����ʱaccept���������һ�����ӣ����������һ�����Ӻ󣬲Ż�����while�������if�жϾ���
					 * Ϊ���ڹر����Ӻ󣬲��ڴ������һ����Ч������
					 */
					if (MainApplication.getInstance().ismThreadSwitch()) {
						mExecutorService.execute(new SocketReciveThread(client, false));// ����һ���ͻ����߳�.
						client = null;
					}
				}
				
				colseServerSocket();
			} catch (IOException ex) {
				mIsRunning = false;
				//MainApplication.getInstance().getmMsgSender().printLog("�����̳߳���"+ex.toString());
				//ԭ���ǣ�ִ��colseServerSocket()ʱ��acceptҲ���쳣��
				//Ҳ���������رգ��ǲ���Ҫ�������ӵ�
				if (!mIsCloseing) {
					//�������ر��׳����쳣Ҫ��������ʱ2S���´򿪼���socket
					delayOpenServerSocket();
				}
			}
		}
	}
}
