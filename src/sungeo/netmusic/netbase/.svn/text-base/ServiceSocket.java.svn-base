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
	private ExecutorService 	mExecutorService 	= null;		// 线程池
	private boolean				mIsCloseing			= false;	//准备关闭监听
	private boolean 			mIsRunning			= false;
	
	private final static int 	sGatewayServerPort 	= 6019;		//监听网关连接固定端口

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
		/*close并没有真正的关闭socket，会等待240s才关闭，
		 * 如果此处执行close，而又在4分钟内又打开程序，会再执行
		 * mServer = new ServerSocket(sGatewayServerPort);
		 * 此时会抛出java.net.bindexception:the address is already in use
		 * 所以，此处并不执行close，只是把mServerThread和mExecutorService置null
		 */
		mIsRunning = false;
		MainApplication.getInstance().setmThreadSwitch(false);
		try {
			if (mServer != null) {
				mIsCloseing = true;
				//在 accept() 中所有当前阻塞的线程都将会抛出 SocketException
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
				//打开前判断网络是否连接，无连接不做处理
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
				
				mExecutorService = Executors.newCachedThreadPool();// 创建一个线程池
				while (MainApplication.getInstance().ismThreadSwitch()) {
					//MainApplication.getInstance().getmMsgSender().printLog("开始监听……");
					Socket client = mServer.accept();
					
					/**
					 * 加下面if判断是为了在关闭监听线程时，是将
					 * MainApplication.getInstance().ismThreadSwitch()置为false，
					 * 而此时accept还处理最后一个连接，处理完最后一个连接后，才会跳出while，下面的if判断就是
					 * 为了在关闭连接后，不在处理最后一个无效的连接
					 */
					if (MainApplication.getInstance().ismThreadSwitch()) {
						mExecutorService.execute(new SocketReciveThread(client, false));// 开启一个客户端线程.
						client = null;
					}
				}
				
				colseServerSocket();
			} catch (IOException ex) {
				mIsRunning = false;
				//MainApplication.getInstance().getmMsgSender().printLog("监听线程出错"+ex.toString());
				//原因是：执行colseServerSocket()时，accept也抛异常，
				//也就是正常关闭，是不需要重新连接的
				if (!mIsCloseing) {
					//非正常关闭抛出的异常要做处理：延时2S重新打开监听socket
					delayOpenServerSocket();
				}
			}
		}
	}
}
