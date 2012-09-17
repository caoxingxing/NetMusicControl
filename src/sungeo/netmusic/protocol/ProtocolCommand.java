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
	 *pc���ͨѶЭ��ؼ���
	 */
	public final static byte ERROR_TYPE_SUCCESS=0;//ִ�гɹ�   
	public final static byte MSG_TYPE_WIRELESS_PROTOCOL = 0x20;//��������
	public final static byte MSG_TYPE_QUERY_ADDRESS = 0x57;//��ѯ��ַ��
	public final static byte MSG_TYPE_ADD_MUSICNAME_RECORD = 0x36;//��Ӹ�������Ϣ
	public final static byte MSG_TYPE_NOTIFY_GATEWAY = (byte) 0xaa;//ͨ������IP�����к�
	public final static byte MSG_TYPE_REQUEST_PLAY = (byte) 0xac; //���߲���
	public final static byte MSG_TYPE_HOST_ALARM = 0x78;//�õ�����̽ͷ��Ϣ
	public final static byte MSG_TYPE_UPDATE_MUSIC = (byte)0xa9;//��������ͬ��
	public final static byte MSG_TYPE_QUERY_MUSIC_VERSION = (byte)0xab;//��ѯ�����汾
	public final static byte MSG_TYPE_QUERY_MUSIC =	(byte)0xB1;   	//��ѯ�����������ط���
	public final static byte MSG_TYPE_UDP_BROADCAST = (byte) 0x12;	//����UDP�㲥����ȡ����IP��ַ

	/**
	 * �ع����ܼҾ�ϵͳͨѶЭ��������
	 */

	//��������

	public final static byte CMD_POWER_ON=0x01;//��
	public final static byte CMD_POWER_OFF=0x02;//��
	public final static byte CMD_PLUS=(byte)0x03;
	public final static byte CMD_MINUS=(byte)0x04;
	public final static byte CMD_PLAY_NEXT=(byte)0x05;
	public final static byte CMD_PLAY_PREV=(byte)0x06;
	//public final static byte CMD_OPERATE_CERTAIN_FILE=(byte)0x07;
	public final static byte CMD_STATE_SYNC=0x09;//ͬ���㲥
	public final static byte CMD_SET_USERCODE_ADDRESS = 0x0a;//�����û����ַ��
	public final static byte CMD_READ_SOFT_VERSION=0x12;//��ȡ�汾����
	public final static byte CMD_READ_STATE = 0x0D;//��ȡָ���豸״̬
	public final static byte CMD_VOLUME_ADJUST = 0x4e;//����������������
	public final static byte CMD_REPLY_BASIC = (byte)0x80;//�ظ����������
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
	 * �ع����ܼҾ�ϵͳͨѶЭ�飺�豸��ַ����
	 */
	public final static char ADDR_CTRL_CENTER_MIN=0;//�Ҿӹ������ĵ�ַ
	public final static char ADDR_MP3_MIN=0X11d0;//������ʽ��������
	public final static char ADDR_MP3_MAX=0X11ef;
	public final static char ADDR_TRANS_CENTER=0X1000;//����ת����
	public final static char ADDR_BROADCAST = 0xfffe;//�㲥��ַ
}
