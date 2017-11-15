package com.beyondray.micplayer.UI.Fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.Toast;

import com.beyondray.micplayer.Adapter.MusicAdapter;
import com.beyondray.micplayer.Player.PlayList;
import com.beyondray.micplayer.R;
import com.beyondray.micplayer.Service.MusicSearchTask;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by beyondray on 2017/11/12.
 */

public class MusicSearchFrag extends Fragment {
    public static final String JSON_URL_PREFIX = "http://s.music.163.com/search/get/?type=1&s='";
    public static final String JSON_URL_SUFFIX = "'&limit=20&offset=0";

    MusicAdapter mMusicAdapter;
    MusicSearchTask mMicSearchTask;
    SearchView mSearchView;
    RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.music_netseach, container, false);
        mSearchView = (SearchView)v.findViewById(R.id.netmusic_sv);
        mRecyclerView = (RecyclerView)v.findViewById(R.id.netmusic_rv);
        mMusicAdapter = new MusicAdapter();
        mRecyclerView.setAdapter(mMusicAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                onQueryTextChange(query);
                //Toast.makeText(getContext(), "Query: " + newText, Toast.LENGTH_SHORT);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!newText.isEmpty()){
                    String jsonUrl = getRealUrl(newText);
                    if(mMicSearchTask != null){
                        mMicSearchTask.cancel(true);
                    }
                    mMicSearchTask = new MusicSearchTask(getContext(), mMusicAdapter);
                    mMicSearchTask.execute(jsonUrl);
                }
                else {
                    mMusicAdapter.setPlayList(new PlayList());
                    mMusicAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });

        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mMusicAdapter.setPlayList(new PlayList());
                mMusicAdapter.notifyDataSetChanged();
                return false;
            }
        });
        return v;
    }

    public static String getRealUrl(String query) {
        String key = null;
        try {
            key = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return JSON_URL_PREFIX + key + JSON_URL_SUFFIX;
    }

}
