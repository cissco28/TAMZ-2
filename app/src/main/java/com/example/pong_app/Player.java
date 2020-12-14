package com.example.pong_app;

import android.graphics.Color;

public class Player {

    public double x;
    public double y;
    public double width;
    public double height;
    public double speed;
    public long color;
    public boolean upAccel;
    public boolean downAccel;
    public double gravity;
    public boolean left;
    public int score;
    public boolean movingUp;
    public boolean movingDown;
    public double moveY;

    public Player(boolean l){
        this.left = l;
        this.score = 0;
        this.upAccel = false;
        this.downAccel = false;
        this.movingUp = false;
        this.movingDown = false;
    }


    public void moveDiff(float diff){
        y += diff;

    }

}
