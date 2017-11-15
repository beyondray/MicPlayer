package com.beyondray.micplayer.Util;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.beyondray.micplayer.Player.AudioPlayer;
import com.beyondray.micplayer.Player.Music;
import com.beyondray.micplayer.SQLite.MusicDB;
import com.beyondray.micplayer.UI.DetailActivity;
import com.beyondray.micplayer.UI.MainActivity;

/**
 * Created by beyondray on 2017/11/15.
 */

public class SyncMgr {
    public static Handler mHandler = null;
    public final static int UI_SYNC = 1;

    public static void sendSyncUIMsg(Music m)
    {
        Message msg = new Message();
        msg.what = UI_SYNC;
        Bundle b = new Bundle();
        b.putSerializable("Music", m);
        msg.setData(b);
        if(mHandler != null)mHandler.sendMessage(msg);
    }

    public static void updatePlayMic(Music m)
    {
        AudioPlayer.singleton.play(m);
        int playIdx = AudioPlayer.singleton.getPlayList().contains(m);
        if(playIdx != -1) AudioPlayer.singleton.setPlayIdx(playIdx);
    }

    public static void updateLatestMiniMic()
    {
        boolean hasRecord = AudioPlayer.singleton.getRecordList().queue.size() > 0;
        if(hasRecord){
            Music latestMic = AudioPlayer.singleton.getRecordList().queue.get(0);
            updatePlayMic(latestMic);
            SyncMgr.sendSyncUIMsg(latestMic);
        }
    }

    public static void updateMicRecord()
    {
        MusicDB.Table t = MainActivity.db.get(MusicDB.T_RD_MIC);
        Music m = AudioPlayer.singleton.getPlayingMusic();
        if(t.contains(m)){
            t.delete(m);
            AudioPlayer.singleton.getRecordList().remvoe(m);
        }
        t.insert(m, 0);
        AudioPlayer.singleton.getRecordList().queue.add(0, m);
        MainActivity.mVPagerAdapter.updateMicRecordRv();
    }

    public static void updatePlayList(View view)
    {
        Music m = AudioPlayer.singleton.getPlayingMusic();
        if(m == null){
            Snackbar.make(view, "请选择要添加的歌曲", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
            return;
        }
        MusicDB.Table t = MainActivity.db.get(MusicDB.T_PL_MIC);
        if(!t.contains(m)) {
            t.insert(m, AudioPlayer.singleton.getPlayList().size());
            AudioPlayer.singleton.getPlayList().add(m);
            MainActivity.mVPagerAdapter.updatePlayListRv();
            Snackbar.make(view, "已添加到播放列表", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }
        else{
            Snackbar.make(view, "播放列表中已存在该歌曲", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        }
    }
}
