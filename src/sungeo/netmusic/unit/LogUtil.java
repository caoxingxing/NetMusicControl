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
	public static String exction = MainApplication.getInstance().getExtStoDir() + "/" + "������־" + "/";

	public LogUtil() {
		super();
	}

	public static String getFileName() {
		StringBuffer str = new StringBuffer();
		final Calendar c = Calendar.getInstance();

		int mYear = c.get(Calendar.YEAR); //��ȡ��ǰ���

		int mMonth = c.get(Calendar.MONTH);//��ȡ��ǰ�·�

		int mDay = c.get(Calendar.DAY_OF_MONTH);//��ȡ��ǰ�·ݵ����ں���

		str.append(mYear);
		str.append("��");
		str.append(mMonth);
		str.append("��");
		str.append(mDay);
		str.append("��");
		str.append(".txt");
		return str.toString();
	}

	public static String getTimeLog() {
		StringBuffer str = new StringBuffer();
		final Calendar c = Calendar.getInstance();

		int mHour = c.get(Calendar.HOUR_OF_DAY);//��ȡ��ǰ��Сʱ��

		int mMinute = c.get(Calendar.MINUTE);//��ȡ��ǰ�ķ�����

		int mSecond = c.get(Calendar.SECOND);

		str.append(mHour);
		str.append("ʱ");
		str.append(mMinute);
		str.append("��");
		str.append(mSecond);
		str.append("��");
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
			mMainApp.getmMsgSender().showStringMsg(saveFileNameS+"�ļ�������", 1);
		} catch (IOException e) {
			mMainApp.getmMsgSender().showStringMsg("���ļ�����", 1);
		} finally {
			try {
				if (os != null) {
					os.close();
				}

			} catch (IOException e) {
				mMainApp.getmMsgSender().showStringMsg("�رմ��ļ�����", 1);
			}
		}
	}
	
	/**
	 * ���ܣ���¼��־<br>
	 * @param savePathStr ������־·��
	 * @param saveFileNameS ������־�ļ���
	 * @param saveDataStr ������־����
	 * @param saveTypeStr �������ͣ�falsΪ���Ǳ��棬trueΪ��ԭ���ļ�����ӱ���
	 */
	public static void recordLog(String savePathStr,String saveFileNameS,String saveDataStr,boolean saveTypeStr) {
		try {

			String savePath = savePathStr;
			String saveFileName = saveFileNameS;
			String saveData = saveDataStr;
			boolean saveType =saveTypeStr;

			// ׼����Ҫ������ļ�
			File saveFilePath = new File(savePath);
			if (!saveFilePath.exists()) {
				saveFilePath.mkdirs();
			}
			File saveFile = new File(savePath +"/"+ saveFileName);
			if (!saveType && saveFile.exists()) {
				saveFile.delete();
				saveFile.createNewFile();
				// ���������ļ�
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
