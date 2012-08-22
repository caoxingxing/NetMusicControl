/*
 * 超时处理适配器
 */

package sungeo.netmusic.unit;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 超时处理适配器
 * @author caoxingxing
 */
public class OverTimeAdapter
{
	private ActionListener 	mActionListener;
	private long 			mWaitTime;
	private Timer 			mTimer;
	private int 			mPlus;
	private boolean 		mRunning = false;

	/**
	 * 开始超时计时
	 */
	public void start()
	{
		mTimer = new Timer();
		setRunning(true);

		mTimer.schedule(new TimerTask() {

			@Override
			public void run() {                
				handleOverTimeEvent();
			}
		}, getWaitTime());
	}

	private void handleOverTimeEvent() {

		// 调用用户自定义处理函数
		if (mActionListener != null) {
			mActionListener.overTimePerformed(mPlus);
		}

		// 关闭定时器
		cancel();
	}

	/**
	 * 取消计时
	 */
	public void cancel()
	{
		if(isRunning())
		{
			mTimer.cancel();
			setRunning(false);
		}
	}

	/**
	 * 获取计时类附加参数
	 * @return 附加参数
	 */
	public int getPlus()
	{
		return mPlus;
	}

	public void setPlus(int plus)
	{
		mPlus = plus;
	}

	public long getWaitTime()
	{
		return mWaitTime;
	}

	/**
	 * 设置超时单位
	 * @param waitTime 超时单位（毫秒）
	 */
	public void setWaitTime(long waitTime)
	{
		mWaitTime = waitTime;
	}

	public ActionListener getActionListener()
	{
		return mActionListener;
	}

	public void setActionListener(ActionListener actionListener)
	{
		mActionListener = actionListener;
	}

	/**
	 * @return the running
	 */
	public boolean isRunning() {
		return mRunning;
	}

	/**
	 * @param mRunning the running to set
	 */
	public void setRunning(boolean isrunning) {
		mRunning = isrunning;
	}
}
