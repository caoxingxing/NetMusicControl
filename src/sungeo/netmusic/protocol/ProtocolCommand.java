/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sungeo.netmusic.protocol;

/**
 *
 * @author caoxingxing
 */
public class ProtocolCommand {

	/**
	 *pc软件通讯协议关键字
	 */
	public final static byte ERROR_TYPE_SUCCESS=0;//执行成功   
	public final static byte MSG_TYPE_WIRELESS_PROTOCOL = 0x20;//无线命令
	public final static byte MSG_TYPE_QUERY_ADDRESS = 0x57;//查询地址码
	public final static byte MSG_TYPE_ADD_MUSICNAME_RECORD = 0x36;//添加歌曲名信息
	public final static byte MSG_TYPE_NOTIFY_GATEWAY = (byte) 0xaa;//通报网关IP和序列号
	public final static byte MSG_TYPE_REQUEST_PLAY = (byte) 0xac; //在线播放
	public final static byte MSG_TYPE_HOST_ALARM = 0x78;//得到报警探头信息
	public final static byte MSG_TYPE_UPDATE_MUSIC = (byte)0xa9;//歌曲更新同步
	public final static byte MSG_TYPE_QUERY_MUSIC_VERSION = (byte)0xab;//查询歌曲版本
	public final static byte MSG_TYPE_QUERY_MUSIC =	(byte)0xB1;   	//查询歌曲，由网关发出
	public final static byte MSG_TYPE_UDP_BROADCAST = (byte) 0x12;	//发送UDP广播，获取网关IP地址

	/**
	 * 曦光智能家居系统通讯协议命令字
	 */

	//操作类型

	public final static byte CMD_POWER_ON=0x01;//开
	public final static byte CMD_POWER_OFF=0x02;//关
	public final static byte CMD_PLUS=(byte)0x03;
	public final static byte CMD_MINUS=(byte)0x04;
	public final static byte CMD_PLAY_NEXT=(byte)0x05;
	public final static byte CMD_PLAY_PREV=(byte)0x06;
	//public final static byte CMD_OPERATE_CERTAIN_FILE=(byte)0x07;
	public final static byte CMD_STATE_SYNC=0x09;//同步广播
	public final static byte CMD_SET_USERCODE_ADDRESS = 0x0a;//设置用户码地址码
	public final static byte CMD_READ_SOFT_VERSION=0x12;//读取版本命令
	public final static byte CMD_READ_STATE = 0x0D;//读取指定设备状态
	public final static byte CMD_VOLUME_ADJUST = 0x4e;//背景音乐音量调节
	public final static byte CMD_REPLY_BASIC = (byte)0x80;//回复的命令基数
	public final static byte CMD_REPLY_POWER_ON=(byte)(CMD_POWER_ON+CMD_REPLY_BASIC);//0x81;
	public final static byte CMD_REPLY_POWER_OFF=(byte)(CMD_POWER_OFF+CMD_REPLY_BASIC);//0x82;
	public final static byte CMD_REPLY_PLUS = (byte)(CMD_PLUS + CMD_REPLY_BASIC);
	public final static byte CMD_REPLY_SET_USERCODE_ADDRESS = (byte)(CMD_SET_USERCODE_ADDRESS + CMD_REPLY_BASIC);
	public final static byte CMD_REPLY_READ_VERSION = (byte)(CMD_READ_SOFT_VERSION + CMD_REPLY_BASIC);
	public final static byte CMD_REPLY_MINUS = (byte)(CMD_MINUS + CMD_REPLY_BASIC);
	public final static byte CMD_REPLY_READ_STATE = (byte)(CMD_READ_STATE + CMD_REPLY_BASIC);
	public final static byte CMD_REPLY_PLAY_NEXT = (byte)(CMD_PLAY_NEXT + CMD_REPLY_BASIC);
	public final static byte CMD_REPLY_PLAY_PREV = (byte)(CMD_PLAY_PREV + CMD_REPLY_BASIC);
	public final static byte CMD_REPLY_VOLUME_ADJUST=(byte)(CMD_VOLUME_ADJUST + CMD_REPLY_BASIC);

	/**
	 * 曦光智能家居系统通讯协议：设备地址定义
	 */
	public final static char ADDR_CTRL_CENTER_MIN=0;//家居管理中心地址
	public final static char ADDR_MP3_MIN=0X11d0;//互联网式背景音乐
	public final static char ADDR_MP3_MAX=0X11ef;
	public final static char ADDR_TRANS_CENTER=0X1000;//中心转发器
	public final static char ADDR_BROADCAST = 0xfffe;//广播地址
}
