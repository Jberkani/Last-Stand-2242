package com.jberdev.lastStand2242;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class InfiniteScroller extends RecyclerView {

    private static final byte REFRESH_RATE = 100;
    private InfiniteScrollerAdapter backGroundAdapt = null;
    private Drawable[] backgroundList;
    private final int scroll_duration = 10000;
    private Handler mHandler;
    private final static byte SIZE = 7;
    private int background_id = 0;

    private Thread translation_dimension;

    private final View.OnTouchListener DISABLE_ALL_TOUCH = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return true;
        }
    };

    /**
     * Constructor to create programmatically an infiniteScrollingBackground
     * begin scrolling by calling animate_infinite_scroll()
     * @param context the context of the activity
     * @param resId identifier of the background drawable resource to apply
     * on the infiniteScrollingBackground
     */

    public InfiniteScroller(@NonNull Context context, @NonNull int resId) {
        super(context);
        background_id = resId;
        init();

    }

    public InfiniteScroller(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        background_id = getBackgroundAttribute(attrs);
        init();
    }

    public InfiniteScroller(@NonNull Context context, @Nullable AttributeSet attrs, @Nullable int defStyleAttr){
        super(context, attrs, defStyleAttr);
        background_id = getBackgroundAttribute(attrs);
        init();
    }



    private void init() {
        mHandler = new Handler(Looper.getMainLooper());
        load_backgrounds(background_id);
        backGroundAdapt = new InfiniteScrollerAdapter(backgroundList, R.layout.simple_space_layout);
        setAdapter(backGroundAdapt);

        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        setLayoutManager(manager);
        disable_touch_events();
    }


    private int getBackgroundAttribute(AttributeSet attrs){
        int id = 0;
        if(attrs != null){
            TypedArray attr = getContext().obtainStyledAttributes(attrs, R.styleable.InfiniteScrollingBackground);
            if(attr.getString(0).equals("simple_space_layout"))
                id = R.drawable.space;
            attr.recycle();
            return id;
        }else
            return -1;
    }

    private void load_backgrounds(int background_id){
        backgroundList = new Drawable[SIZE];
        for (int i = 0; i < SIZE - 1; i++) {
                try {
                    backgroundList[i] = getContext().getResources().getDrawable(background_id, null);
                }catch(Resources.NotFoundException e){
                    throw new RuntimeException("No background found for infiniteScrollingBackground !" + e);
                }
            }

    }

    private void disable_touch_events(){
        setOverScrollMode(RecyclerView.SCREEN_STATE_OFF);
        setOnTouchListener(DISABLE_ALL_TOUCH);
    }


    /**
     * Begin the infinite scrolling animation of the infinite_scroller
     * @param speed the speed of the movement
     */
    public void animate_infinite_scroll(float speed) {
        float step =  (speed/100);

        translation_dimension = new Thread(() -> {
            try {
                while(!Thread.interrupted()){
                    scrollBy(0, (int) step);
                    Thread.sleep(REFRESH_RATE);
                }
            } catch (InterruptedException e) {
                Log.d("FIN : ", "translation du fond" + e);
            }
        });
        translation_dimension.start();


    }

    public void stop_infinite_scroll(){
        translation_dimension.interrupt();
    }
}
