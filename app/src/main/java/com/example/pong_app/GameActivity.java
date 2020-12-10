package com.example.pong_app;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.WindowManager;

public class GameActivity extends AppCompatActivity {

    GameView gameView;
    float x1,x2,y1,y2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //getSupportActionBar().hide();


        gameView = findViewById(R.id.gameView);
    }

    protected void init(){

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                y1 = event.getY();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                y2 = event.getY();
                float yDiff = y1 - y2;

                if (yDiff > 0) {
                    gameView.player1.upAccel = true;
                    gameView.player1.downAccel = false;
                } else if (yDiff < 0) {
                    gameView.player1.upAccel = false;
                    gameView.player1.downAccel = true;
                }
                else {
                    gameView.player1.upAccel = false;
                    gameView.player1.downAccel = false;
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                gameView.player1.upAccel = false;
                gameView.player1.downAccel = false;
                break;
            }
        }

        return super.onTouchEvent(event);
    }

}


 class GameSet {

     int GAME_WIDTH;
     int GAME_HEIGHT;

     int BALL_COLOR = 0xFFFFFFFF;
     int PLAYERS_COLOR = 0xFFFFFFFF;
     int SCORE_COLOR = 0xFFFFFFFF;
     int BACKGROUND_COLOR = 0x00000000;
     int MAX_BALL_SPEED = 1000;
     double PLAYER_SPEED = 1.5;
     int MAX_SCORE = 10;
     int PLAYER_SPACING = 10;
     int PLAYER_SECTIONS = 11;
     double BALL_SPEED_INCREASE = -1.25;
     double BALL_INITIAL_SPEED = -300;
     int PLAYER_HEIGHT;
     int PLAYER_WIDTH;
     int BALL_RADIUS = 12;
     int PLAYER_SECTION_HEIGHT;
     double DIFFICULTY = 0.15;
     double PLAYER_GRAVITY = 0.9;

    GameSet(int gw, int gh, int pw, int ph, int psh){
        this.GAME_WIDTH = gw;
        this.GAME_HEIGHT = gh;
        this.PLAYER_HEIGHT = ph;
        this.PLAYER_WIDTH = pw;
        this.PLAYER_SECTION_HEIGHT = psh;
    }

}


