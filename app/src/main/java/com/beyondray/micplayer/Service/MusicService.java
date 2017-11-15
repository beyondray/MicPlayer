package com.beyondray.micplayer.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.beyondray.micplayer.Player.AudioPlayer;
import com.beyondray.micplayer.Player.PlayEvent;
import com.beyondray.micplayer.Util.DownloadUtil;
import com.beyondray.micplayer.Util.SyncMgr;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by beyondray on 2017/11/10.
 */

public class MusicService extends Service{

    private static ExecutorService mPool;
    private int miDLThreads = 3;
    private Context mContext;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        mPool = Executors.newFixedThreadPool(miDLThreads);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        mPool.shutdown();
        super.onDestroy();
    }

    public  static class DownloadThread implements Runnable{
        DownloadUtil mUtil;
        public DownloadThread(DownloadUtil util){
            mUtil = util;
        }
        @Override
        public void run() {
            mUtil.download();
        }
    }

    public static void downloadMusic(final DownloadUtil util) {
        mPool.execute(new Thread(new DownloadThread(util)));
    }

    @Subscribe
    public void onMsgEvent(PlayEvent e) {
        if(e.getMusic() != null)
        {
            if(e.getAction() == PlayEvent.Action.DOWNLOAD)
            {
              /*  DownloadUtil dlUtil = new DownloadUtil(e.getMusic(), "/a.mp3", miDLThreads, null, MainActivity.mdb);
                downloadMusic(dlUtil);*/
            }
            else{
                SyncMgr.updatePlayMic(e.getMusic());
            }
        }
        else
        {
            switch (e.getAction())
            {
                case PLAY:
                    AudioPlayer.singleton.play();
                    break;
                case PAUSE:
                    AudioPlayer.singleton.pause();
                    break;
                case RESUME:
                    AudioPlayer.singleton.resumeEx();
                    break;
                case NEXT:
                    AudioPlayer.singleton.next();
                    break;
                case PREV:
                    AudioPlayer.singleton.prev();
                    break;
            }
        }
        SyncMgr.sendSyncUIMsg(AudioPlayer.singleton.getPlayingMusic());
        SyncMgr.updateMicRecord();
    }
}
