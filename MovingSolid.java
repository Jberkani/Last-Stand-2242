package com.jberdev.lastStand2242;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


import androidx.annotation.NonNull;


import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;


public abstract class MovingSolid extends androidx.appcompat.widget.AppCompatImageView{
    private int mSpeed = 0;
    protected TimeInterpolator interpolator = null;
    private boolean isListening = false;

    private RectF hitBox;
    private final Paint mPainter = new Paint(Paint.ANTI_ALIAS_FLAG);

    protected OnCollisionListener colListener = null;

    //Optimisation possible :
    //Liste de Hitboxs à la place d'une liste de MovingSolids : Pas forcément nécessaire
    //private final static List<RectF> hitboxList = new ArrayList<>();

    public static LinkedBlockingQueue<MovingSolid> movingSolidsList = new LinkedBlockingQueue<>();

    private final static SolidCollisionLoop collisionLoop = new SolidCollisionLoop(movingSolidsList);


    Animator.AnimatorListener listenerAnim = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animator) {
        }

        @Override
        public void onAnimationEnd(Animator animator) {
        }

        @Override
        public void onAnimationCancel(Animator animator) {
        }

        @Override
        public void onAnimationRepeat(Animator animator) {
        }
    };

    protected void setHitbox(float[] coords){
        hitBox = new RectF( coords[0],
                coords[1],
                coords[2],
                coords[3]);
    }

    public int getSpeed(){
        return mSpeed;
    }

    public void setListenerAnim(Animator.AnimatorListener animatorListener){
        listenerAnim = animatorListener;
    }

    public boolean isListeningForCollision(){
        return isListening;
    }
    public void setInterpolator (TimeInterpolator interpol){
        interpolator = interpol;
    }

    public MovingSolid(Context context, float[] hitboxCoords) {
        super(context);
        init(hitboxCoords);
    }
    public MovingSolid(Context context, AttributeSet attrs, float[] hitboxCoords) {
        super(context, attrs);
        init(hitboxCoords);

    }
    public MovingSolid(Context context, AttributeSet attrs, int defStyle, float[] hitboxCoords) {
        super(context, attrs, defStyle);
        init(hitboxCoords);

    }

    private void init(float[] coords) {
        mPainter.setColor(Color.CYAN);

        setHitbox(coords);

        movingSolidsList.add(this);


        /*hitboxList.put(getId(), hitBox);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                refreshHitbox();
            }
        }, 10, 40);*/
    }

    public void move(float xtranslate, float ytranslate, boolean relative, int speed){
        ObjectAnimator translation;
        Path path;
        path = new Path();
        float xpos = getX();
        float ypos = getY();
        int duration;


        if(!relative) {

            duration = (int) ((Math.abs(xpos - xtranslate) + Math.abs(ypos - ytranslate)) * 1000 / speed);
            path.moveTo(xpos, ypos);
            path.lineTo(xtranslate, ytranslate);
        }
        else {
            duration = speed;
            path.moveTo(xpos, ypos);
            path.lineTo(xtranslate + xpos, ytranslate + ypos);
        }
        translation = ObjectAnimator.ofFloat(this, View.X,
                View.Y, path).setDuration(duration);
        try {
            translation.addListener(listenerAnim);
            translation.setInterpolator(interpolator);
            translation.setAutoCancel(true);
            translation.start();
        }catch(NullPointerException e) {
            Log.e("Nulpointeur", "Exeption null pointeur");
        }catch (IndexOutOfBoundsException e){
            Log.e("OutOfBounds", "Exeption Out Of Bounds");
        }

    }

    /*@RequiresApi(api = Build.VERSION_CODES.N)
    private void refreshHitbox(){
        RectF newRect = new RectF(getX() + getHitbox().left, getY() + getHitbox().top,
                getX() + getHitbox().right, getY() + getHitbox().bottom);
        hitboxList.set(getId(), newRect);
    }
    */


    /**
     * Static Nested Class SolidCollisionLoop
     * Check for collisions between movingObjects periodically
     * Use setCollisionListener to detect collisions
     * on an active Moving Object
     */
    private static class SolidCollisionLoop extends Timer{
        private final LinkedBlockingQueue<MovingSolid> all_solids;
        private final LinkedBlockingQueue<MovingSolid> all_listening_solids;
        private final TimerTask mLoop;

        public SolidCollisionLoop(@NonNull LinkedBlockingQueue<MovingSolid> solids_List){

            all_listening_solids = new LinkedBlockingQueue<>();
            all_solids = solids_List;
            final Handler mHandler = new Handler();
            mLoop = new TimerTask() {
                @Override
                public void run() {
                    try {

                        for (MovingSolid ls : all_listening_solids) {
                            for (MovingSolid ms : all_solids) {
                                if (areInCollision(ls, ms)) {
                                    mHandler.post(() -> ls.getCollisionListener().onCollision(ls, ms));
                                }
                            }
                        }
                    } catch (NullPointerException e) {
                        Log.e("NullPointerException : ", "" + e);
                    }
                }
            };

            start();
        }
        public void addToListeningList(MovingSolid solid){
            all_listening_solids.add(solid);
        }

        public void removeFromListeningList(MovingSolid solid){
            all_listening_solids.remove(solid);
        }

        private void start(){
            schedule(mLoop, 0, OnCollisionListener.CHECK_RATE);
        }
        /**
         * The loop stop to detect collisions when this method is called
         */
        public void stop(){
            purge();
            cancel();
        }
        /**
         * Test if two movingObjects are or not in collision by testing the
         * intersection of their hitboxs
         * @param view MovingObject1 to get the first Hitbox
         * @param hitboxHit MovingObject2 to get the second Hitbox
         * @return true if the hitboxs of the movingObjects are in collisions
         */
        private boolean areInCollision(MovingSolid view, MovingSolid hitboxHit){

            final float X = view.getX();
            final float Y = view.getY();

            final float otherX = hitboxHit.getX();
            final float otherY = hitboxHit.getY();

            RectF mOwnRect = new RectF(X + view.getHitbox().left, Y + view.getHitbox().top,
                    X + view.getHitbox().right, Y + view.getHitbox().bottom);
            RectF otherRect = new RectF(otherX + hitboxHit.getHitbox().left, otherY + hitboxHit.getHitbox().top,
                    otherX + hitboxHit.getHitbox().right,  otherY + hitboxHit.getHitbox().bottom);

            return RectF.intersects(mOwnRect, otherRect);
        }

    }
    /**
     * Implement to define special behaviour when a movingSolid is hit by another movingSolid
     */
    public interface OnCollisionListener {
        int CHECK_RATE = 10;
        /**
         * Callback method
         * override when creating an object to define a behaviour whenever a collision is detected
         * @param view active view that got hit
         * @param hit view that hit the active view
         */
        default void onCollision(MovingSolid view, MovingSolid hit){
        }
    }


    public void setOnCollisionListener(@NonNull OnCollisionListener listener){
        colListener = listener;
        start_collision_listening();
        }
    public RectF getHitbox(){
        return hitBox;
    }

    protected void destroyView() {
        stop_collision_listening();
        movingSolidsList.remove(this);
        setVisibility(GONE);
        setImageDrawable(null);
        setHitbox(new float[]{0, 0, 0, 0});
        Log.d("length : ", movingSolidsList.size() + "");
    }
    public void start_collision_listening(){
        collisionLoop.addToListeningList(this);
        isListening = true;
    }
    public void stop_collision_listening(){
        collisionLoop.removeFromListeningList(this);
        isListening = false;
    }
    public void changePos(float[] pos){
        setX(pos[0]);
        setY(pos[1]);
    }
    public OnCollisionListener getCollisionListener(){
        return colListener;
    }

}
