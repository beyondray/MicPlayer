package com.beyondray.micplayer.UI.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beyondray.micplayer.Adapter.MusicAdapter;
import com.beyondray.micplayer.Player.AudioPlayer;
import com.beyondray.micplayer.Player.Music;
import com.beyondray.micplayer.R;
import com.beyondray.micplayer.SQLite.MusicDB;
import com.beyondray.micplayer.UI.MainActivity;
import com.beyondray.micplayer.Util.SyncMgr;

/**
 * Created by beyondray on 2017/11/14.
 */

public class MusicRecordFrag extends Fragment {
    MusicAdapter mMusicAdapter;
    RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.music_record, container, false);
        mRecyclerView = (RecyclerView)v.findViewById(R.id.rdmusic_rv);
        mMusicAdapter = new MusicAdapter(AudioPlayer.singleton.getRecordList());
        mRecyclerView.setAdapter(mMusicAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        return v;
    }

    public void updatePlayList(){
        mMusicAdapter.notifyDataSetChanged();
    }

}
