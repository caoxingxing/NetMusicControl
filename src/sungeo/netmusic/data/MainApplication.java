
package sungeo.netmusic.data;

import android.app.Application;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.DisplayMetrics;

import org.apache.http.conn.util.InetAddressUtils;

import sungeo.netmusic.broadcastreceiver.CustomBroadcastReceiver;
import sungeo.netmusic.broadcastreceiver.HardwareBroadcastReceiver;
import sungeo.netmusic.broadcastreceiver.HeadsetBroadcastReceiver;
import sungeo.netmusic.broadcastreceiver.NetworkStateBroadcastReceiver;
import sungeo.netmusic.broadcastreceiver.NotifiBroadcastReceiver;
import sungeo.netmusic.broadcastreceiver.PhoneStateBroadcastReceiver;
import sungeo.netmusic.broadcastreceiver.ScreenOnBroadcastReceiver;
import sungeo.netmusic.download.DownloadMgr;
import sungeo.netmusic.manager.AlbumMgr;
import sungeo.netmusic.manager.IpInfoMgr;
import sungeo.netmusic.manager.MediaMgr;
import sungeo.netmusic.manager.VersionMgr;
import sungeo.netmusic.netbase.ClientSocket;
import sungeo.netmusic.netbase.ServiceSocket;
import sungeo.netmusic.netbase.UdpSocket;
import sungeo.netmusic.unit.MsgSender;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainApplication extends Application {
    private int mCurPlayAlbumId;
    private int mLastPlayAlbumId;
    private int mCurSongIndex;
    private int mNextSongIndex;
    private int mCurShowAlbumId;
    private int mCurDownloadAlbumId;
    private int mAllSongCount;

    private boolean mCallPhone = false;
    private boolean mReceiverSms = false;
    private boolean mNotifiSucess = false;
    private boolean mThreadSwitch = false;// 监听网关连接线程开关
    private boolean mGpioIsOpen = false;// GPIO的开关状态
    private boolean mUdpSended = false; // 已经发送过UDP广播
    private boolean mIflytekTts = true; // 是否使用讯飞语音报警

    private String mServerVersion;
    private String mLastPlaySongName;
    private String mHostAlarmName = null;
    private String mUdiskMountedPath; // U盘挂载位置。

    private KeyguardLock mKeyguradLock;
    private IpInfoMgr mIpInfoMgr;
    private AlbumMgr mAlbumMgr;
    private MediaMgr mMediaMgr;
    private VersionMgr mVersionMgr;
    private DownloadMgr mDownMgr;
    private MsgSender mMsgSender;
    private ConfigPreferences mConfig;
    private ServiceSocket mServerSocket;
    private ClientSocket mClientSocket;
    private WakeLock mWakeLock;

    private HardwareBroadcastReceiver mHardwareReceiver;
    private HeadsetBroadcastReceiver mHeadsetReceiver;
    private ScreenOnBroadcastReceiver mScreenOnReceiver;
    private PhoneStateBroadcastReceiver mPhoneReceiver;
    private NotifiBroadcastReceiver mNotifiReceiver;
    private NetworkStateBroadcastReceiver mNetworkStateReceiver;
    private CustomBroadcastReceiver mCustomReceiver;

    private List<AlbumRecordInfo> mAllAlbum = new ArrayList<AlbumRecordInfo>();
    private List<AlbumRecordInfo> mDownAlbum = new ArrayList<AlbumRecordInfo>();

    public final static int ALBUM_NUM = 6;
    public static boolean isPlaying = false;
    public static boolean isPause = false;
    public static boolean isSilent = false;
    private static MainApplication instance;
    public DisplayMetrics mMetrics;

    public static MainApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mMsgSender = new MsgSender();
        mMediaMgr = new MediaMgr();
        mAlbumMgr = new AlbumMgr();
        mVersionMgr = new VersionMgr();
        mIpInfoMgr = new IpInfoMgr();
        mConfig = new ConfigPreferences();
        mServerSocket = new ServiceSocket();
        mLastPlayAlbumId = AlbumInfoDb.FIRST_ALBUM_ID;
        mCurDownloadAlbumId = -1;

        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

        setmKeyguradLock(keyguardManager.newKeyguardLock("netmusicctrl"));

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "netmusic");
        // WifiManager wifiManager = (WifiManager)
        // getSystemService(Context.WIFI_SERVICE);
        // mWifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL,
        // "netmusic");
        mWakeLock.acquire();
        // mWifiLock.acquire();
        initAllReceiver();
        initAllAlbumInfo();
    }

    @Override
    public void onTerminate() {
        if (mKeyguradLock == null) {
            return;
        }

        mKeyguradLock.reenableKeyguard();
        // mWifiLock.release();
        mWakeLock.release();
    }

    public void saveVersion(int id) {
        if (mServerVersion == null) {
            return;
        }

        String[] strS = mServerVersion.split("//.");
        String localVer = getmConfig().getVersion();
        if (localVer == null) {
            return;
        }

        String[] strL = localVer.split("//.");
        int len = strS.length;
        if (len != strL.length) {
            return;
        }

        if (id < 0 || id >= len) {
            return;
        }

        if (id + 1 == AlbumInfoDb.SIXTH_ALBUM_ID) {
            int verSix = Integer.valueOf(strL[id]);
            verSix++;
            strL[id] = String.valueOf(verSix);
        } else {
            strL[id] = strS[id];
        }

        StringBuffer localVersion = new StringBuffer();
        for (int i = 0; i < len; i++) {
            localVersion.append(strL[i]);
            if (i != len - 1) {
                localVersion.append(".");
            }
        }
        getmConfig().saveVersion(localVersion.toString());
    }

    /**
     * @param type
     * @param data
     */
    public void saveDataToDb(int type, byte[] data) {
        AlbumInfoDb aid = new AlbumInfoDb();
        aid.saveData(type, data);

        AlbumRecordInfo album = mAllAlbum.get(type - 1);
        album.setAlbumRecords(data);
    }

    public byte[] getDataFromDb(int type) {
        AlbumInfoDb aid = new AlbumInfoDb();
        return aid.getData(type);
    }

    public byte[] getAlbumBytesByPos(int pos) {
        int len = mAllAlbum.size();
        for (int i = 0; i < len; i++) {
            AlbumRecordInfo album = (AlbumRecordInfo) mAllAlbum.get(i);
            if (album != null && album.getPosition() == pos) {
                return album.getAlbumSongBytes();
            }
        }

        return null;
    }

    private void initAllReceiver() {
        mHardwareReceiver = new HardwareBroadcastReceiver();
        mHeadsetReceiver = new HeadsetBroadcastReceiver();
        mScreenOnReceiver = new ScreenOnBroadcastReceiver();
        mPhoneReceiver = new PhoneStateBroadcastReceiver();
        mNotifiReceiver = new NotifiBroadcastReceiver(this);
        mNetworkStateReceiver = new NetworkStateBroadcastReceiver();
        mCustomReceiver = new CustomBroadcastReceiver();
    }

    public void initAllAlbumInfo() {
        getmAllAlbum().clear();
        for (int i = 0; i < ALBUM_NUM; i++) {
            AlbumRecordInfo album = new AlbumRecordInfo();

            getAllAlbumInfoByDb(i, album);

            initAlbumMode(album);
            initAlbumCurId(album);

            getmAllAlbum().add(album);
        }
    }

    private void initAlbumMode(AlbumRecordInfo album) {
        if (album == null) {
            return;
        }

        if (album.getPlayMode() == -1) {
            album.setPlayMode(MediaMgr.PLAY_MODE_ORDER);
        }
    }

    private void initAlbumCurId(AlbumRecordInfo album) {
        if (album == null) {
            return;
        }

        if (album.getCurPlayIndex() == -1) {
            album.setCurPlayIndex(0);
        }
    }

    private void getAllAlbumInfoByDb(int id, AlbumRecordInfo album) {
        int type = id + 1;

        if (type == 0 || id >= ALBUM_NUM || id < 0) {
            return;
        }

        byte[] data = getDataFromDb(type);
        if (data == null) {
            StringBuffer str = new StringBuffer();
            if (type == AlbumInfoDb.SIXTH_ALBUM_ID) {
                str.append("用户专辑");
            } else {
                str.append("专辑");
                str.append(id + 1);
            }

            album.setAlbumName(str.toString());
            album.setPosition(type);
        } else {
            album.setAlbumRecords(data);
        }
    }

    public AlbumRecordInfo getCurAlbum() {
        if (mCurPlayAlbumId <= 0 || mCurPlayAlbumId > ALBUM_NUM) {
            return null;
        }

        return (AlbumRecordInfo) mAllAlbum.get(mCurPlayAlbumId - 1);
    }

    public String getCurSongName() {
        AlbumRecordInfo album = getCurAlbum();
        if (album == null) {
            return "";
        }

        String[] songName = album.getSongName();

        if (songName == null) {
            return "";
        }

        int count = songName.length;
        if (mCurSongIndex < 0 || mCurSongIndex >= count) {
            return "";
        }
        return album.getSongName()[mCurSongIndex];
    }

    public String getNextSongName() {
        AlbumRecordInfo album = getCurAlbum();
        if (album == null) {
            return "";
        }

        String[] songName = album.getSongName();
        if (songName == null) {
            return "";
        }

        int count = songName.length;
        if (mNextSongIndex < 0 || mNextSongIndex >= count) {
            return "";
        }

        return album.getSongName()[mNextSongIndex];
    }

    public String getExtStoDir() {
        if (!android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            File file = new File("/local");
            if (!file.exists()) {
                return null;
            }
            return "/local";
        }

        return Environment.getExternalStorageDirectory().toString();
    }

    public void setCallPhone(boolean isCallPhone) {
        mCallPhone = isCallPhone;
    }

    public void startNetTimer() {
        if (getmClientSocket() == null) {
            setmClientSocket(new ClientSocket());
        }
        delayOpenSocket();

        mVersionMgr.startCheckVersion();
    }

    public void startAllTimer() {
        startNetTimer();
        if (getExtStoDir() != null) {
            mAlbumMgr.start();
        }
    }

    public void stopNetTimer() {
        mVersionMgr.stopCheckVersion();
        if (mServerSocket != null) {
            mServerSocket.colseServerSocket();
            mServerSocket = null;
        }

        mIpInfoMgr.stopIpInfoTimer();
    }

    public void stopAllTimer() {
        stopNetTimer();
        mAlbumMgr.stop();
    }

    public void delayOpenSocket() {
        // 这里要延时2S，等待界面显示后再检查网络，否则网络可能还没准备好，检查的结果是错误的
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                if (checkNetwork()) {
                    startUdpBroadcast();
                    // openSocket();
                    mIpInfoMgr.startIpInfoTimer();
                } else {
                    mMsgSender.sendShowWifiMsg();
                }
            }
        }, 2000);
    }

    private void startUdpBroadcast() {
        if (!mUdpSended) {
            mUdpSended = true;
        } else
            return;

        sendUdpBroadcast();
    }

    /**
     * 这个函数不能连续调用，必须是同步的
     */
    public void sendUdpBroadcast() {
        UdpSocket udpSocket = new UdpSocket();
        boolean flag = udpSocket.sendUdpPacket();
        /**
         * 在没有找到网关时，将不会打开6019端口，暂时先注释if(flag)，需要再打开
         */
        //if (flag)
            openSocket();
    }

    private void openSocket() {
        int serial = getmConfig().getSelfSerial();
        if (serial != 0) {
            getmClientSocket().startConnectGateway(ClientSocket.NOTIFI_GATEWAY_IP);
        }

        if (mServerSocket == null) {
            mServerSocket = new ServiceSocket();
        }
        if (!mServerSocket.ismIsRunning()) {
            mServerSocket.startServerSocket();
        }
    }

    public boolean checkNetwork() {
        ConnectivityManager cwjManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = cwjManager.getActiveNetworkInfo();
        if (activeNetInfo == null || !activeNetInfo.isConnected()) {
            activeNetInfo = null;
            cwjManager = null;
            return false;
        }

        if (activeNetInfo.getState() == NetworkInfo.State.CONNECTED) {
            activeNetInfo = null;
            cwjManager = null;
            return true;
        }

        activeNetInfo = null;
        cwjManager = null;
        return false;
    }

    public boolean checkWifiwork() {
        ConnectivityManager cwjManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = cwjManager.getActiveNetworkInfo();
        if (activeNetInfo == null || !activeNetInfo.isConnected()) {
            activeNetInfo = null;
            cwjManager = null;
            return false;
        }

        if ((activeNetInfo.getState() == NetworkInfo.State.CONNECTED)
                && (activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI)) {
            activeNetInfo = null;
            cwjManager = null;
            return true;
        }

        activeNetInfo = null;
        cwjManager = null;
        return false;
    }

    public void registerReceiver() {
        IntentFilter usbFilter = new IntentFilter();
        usbFilter.addAction(Intent.ACTION_MEDIA_MOUNTED); // 设备被挂接（mount）。
        usbFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED); // 设备被卸载（unmounted）。
        usbFilter.addDataScheme("file");
        registerReceiver(mHardwareReceiver, usbFilter);

        registerReceiver(mScreenOnReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
        registerReceiver(mPhoneReceiver, new IntentFilter("android.intent.action.PHONE_STATE"));
        registerReceiver(mHeadsetReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
        registerReceiver(mNotifiReceiver, new IntentFilter("com.notification.sendMsg"));
        IntentFilter netStateFilter = new IntentFilter();
        netStateFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(mNetworkStateReceiver, netStateFilter);
        registerReceiver(mCustomReceiver, new IntentFilter("cxxowl.sungeo.com"));
    }

    public void unRegisterReceiver() {
        unregisterReceiver(mHardwareReceiver);
        unregisterReceiver(mScreenOnReceiver);
        unregisterReceiver(mPhoneReceiver);
        unregisterReceiver(mHeadsetReceiver);
        unregisterReceiver(mNotifiReceiver);
        unregisterReceiver(mNetworkStateReceiver);
        unregisterReceiver(mCustomReceiver);
    }

    public int getNextNotNullAlbumId(int albumId) {
        int id = -1;

        id = albumId;
        int value = 0;
        if (albumId == AlbumInfoDb.SIXTH_ALBUM_ID) {
            id = AlbumInfoDb.FIRST_ALBUM_ID;
        } else {
            id++;
        }
        value = id;
        for (int i = id; i != albumId; i = value) {
            AlbumRecordInfo album = (AlbumRecordInfo) mAllAlbum.get(i - 1);
            if (album != null && album.getSongCount() != 0) {
                return i;
            }

            if (i == AlbumInfoDb.SIXTH_ALBUM_ID) {
                value = AlbumInfoDb.FIRST_ALBUM_ID;
            } else {
                value++;
            }
        }

        return albumId;
    }

    public int getPreNotNullAlbumId(int albumId) {
        int id = -1;

        id = albumId;
        int value = 0;
        if (albumId == AlbumInfoDb.FIRST_ALBUM_ID) {
            id = AlbumInfoDb.SIXTH_ALBUM_ID;
        } else {
            id--;
        }
        value = id;
        for (int i = id; i != albumId; i = value) {
            AlbumRecordInfo album = (AlbumRecordInfo) mAllAlbum.get(i - 1);
            if (album != null && album.getSongCount() != 0) {
                return i;
            }

            if (i == AlbumInfoDb.FIRST_ALBUM_ID) {
                value = AlbumInfoDb.SIXTH_ALBUM_ID;
            } else {
                value--;
            }
        }
        return albumId;
    }

    public String getAppVersionName() {

        String versionName = "";

        try {
            PackageManager pm = getPackageManager();

            PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);

            versionName = pi.versionName;

            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
        }

        return versionName;
    }

    public String getAppName() {
        String appName = "";

        try {
            PackageManager pm = getPackageManager();

            ApplicationInfo ai = pm.getApplicationInfo(getPackageName(), 0);

            appName = ai.loadLabel(pm).toString();

            if (appName == null || appName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
        }

        return appName;
    }

    public String getLocalIpAddress() {
        String ipv4;
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
                    .hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
                        .hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && InetAddressUtils.isIPv4Address(ipv4 = inetAddress.getHostAddress())) {
                        return ipv4;
                    }
                }
            }
        } catch (SocketException ex) {
            MainApplication.getInstance().getmMsgSender().sendStrMsg("获得本机IP失败");
        }
        return null;
    }

    public boolean isCallPhone() {
        return mCallPhone;
    }

    public void setReceiverSms(boolean isReceiverSms) {
        mReceiverSms = isReceiverSms;
    }

    public boolean isReceiverSms() {
        return mReceiverSms;
    }

    public void setmAlbumMgr(AlbumMgr mAlbumMgr) {
        this.mAlbumMgr = mAlbumMgr;
    }

    public AlbumMgr getmAlbumMgr() {
        return mAlbumMgr;
    }

    public void setmCurPlayAlbumId(int mCurPlayAlbumId) {
        this.mCurPlayAlbumId = mCurPlayAlbumId;
    }

    public int getmCurPlayAlbumId() {
        return mCurPlayAlbumId;
    }

    public void setmLastPlayAlbumId(int mLastPlayAlbumId) {
        this.mLastPlayAlbumId = mLastPlayAlbumId;
    }

    public int getmLastPlayAlbumId() {
        return mLastPlayAlbumId;
    }

    public void setmCurSondIndex(int mCurSondIndex) {
        this.mCurSongIndex = mCurSondIndex;
    }

    public int getmCurSondIndex() {
        return mCurSongIndex;
    }

    public void setmCurShowAlbumId(int mCurShowAlbumId) {
        this.mCurShowAlbumId = mCurShowAlbumId;
    }

    public int getmCurShowAlbumId() {
        return mCurShowAlbumId;
    }

    public void setmMediaMgr(MediaMgr mMediaMgr) {
        this.mMediaMgr = mMediaMgr;
    }

    public MediaMgr getmMediaMgr() {
        return mMediaMgr;
    }

    public void setmDownAlbum(List<AlbumRecordInfo> mDownAlbum) {
        this.mDownAlbum = mDownAlbum;
    }

    public List<AlbumRecordInfo> getmDownAlbum() {
        return mDownAlbum;
    }

    public void setmDownMgr(DownloadMgr mDownMgr) {
        this.mDownMgr = mDownMgr;
    }

    public DownloadMgr getmDownMgr() {
        return mDownMgr;
    }

    public void setmAllAlbum(List<AlbumRecordInfo> mAllAlbum) {
        this.mAllAlbum = mAllAlbum;
    }

    public List<AlbumRecordInfo> getmAllAlbum() {
        return mAllAlbum;
    }

    public void setmAllSongCount(int mAllSongCount) {
        this.mAllSongCount = mAllSongCount;
    }

    public int getmAllSongCount() {
        return mAllSongCount;
    }

    public void setmNextSongIndex(int mNextSongIndex) {
        this.mNextSongIndex = mNextSongIndex;
    }

    public int getmNextSongIndex() {
        return mNextSongIndex;
    }

    public void setmServerVersion(String mServerVersion) {
        this.mServerVersion = mServerVersion;
    }

    public String getmServerVersion() {
        return mServerVersion;
    }

    public void setmLastPlaySongName(String mLastPlaySongName) {
        if (mLastPlaySongName == null) {
            return;
        }
        int pos = mLastPlaySongName.lastIndexOf("/");
        int len = mLastPlaySongName.length();
        String name = "";
        if (pos != -1) {
            name = mLastPlaySongName.substring(pos + 1, len);
        } else {
            name = mLastPlaySongName;
        }

        this.mLastPlaySongName = name;
    }

    public boolean IsDeviceModel() {
        // TelephonyManager
        // phoneMgr=(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        // 定制的平板型号是txf0701，系统版本是2.2.1，版本号txf0701_20110805，如果是些型号的则用到GPIO的控制，否则不用GPIO

        String brand = Build.BRAND;
        String device = Build.DEVICE;
        String hardware = Build.HARDWARE;

        if (brand == null || device == null || hardware == null) {
            return false;
        }

        if (brand.equals("GeMei") && device.equals("G1") && hardware.equals("cc1900")) {
            return true;
        }

        return false;
        // phoneMgr.getLine1Number();//本机电话号码
        // Build.VERSION.SDK;//SDK版本号
        // Build.VERSION.RELEASE;//Firmware/OS 版本号
    }

    public void initLastPlaySongName() {
        mLastPlaySongName = null;
    }

    public String getmLastPlaySongName() {
        return mLastPlaySongName;
    }

    public void setmConfig(ConfigPreferences mConfig) {
        this.mConfig = mConfig;
    }

    public ConfigPreferences getmConfig() {
        return mConfig;
    }

    public void setmNotifiSucess(boolean mNotifiSucess) {
        this.mNotifiSucess = mNotifiSucess;
    }

    public boolean ismNotifiSucess() {
        return mNotifiSucess;
    }

    public void setmThreadSwitch(boolean mThreadSwitch) {
        this.mThreadSwitch = mThreadSwitch;
    }

    public boolean ismThreadSwitch() {
        return mThreadSwitch;
    }

    public void setmHostAlarmName(String mHostAlarmName) {
        this.mHostAlarmName = mHostAlarmName;
    }

    public String getmHostAlarmName() {
        return mHostAlarmName;
    }

    public void setmUdiskMountedPath(String mUdiskMountedPath) {
        this.mUdiskMountedPath = mUdiskMountedPath;
    }

    public String getmUdiskMountedPath() {
        return mUdiskMountedPath;
    }

    public void setmMsgSender(MsgSender mMsgSender) {
        this.mMsgSender = mMsgSender;
    }

    public MsgSender getmMsgSender() {
        return mMsgSender;
    }

    public void setmVersionMgr(VersionMgr mVersionMgr) {
        this.mVersionMgr = mVersionMgr;
    }

    public VersionMgr getmVersionMgr() {
        return mVersionMgr;
    }

    public void setmCurDownloadAlbumId(int mCurDownloadAlbumId) {
        this.mCurDownloadAlbumId = mCurDownloadAlbumId;
    }

    public int getmCurDownloadAlbumId() {
        return mCurDownloadAlbumId;
    }

    public void setmServerSocket(ServiceSocket mServerSocket) {
        this.mServerSocket = mServerSocket;
    }

    public ServiceSocket getmServerSocket() {
        return mServerSocket;
    }

    public void setmClientSocket(ClientSocket mClientSocket) {
        this.mClientSocket = mClientSocket;
    }

    public ClientSocket getmClientSocket() {
        return mClientSocket;
    }

    public void setmKeyguradLock(KeyguardLock mKeyguradLock) {
        this.mKeyguradLock = mKeyguradLock;
    }

    public KeyguardLock getmKeyguradLock() {
        return mKeyguradLock;
    }

    public void setmGpioIsOpen(boolean mGpioIsOpen) {
        this.mGpioIsOpen = mGpioIsOpen;
    }

    public boolean ismGpioIsOpen() {
        return mGpioIsOpen;
    }

    public void setmIflytekTts(boolean mIflytekTts) {
        this.mIflytekTts = mIflytekTts;
    }

    public boolean ismIflytekTts() {
        return mIflytekTts;
    }
}
