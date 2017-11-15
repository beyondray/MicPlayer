package com.beyondray.micplayer.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.beyondray.micplayer.Player.Music;
import com.beyondray.micplayer.Player.PlayList;

/**
 * Created by beyondray on 2017/11/14.
 */

public class MusicDB {
    public static final String T_PL_MIC = MusicDBHelper.T_PL_MIC;
    public static final String T_RD_MIC = MusicDBHelper.T_RD_MIC;
    private MusicDBHelper mDbHelper;
    private Table mTable;

    public MusicDB(Context context)
    {
        mDbHelper = new MusicDBHelper(context, "Music.db", null, 1);
        mTable = new Table(T_PL_MIC);
    }

    public Table get(String table){
        switch (table)
        {
            case T_PL_MIC:
                mTable.setName(T_PL_MIC);
                break;
            case T_RD_MIC:
                mTable.setName(T_RD_MIC);
                break;
        }
        return mTable;
    }

    public class Table
    {
        String TABLE_NAME;

        public Table(String name)
        {
            setName(name);
        }

        public void setName(String name)
        {
            TABLE_NAME = name;
        }

        public boolean contains(Music m)
        {
            return mDbHelper.contains(TABLE_NAME, m);
        }

        public void insert(Music m, int pos)
        {
            mDbHelper.insert(TABLE_NAME, m, pos);
        }

        public void update(Music m, int pos)
        {
            mDbHelper.update(TABLE_NAME, m, pos);
        }

        public void delete(Music m)
        {
            mDbHelper.delete(TABLE_NAME, m);
        }

        public PlayList getData()
        {
            return mDbHelper.getData(TABLE_NAME);
        }

        public PlayList getDataByTimeSeq()
        {
            return mDbHelper.getDataByTimeSeq(TABLE_NAME);
        }
    }

}
