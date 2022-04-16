package com.jberdev.lastStand2242;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.os.Handler;


public abstract class Ship extends MovingSolid {
    private int explosion_type = 0;
    private final static short EXPLODE_PERIOD = 100;
    /**
     * ****************************************************************************
     * Explode animation types :
     *
    */
    static protected final byte INTERCEPTOR_EXPLOSION = 0, PLAYER_SHIP_EXPLOSION = 1;
    private final static List<Drawable[]> explodeAnimations = new ArrayList<>();

    /**
     * ***************************************************************************
     */
    private short explodeRepetitions = 0;
    private boolean draw_explosion = false;


    public Ship(Context context, float[] hitboxCoords) {
        super(context, hitboxCoords);
        init();
    }

    public Ship(Context context, AttributeSet attrs, float[] hitboxCoords) {
        super(context, attrs, hitboxCoords);
        init();
    }

    public Ship(Context context, AttributeSet attrs, int defStyle, float[] hitboxCoords) {
        super(context, attrs, defStyle, hitboxCoords);
        init();
    }

    private void init() {
        if(explodeAnimations.size() == 0) {
            for(int i = 0; i < PLAYER_SHIP_EXPLOSION; i++) {
                explodeAnimations.add(i, loadExplosions(i));
            }
        }
    }

    protected Bullet shoot(float[] pos, int bullet_type){
        ShipSpawner spawner = new ShipSpawner(getContext() ,(RelativeLayout) getParent());

        Bullet bullet = (Bullet) spawner.spawnMovingObject("Bullet", null , pos);
        bullet.setX(pos[0]);
        bullet.setY(pos[1]);

        return bullet;
    }

    protected void setExplosionType(int type){
        explosion_type = type;
    }


    /**
     * Play the animation of the ship exploding
     */
    public void explode_ship() {
        if (!draw_explosion) {
            setImageDrawable(null);
            Handler refresh_loop = new Handler(Looper.getMainLooper());
            stop_collision_listening();
            refresh_loop.post(new Runnable() {
                @Override
                public void run() {
                if (explodeRepetitions <= 6) {
                    setImageDrawable(explodeAnimations.get(explosion_type)[explodeRepetitions]);
                    explodeRepetitions++;
                    draw_explosion = false;
                    refresh_loop.postDelayed(this, EXPLODE_PERIOD);
                } else {
                    destroyView();
                }
                }
            });
            setScaleX(4);
            setScaleY(4);
        }
    }

    /**
     * load the corresponding explosion type
     * @param explosion_type Ship Constant
     */
    private Drawable[] loadExplosions(int explosion_type){
            Drawable[] explodeAnim = new Drawable[7];
            for (int i = 1; i < 8; i++) {
                String filePath = "basic_explosion/explosion";
                Log.d("loading", "EXPLOSIONS LOADED");
                filePath = filePath + i + ".png";
                try {
                    InputStream is = getContext().getAssets().open(filePath);
                    Drawable d = Drawable.createFromStream(is, null);
                    explodeAnim[i - 1] = d;
                    try {
                        is.close();
                    } catch (IOException e) {
                        throw new RuntimeException("Error while closing loading inputStream for ship's explosion sprites" + e);
                    }
                } catch (IOException e) {
                    throw new RuntimeException("No explosion Sprites found for ship" + e);
                }
            }
            return explodeAnim;

    }

}
