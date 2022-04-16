package com.jberdev.lastStand2242;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.jberdev.lastStand2242.util.CSVFile;

import java.io.InputStream;
import java.security.InvalidParameterException;
import java.util.List;

/**
 * Utility class that provides all methods needed to spawn
 * MovingObjects (Bullets, EnnemyShips...)
 * and handle the creation of waves of EnnemyShips
 */
public class ShipSpawner {
    int spawning_zone_height = 0;
    public final static byte COLUMN_NUMBER = 5;
    public final static byte ROW_NUMBER = 5;

    RelativeLayout relativeLayout = null;
    boolean waveCreation = false;
    public static int SCREEN_WIDTH = 0;
    public static int SCREEN_HEIGHT = 0;

    public ShipSpawner(Context context,RelativeLayout parent) {
        relativeLayout = parent;
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        SCREEN_WIDTH  = metrics.widthPixels;
        SCREEN_HEIGHT = metrics.heightPixels;
        Drawable ennemyShip = context.getResources().getDrawable(R.drawable.vaisseau_ugly_corp, null);
        spawning_zone_height = (ennemyShip.getIntrinsicHeight() + 10) * ROW_NUMBER;
    }

    //That's some niceeeee stuff, took a while but it is working yeeees

    /**
     * Spawn a MovingSolid child with the given type at the given location
     * @param objectClass name of a potential moving object's children
     * @param type the type of Bullet | EnnemyShip, it will define its behaviour and aspect
     * @param pos the pos [x, y] of the MovingSolid that will be created
     * @return Bullet | EnemyShip, A relevant moving object according to the given parameters
     */
    public MovingSolid spawnMovingObject(Object objectClass , String type, float[] pos) {
        MovingSolid newShip = null;
        if(objectClass instanceof String){
            String strObjectClass = (String) objectClass;
            if(objectClass.equals("EnnemyShip")) {
                newShip = new EnnemyShip(relativeLayout.getContext(), type);
                relativeLayout.addView(newShip);
                newShip.changePos(pos);
                newShip.move(newShip.getX(), ShipSpawner.SCREEN_HEIGHT + 10, false, 200);
                newShip.setRotation(180);
            }else if(objectClass.equals("Bullet")){
                newShip = new Bullet(relativeLayout.getContext());
                relativeLayout.addView(newShip);
                newShip.changePos(pos);
            }
            return newShip;
        }else{
            throw new InvalidParameterException();
        }
    }



    public void spawnWave(InputStream in, int waveId) {

        CSVFile waveFile = new CSVFile(in);

        List<String> csv_lines = waveFile.read();
        waveCreation = true;
        float caseWidth = (float) SCREEN_WIDTH / COLUMN_NUMBER;


        Log.d("height", spawning_zone_height + "");

        float caseHeight = (float) spawning_zone_height / ROW_NUMBER;

        String wave = csv_lines.get(waveId); //On récupère la ligne du document qui contient la vague
        String[] commas_split = wave.split(","); //On récupère les params de chaque vaisseau dans une liste
        for (String s : commas_split) { // Pour chaque vaisseau
            String[] slash_split = s.split("/"); // On sépare les coords du type de vaisseau
            String ship_type = slash_split[1];     //On récupère le type de vaisseau

            String[] coords = slash_split[0].split(";"); //On récupère les 2 coords


            float Xcoord = Float.parseFloat(coords[0]);
            float Ycoord = Float.parseFloat(coords[1]);
            float[] pos;
            pos = new float[]{(Xcoord * caseWidth), (Ycoord * caseHeight) - 1000};
            EnnemyShip ship = (EnnemyShip) spawnMovingObject("EnnemyShip", ship_type, pos);

        }


    }
}
