package com.beyondray.micplayer.UI;


import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.nfc.Tag;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beyondray.micplayer.Player.AudioPlayer;
import com.beyondray.micplayer.Player.Music;
import com.beyondray.micplayer.Player.PlayEvent;
import com.beyondray.micplayer.R;
import com.beyondray.micplayer.Service.HttpImgDlTask;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import butterknife.ButterKnife;

/**
 * Created by beyondray on 2017/11/8.
 */

public class MiniMusicView extends FrameLayout implements View.OnClickListener{
    private final String TAG = "MiniMusicView";
    private Context mContext;
    private ViewStub mViewStub;
    private RelativeLayout mLayout;
    private ImageView mIcon;
    private ImageView mControlBtn;
    private ImageView mNextBtn;
    private ProgressBar mLoadMusic;
    private TextView mMusicTitle;
    private TextView mMusicAuthor;
    private ProgressBar mProgressBar;
    private boolean mIsAddView;
    private boolean mIsPlay;
    private int mMusicDuration;
    private boolean mIsPlayComplete;
    private String mCurPlayUrl;
    private Music mMusic;

    public MiniMusicView(Context context) {
        this(context, null);
    }

    public MiniMusicView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MiniMusicView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mIsAddView = false;
        mIsPlay = true;
        mIsPlayComplete = false;
        initView();
        initAttributeSet(attrs);
    }

    private void initAttributeSet(AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray arr = mContext.obtainStyledAttributes(attrs, R.styleable.MiniMusicView);
        final boolean isLoadLayout = arr.getBoolean(R.styleable.MiniMusicView_isLoadLayout, false);
        if (isLoadLayout) {
            initDefaultView();
        }
        final int titleColor = arr.getColor(R.styleable.MiniMusicView_titleColor, Color.parseColor("#000000"));
        setTitleColor(titleColor);
        final int titleSize = arr.getDimensionPixelOffset(R.styleable.MiniMusicView_titleTextSize, -1);
        if (titleSize != -1) {
            setTitleTextSize(titleSize);
        }

        final int bgColor = arr.getColor(R.styleable.MiniMusicView_musicBackgroundColor, Color.parseColor("#eeeeee"));
        setMusicBackgroundColor(bgColor);

        final Drawable progressDrawable = arr.getDrawable(R.styleable.MiniMusicView_progressDrawable);
        if (progressDrawable != null) {
            setProgressDrawable(progressDrawable);
        }
        final Drawable iconDrawable = arr.getDrawable(R.styleable.MiniMusicView_musicIcon);
        if (iconDrawable != null) {
            setIconDrawable(iconDrawable);
        }
        arr.recycle();
    }

    private void initView() {
        View.inflate(mContext, R.layout.default_viewstup, this);
        mViewStub = (ViewStub) findViewById(R.id.vs_mini_view);
    }

    public void initDefaultView() {
        if (mViewStub != null) {
            View view = mViewStub.inflate();
            mLayout = (RelativeLayout) view.findViewById(R.id.ll_layout);
            mIcon = (ImageView) view.findViewById(R.id.iv_music_icon);
            mControlBtn = (ImageView) view.findViewById(R.id.iv_control_btn);
            mNextBtn = (ImageView) view.findViewById(R.id.iv_next_btn);
            mLoadMusic = (ProgressBar) view.findViewById(R.id.pb_loading);
            mMusicTitle = (TextView) view.findViewById(R.id.tv_music_title);
            mMusicAuthor = (TextView) view.findViewById(R.id.tv_music_author);
            mProgressBar = (ProgressBar) view.findViewById(R.id.pb_progress);
            mControlBtn.setOnClickListener(this);
            mNextBtn.setOnClickListener(this);
            mViewStub = null;
        }
    }

    @Override
    public void onClick(View v) {
        Log.v(TAG, "Click");
        switch (v.getId())
        {
            case R.id.iv_control_btn:
                break;
            case R.id.iv_next_btn:
                EventBus.getDefault().post(new PlayEvent(PlayEvent.Action.NEXT));
                break;
        }
    }

    public void sync(Music m)
    {
        if(m == null)return;
        mMusic = m;
        changeLoadingMusicState(true);
        changeControlBtnState(true);
        setTitleText(m.get(Music.Attr.TITLE));
        setAuthor(m.get(Music.Attr.AUTHOR));
        new HttpImgDlTask(mIcon).execute(m.get(Music.Attr.ALBUM_PIC_URL));
    }

    public Music getMusic()
    {
        return mMusic;
    }

    private void controlBtnClick() {
        if (mIsPlay) {
            AudioPlayer.singleton.pause();
            changeControlBtnState(false);
        } else {
            if (!mIsPlayComplete) {
                AudioPlayer.singleton.resume();
            } else {
                AudioPlayer.singleton.play();
                mProgressBar.setProgress(0);
                mIsPlayComplete = false;
            }
            changeControlBtnState(true);
        }
        Log.v(TAG, "controlBtnClick: isPlay=" + mIsPlay);
    }

    @Override
    public void addView(View child) {
        removeAllViews();
        super.addView(child);
        mIsAddView = true;
        Log.v(TAG, "addView: [ " + this.hashCode() + " ]");
    }

    private void changeLoadingMusicState(boolean isLoading) {
        if (!mIsAddView) {
            if (isLoading) {
                mLoadMusic.setVisibility(View.VISIBLE);
                mControlBtn.setVisibility(View.GONE);
            } else {
                mLoadMusic.setVisibility(View.GONE);
                mControlBtn.setVisibility(View.VISIBLE);
            }
        }
    }

    public void changeControlBtnState(boolean isPlay) {
        if (!mIsAddView && mControlBtn != null) {
            if (isPlay) {
                mControlBtn.setImageResource(R.drawable.mini_btn_pause);
                mIsPlay = true;
            } else {
                mControlBtn.setImageResource(R.drawable.mini_btn_play);
                mIsPlay = false;
            }
        }
    }


    public void setTitleColor(int color) {
        if (!mIsAddView && mMusicTitle != null) {
            mMusicTitle.setTextColor(color);
        }
    }

    public void setTitleTextSize(int dimen) {
        if (!mIsAddView && mMusicTitle != null) {
            mMusicTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, dimen);
        }
    }

    public void setMusicBackgroundColor(int color) {
        if (!mIsAddView && mLayout != null) {
            mLayout.setBackgroundColor(color);
        }
    }

    public void setIconDrawable(Drawable background) {
        if (!mIsAddView && mIcon != null) {
            mIcon.setImageDrawable(background);
        }
    }

    public void setProgressDrawable(Drawable drawable) {
        if (!mIsAddView && mProgressBar != null) {
            mProgressBar.setProgressDrawable(drawable);
        }
    }

    public void setProgressMax(int max) {
        if (!mIsAddView && mProgressBar != null) {
            mProgressBar.setMax(max);
        }
    }

    public void setProgress(int progress) {
        if (!mIsAddView && mProgressBar != null) {
            mProgressBar.setProgress(progress);
        }
    }

    public void setTitleText(String text) {
        if (!mIsAddView && mMusicTitle != null) {
            mMusicTitle.setText(text);
        }
    }

    public void setAuthor(String text) {
        if (!mIsAddView && mMusicAuthor != null) {
            mMusicAuthor.setText(text);
        }
    }

    public boolean isPlaying() {
        return mIsPlay;
    }

    public int getMusicDuration() {
        return mMusicDuration;
    }


}
