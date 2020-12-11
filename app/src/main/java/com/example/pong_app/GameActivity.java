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

        myEdit.putInt("player2_score", gameView.player2.score);
        myEdit.putFloat("player2_y", (float)gameView.player2.y);

        myEdit.commit();

        startActivity(new Intent(this,MainActivity.class));
        finish();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

}

