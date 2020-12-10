package com.example.pong_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.preference.PreferenceManager;

public class GameView extends View {

    Ball ball;
    Player player1;
    Player player2;
    GameSet gameSet;
    int canvasWidth;
    int canvasHeight;
    int playerSpacing = 10;
    int playerWidth;
    int playerHeight;

    public GameView(Context context) {
        super(context);
        init(context);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context);
        init(context);
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context);
        init(context);
    }

    public void init(Context context){
        canvasWidth = getWidth();
        canvasHeight = getHeight();

        ball = new Ball();
        player1 = new Player();
        player2 = new Player();
        gameSet = new GameSet(canvasWidth, canvasHeight, canvasHeight/5/12, canvasHeight/5, canvasHeight/5/11);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        ball.x = canvasWidth/2;
        ball.y = canvasHeight/2;
        ball.speed = gameSet.BALL_INITIAL_SPEED;
        ball.radius = gameSet.BALL_RADIUS;
        ball.angle = 20;
        ball.color = gameSet.BALL_COLOR;

        player1.x = gameSet.PLAYER_SPACING;
        player1.y = canvasHeight/2;
        player1.width = gameSet.PLAYER_WIDTH;
        player1.height = gameSet.PLAYER_HEIGHT;
        player1.speed = gameSet.PLAYER_SPEED;
        player1.gravity = gameSet.PLAYER_GRAVITY;
        player1.upAccel = false;
        player1.downAccel = false;
        player1.color = gameSet.PLAYERS_COLOR;

        player2.x = canvasWidth - gameSet.PLAYER_SPACING - gameSet.PLAYER_WIDTH;
        player2.y = canvasHeight/2;
        player2.width = gameSet.PLAYER_WIDTH;
        player2.height = gameSet.PLAYER_HEIGHT;
        player2.speed = gameSet.PLAYER_SPEED;
        player2.gravity = gameSet.PLAYER_GRAVITY;
        player2.upAccel = false;
        player2.downAccel = false;
        player2.color = gameSet.PLAYERS_COLOR;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();

        canvas.drawColor(Color.BLACK);

        paint.setColor(ball.color);
        canvas.drawCircle((float)ball.x,(float) ball.y, (float)ball.radius, paint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //this.canvasHeight = h;
        //this.canvasWidth = w;

        gameSet.GAME_WIDTH = canvasWidth;
        gameSet.GAME_HEIGHT = canvasHeight;
        gameSet.PLAYER_HEIGHT = canvasHeight/5;
        gameSet.PLAYER_WIDTH = canvasHeight/5/12;
        gameSet.PLAYER_SECTION_HEIGHT = canvasHeight/5/11;

        super.onSizeChanged(w, h, oldw, oldh);
    }

}

