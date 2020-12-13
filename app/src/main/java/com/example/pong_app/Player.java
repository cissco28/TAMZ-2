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
    public float moveY;

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
            speed -= 10;
        }
        else if(downAccel){
            speed += 10;
        }
        else if(!upAccel && !downAccel){
            speed *= gravity;
        }

        if(speed >= 30)
            speed = 30;
        else if(speed <= -30)
            speed = -30;

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

    public void moveDiff(){
        if(moveY < y + width/2) {
            y += speed;
        }
        else if(moveY > y + width/2){

        }

    }

}
