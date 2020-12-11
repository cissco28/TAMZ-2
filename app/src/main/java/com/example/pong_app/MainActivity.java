package com.example.pong_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import static com.example.pong_app.GameActivity.NEW_GAME;
import static com.example.pong_app.GameActivity.RESUME_GAME;
import static com.example.pong_app.GameActivity.TWO_PLAYERS_GAME;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.resumeGameButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resumeGame();
            }
        });

        findViewById(R.id.twoPlayersGameButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newTwoPlayerGame();
            }
        });

        findViewById(R.id.onePlayerGameButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newGame();
            }
        });

        findViewById(R.id.settingsButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), SettingsActivity.class));
                finish();
            }
        });

        findViewById(R.id.exitGameButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitgame();
            }
        });
    }

    public void resumeGame(){
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("game_state", RESUME_GAME);
        startActivity(intent);
        finish();
    }

    protected void newGame(){
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("game_state", NEW_GAME);
        startActivity(intent);
        finish();
    }

    protected void newTwoPlayerGame(){
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("game_state", TWO_PLAYERS_GAME);
        startActivity(intent);
        finish();
    }

    protected void exitgame(){
        finish();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }

}