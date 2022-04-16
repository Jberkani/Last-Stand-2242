package com.jberdev.lastStand2242;

import android.annotation.SuppressLint;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Score {
    static int score = 0;
    private static TextView score_disp;

    public Score(RelativeLayout layout){
        score_disp = (TextView) layout.findViewById(R.id.score_disp);
    }

    public static void change_Score(int pointsAdded){
        score += pointsAdded;
        score_disp.setText("Score : "+ score);
    }
}
