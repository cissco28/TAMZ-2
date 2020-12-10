package com.example.pong_app;

import android.graphics.Color;

public class Ball {

    public double x;
    public double y;
    public double speed;
    public double radius;
    public int color;
    public double angle;

    Ball(){

    }

    public void move(double deltaTime){
        double distanceTravelled = speed * deltaTime;
        double deltaX = distanceTravelled * Math.cos(angle);
        double deltaY = distanceTravelled * Math.sin(angle);

        //setX(getX() + deltaX);
        //setY(getY() + deltaY);
        this.y += deltaY;
        this.x += deltaX;


        //this.x += speedX;
        //this.y += speedY;
    }
}
