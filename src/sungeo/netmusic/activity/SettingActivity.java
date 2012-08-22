package sungeo.netmusic.activity;

import java.util.regex.Pattern;

import sungeo.netmusic.R;
import sungeo.netmusic.data.ConfigPreferences;
import sungeo.netmusic.data.MainApplication;
import sungeo.netmusic.netbase.ClientSocket;
import sungeo.netmusic.unit.MsgSender;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SettingActivity extends BaseActivity{
	private EditText mEditSerial;
	private EditText mEditIp;
	private EditText mEditAddr;
	private EditText mEditUsercode;
	private Button mBtnConfirm;
	private boolean  mFlag = false;
	private final static int    ITEM_EDIT	 = 0;
	private final static int 	ITEM_FIND_GATEWAY = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setserail);
		initEditTextSerial();
		initBtn();
		initEditTextIP();
		initEditTextPoweraddress();
		initEditTextUsercodeAddr();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean ret = true;

		menu.add(0, ITEM_EDIT, ITEM_EDIT, "修改");      
		//menu.add(0, ITEM_FIND_GATEWAY, ITEM_FIND_GATEWAY, "发现网关");

		return ret;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean ret = true;
		int itemId = item.getItemId();

		if (itemId == ITEM_EDIT) {
			if (mFlag) {
				item.setTitle("修改");
				mEditSerial.setEnabled(false);
				mEditIp.setEnabled(false);
				mEditAddr.setEnabled(false);
				mEditUsercode.setEnabled(false);
				
				mEditSerial.setFocusableInTouchMode(false);
				mEditIp.setFocusableInTouchMode(false);
				mEditAddr.setFocusableInTouchMode(false);
				mEditUsercode.setFocusableInTouchMode(false);
				
				mBtnConfirm.setVisibility(View.GONE);
				
				mFlag = false;
			} else {
				item.setTitle("取消修改");
				mEditSerial.setEnabled(true);
				mEditIp.setEnabled(true);
				mEditAddr.setEnabled(true);
				mEditUsercode.setEnabled(true);
				
				mEditSerial.setFocusableInTouchMode(true);
				mEditIp.setFocusableInTouchMode(true);
				mEditAddr.setFocusableInTouchMode(true);
				mEditUsercode.setFocusableInTouchMode(true);
				
				mBtnConfirm.setVisibility(View.VISIBLE);
				mFlag = true;
			}
		} else if (itemId == ITEM_FIND_GATEWAY){
			if (MainApplication.getInstance().checkNetwork()) {
				MainApplication.getInstance().getmMsgSender().sendStrMsg("正在发送广播……");
				MainApplication.getInstance().sendUdpBroadcast();
			} else {
				MainApplication.getInstance().getmMsgSender().sendShowWifiMsg();
			}
		}
		return ret;
	}

	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			saveAll(true);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void initEditTextPoweraddress() {
		mEditAddr = (EditText) findViewById(R.id.edit_power_addr);
		if (mEditAddr == null) {
			return;
		}

		char address = mMainApp.getmConfig().getSelfAddr();
		if (!String.valueOf(Integer.toHexString((int)address)).equals("0")) {
			mEditAddr.setText(String.valueOf(Integer.toHexString((int)address)));
		}
		mEditAddr.setEnabled(false);
		mEditAddr.setFocusableInTouchMode(false);
	}

	private void initEditTextUsercodeAddr() {
		mEditUsercode = (EditText)findViewById(R.id.edit_usercode_addr);
		if (mEditUsercode == null) {
			return;
		}
		
		char usercode = mMainApp.getmConfig().getUsercode();
		if (!String.valueOf(Integer.toHexString((int)usercode)).equals("0")) {
			mEditUsercode.setText(String.valueOf(Integer.toHexString((int)usercode)));
		}
		mEditUsercode.setEnabled(false);
		mEditUsercode.setFocusableInTouchMode(false);
	}
	
	private void initEditTextIP() {
		mEditIp = (EditText)findViewById(R.id.edit_gatway_ip);
		if (mEditIp == null) {
			return;
		}

		mEditIp.setText("192.168.1.11");//默认显示一个IP地址
		if (!mMainApp.getmConfig().getGatewayIp().equals("")) {
			mEditIp.setText(mMainApp.getmConfig().getGatewayIp());
		}
		mEditIp.setEnabled(false);
		mEditIp.setFocusableInTouchMode(false);
	}

	private void initEditTextSerial() {
		mEditSerial = (EditText) findViewById(R.id.edit_serial);
		if (mEditSerial == null) {
			return;
		}

		int serial = mMainApp.getmConfig().getSelfSerial();

		if (serial != 0) {
			mEditSerial.setText(Integer.toHexString(serial));
			mEditSerial.setEnabled(false);
			mEditSerial.setFocusableInTouchMode(false);
		} else {
			mFlag = true;
		}
	}

	private void initBtn() {
		mBtnConfirm = (Button) findViewById(R.id.btn_ok);
		if (!mFlag)
			mBtnConfirm.setVisibility(View.GONE);
		
		mBtnConfirm.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				saveAll(false);
			}});
	}

	private void saveAll(boolean back) {
		boolean ret = saveSerial();
		if (ret) {
			setSerialTag();
		}

		boolean flag = saveGatewayIP();
		if (!flag) {
			mMainApp.getmMsgSender().sendStrMsg("IP地址不正确！");
		}

		if (ret && flag && getSerialTag()) {
			mMainApp.getmClientSocket().startConnectGateway(ClientSocket.NOTIFI_GATEWAY_IP);
		}

		boolean result = saveAddress();
		if (!result) {
			//mMainApp.getmMsgSender().sendStrMsg("地址码格式不正确！");
		}
		
		boolean user = saveUsercode();
		if (!user) {
			//mMainApp.getmMsgSender().sendStrMsg("用户码格式不正确！");
		}
		if (ret && flag) {
			if (!back) {
				mMainApp.getmMsgSender().sendStrMsg("保存成功！");
			}

			finish();
		}
	}

	private boolean checkSerial(int serial) {
		boolean ret = true;
		if (serial > ConfigPreferences.BG_MAX_SERIAL || serial < ConfigPreferences.BG_MIN_SERIAL) {
			ret = false;
		}
		return ret;
	}

	private boolean saveAddress() {
		boolean ret = true;
		if (mEditAddr == null) {
			return false;
		}
		String add = mEditAddr.getText().toString();
		if (add == null || add.equals("")
				|| checkHexStr(add)) {
			return false;
		}
		int address = 0;
		try {
			address = Integer.parseInt(add, 16);
		} catch (NumberFormatException e) {
			//mMainApp.getmMsgSender().sendStrMsg("地址码格式不正确，请重新输入");
			return false;
		}

		mMainApp.getmConfig().saveSelfAddr((char)address);

		return ret;
	}

	private boolean saveUsercode() {
		boolean ret = true;
		if (mEditUsercode == null) {
			return false;
		}
		String add = mEditUsercode.getText().toString();
		if (add == null || add.equals("")
				|| checkHexStr(add)) {
			return false;
		}
		int usercode = 0;
		try {
			usercode = Integer.parseInt(add, 16);
		} catch (NumberFormatException e) {
			//mMainApp.getmMsgSender().sendStrMsg("用户码格式不正确，请重新输入");
			return false;
		}

		mMainApp.getmConfig().saveUsercode((char)usercode);

		return ret;
	}
	
	private boolean saveGatewayIP() {
		if (mEditIp == null) {
			return false;
		}

		String ip = mEditIp.getText().toString();
		if (ip == null || ip.contentEquals("")) {
			//提示输入有误
			showToastStr("输入IP有误 ，请重新输入");
			return false;
		}

		mMainApp.getmConfig().saveGatewayIp(ip);
		return true;
	}

	private boolean saveSerial() {
		if (mEditSerial == null) {
			return false;
		}

		String strSerial = mEditSerial.getText().toString();
		if (strSerial == null 
				|| strSerial.contentEquals("")
				|| checkHexStr(strSerial)) {
			//提示输入有误
			showToastStr("输入序列号有误 ，请重新输入");
			return false;
		}

		int serial = Integer.valueOf(strSerial, 16);
		boolean ret = checkSerial(serial);
		if (!ret) {
			//提示输入有误
			showToastStr("输入序列号有误，请重新输入");
			return false;
		}

		if (!mMainApp.getmVersionMgr().ismRunning()) {
			mMainApp.getmVersionMgr().startCheckVersion();
		}
		mMainApp.getmConfig().saveSelfSerial(serial);
		mEditSerial.setEnabled(false);
		return true;
	}

	private boolean checkHexStr(String str) {
		boolean flag = false;
		for(int i=0;i<str.length();i++){//循环遍历字符串
			//用char包装类中的判断数字的方法判断每一个字符
			if(Pattern.compile("(?i)[g-z]").matcher(str).find()){
				flag=true; // 循环完毕以后，如果为true，则代表字符串中包含非法的16进制数字
			}
		}
		return flag;
	}

	private void setSerialTag() {
		SharedPreferences myShare = getPreferences(Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = myShare.edit();

		editor.putBoolean("serialtag", true);
		editor.commit();
	}

	private boolean getSerialTag() {
		SharedPreferences myShare = getPreferences(Activity.MODE_PRIVATE);

		return myShare.getBoolean("serialtag", false);
	}

	@Override
	public void refreshUi(Message msg) {
		if (msg == null) {
			return;
		}
		
		if (msg.what == MsgSender.MSG_UDP_BROADCAST_RECIVE) {
			MainApplication.getInstance().getmMsgSender().sendStrMsg("已经找到网关！");
			if (!mMainApp.getmConfig().getGatewayIp().equals("")) {
				mEditIp.setText(mMainApp.getmConfig().getGatewayIp());
			}
		}
	}
}
