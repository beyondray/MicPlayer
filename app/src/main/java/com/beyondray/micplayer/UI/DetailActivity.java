package com.beyondray.micplayer.UI;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beyondray.micplayer.Player.Music;
import com.beyondray.micplayer.Player.PlayEvent;
import com.beyondray.micplayer.R;
import com.beyondray.micplayer.Service.HttpImgDlTask;
import com.beyondray.micplayer.Util.SyncMgr;

import org.greenrobot.eventbus.EventBus;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by beyondray on 2017/11/11.
 */

public class DetailActivity extends AppCompatActivity {
    @BindView(R.id.ib_play)
    ImageView mImgPlay;

    @BindView(R.id.tv_music_title)
    TextView mTitle;

    @BindView(R.id.tv_music_author)
    TextView mAuthor;

    @BindView(R.id.tv_music_icon)
    CircleImageView mIcon;

    @BindView(R.id.music_detail_bg)
    LinearLayout mBackground;

    private boolean mbPlay;
    private Music mMusic;

    public static Handler mHandler;

    public void initHandler()
    {
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what)
                {
                    case SyncMgr.UI_SYNC:
                        Bundle b = msg.getData();
                        Music m = (Music)b.getSerializable("Music");
                        sync(m);
                        break;
                }
            }
        };
        SyncMgr.mHandler = mHandler;
    }

    public void sync(Music m)
    {
        if(m == null)return;
        mMusic = m;
        mTitle.setText(mMusic.get(Music.Attr.TITLE));
        mAuthor.setText(mMusic.get(Music.Attr.AUTHOR));
        new HttpImgDlTask(mIcon).execute(mMusic.get(Music.Attr.ALBUM_PIC_URL));
        startRotate(mIcon);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_detail);
        ButterKnife.bind(this);
        initHandler();

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        mbPlay = b.getBoolean("bPlay", false);
        mMusic  = (Music) b.getSerializable("Music");
        sync(mMusic);
        //mBackground.setBackground(mIcon.getDrawable());
        setPlayRes();
    }

    public void startRotate(ImageView imgView){
        Animation operatingAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        if(operatingAnim!=null){
            imgView.startAnimation(operatingAnim);
        }
    }

    private void setPlayRes()
    {
        int iRes = mbPlay?R.drawable.music_pause:R.drawable.music_play;
        mImgPlay.setImageResource(iRes);
    }

    @OnClick({R.id.ib_pre, R.id.ib_play, R.id.ib_next})
    public void onClick(View v){
        PlayEvent e = new PlayEvent(PlayEvent.Action.PLAY);
        switch (v.getId())
        {
            case R.id.ib_pre:
                mbPlay = true;
                e.setAction(PlayEvent.Action.PREV);
                break;
            case R.id.ib_play:
                mbPlay = !mbPlay;
                PlayEvent.Action a = mbPlay ? PlayEvent.Action.RESUME: PlayEvent.Action.PAUSE;
                e.setAction(a);
                break;
            case R.id.ib_next:
                mbPlay = true;
                e.setAction(PlayEvent.Action.NEXT);
                break;
        }
        setPlayRes();
        EventBus.getDefault().post(e);
    }

}
