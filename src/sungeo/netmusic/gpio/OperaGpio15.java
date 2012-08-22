package sungeo.netmusic.gpio;


public class OperaGpio15{
	public native int  setOn();
	public native int  setoff();

	static {
		System.loadLibrary("gpioo15");//load system/lib/libled_servers.so
	}

	public boolean openGpio15() {
		int res = setOn();
		if(0 == res)
		{
			return true;
		}
		return false;			 
	}
	public boolean closeGpio15() {
		int res = setoff();
		if(0 == res)
		{
			return true;
		}
		return false;
	}
}
