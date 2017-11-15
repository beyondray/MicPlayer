package com.beyondray.micplayer.Player;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by beyondray on 2017/11/10.
 */

public class Music implements Serializable{
    private Drawable albumPic = null;
    public enum  Attr
    {
        ID, TITLE, AUTHOR, URL, ALBUM_PIC_URL
    }
    private Map<Attr, String> attrs = new HashMap<Attr, String>();

    //fucntion
    public Music(String url){ set(Attr.URL, url);}
    public Music(String id, String title, String author, String url, String albumPicUrl){
        set(Attr.ID, id);
        set(Attr.URL, url);
        set(Attr.TITLE, title);
        set(Attr.AUTHOR, author);
        set(Attr.ALBUM_PIC_URL, albumPicUrl);
    }
    public String get(Attr attr){ return attrs.get(attr); }
    public void set(Attr attr, String str){ attrs.put(attr, str); }
    public void setAlbumPic(Drawable pic){this.albumPic = pic;}
    public Drawable getAlbumPic() { return albumPic;}
    public String[] getAllData(){
        String id = get(Music.Attr.ID);
        String title = get(Attr.TITLE);
        String author = get(Music.Attr.AUTHOR);
        String url = get(Music.Attr.URL);
        String albumPicUrl = get(Music.Attr.ALBUM_PIC_URL);
        return new String[]{id, title, author, url, albumPicUrl};
    }
}
