package com.example.pong_app;

import android.graphics.Color;

public class Player {

    public double x;
    public double y;
    public double width;
    public double height;
    public double speed;
    public int color;
    public boolean upAccel;
    public boolean downAccel;
    public double gravity;

    public Player(){

    }

    public void move(){
        if(upAccel){
            speed -= 1;
        }
        else if(downAccel){
            speed += 1;
        }
        else if(!upAccel && !downAccel){
            speed *= gravity;
        }

        if(speed >= 5)
            speed = 5;
        else if(speed <= -5)
            speed = -5;

        y += speed;

    }

}
