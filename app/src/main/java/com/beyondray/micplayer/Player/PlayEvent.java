package com.beyondray.micplayer.Player;

/**
 * Created by beyondray on 2017/11/10.
 */

public class PlayEvent {
    public enum Action {
        PLAY, PAUSE, RESUME, NEXT, PREV, SEEK, DOWNLOAD
    }
    private Music mic = null;
    private Action a = Action.PLAY;
    public PlayEvent(){};
    public PlayEvent(Action a){ this.a = a; }
    public PlayEvent(Music m) { mic = m;}
    public Action getAction() { return a; }
    public void setAction(Action a) { this.a = a;}
    public Music getMusic() { return mic; }
    public void setMusic(Music m){ mic = m;}
}
