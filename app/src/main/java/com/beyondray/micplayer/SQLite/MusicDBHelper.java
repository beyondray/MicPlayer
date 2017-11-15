package com.beyondray.micplayer.SQLite;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.Settings;
import android.util.Log;

import com.beyondray.micplayer.Player.Music;
import com.beyondray.micplayer.Player.PlayList;

/**
 * Created by beyondray on 2017/11/14.
 */

public class MusicDBHelper extends SQLiteOpenHelper {
    //playList music
    public static final String T_PL_MIC = "pl_music";
    //music record
    public static final String T_RD_MIC = "rd_music";
    private SQLiteDatabase mDb;

    public MusicDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mDb = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        mDb = db;
        create(T_PL_MIC);
        create(T_RD_MIC);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        drop(T_PL_MIC);
        drop(T_RD_MIC);
        onCreate(db);
    }

    public void create(String table)
    {
        String CREATE_TABLE = "create table "+ table +"("
                + "id integer primary key,"
                + "title text,"
                + "author text,"
                + "url text,"
                + "albumPicUrl text,"
                + "pos integer, "
                + "time text) ";
        mDb.execSQL(CREATE_TABLE);
    }

    public void drop(String table)
    {
        String DROP_TABLE = "drop table if exists "+ table;
        mDb.execSQL(DROP_TABLE);
    }

    public void insert(String table, Music m, int pos)
    {
        String INSERT_TABLE = "insert into " + table
                +" (id, title, author, url, albumPicUrl, pos, time) values(?, ?, ?, ?, ?, ?, ?)";
        long time = System.currentTimeMillis();
        String[] data = new String[7];
        System.arraycopy(m.getAllData(), 0, data, 0, 5);
        data[5] = Integer.toString(pos);
        data[6] = Long.toString(time);
        mDb.execSQL(INSERT_TABLE, data);
    }

    public void update(String table, Music m, int pos)
    {
        if(contains(table, m))
            delete(table, m);
        insert(table, m, pos);
    }

    public boolean contains(String table, Music m)
    {
        String id = m.get(Music.Attr.ID);
        Cursor cursor = mDb.query(table, null, "id = ?", new String[]{id}, null, null, null);
        boolean bExist = cursor.moveToFirst();
        cursor.close();
        return bExist;
    }

    public void delete(String table, Music m)
    {
        String DELETE_MUSIC_ITME = "delete from " + table + " where id = ?";
        String id = m.get(Music.Attr.ID);
        mDb.execSQL(DELETE_MUSIC_ITME, new String[]{id});
    }

    public PlayList getData(Cursor cursor)
    {
        PlayList playList = new PlayList();
        if(cursor.moveToFirst())
        {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String author = cursor.getString(cursor.getColumnIndex("author"));
                String url = cursor.getString(cursor.getColumnIndex("url"));
                String albumPicUrl = cursor.getString(cursor.getColumnIndex("albumPicUrl"));
                Music s = new Music(Integer.toString(id), title, author, url, albumPicUrl);
                String time = cursor.getString(cursor.getColumnIndex("time"));
                long ltime = Long.parseLong(time);
                //Log.v("length", time);
                playList.add(s);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return playList;
    }

    public PlayList getData(String table)
    {
        String QUERY_MUSIC = "select * from "+ table + " order by pos";
        Cursor cursor = mDb.rawQuery(QUERY_MUSIC, null);
        return getData(cursor);
    }

    public PlayList getDataByTimeSeq(String table)
    {
        String QUERY_MUSIC = "select * from "+ table + " order by time desc";
        Cursor cursor = mDb.rawQuery(QUERY_MUSIC, null);
        return getData(cursor);
    }
}
