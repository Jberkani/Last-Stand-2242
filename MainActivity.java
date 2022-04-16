package com.jberdev.lastStand2242;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;


public class MainActivity extends Activity implements JoystickView.JoystickListener{
    private PlayerShip playerShip = null;

    private RelativeLayout layout = null;


    private ShipSpawner initializer = null;

    private HashMap<String,HashMap<String,int[]>> waves = null;

    private UIcontrols controls = null;
    private MovingSolid.OnCollisionListener ennemyShipsListener = null;
    private InfiniteScroller scroller = null;
    private JoystickView joystick = null;



    private Score global_score;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scroller = findViewById(R.id.background);
        scroller.animate_infinite_scroll(250);

        LevelManager lvl_mg = new LevelManager(this);
        lvl_mg.start_Level(0);

        layout = findViewById(R.id.rellayout);

        joystick = findViewById(R.id.joystick);
        global_score = new Score(layout);
        Score.change_Score(0);

        controls = new UIcontrols(layout, R.id.main_ship);
        controls.setShootControls(R.id.shoot_button);
        controls.setMoveControls(R.id.joystick);
        /*
        controls.setMainShipMoveControls(R.id.up_button, R.id.left_button,
                R.id.right_button, R.id.bottom_button);

         */

        playerShip = (PlayerShip) findViewById(R.id.main_ship);


        //WAVE CREATION
        initializer = new ShipSpawner(this ,layout);


            final Handler handler = new Handler();
        //Implémenter le systeme de niveau !
        //Plusieurs activités ?
        final Runnable toRun = () -> {
            // Do something after 5s = 5000ms
            InputStream in;

            try {
                in = getAssets().open("waves_1.csv");
                int rand = (int) (Math.random() * 5);
                initializer.spawnWave(in, rand);

            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toRun.run();
                handler.postDelayed(this::run, 5000);
            }
        }, 5000);
    }
    @Override
    public void onJoystickMoved(float xPercent, float yPercent, int id) {
        Log.d("Movement detected : ", "x : " + xPercent + " y : " + yPercent);
    }
}
;
