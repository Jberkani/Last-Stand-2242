package com.jberdev.lastStand2242;

import android.content.Context;
import android.media.MediaPlayer;
import android.provider.MediaStore;


public class LevelManager {
    private static MediaPlayer mp;

    private Context context;

    public LevelManager(Context cont){
        context = cont;
        mp = MediaPlayer.create(context, R.raw.wide_walking);

    }

    public void start_Level(int lvl_nb){
        play_Song(R.raw.wide_walking);
    }

    private void play_Song(int resID){
        mp.start();
    }
}
