package com.jberdev.lastStand2242;

import android.animation.Animator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

public class EnnemyShip extends Ship {

    public byte HP = 0;
    boolean draw_explosion = false;
    private final static float[] HITBOX_COORDS = new float[]{35,
            5, 140, 180};
    protected short pointValue = 0;

    private static Drawable interceptor_sprite;

    private Object ennemy_ship_type;
    protected final Animator.AnimatorListener DELETE_WHEN_OUT_OF_BOUNDS = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animator) {
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            if (getY() >= ShipSpawner.SCREEN_HEIGHT) {
                destroyView();
            }
        }
        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    };


    private final OnCollisionListener INTERCEPTOR_GOT_HIT = null;
    private final OnCollisionListener SPRAWLER_GOT_HIT = null;






    public EnnemyShip(Context context, String shipType) {
        super(context, HITBOX_COORDS);
        ennemy_ship_type = shipType;
        init(null);
    }

    public EnnemyShip(Context context, AttributeSet attrs) {
        super(context, attrs, HITBOX_COORDS);
        init(attrs);
    }

    public EnnemyShip(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle, HITBOX_COORDS);
        init(attrs);
    }



    private void init(AttributeSet attrs) {
        // On récupère le tableau d'attributs depuis le paramètre que donne le constructeur

        TypedArray attr = getContext().obtainStyledAttributes(attrs, R.styleable.EnnemyShip);

        setListenerAnim(DELETE_WHEN_OUT_OF_BOUNDS);
        if(attrs != null)
            ennemy_ship_type = attr.getString(R.styleable.EnnemyShip_ennemy_ship_type);

        if ("interceptor".equals(ennemy_ship_type))
            init_interceptor();

        attr.recycle();
    }

    private void init_interceptor(){
        pointValue = 10;
        if(interceptor_sprite == null)
            interceptor_sprite = getContext().getResources().getDrawable(R.drawable.vaisseau_ugly_corp, null);
        setExplosionType(INTERCEPTOR_EXPLOSION);
        setImageDrawable(interceptor_sprite);
        setOnCollisionListener(new OnCollisionListener() {
            @Override
             public void onCollision(MovingSolid view, MovingSolid hit) {
                    if (hit instanceof Bullet) {
                        explode_ship();
                        hit.destroyView();
                        Score.change_Score(pointValue);

                    } else if (hit instanceof PlayerShip)
                        ((Ship) hit).explode_ship();
            }
        });
    }
}

