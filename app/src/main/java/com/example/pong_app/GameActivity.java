package com.example.pong_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

public class GameActivity extends AppCompatActivity {

    GameView gameView;
    public static int NEW_GAME = 1;
    public static int TWO_PLAYERS_GAME = 2;
    public static int RESUME_GAME = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gameView = new GameView(this, this);

        setContentView(gameView);

        //getIntent().getBooleanExtra("new_game", true);

    }

    // This method executes when the player starts the game
    @Override
    protected void onResume() {
        super.onResume();

        // Tell the pongView resume method to execute
        //pongView.resume();
    }

    // This method executes when the player quits the game
    @Override
    protected void onPause() {
        super.onPause();

        // Tell the pongView pause method to execute
        //pongView.pause();
    }

    @Override
    public void onStart() {


        super.onStart();
    }

    @Override
    public void onBackPressed() {
        this.gameView.pause();

        SharedPreferences sharedPreferences = getSharedPreferences("save", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();

        myEdit.putInt("game_mode", gameView.getGameMode());
        myEdit.putLong("previous_time", gameView.getPreviousTime());

        myEdit.putFloat("ball_x", (float)gameView.ball.x);
        myEdit.putFloat("ball_y", (float)gameView.ball.y);
        myEdit.putFloat("ball_speed", (float)gameView.ball.speed);
        myEdit.putFloat("ball_angle", (float)gameView.ball.angle);

        myEdit.putInt("player1_score", gameView.player1.score);
        myEdit.putFloat("player1_y", (float)gameView.player1.y);
        myEdit.putFloat("player1_move_y", (float)gameView.player1.moveY);

        myEdit.putInt("player2_score", gameView.player2.score);
        myEdit.putFloat("player2_y", (float)gameView.player2.y);
        myEdit.putFloat("player2_move_y", (float)gameView.player2.moveY);

        myEdit.commit();

        startActivity(new Intent(this,MainActivity.class));
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean left = event.getX() < gameView.canvasWidth/2;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                if(!left && gameView.gameMode == 2)
                    gameView.y1right = event.getY();
                else{
                    gameView.y1left = event.getY();
                }
                break;
            }
            /*
            case MotionEvent.ACTION_MOVE: {
                if(!left && gameMode == 2){
                    y2right = event.getY();
                    yDiff = y1right - y2right;
                    y1right = y2right;

                        player2.moveDiff(yDiff);

                }
                else{
                    y2left = event.getY();
                    yDiff = y1left - y2left;
                    y1left = y2left;

                        player1.moveDiff(yDiff);

                }
                break;
            }*/

            case MotionEvent.ACTION_MOVE: {
                if(!left && gameView.gameMode == 2){
                    gameView.y2right = event.getY();
                    gameView.yDiff = gameView.y1right - gameView.y2right;
                    gameView.y1right = gameView.y2right;
                    if (gameView.yDiff > 1) {
                        gameView.player2.upAccel = true;
                        gameView.player2.downAccel = false;
                    } else if (gameView.yDiff < -1) {
                        gameView.player2.upAccel = false;
                        gameView.player2.downAccel = true;
                    }
                }
                else{
                    gameView.y2left = event.getY();
                    gameView.yDiff = gameView.y1left - gameView.y2left;
                    gameView.y1left = gameView.y2left;
                    if (gameView.yDiff > 1) {
                        gameView.player1.upAccel = true;
                        gameView.player1.downAccel = false;
                    } else if (gameView.yDiff < -1) {
                        gameView.player1.upAccel = false;
                        gameView.player1.downAccel = true;
                    }
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                if(!left && gameView.gameMode == 2){
                    gameView.player2.upAccel = false;
                    gameView.player2.downAccel = false;
                }
                else {
                    gameView.player1.upAccel = false;
                    gameView.player1.downAccel = false;
                }
                break;
            }
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

}

