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

    public Player(boolean l){
        this.left = l;
        this.score = 0;
        this.upAccel = false;
        this.downAccel = false;
        this.movingUp = false;
        this.movingDown = false;
    }

    public void move(){
        if(upAccel){
            speed -= 3;
        }
        else if(downAccel){
            speed += 3;
        }
        else if(!upAccel && !downAccel){
            speed *= gravity;
        }

        if(speed >= 15)
            speed = 15;
        else if(speed <= -15)
            speed = -15;

        y += speed;

        if(speed > 0.1){
            this.movingUp = true;
            this.movingDown = false;
        }
        else if(speed < -0.1){
            this.movingUp = false;
            this.movingDown = true;
        }
        else {
            this.movingUp = false;
            this.movingDown = false;
        }

    }

}
