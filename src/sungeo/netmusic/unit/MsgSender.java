
package sungeo.netmusic.unit;

import sungeo.netmusic.activity.BaseActivity;
import sungeo.netmusic.broadcastreceiver.NotifiBroadcastReceiver;
import sungeo.netmusic.data.MainApplication;
import sungeo.netmusic.gpio.OperaGpio15;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;

public class MsgSender {
    private int debugLevel = 0;
    public static final int MSG_STRING = 0x4662; // �ַ���
    public static final int MSG_AUTOPLAY = 0x4663; // �Զ����ţ�֪ͨ���������ǰ����
    public static final int MSG_REFRESH_PLAYSTATE = 0x4665; // ˢ�²���״̬
    public static final int MSG_DOWNLOAD_ING = 0x4670;
    public static final int MSG_INITSINGLE_ALBUM = 0x4671;
    public static final int MSG_SHOWWIFI_DIALOG = 0x4674; // ��ʾ��WIFI�ĶԻ���
    public static final int MSG_CANCEL_WIFI_DIALOG = 0x4679; // �ر�WIFI��������ʾ��
    public static final int MSG_STOP_COPY = 0x4675;
    public static final int MSG_COPY_ERROR = 0x4676;
    public static final int MSG_COPY_PROGRESS = 0x4677;
    public static final int MSG_ALARM_RING = 0x4678; // ������ʾ��
    public static final int MSG_PARSE_PB_ALBUM_FINISH = 0x4680;
    public static final int MSG_PARSE_PB_SONG_FINISH = 0x4681;
    public static final int MSG_PARSE_PB_LOCAL_FINISH = 0x4682;
    public static final int MSG_PARSE_PB_VERSION_FINISH = 0x4683;
    public static final int MSG_PARSE_PB_CATEGORY_FINISH = 0x4684;
    public static final int MSG_SUBMIT_PB_ALBUM_FINISH = 0x4685;
    public static final int MSG_PARSE_PB_ERROR = 0x4686;
    public static final int MSG_START_PARSE_PB = 0x4687;
    public static final int MSG_UDP_BROADCAST_RECIVE = 0x4688;// UDP�㲥����

    /**
     * @param msg Ҫ�ڵ�ǰactivity����ʾ����Ϣ��
     * @param debugLevel ���Եȼ�������ԽС���ȼ�Խ�ߡ�
     */
    public void showStringMsg(String msg, int debugLevel) {
        if (getDebugLevel() < debugLevel) {
            return;
        }

        sendStrMsg(msg);
    }

    public void sendStrMsg(String str) {
        if (BaseActivity.sCurActivity == null) {
            return;
        }
        StringBuffer strBuf = new StringBuffer();
        strBuf.append(str);
        strBuf.append(" ");
        Bundle bundle = new Bundle();
        bundle.putString("message", strBuf.toString());
        Message msg = BaseActivity.sCurActivity.getMsgHandler().obtainMessage();
        msg.what = MSG_STRING;// �ַ�����Ϣ����
        msg.setData(bundle);
        BaseActivity.sCurActivity.getMsgHandler().sendMessage(msg);
    }

    public void setDebugLevel(int debugLevel) {
        this.debugLevel = debugLevel;
    }

    private int getDebugLevel() {
        return debugLevel;
    }

    public String formatStr(String str) {
        if (str == null) {
            return null;
        }
        StringBuffer tmpStr = new StringBuffer();
        int strLen = str.length();
        if (strLen > 10) {
            String fstr = str.substring(0, 2);

            tmpStr.append(fstr);
            tmpStr.append("...");

            String bstr = str.substring(strLen - 2, strLen);

            tmpStr.append(bstr);
            return tmpStr.toString();
        } else {
            return str;
        }
    }

    public void sendAutoPlay(int curId) {
        if (BaseActivity.sCurActivity == null) {
            return;
        }
        Message msg = BaseActivity.sCurActivity.getMsgHandler().obtainMessage();
        msg.what = MSG_AUTOPLAY;
        msg.arg1 = curId;
        BaseActivity.sCurActivity.getMsgHandler().sendMessage(msg);
    }

    public void sendHostAlarm() {
        if (MainApplication.getInstance().getmMediaMgr().isPlaying()) {
            MainApplication.getInstance().getmMediaMgr().pause(false);
            MainApplication.getInstance().getmMediaMgr().setTtsPause(true);
        }

        if (MainApplication.getInstance().IsDeviceModel()) {
            OperaGpio15 ls = new OperaGpio15();
            boolean resp = ls.closeGpio15();
            if (resp) {
                MainApplication.getInstance().setmGpioIsOpen(false);
            }
        }

        StringBuffer str = new StringBuffer();
        str.append("�ع�Ƽ� ������ʾ ");
        str.append(MainApplication.getInstance().getmHostAlarmName());
        str.append(" ��������");
        sendAlarmToTts(str.toString());
        MainApplication.getInstance().setmHostAlarmName(null);
    }

