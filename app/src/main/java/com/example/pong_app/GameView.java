package com.example.pong_app;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.preference.PreferenceManager;

import java.util.Random;

import static android.content.Context.MODE_PRIVATE;
import static com.example.pong_app.GameActivity.HIT_PADDLE;
import static com.example.pong_app.GameActivity.HIT_WALL;
import static com.example.pong_app.GameActivity.NEW_GAME;
import static com.example.pong_app.GameActivity.RESUME_GAME;
import static com.example.pong_app.GameActivity.SCORE_LEFT;
import static com.example.pong_app.GameActivity.SCORE_RIGHT;
import static com.example.pong_app.GameActivity.TWO_PLAYERS_GAME;
import static com.example.pong_app.GameSet.BALL_SPEED_INCREASE;
import static com.example.pong_app.GameSet.PLAYERDOWN_SECTION_ANGLES;
import static com.example.pong_app.GameSet.PLAYERUP_SECTION_ANGLES;
import static com.example.pong_app.GameSet.PLAYER_GRAVITY;
import static com.example.pong_app.GameSet.PLAYER_SECTIONS;
import static com.example.pong_app.GameSet.PLAYER_SECTION_ANGLES;
import static com.example.pong_app.GameSet.PLAYER_SPEED;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private static final Random random = new Random();
    private PhoneCallListener phoneCallListener;
    Drawable background;
    float yDiff;
    public Ball ball;
    public Player player1;
    public Player player2;
    private GameThread gameThread;
    private SurfaceHolder surfaceHolder;
    private boolean running = true;
    public boolean paused;
    private boolean drawNet, ballSpeedIncrease;
    private boolean drawFPS, limiteFPS;
    int avgFPS = 0;
    double frameTime;
    double diffTime = 0;
    int FPS, MAX_BALL_SPEED;
    Paint paint;
    int canvasWidth, canvasHeight;
    long SCORE_COLOR, NET_COLOR, BACKGROUND_COLOR;
    double BALL_INITIAL_SPEED;
    double DIFFICULTY;
    double secondsElapsed;
    int MAX_SCORE, PLAYER_SPACING;
    float y1left,y2left, y1right,y2right;
    public int gameMode;
    private long previousTime;
    private int gameState;
    boolean ballHitCurrentSection, endGame;
    long moveRightTime, moveLeftTime;
    GameActivity gameActivity;
    String winner;
    boolean up = true;
    SoundPool soundPool;

    public GameView(Context context, GameActivity activity, SoundPool sp) {
        super(context);

        this.setFocusable(true);

        this.getHolder().addCallback(this);

        gameState = activity.getIntent().getIntExtra("game_state", 0);

        gameActivity = activity;

        soundPool = sp;


    }


    public GameView(Context context, AttributeSet attrs) {
        super(context);
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context);
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void init(Context context){

        phoneCallListener = new PhoneCallListener(this);

        surfaceHolder = getHolder();

        paint = new Paint();

        ball = new Ball();
        player1 = new Player(true);
        player2 = new Player(false);

        //PreferenceManager.setDefaultValues(context, R.xml.root_preferences, false);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        limiteFPS = sharedPreferences.getBoolean("limitFPS", false);
        if(limiteFPS){
            FPS = sharedPreferences.getInt("maxFPS", 60);
            frameTime = 1/(double)FPS;
        }

        MAX_BALL_SPEED = sharedPreferences.getInt("maxBallSpeed", 1200);
        BALL_INITIAL_SPEED = sharedPreferences.getInt("ballSpeed", 300);
        //BALL_INITIAL_SPEED = 250;
        DIFFICULTY = Double.parseDouble(sharedPreferences.getString("difficulty", "0.1"));
        MAX_SCORE = sharedPreferences.getInt("maxScore", 10);
        drawNet = sharedPreferences.getBoolean("middleNet", false);
        drawFPS = sharedPreferences.getBoolean("drawFPS", false);
        ballSpeedIncrease = sharedPreferences.getBoolean("ballSpeedIncrease", true);
        SCORE_COLOR = Long.parseLong(sharedPreferences.getString("scoreColor", "FFFFFFFF"), 16);
        PLAYER_SPACING = sharedPreferences.getInt("playerSpacing", 50);

        if(drawNet){
            NET_COLOR = Long.parseLong(sharedPreferences.getString("netColor", "FFFFFFFF"), 16);
        }

        if(sharedPreferences.getBoolean("bgIsImage", true)){

            int bg = getResources().getIdentifier(sharedPreferences.getString("backgroundImage", "bg_space"),
                                                    "drawable", context.getPackageName());

            background = getResources().getDrawable(bg, null);
            background.setBounds(0,0,canvasWidth, canvasHeight);

            BACKGROUND_COLOR = -1;
        }
        else{
            BACKGROUND_COLOR = Long.parseLong(sharedPreferences.getString("backgroundColor", "00000000"), 16);
            background = null;
        }

        ball.radius = sharedPreferences.getInt("ballSize", 20);
        ball.color = Long.parseLong(sharedPreferences.getString("ballColor", "FFFFFFFF"), 16);
        ball.speed = sharedPreferences.getInt("ballSpeed", 300);

        player1.x = PLAYER_SPACING;
        player1.height = ((double)sharedPreferences.getInt("playerHeight", 25) / 100) * canvasHeight;
        player1.width =  ((double)sharedPreferences.getInt("playerWidth", 25) / 100) * player1.height;
        player1.speed = PLAYER_SPEED;
        player1.gravity = PLAYER_GRAVITY;
        player1.color = Long.parseLong(sharedPreferences.getString("player1Color", "FFFFFFFF"), 16);

        player2.width = player1.width;
        player2.x = canvasWidth - player2.width - PLAYER_SPACING;
        player2.height = player1.height;
        player2.speed = PLAYER_SPEED;
        player2.gravity = PLAYER_GRAVITY;
        player2.color = Long.parseLong(sharedPreferences.getString("player2Color", "FFFFFFFF"), 16);

        if(gameState == NEW_GAME){
            gameMode = 1;
            previousTime = 0;
            prepareNewGame();
        }
        else if(gameState == TWO_PLAYERS_GAME){
            gameMode = 2;
            previousTime = 0;
            prepareNewGame();
        }
        else if(gameState == RESUME_GAME){
            prepareResumeGame();
        }

    }

    protected void prepareNewGame(){
        resetBall();

        endGame = false;
        paused = false;

        player1.x = PLAYER_SPACING;
        player1.y = (float)canvasHeight/2;
        player1.score = 0;
        player1.moveY = (double)player1.y;

        player2.x = canvasWidth - PLAYER_SPACING - player1.width;
        player2.y = (float)canvasHeight/2;
        player2.score = 0;
        player2.moveY = (double)player2.y;

        resume();
    }

    protected void prepareResumeGame(){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("save", MODE_PRIVATE);
        if(sharedPreferences != null) {
            gameMode = sharedPreferences.getInt("game_mode", 1);
            previousTime = sharedPreferences.getLong("previous_time", 0);
            ball.x = sharedPreferences.getFloat("ball_x", (float) canvasWidth/2);
            ball.y = sharedPreferences.getFloat("ball_y", (float)canvasHeight/2);
            //ball.speed = sharedPreferences.getFloat("ball_speed", 500);
            ball.angle = sharedPreferences.getFloat("ball_angle", 20);
            player1.y = sharedPreferences.getFloat("player1_y", (float)canvasHeight/2);
            player1.score = sharedPreferences.getInt("player1_score", 0);
            player1.moveY = (double) sharedPreferences.getFloat("player1_move_y", (float)canvasHeight/2);
            player2.y = sharedPreferences.getFloat("player2_y", (float)canvasHeight/2);
            player2.score = sharedPreferences.getInt("player2_score", 0);
            player1.moveY = (double) sharedPreferences.getFloat("player2_move_y", (float)canvasHeight/2);
        }
    }

    public void redraw(Canvas canvas){
        if (surfaceHolder.getSurface().isValid()) {
            //canvas = surfaceHolder.lockCanvas();

            paint = new Paint();


            if(BACKGROUND_COLOR == -1 ){            // && background != null
                background.draw(canvas);
            }
            else{
                //canvas.drawColor((int)BACKGROUND_COLOR);
                paint.setColor(Color.BLACK);
                canvas.drawRect((float)0, (float)0, (float)canvasWidth, (float)canvasHeight, paint);
            }

            if(drawFPS){
                paint.setColor(Color.YELLOW);
                paint.setTextSize(50);
                canvas.drawText(String.valueOf(avgFPS), 1, 51, paint);
            }

            //----- NET
            if(drawNet){
                paint.setColor((int)NET_COLOR);
                paint.setStrokeWidth(5);
                double y = 0;
                double x = canvasWidth/2;
                double h = canvasHeight/15;
                while(true){
                    if(y + 2*h < canvasHeight){
                        canvas.drawLine((float)x, (float)y, (float)x, (float)y + (float)h, paint);
                    }
                    else{
                        canvas.drawLine((float)x, (float)y, (float)x, canvasHeight, paint);
                        break;
                    }
                    y += 2*h;
                }
            }

            //----- BALL
            paint.setColor((int)ball.color);
            canvas.drawCircle((float)ball.x,(float) ball.y, (float)ball.radius, paint);

            //----- LEFT PLAYER
            paint.setColor((int)player1.color);
            canvas.drawRect((float)player1.x, (float)player1.y, (float)player1.x + (float)player1.width, (float)player1.y + (float)player1.height, paint);

            //----- RIGHT PLAYER
            paint.setColor((int)player2.color);
            canvas.drawRect((float)player2.x, (float)player2.y, (float)player2.x + (float)player1.width, (float)player2.y + (float)player1.height, paint);

            //----- SCORE TEXT
            paint.setColor((int)SCORE_COLOR);
            paint.setTextSize(60);
            canvas.drawText(String.valueOf(player1.score), canvasWidth/4, canvasHeight/5, paint);
            canvas.drawText(String.valueOf(player2.score), 3*canvasWidth/4, canvasHeight/5, paint);

            if(endGame){
                //paint.setColor(SCORE_COLOR);
                canvas.drawText(winner + " player win !", canvasWidth/3, canvasHeight/2, paint);
                canvas.drawText("Tap on screen to start new game", canvasWidth/3 - 200, canvasHeight/2 + 100, paint);
                canvas.drawText("Tap on back button to go to menu", canvasWidth/3 - 200, canvasHeight/2 + 200, paint);
            }
            else if(paused){
                //paint.setColor(SCORE_COLOR);
                canvas.drawText("Game paused !", canvasWidth/3, canvasHeight/2, paint);
                canvas.drawText("Tap on screen to continue", canvasWidth/3 - 50, canvasHeight/2 + 100, paint);
                canvas.drawText("Tap on back button to go to menu", canvasWidth/3 - 150, canvasHeight/2 + 200, paint);
            }

            //surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
    }

    protected boolean collision(Ball b, Player p){
        return b.x - b.radius <= p.x + p.width &&
                b.x +  b.radius >= p.x &&
                b.y + b.radius >= p.y &&
                b.y - b.radius <= p.y + p.height;
    }

    protected void playerBallCollision(Player player){

        int j = 1;
        for (int i = 0; i < PLAYER_SECTIONS; i++) {
            if(j == PLAYER_SECTIONS){
                ballHitCurrentSection = ball.y  <= player.y + player.height + ball.radius;
            }
            else {
                ballHitCurrentSection = ball.y  <= player.y + j * player.height / PLAYER_SECTIONS;
            }
            if (ballHitCurrentSection) {
                if(player.movingUp){
                    if(player.left) {
                        ball.angle = PLAYERUP_SECTION_ANGLES[i] * (-1);
                    }
                    else{
                        ball.angle = PLAYERUP_SECTION_ANGLES[i];
                    }
                }
                else if(player.movingDown){
                    if(player.left) {
                        ball.angle = PLAYERDOWN_SECTION_ANGLES[i];
                    }
                    else{
                        ball.angle = PLAYERDOWN_SECTION_ANGLES[i] * (-1);
                    }
                }
                else{
                    if(player.left) {
                        ball.angle = PLAYER_SECTION_ANGLES[i];
                    }
                    else{
                        ball.angle = PLAYER_SECTION_ANGLES[i] * (-1);
                    }
                }
                break;
            }
            j++;
        }


        if(ballSpeedIncrease) {
            if (ball.speed < MAX_BALL_SPEED && ball.speed > MAX_BALL_SPEED * (-1)) {
                ball.speed = ball.speed * BALL_SPEED_INCREASE;
            } else {
                ball.speed = ball.speed * (-1);
            }
        }
        else{
            ball.speed = ball.speed * (-1);
        }


        if(ball.x < player.x + player.width && ball.y > player.y + player.height){
            ball.y = player.y + player.height + ball.radius + 1;
            if(ball.x < player.width/2){
                ball.angle = 80 * Math.PI / 180;
            }
        }
        else if(ball.x < player.x + player.width && ball.y < player.y){
            ball.y = player.y - ball.radius - 1;
            if(ball.x < player.width/2){
                ball.angle = -80 * Math.PI / 180;
            }
        }
        else{
            if(player.left) {
                ball.x = player.x + player.width + ball.radius + 1;
            }
            else{
                ball.x = player.x - ball.radius - 1;
            }
        }
    }

    public void pause() {
        paused = true;
        up = false;
        gameThread.setRunning(false);
        boolean retry = true;
        while(retry) {
            try {

                // Parent thread must wait until the end of GameThread.
                this.gameThread.join();
                retry = false;
            }catch(InterruptedException e)  {
                e.printStackTrace();
                retry = true;
            }

        }
        previousTime = 0;
    }

    public void pauseGame(){
        pause();
        /*
        if(surfaceHolder.lockCanvas() == null){

        }*/
    }

    private void winPlayer(boolean left){
        resetBall();
        endGame = true;
        if(left){
            winner = "Left";
        }
        else {
            winner = "Right";
        }

        gameThread.setRunning(false);
        previousTime = 0;
    }

    public void endGame(){
        gameActivity.endGame();
    }


    public void resume() {
        endGame = false;
        paused = false;
        gameThread = new GameThread(surfaceHolder, this, FPS, limiteFPS);
        gameThread.setRunning(true);
        gameThread.start();
    }

    protected void update(long currentTime){

        if(paused || endGame){
            return;
        }

        if (previousTime == 0) {
            previousTime = currentTime;
            return;
        }


        //------------------- kontrola pozicie protihraca ------------------------------
        if (player2.y < 0)
            player2.y = 0;
        else if (player2.y + player2.height > canvasHeight)
            player2.y = canvasHeight - player2.height;

        // -------------------- kontrola kolizie hraca s hornym a dolnym okrajom --------------------------
        if (player1.y < 0)
            player1.y = 0;
        if (player1.y > canvasHeight - player1.height)
            player1.y = canvasHeight - player1.height;


        //------------------------ kontrola kolizie micku s lavym a pravym okrajom -----------------------
        if (ball.x <= 0) {
            //userScore.play();
            player2.score++;
            soundPool.play(SCORE_RIGHT, 1.0f,1.0f,1,0,1.0f);
            resetBall();
        }

        else if (ball.x >= canvasWidth) {
            //oppScore.play();
            soundPool.play(SCORE_LEFT, 1.0f,1.0f,1,0,1.0f);
            player1.score++;
            resetBall();
        }


        //-------------------- kontrola kolizie micku s vrchnym a spodnym okrajom ------------------
        if (ball.y + ball.radius >= canvasHeight) {
            soundPool.play(HIT_WALL, 1.0f,1.0f,1,0,1.0f);
            ball.angle = ball.angle * -1;
            ball.y = canvasHeight - ball.radius - 1;
        }
        else if (ball.y - ball.radius <= 0) {
            soundPool.play(HIT_WALL, 1.0f,1.0f,1,0,1.0f);
            ball.angle = ball.angle * -1;
            ball.y = ball.radius + 1;
        }

        //---------------------------- kontrola skore -----------------------------------------------

        if (player1.score >= MAX_SCORE) {
            winPlayer(true);
        }
        if(player2.score >= MAX_SCORE){
            winPlayer(false);
        }


        if(ball.x < canvasWidth / 2){
            if(collision(ball, player1)){
                soundPool.play(HIT_PADDLE, 1.0f,1.0f,1,0,1.0f);
                playerBallCollision(player1);
            }

        }
        else{
            if(collision(ball, player2)){
                soundPool.play(HIT_PADDLE, 1.0f,1.0f,1,0,1.0f);
                playerBallCollision(player2);
            }
        }


        if(gameMode == 1){
            if(ball.x >= canvasWidth / 4){
                player2.y += ((ball.y - (player2.y + player2.height/2)))* (DIFFICULTY*0.75);
            }
            else{
                player2.y += ((ball.y - (player2.y + player2.height/2)))* DIFFICULTY;
            }
            //player1.move();
        }
        else{
            //player1.move();
            //player2.move();
        }

        diffTime = (System.nanoTime() - previousTime) / 1_000_000_000.0;
        ball.move(diffTime);
        previousTime = System.nanoTime();


        if(previousTime - moveLeftTime > 30_000_000){
            player1.movingUp = false;
            player1.movingDown = false;
        }

        if(previousTime - moveRightTime > 30_000_000){
            player2.movingUp = false;
            player2.movingDown = false;
        }
    }

    public void resetBall(){
        boolean towardsOpponent = random.nextBoolean();
        double initialAngle = PLAYER_SECTION_ANGLES[random.nextInt(PLAYER_SECTIONS - 1)];

        ball.speed = towardsOpponent ? -BALL_INITIAL_SPEED : BALL_INITIAL_SPEED;
        ball.angle = towardsOpponent ? -initialAngle : initialAngle;
        ball.x = canvasWidth / 2;
        ball.y = canvasHeight / 2;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public GameThread getGameThread(){
        return this.gameThread;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        canvasWidth = getWidth();
        canvasHeight = getHeight();

        init(getContext());

        //holder.setFixedSize(canvasWidth, canvasHeight);

        this.gameThread = new GameThread(holder,this, FPS, limiteFPS);
        this.gameThread.setRunning(true);
        this.gameThread.start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        boolean retry= true;
        this.gameThread.setRunning(false);
        while(retry) {
            try {

                // Parent thread must wait until the end of GameThread.
                this.gameThread.join();
                retry = false;
            }catch(InterruptedException e)  {
                e.printStackTrace();
                retry = true;
            }
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!paused && !endGame) {
            boolean left = event.getX() < canvasWidth / 2;
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN: {
                    if (!left && gameMode == 2)
                        y1right = event.getY();
                    else {
                        y1left = event.getY();
                    }
                    break;
                }

                case MotionEvent.ACTION_MOVE: {
                    if (!left && gameMode == 2) {

                        y2right = event.getY();
                        yDiff = y2right - y1right;
                        if (yDiff < 0) {
                            player2.movingDown = false;
                            player2.movingUp = true;
                        } else if (yDiff > 0) {
                            player2.movingUp = false;
                            player2.movingDown = true;
                        }
                        y1right = y2right;

                        moveRightTime = System.nanoTime();

                        player2.moveDiff(yDiff);

                    } else {

                        y2left = event.getY();
                        yDiff = y2left - y1left;
                        if (yDiff < 0) {
                            player1.movingDown = false;
                            player1.movingUp = true;
                        } else if (yDiff > 0) {
                            player1.movingUp = false;
                            player1.movingDown = true;
                        }
                        y1left = y2left;

                        moveLeftTime = System.nanoTime();

                        player1.moveDiff(yDiff);

                    }
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    if (!left && gameMode == 2) {
                        player2.movingUp = false;
                        player2.movingDown = false;
                    } else {
                        player1.movingUp = false;
                        player1.movingDown = false;
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
                        if(endGame){
                            prepareNewGame();
                        }
                        else {
                            resume();
                        }
                    }
                    up = false;
                    break;
                }
            }
        }
        return true;
    }

    public int getGameMode(){
        return this.gameMode;
    }

    public long getPreviousTime() {
        return this.previousTime;
    }


    public void setAvgFPS(double fps){
        this.avgFPS = (int)fps;
    }
}

