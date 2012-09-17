package sungeo.netmusic.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import sungeo.netmusic.data.MainApplication;

public class CustomBroadcastReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        String key = intent.getStringExtra("netmusic_ctrl_type");
        if (key == null) {
            return;
        }
        if (key.equals("netmusic_play")) {
            int albumId = intent.getIntExtra("netmusic_album_id", -1);
            int songId = intent.getIntExtra("netmusic_song_id", -1);
            if (albumId == -1 || songId == -1) {
                MainApplication.getInstance().getmMediaMgr().start();
            } else {
                MainApplication.getInstance().getmMediaMgr().remotePlay(albumId, songId);
            }
            MainApplication.getInstance().getmMediaMgr().broadcastState();
        } else if (key.equals("netmusic_pause")) {
            MainApplication.getInstance().getmMediaMgr().pause(true);
            MainApplication.getInstance().getmMediaMgr().broadcastState();
        } else if (key.equals("netmusic_pre")) {
            MainApplication.getInstance().getmMediaMgr().remotePlayPre();
            MainApplication.getInstance().getmMediaMgr().broadcastState();
        } else if (key.equals("netmusic_next")) {
            MainApplication.getInstance().getmMediaMgr().remotePlayNext();
            MainApplication.getInstance().getmMediaMgr().broadcastState();
        } else if (key.equals("netmusic_silent")) {
            int flag = intent.getIntExtra("netmusic_is_silent", 0);
            boolean result = false;
            if (flag == 1) {
                result = true;
            }
            MainApplication.getInstance().getmMediaMgr().setMute(result);
            MainApplication.getInstance().getmMediaMgr().broadcastState();
        } else if (key.equals("netmusic_add")) {
            MainApplication.getInstance().getmMediaMgr().volumeAdd();
            MainApplication.getInstance().getmMediaMgr().broadcastState();
        } else if (key.equals("netmusic_dec")) {
            MainApplication.getInstance().getmMediaMgr().volumeDec();
            MainApplication.getInstance().getmMediaMgr().broadcastState();
        }
    }
}
