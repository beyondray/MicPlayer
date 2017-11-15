package com.beyondray.micplayer.UI.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beyondray.micplayer.Adapter.MusicAdapter;
import com.beyondray.micplayer.Player.AudioPlayer;
import com.beyondray.micplayer.R;
import com.beyondray.micplayer.SQLite.MusicDB;
import com.beyondray.micplayer.UI.MainActivity;

import java.util.Collections;

/**
 * Created by beyondray on 2017/11/12.
 */

public class PlayListFrag extends Fragment {

    MusicAdapter mMusicAdapter;
    RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.music_playlist, container, false);
        mRecyclerView = (RecyclerView)v.findViewById(R.id.plmusic_rv);
        mMusicAdapter = new MusicAdapter(AudioPlayer.singleton.getPlayList());
        mRecyclerView.setAdapter(mMusicAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP|ItemTouchHelper.DOWN,ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int srcPos = viewHolder.getAdapterPosition();
                int destPos = target.getAdapterPosition();
                mMusicAdapter.notifyItemMoved(srcPos, destPos);
                Collections.swap(mMusicAdapter.getPlayList().queue,srcPos, destPos);
                MusicDB.Table t = MainActivity.db.get(MusicDB.T_PL_MIC);
                t.update(mMusicAdapter.getPlayList().queue.get(srcPos), srcPos);
                t.update(mMusicAdapter.getPlayList().queue.get(destPos), destPos);
                updatePlayListIdx(srcPos, destPos);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                MusicDB.Table t = MainActivity.db.get(MusicDB.T_PL_MIC);
                int pos = viewHolder.getAdapterPosition();
                t.delete(mMusicAdapter.getPlayList().queue.get(pos));
                mMusicAdapter.getPlayList().queue.remove(pos);
                mMusicAdapter.getPlayList().fitPlayIdx();
                mMusicAdapter.notifyDataSetChanged();
            }
        });
        //将recycleView和ItemTouchHelper绑定
        touchHelper.attachToRecyclerView(mRecyclerView);
        return v;
    }

    private void updatePlayListIdx(int srcPos, int destPos){
        int idx = AudioPlayer.singleton.getPlayIdx();
        if(idx == srcPos)
            AudioPlayer.singleton.setPlayIdx(destPos);
        else if(idx == destPos)
            AudioPlayer.singleton.setPlayIdx(destPos-1);
        else if(idx > srcPos && idx < destPos)
            AudioPlayer.singleton.setPlayIdx(idx-1);
        else if(idx > destPos && idx < srcPos)
            AudioPlayer.singleton.setPlayIdx(idx+1);
    }
    public void updatePlayList(){
        mMusicAdapter.notifyDataSetChanged();
    }
}
