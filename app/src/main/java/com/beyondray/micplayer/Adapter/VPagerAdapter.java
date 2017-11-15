package com.beyondray.micplayer.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.beyondray.micplayer.UI.Fragment.MusicRecordFrag;
import com.beyondray.micplayer.UI.Fragment.MusicSearchFrag;
import com.beyondray.micplayer.UI.Fragment.PlayListFrag;

/**
 * Created by beyondray on 2017/11/11.
 */

public class VPagerAdapter extends FragmentPagerAdapter {
    public static String TAG = "VPagerAdapter";
    public static String[] titles = { "网络音乐", "播放列表", "最近播放"};
    private int mFragNums = 3;

    private MusicSearchFrag mMicSearchFrag;
    private PlayListFrag mPlayListFrag;
    private MusicRecordFrag mMicRecordFrag;

    public VPagerAdapter(FragmentManager fm) {
        super(fm);
        mMicSearchFrag = new MusicSearchFrag();
        mPlayListFrag = new PlayListFrag();
        mMicRecordFrag = new MusicRecordFrag();
    }

    @Override
    public Fragment getItem(int position) {
        Log.v(TAG, Integer.toString(position));
        Fragment frag = new Fragment();
        switch (position)
        {
            case 0:
                frag = mMicSearchFrag;
                break;
            case 1:
                frag = mPlayListFrag;
                break;
            case 2:
                frag = mMicRecordFrag;
                break;
        }
        return  frag;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return mFragNums;
    }

    public void updatePlayListRv(){
        mPlayListFrag.updatePlayList();
    }
    public void updateMicRecordRv() { mMicRecordFrag.updatePlayList();}
}


