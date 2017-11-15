package com.beyondray.micplayer.Service;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.beyondray.micplayer.Adapter.MusicAdapter;
import com.beyondray.micplayer.Player.Music;
import com.beyondray.micplayer.Player.PlayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by beyondray on 2017/11/12.
 */

public class MusicSearchTask extends AsyncTask<String, Void, PlayList> {
    public static String TAG = "MusicSearchTask";
    public static String URL_PREFIX = "http://music.163.com/song/media/outer/url?id=";

    private Context mContext;
    private MusicAdapter mAdapter;

    public MusicSearchTask(Context context, MusicAdapter adapter){
        mContext = context;
        mAdapter = adapter;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected PlayList doInBackground(String... params) {
        String url = params[0];
        PlayList playList = new PlayList();
        try {
            HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();
            conn.setConnectTimeout(5000);

            //缓存处理提高效率
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            //解析JSON网络数据
            Log.d(TAG, "jsonStr = " + sb.toString());
            playList = parseJSONObj(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return playList;
    }

    @Override
    protected void onPostExecute(PlayList playList) {
        super.onPostExecute(playList);
        if(playList.queue.size() == 0){
            Toast.makeText(mContext, "Please check the internet!!", Toast.LENGTH_SHORT);
        }
        if(mAdapter != null){
            mAdapter.setPlayList(playList);
            mAdapter.notifyDataSetChanged();
        }
    }

    private PlayList parseJSONObj(String json)
    {
        PlayList playList = new PlayList();
        try {
            JSONObject jsonObj = new JSONObject(json);
            JSONObject result = jsonObj.getJSONObject("result");
            JSONArray songs = result.getJSONArray("songs");

            for (int i = 0; i < songs.length(); i++) {
                JSONObject song = songs.getJSONObject(i);
                String id = song.getString("id");
                String title = song.getString("name");
                String author = song.getJSONArray("artists").getJSONObject(0).getString("name");
                String audioUrl = URL_PREFIX + id + ".mp3";
                String albumPicUrl = song.getJSONObject("album").getString("picUrl");
                Log.d(TAG, "downloadUrl = " + audioUrl);

                //save music data
                Music s = new Music(id, title, author, audioUrl, albumPicUrl);
                playList.add(s);
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return playList;
    }
}
