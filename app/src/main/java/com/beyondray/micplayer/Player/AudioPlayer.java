package com.beyondray.micplayer.Player;

import android.media.MediaPlayer;

import com.beyondray.micplayer.Util.SyncMgr;

import java.io.IOException;

/**
 * Created by beyondray on 2017/11/10.
 */

public class AudioPlayer implements MediaPlayer.OnCompletionListener{

    public static AudioPlayer singleton = new AudioPlayer();
    MMPlayer mPlayer;
    PlayList playList;
    PlayList recordList;
    Music mPlayMic;

    private AudioPlayer()
    {
        mPlayer = new MMPlayer();
        mPlayer.setOnCompletionListener(this);
    }
    public AudioPlayer getSingleton() { return singleton; }
    public PlayList getPlayList() { return playList; }
    public void setPlayList(PlayList pl){ playList = pl;}
    public int getPlayIdx() { return playList.getPlayIdx();}
    public void setPlayIdx(int idx) { playList.setPlayIdx(idx);}
    public PlayList getRecordList() { return recordList; }
    public void setRecordList(PlayList pl){recordList = pl;}

    public boolean isPlaying() { return mPlayer.isPlaying(); }
    public PlayList.PlayMode getPlayMode() { return playList.playMode;}
    public void setPlayMode(PlayList.PlayMode m){ playList.playMode = m;}

    public void pause() {
        mPlayer.pause();
    }
    public void resume() {
        mPlayer.start();
    }
    public void stop() {
        mPlayer.stop();
    }

    public void play() {
        playEx(playList.getCurMusic());
    }
    public void prev() {
        int playIdx = playList.contains(mPlayMic);
        if(playIdx == -1)playIdx = 0;
        playList.setPlayIdx(playIdx);
        playEx(playList.getPrevMusic());
    }
    public void next() {
        int playIdx = playList.contains(mPlayMic);
        playList.setPlayIdx(playIdx);
        playEx(playList.getNextMusic());
    }

    public int getCurPos() { return mPlayer.getCurrentPosition(); }
    public int getDuration() { return mPlayer.getDuration(); }
    public Music getPlayingMusic() { return mPlayMic;}

    public void play(Music m)
    {
        if(m == null)return;
        try {
            mPlayMic = m;
            mPlayer.reset();
            mPlayer.setDataSource(m.get(Music.Attr.URL));
            mPlayer.prepareAsync();
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mPlayer.start();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void playEx(Music m)
    {
        if(m != null)
            play(m);
        else
            play(mPlayMic);
    }

    public void resumeEx() {
        if(mPlayer.getState() == MMPlayer.Status.IDLE)
            play();
        else
            resume();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(playList.size() > 0)
            if(singleton.getPlayMode() == PlayList.PlayMode.LOOP)
                play();
            else
                next();
        else{
            play(mPlayMic);
        }
        SyncMgr.sendSyncUIMsg(getPlayingMusic());
    }
}
