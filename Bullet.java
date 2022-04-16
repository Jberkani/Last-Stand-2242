package com.jberdev.lastStand2242;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;

public class Bullet extends MovingSolid {
    public long creation_time = 0;
    private final static float[] HITBOX_COORDS = new float[]{15,
            5, 70, 95};
    /**
    Bullet types :
    ****************************************************************
    */

    public final static byte CLASSIC_BULLET = 0, LASER = 1, CAM = 2, ENNEMY_CLASSIC_BULLET = 3;
    private byte bullet_type;
    /**
    ****************************************************************
     */
    protected final Animator.AnimatorListener DELETE_WHEN_OUT_OF_BOUNDS = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animator) {
         creation_time = System.currentTimeMillis();
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            Log.d("getY",getY() + "Y !!!");
            if(getY() <= -100)
                destroyView();
        }


        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    };

    public Bullet(Context context) {
        super(context, HITBOX_COORDS);
        init();
    }
    public Bullet(Context context, AttributeSet attrs) {
        super(context, attrs, HITBOX_COORDS);
        init();
    }
    public Bullet(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle, HITBOX_COORDS);
        init();
    }

    public void setBullet_type(byte bullet_type) {
        this.bullet_type = bullet_type;
    }

    public byte getBullet_type(){
        return bullet_type;
    }

    private void init(){
        setInterpolator(null);
        setListenerAnim(DELETE_WHEN_OUT_OF_BOUNDS);
        setImageDrawable(getContext().getResources().getDrawable(R.drawable.bullet,  null));
    }

}
