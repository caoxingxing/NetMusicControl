/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sungeo.netmusic.unit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import sungeo.netmusic.data.MainApplication;
import android.content.Context;

/**
 *
 * @author Administrator
 */
public class LogUtil {
	public static String exction = MainApplication.getInstance().getExtStoDir() + "/" + "测试日志" + "/";

	public LogUtil() {
		super();
	}

	public static String getFileName() {
		StringBuffer str = new StringBuffer();
		final Calendar c = Calendar.getInstance();

		int mYear = c.get(Calendar.YEAR); //获取当前年份

		int mMonth = c.get(Calendar.MONTH);//获取当前月份

		int mDay = c.get(Calendar.DAY_OF_MONTH);//获取当前月份的日期号码

		str.append(mYear);
		str.append("年");
		str.append(mMonth);
		str.append("月");
		str.append(mDay);
		str.append("日");
		str.append(".txt");
		return str.toString();
	}

	public static String getTimeLog() {
		StringBuffer str = new StringBuffer();
		final Calendar c = Calendar.getInstance();

		int mHour = c.get(Calendar.HOUR_OF_DAY);//获取当前的小时数

		int mMinute = c.get(Calendar.MINUTE);//获取当前的分钟数

		int mSecond = c.get(Calendar.SECOND);

		str.append(mHour);
		str.append("时");
		str.append(mMinute);
		str.append("分");
		str.append(mSecond);
		str.append("秒");
		return str.toString();
	}

	public static void recordLog(String saveFileNameS,String saveDataStr) {
		FileOutputStream os = null;
		MainApplication mMainApp = MainApplication.getInstance();
		byte[] data = saveDataStr.getBytes();
		try {
			os = mMainApp.openFileOutput(saveFileNameS, Context.MODE_APPEND);
			int len = data.length;
			byte[] bodyLen = new byte[4];
			bodyLen[3] = (byte)len;
			bodyLen[2] = (byte)(len >> 8);
			bodyLen[1] = (byte)(len >> 16);
			bodyLen[0] = (byte)(len >> 24);
			os.write(bodyLen);
			os.write(data);
		} catch (FileNotFoundException e) {
			mMainApp.getmMsgSender().showStringMsg(saveFileNameS+"文件不存在", 1);
		} catch (IOException e) {
			mMainApp.getmMsgSender().showStringMsg("打开文件出错", 1);
		} finally {
			try {
				if (os != null) {
					os.close();
				}

			} catch (IOException e) {
				mMainApp.getmMsgSender().showStringMsg("关闭打开文件出错", 1);
			}
		}
	}
	
	/**
	 * 功能：记录日志<br>
	 * @param savePathStr 保存日志路径
	 * @param saveFileNameS 保存日志文件名
	 * @param saveDataStr 保存日志数据
	 * @param saveTypeStr 保存类型，fals为覆盖保存，true为在原来文件后添加保存
	 */
	public static void recordLog(String savePathStr,String saveFileNameS,String saveDataStr,boolean saveTypeStr) {
		try {

			String savePath = savePathStr;
			String saveFileName = saveFileNameS;
			String saveData = saveDataStr;
			boolean saveType =saveTypeStr;

			// 准备需要保存的文件
			File saveFilePath = new File(savePath);
			if (!saveFilePath.exists()) {
				saveFilePath.mkdirs();
			}
			File saveFile = new File(savePath +"/"+ saveFileName);
			if (!saveType && saveFile.exists()) {
				saveFile.delete();
				saveFile.createNewFile();
				// 保存结果到文件
				FileOutputStream fos = new FileOutputStream(saveFile, saveType);
				fos.write(saveData.getBytes());
				fos.close();
			} else if (saveType && saveFile.exists()) {
				//saveFile.createNewFile();
				FileOutputStream fos = new FileOutputStream(saveFile, saveType);
				fos.write(saveData.getBytes());
				fos.close();
			}else if (saveType && !saveFile.exists()) {
				saveFile.createNewFile();
				FileOutputStream fos = new FileOutputStream(saveFile, saveType);
				fos.write(saveData.getBytes());
				fos.close();
			}



		} catch (Exception e) {
			//recordLog(savePathStr, saveFileNameS, saveDataStr, saveTypeStr);

			e.printStackTrace();
		}
	}
}
