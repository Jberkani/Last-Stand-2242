package com.jberdev.lastStand2242;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;



public class UIcontrols {

    private PlayerShip mainShip = null;

    private RelativeLayout relLayout = null;

    private float xMove = 0;
    private float yMove = 0;

    private final static byte DURATION = 10;
    private boolean canShoot = false;


    public final static byte SHOOT_DELAY = 10;
    public final static byte MOVE_DELAY = 10;


    //Constructeur
    public UIcontrols(RelativeLayout layout, int playerShipId){
        relLayout = layout;
        mainShip = (PlayerShip) relLayout.findViewById(playerShipId);
    }

    public void setMoveControls(int JoystickId){
        JoystickView controller = (JoystickView) relLayout.findViewById(JoystickId);
        controller.setOnMovedListener(new JoystickView.JoystickListener() {
            @Override
            public void onJoystickMoved(float xPercent, float yPercent, int id) {
                /*xMove = controller.getCenterX();
                yMove = controller.getCenterY();*/
                xMove = xPercent;
                yMove = yPercent;
                Log.d("Movement", "déplacement X : " + xPercent + "Deplacement Y : " + yPercent);
            }
        });
        Timer moveThread = new Timer();
        moveThread.schedule(new TimerTask() {
            @Override
            public void run() {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mainShip.move(xMove, yMove, true, DURATION);
                    }
                });
            }
        }, 0, MOVE_DELAY);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setShootControls(int shootButtonId){
        Button shoot = (Button) relLayout.findViewById(shootButtonId);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(canShoot)
                    mainShip.startShootSequence(Bullet.CLASSIC_BULLET);
            }
        },0, SHOOT_DELAY);
        shoot.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                canShoot = true;
            }
            else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                canShoot = false;
            return true;
        });

    }
}
