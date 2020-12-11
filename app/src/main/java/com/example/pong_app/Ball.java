package com.example.pong_app;

import android.graphics.Color;

import java.util.Random;


public class Ball {

    public double x;
    public double y;
    public double speed;
    public double radius;
    public long color;
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

        //System.out.println(this.x);
        //System.out.println(this.y);


        //this.x += speedX;
        //this.y += speedY;
    }

}
