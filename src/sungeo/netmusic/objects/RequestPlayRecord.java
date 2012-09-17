package sungeo.netmusic.objects;

import java.io.UnsupportedEncodingException;


public class RequestPlayRecord implements PackageRecord{
    private String mUrl;
    @Override
    public byte[] getRecordBytes() {
       
        return null;
    }

    @Override
    public boolean setRecordBytes(byte[] data) {
        if (data == null)
            return false;
        if (data.length <= 0)
            return false;

        String urlStr = null;

        try {
            urlStr = new String(data, "GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (urlStr == null || urlStr.length() < 1)
            return false;
        
        mUrl = urlStr;

        return true;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public String getmUrl() {
        return mUrl;
    }

}
