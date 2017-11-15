package com.beyondray.micplayer.Adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.beyondray.micplayer.Player.AudioPlayer;
import com.beyondray.micplayer.Player.PlayEvent;
import com.beyondray.micplayer.Player.PlayList;
import com.beyondray.micplayer.Player.Music;
import com.beyondray.micplayer.R;
import com.beyondray.micplayer.Service.HttpImgDlTask;
import com.beyondray.micplayer.Service.MusicService;
import com.beyondray.micplayer.UI.MiniMusicView;
import com.beyondray.micplayer.Util.DownloadUtil;

import org.greenrobot.eventbus.EventBus;

import butterknife.OnClick;

/**
 * Created by beyondray on 2017/11/11.
 */

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {
    private String TAG = "MusicAdapter";
    private PlayList playList = new PlayList();

    static class ViewHolder extends RecyclerView.ViewHolder{
        View view;
        ImageView icon;
        TextView title;
        TextView author;
        ImageView download;

        public ViewHolder(View v){
            super(v);
            view = v;
            icon = (ImageView)view.findViewById(R.id.music_icon);
            title = (TextView)view.findViewById(R.id.music_title);
            author = (TextView)view.findViewById(R.id.music_author);
            download = (ImageView)view.findViewById(R.id.music_download);
        }
    }

    public MusicAdapter(){}
    public MusicAdapter(PlayList pl)
    {
        playList = pl;
    }


    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.music_item, parent, false);
        final ViewHolder vh = new ViewHolder(view);

        vh.view.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int pos = vh.getAdapterPosition();
                Log.v(TAG, "click"+Integer.toString(pos));
                Music m = playList.queue.get(pos);
                EventBus.getDefault().post(new PlayEvent(m));

            }
        });
        vh.download.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Download Tip");
                builder.setMessage("It's not supported for the moment!");
                //builder.setMessage("Are you sure download this music?");
                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setPositiveButton("sure", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int pos = vh.getAdapterPosition();
                        Music m = playList.queue.get(pos);
                        PlayEvent e = new PlayEvent(m);
                        e.setAction(PlayEvent.Action.DOWNLOAD);
                        EventBus.getDefault().post(e);
                    }
                });
                builder.show();
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(playList.queue.size() > 0){
            Music m = playList.queue.get(position);
            holder.title.setText(m.get(Music.Attr.TITLE));
            holder.author.setText(m.get(Music.Attr.AUTHOR));
        }
    }

    @Override
    public int getItemCount() {
        return playList.queue.size();
    }

    public PlayList getPlayList(){ return playList; }
    public void setPlayList(PlayList pl) { playList = pl; }
}
