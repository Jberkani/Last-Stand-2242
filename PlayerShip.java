package com.jberdev.lastStand2242;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

import java.util.Timer;
import java.util.TimerTask;

public class PlayerShip extends Ship {
    public final static int TOTAL_HEALTH = 100;
    public int remaining_life = TOTAL_HEALTH;
    public boolean canShoot = false;
    public final static float[] INITIAL_PLAYERSHIP_POS = new float[]{432,800};
    private final static Timer SHOOT_TIMER = new Timer();
    public static int SHOOT_PERIOD = 500;
    private final TimerTask CAN_SHOOT_ENABLING = new TimerTask(){
        @Override
        public void run() {
            canShoot = true;
        }
    };
    private final static float[] HITBOX_COORDS = new float[]{40,
    0, 156, 170};
    private boolean shootRight = true;
    private boolean bullet_was_shot = false;




    private void init(){
        SHOOT_TIMER.schedule(CAN_SHOOT_ENABLING, 0, SHOOT_PERIOD);
        setInterpolator(null);
        setX(INITIAL_PLAYERSHIP_POS[0]);
        setY(INITIAL_PLAYERSHIP_POS[1]);
        setExplosionType(INTERCEPTOR_EXPLOSION);
    }

    public void move(float xtranslate, float ytranslate, boolean relative, int vitesse){
        // On récupère les dimensions de l'écran
        int screenWidth = ShipSpawner.SCREEN_WIDTH;
        int screenHeight = ShipSpawner.SCREEN_HEIGHT;

        float xpos = getX();
        float ypos = getY();
        if(relative)
            if(xpos + xtranslate <= screenWidth && xpos + xtranslate > 0
                    && ypos + ytranslate <= screenHeight && ypos + ytranslate > 0)
                super.move(xtranslate, ytranslate, relative, vitesse);
        else
                super.move(xtranslate, ytranslate, relative, vitesse);
    }


    public PlayerShip(Context context) {
        super(context, HITBOX_COORDS);
        init();
    }
    public PlayerShip(Context context, AttributeSet attrs) {
        super(context, attrs, HITBOX_COORDS);
        init();
    }
    public PlayerShip(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle, HITBOX_COORDS);
        init();
    }

    protected Bullet shoot(){
        if(canShoot) {
            float[] pos;
            if(shootRight) {
                pos = new float[]{ getX() +  (float) getWidth() / 2, getY()};
                shootRight = false;
            }else {
                pos = new float[]{getX(), getY()};
                shootRight = true;
            }
            canShoot = false;
            Bullet bullet= super.shoot(pos, Bullet.CLASSIC_BULLET);
            bullet.move(bullet.getX(), -120, false,  1000);

            return bullet;
        }else
            return null;
    }

    /**
     * The Ship calls the shoot method on the UI thread which creates a Bullet
     * @param bullet_type The Bullet type to create
     * Bullet types are bytes constants in Bullet class
     */
    public void startShootSequence(byte bullet_type){
        bullet_was_shot = true;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(bullet_was_shot){
            shoot();
            bullet_was_shot = false;
        }
    }
}
