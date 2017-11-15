package com.beyondray.micplayer.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by beyondray on 2017/11/10.
 */

public class PlayList {
    public enum PlayMode{
        CYCLE, RANDOM, LOOP
    }
    public List<Music> queue = new ArrayList<Music>();
    int queIdx = 0;
    PlayMode playMode = PlayMode.CYCLE;

    public PlayList(){};
    public PlayList(List<Music> q, int idx)
    {
        queue = q;
        queIdx = idx;
    }

    public int contains(Music m){
        for(int i = 0; i < queue.size(); i++){
            if(queue.get(i).get(Music.Attr.ID).equals(m.get(Music.Attr.ID))){
                return i;
            }
        }
        return -1;
    }
    public void add(Music m)
    {
        int i = contains(m);
        if(i == -1)queue.add(m);
    }
    public void remvoe(Music m)
    {
        int i = contains(m);
        if(i != -1)queue.remove(i);
    }
    public int size(){
        return queue.size();
    }

    public void setPlayMode(PlayMode mode){ playMode = mode;}
    public int getPlayIdx() { return queIdx; }
    public void setPlayIdx(int idx){ queIdx = idx;}
    public void fitPlayIdx() {
        queIdx = queIdx >= queue.size() ? queue.size()-1 : (queIdx < 0 ? 0 : queIdx);
    }
    private int nextIdx(int i) {
        int _t = (queIdx + i) % queue.size();
        return _t >= 0 ? _t : _t + queue.size();
    }
    private int randomIdx() {
        int idx;
        do{
            idx = new Random().nextInt(queue.size());
        }while (idx == queIdx);
        return idx;
    }

    public Music getNextMusic(int gap)
    {
        if(queue.isEmpty())return null;
        switch (playMode)
        {
            case CYCLE: queIdx = nextIdx(gap);break;
            case RANDOM: queIdx = randomIdx();break;
            case LOOP: queIdx = nextIdx(gap);break;
        }
        return queue.get(queIdx);
    }

    public Music getCurMusic()
    {
        return queue.get(queIdx);
    }
    public Music getPrevMusic(){
        return getNextMusic(-1);
    }
    public Music getNextMusic(){
        return getNextMusic(1);
    }
}
