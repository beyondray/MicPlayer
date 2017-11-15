package com.beyondray.micplayer.UI;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;

import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.beyondray.micplayer.Adapter.VPagerAdapter;
import com.beyondray.micplayer.Player.AudioPlayer;
import com.beyondray.micplayer.Player.PlayList;
import com.beyondray.micplayer.SQLite.MusicDB;
import com.beyondray.micplayer.Service.MusicService;
import com.beyondray.micplayer.R;
import com.beyondray.micplayer.Player.Music;
import com.beyondray.micplayer.Util.SyncMgr;

/**
 * Created by beyondray on 2017/11/10.
 */

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener  {
    static String TAG = "MainActivity";
    final static int DETAIL_ACTI_CODE = 1;

    public static MusicDB db;
    public static Handler mHandler;

    public static VPagerAdapter mVPagerAdapter;
    MiniMusicView mMusicView;
    ViewPager mViewPager;
    TabLayout mTabLayout;

    public void loadDB()
    {
        db = new MusicDB(this);

        //table playList
        MusicDB.Table t_pl_mic = MainActivity.db.get(MusicDB.T_PL_MIC);
        AudioPlayer.singleton.setPlayList(t_pl_mic.getData());

        //table musicRecord
        MusicDB.Table t_rd_mic = MainActivity.db.get(MusicDB.T_RD_MIC);
        AudioPlayer.singleton.setRecordList(t_rd_mic.getDataByTimeSeq());
    }

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
                        mMusicView.sync(m);

                        break;
                }
            }
        };
        SyncMgr.mHandler = mHandler;
    }

    public void setupMiniView()
    {
        mMusicView = (MiniMusicView) findViewById(R.id.mmv_music);
        mMusicView.initDefaultView();
        Drawable drawable = ContextCompat.getDrawable(MainActivity.this, R.drawable.img_bg);
        mMusicView.setIconDrawable(drawable);
        mMusicView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                Bundle b = new Bundle();
                b.putSerializable("Music", mMusicView.getMusic());
                b.putBoolean("bPlay", AudioPlayer.singleton.isPlaying());
                intent.putExtras(b);
                startActivityForResult(intent, DETAIL_ACTI_CODE);
            }
        });
        //load latest music
        SyncMgr.updateLatestMiniMic();
    }

    public void setupViewPager()
    {
        mTabLayout = (TabLayout)findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mVPagerAdapter = new VPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mVPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    public void setupToolbarAndNavgation()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        int playModeId = pref.getInt("playMode", R.id.play_cycle);
        navigationView.setCheckedItem(playModeId);
        setPlayMode(playModeId);
    }

    public void setupBtnAddMusic()
    {
        FloatingActionButton btnAddMic = (FloatingActionButton) findViewById(R.id.fab);
        btnAddMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SyncMgr.updatePlayList(view);
            }
        });
    }

    public void initUI()
    {
        setupToolbarAndNavgation();
        setupBtnAddMusic();
        setupMiniView();
        setupViewPager();
    }

    public void startService()
    {
        Intent intent = new Intent(this, MusicService.class);
        startService(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initHandler();
        loadDB();
        startService();
        initUI();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setPlayMode(int id)
    {
        PlayList.PlayMode mode = PlayList.PlayMode.CYCLE;
        switch (id)
        {
            case R.id.play_cycle:
                mode = PlayList.PlayMode.CYCLE;
                break;
            case R.id.play_random:
                mode = PlayList.PlayMode.RANDOM;
                break;
            case R.id.play_loop:
                mode = PlayList.PlayMode.LOOP;
                break;
        }
        AudioPlayer.singleton.setPlayMode(mode);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        setPlayMode(id);

        //save playmode data
        SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
        editor.putInt("playMode", id);
        editor.apply();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case DETAIL_ACTI_CODE:
                SyncMgr.mHandler = mHandler;
                SyncMgr.sendSyncUIMsg(AudioPlayer.singleton.getPlayingMusic());
                break;
        }
    }
}
