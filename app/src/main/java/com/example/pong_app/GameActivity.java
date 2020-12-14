package com.example.pong_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

public class GameActivity extends AppCompatActivity {

    GameView gameView;
    public static int NEW_GAME = 1;
    public static int TWO_PLAYERS_GAME = 2;
    public static int RESUME_GAME = 3;
    float y1left,y2left, y1right,y2right, yDiff;
    long moveRightTime, moveLeftTime;
    boolean up;
    int tapCounter;
    long tapTime;
    int hitWall, hitPaddle, scoreLeft, scoreRigh;
    SoundPool soundPool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
        hitWall = soundPool.load(this, R.raw.hit_wall, 1);
        hitPaddle = soundPool.load(this, R.raw.hit_paddle, 1);
        scoreLeft = soundPool.load(this, R.raw.score_player, 1);
        scoreRigh = soundPool.load(this, R.raw.score_opponent, 1);

        gameView = new GameView(this, this,soundPool);

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

    public void endGame(){
        //this.gameView.pause();

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
    public void onBackPressed() {
        if(gameView.endGame || gameView.paused){
            endGame();
        }
        else{
            gameView.pauseGame();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!gameView.paused && !gameView.endGame) {
            boolean left = event.getX() < gameView.canvasWidth / 2;
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN: {
                    if (!left && gameView.gameMode == 2)
                        y1right = event.getY();
                    else {
                        y1left = event.getY();
                    }
                    break;
                }

                case MotionEvent.ACTION_MOVE: {
                    if (!left && gameView.gameMode == 2) {

                        y2right = event.getY();
                        yDiff = y2right - y1right;
                        if (yDiff < 0) {
                            gameView.player2.movingDown = false;
                            gameView.player2.movingUp = true;
                        } else if (yDiff > 0) {
                            gameView.player2.movingUp = false;
                            gameView.player2.movingDown = true;
                        }
                        y1right = y2right;

                        gameView.moveRightTime = System.nanoTime();

                        gameView.player2.moveDiff(yDiff);

                    } else {

                        y2left = event.getY();
                        yDiff = y2left - y1left;
                        if (yDiff < 0) {
                            gameView.player1.movingDown = false;
                            gameView.player1.movingUp = true;
                        } else if (yDiff > 0) {
                            gameView.player1.movingUp = false;
                            gameView.player1.movingDown = true;
                        }
                        y1left = y2left;

                        gameView.moveLeftTime = System.nanoTime();

                        gameView.player1.moveDiff(yDiff);

                    }
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    if (!left && gameView.gameMode == 2) {
                        gameView.player2.movingUp = false;
                        gameView.player2.movingDown = false;
                    } else {
                        gameView.player1.movingUp = false;
                        gameView.player1.movingDown = false;
                    }
                    break;
                }
            }
        }
        else{
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN: {
                    up = true;
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    if(up){
                        if(gameView.endGame){
                            gameView.prepareNewGame();
                        }
                        else {
                            gameView.resume();
                        }
                    }
                }
                up = false;
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