    public void sendAlarmToTts(String ttsMsg) {
        if (BaseActivity.sCurActivity == null) {
            return;
        }
        Message msg = BaseActivity.sCurActivity.getMsgHandler().obtainMessage();
        msg.what = MSG_ALARM_RING;
        msg.obj = ttsMsg;
        BaseActivity.sCurActivity.getMsgHandler().sendMessage(msg);
    }

    public void sendProgressToRecMsg(String name) {
        Intent intent = new Intent("com.notification.sendMsg");
        intent.putExtra("notifi_msg", name);
        intent.putExtra("notifi_type", NotifiBroadcastReceiver.REFRESH_DOWNLOAD_PROGRESS);
        MainApplication.getInstance().sendBroadcast(intent);
    }

    public void sendCanelToRecMsg() {
        Intent intent = new Intent("com.notification.sendMsg");
        intent.putExtra("notifi_type", NotifiBroadcastReceiver.CANEL_NOTIFICATION);
        MainApplication.getInstance().sendBroadcast(intent);
    }

    public void sendShowToRecMsg() {
        Intent intent = new Intent("com.notification.sendMsg");
        intent.putExtra("notifi_type", NotifiBroadcastReceiver.SHOW_NOTIFICATION);
        MainApplication.getInstance().sendBroadcast(intent);
    }

    public void sendCancelWifiDialogMsg() {
        sendMsg(MSG_CANCEL_WIFI_DIALOG);
    }

    public void sendShowWifiMsg() {
        sendMsg(MSG_SHOWWIFI_DIALOG);
    }

    public void sendRefreshPlayState() {
        sendMsg(MSG_REFRESH_PLAYSTATE);
    }

    public void sendInitSingleAlbum(int id) {
        if (BaseActivity.sCurActivity == null) {
            return;
        }
        Message msg = BaseActivity.sCurActivity.getMsgHandler().obtainMessage();
        msg.what = MSG_INITSINGLE_ALBUM;
        msg.arg1 = id;
        BaseActivity.sCurActivity.getMsgHandler().sendMessage(msg);
    }

    public void sendCopyProgress(int msgType, int progress) {
        if (BaseActivity.sCurActivity == null) {
            return;
        }
        Message msg = BaseActivity.sCurActivity.getMsgHandler().obtainMessage();
        msg.what = msgType;
        msg.arg1 = progress;
        BaseActivity.sCurActivity.getMsgHandler().sendMessage(msg);
    }

    public void sendQueryVersionFinish(int version) {
        if (BaseActivity.sCurActivity == null) {
            return;
        }

        Message msg = BaseActivity.sCurActivity.getMsgHandler().obtainMessage();
        msg.what = MSG_PARSE_PB_VERSION_FINISH;
        msg.arg1 = version;
        BaseActivity.sCurActivity.getMsgHandler().sendMessage(msg);
    }

    public void sendPostForm(String str) {
        if (BaseActivity.sCurActivity == null) {
            return;
        }

        Message msg = BaseActivity.sCurActivity.getMsgHandler().obtainMessage();
        msg.what = MSG_SUBMIT_PB_ALBUM_FINISH;
        msg.obj = str;
        BaseActivity.sCurActivity.getMsgHandler().sendMessage(msg);
    }

    public void sendMsg(int msgType) {
        if (BaseActivity.sCurActivity == null) {
            return;
        }
        Message msg = BaseActivity.sCurActivity.getMsgHandler().obtainMessage();
        msg.what = msgType;
        BaseActivity.sCurActivity.getMsgHandler().sendMessage(msg);
    }

    public void broadcastState(byte isPlay, byte isSilent, byte albumId, byte songId) {
        if (BaseActivity.sCurActivity == null) {
            return;
        }
        byte[] state = new byte[4];
        state[0] = isPlay;
        state[1] = isSilent;
        state[2] = albumId;
        state[3] = songId;
        //state[0]Ϊ1�ǲ��ţ�Ϊ2��ֹͣ��
        //state[1]������ֵ��
        //state[2]�ǵ�ǰ���ŵ�ר����ţ�
        //state[3]�ǵ�ǰ���ŵĸ������
        Intent intent = new Intent("netmusic_state_broadcast");
        intent.putExtra("netmusic_state", state);
        BaseActivity.sCurActivity.sendBroadcast(intent);
    }
    
    public void printLog(String msg) {
        setDebugLevel(6);// ��Ҫ��ӡ��־ʱ����������Ϊ6,����Ҫ��ӡʱ��Ϊ5����
        OutputLogToSD(msg);
        setDebugLevel(1);
    }

    private void OutputLogToSD(String msg) {
        if (debugLevel <= 5) {
            return;
        }
        StringBuffer str = new StringBuffer();
        str.append(LogUtil.getTimeLog());
        str.append(":");
        str.append(msg);
        str.append("\r\n");
        LogUtil.recordLog(LogUtil.getFileName(), str.toString());
        LogUtil.recordLog(LogUtil.exction, LogUtil.getFileName(), str.toString(), true);
    }
}
