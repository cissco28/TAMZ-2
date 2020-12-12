package com.example.pong_app;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
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
import static com.example.pong_app.GameActivity.NEW_GAME;
import static com.example.pong_app.GameActivity.RESUME_GAME;
import static com.example.pong_app.GameActivity.TWO_PLAYERS_GAME;
import static com.example.pong_app.GameSet.BALL_SPEED_INCREASE;
import static com.example.pong_app.GameSet.MAX_BALL_SPEED;
import static com.example.pong_app.GameSet.PLAYERDOWN_SECTION_ANGLES;
import static com.example.pong_app.GameSet.PLAYERUP_SECTION_ANGLES;
import static com.example.pong_app.GameSet.PLAYER_GRAVITY;
import static com.example.pong_app.GameSet.PLAYER_SECTIONS;
import static com.example.pong_app.GameSet.PLAYER_SECTION_ANGLES;
import static com.example.pong_app.GameSet.PLAYER_SPEED;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private static final Random random = new Random();
    Drawable background;
    float yDiff;
    public Ball ball;
    public Player player1;
    public Player player2;
    private GameThread gameThread;
    private SurfaceHolder surfaceHolder;
    private boolean running = true;
    private boolean paused;
    private boolean drawNet, ballSpeedIncrease;
    private boolean drawFPS;
    double avgFPS;
    Paint paint;
    int canvasWidth, canvasHeight;
    long SCORE_COLOR, NET_COLOR, BACKGROUND_COLOR;
    double BALL_INITIAL_SPEED;
    double DIFFICULTY;
    int MAX_SCORE, PLAYER_SPACING;
    float y1left,y2left, y1right,y2right;
    private int gameMode;
    private long previousTime;
    private int gameState;

    public GameView(Context context, Activity activity) {
        super(context);

        this.setFocusable(true);

        this.getHolder().addCallback(this);

        gameState = activity.getIntent().getIntExtra("game_state", 0);

    }


    public GameView(Context context, AttributeSet attrs) {
        super(context);
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context);
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void init(Context context){

        //System.out.println(canvasWidth);
        //System.out.println(canvasHeight);

        drawFPS = false;
        avgFPS = 0;

        surfaceHolder = getHolder();

        paint = new Paint();

        ball = new Ball();
        player1 = new Player(true);
        player2 = new Player(false);

        //PreferenceManager.setDefaultValues(context, R.xml.root_preferences, false);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        BALL_INITIAL_SPEED = sharedPreferences.getInt("ballSpeed", 500);
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

            int bg = getResources().getIdentifier(sharedPreferences.getString("backgroundImage", "bg_space_720"),
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

        System.out.println(player1.x);
        System.out.println(player1.y);
        System.out.println(player1.color);
        System.out.println(player1.width);
        System.out.println(player1.height);

        System.out.println(player2.x);
        System.out.println(player2.y);
        System.out.println(player2.color);
        System.out.println(player2.width);
        System.out.println(player2.height);


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

        player1.x = PLAYER_SPACING;
        player1.y = canvasHeight/2;

        player2.x = canvasWidth - PLAYER_SPACING - player1.width;
        player2.y = canvasHeight/2;
    }

    protected void prepareResumeGame(){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("save", MODE_PRIVATE);
        if(sharedPreferences != null) {
            gameMode = sharedPreferences.getInt("game_mode", 1);
            previousTime = sharedPreferences.getLong("previous_time", 0);
            ball.x = sharedPreferences.getFloat("ball_x", canvasWidth/2);
            ball.y = sharedPreferences.getFloat("ball_y", canvasHeight/2);
            ball.speed = sharedPreferences.getFloat("ball_speed", 500);
            ball.angle = sharedPreferences.getFloat("ball_angle", 20);
            player1.y = sharedPreferences.getFloat("player1_y", canvasHeight/2);
            player1.score = sharedPreferences.getInt("player1_score", 0);
            player2.y = sharedPreferences.getFloat("player2_y", canvasHeight/2);
            player2.score = sharedPreferences.getInt("player2_score", 0);
        }
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);


        if (surfaceHolder.getSurface().isValid()) {
            //canvas = surfaceHolder.lockCanvas();

            paint = new Paint();


            if(BACKGROUND_COLOR == -1 ){            // && background != null
                background.draw(canvas);
            }
            else{
                canvas.drawColor((int)BACKGROUND_COLOR);
                //paint.setColor(Color.BLACK);
                //canvas.drawRect((float)0, (float)0, (float)canvasWidth, (float)canvasHeight, paint);
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

            //surfaceHolder.unlockCanvasAndPost(canvas);
        }

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
        for (int i = 0; i < 11; i++) {
            boolean ballHitCurrentSection = ball.y  <= player.y + j * player.height / 11;
            if (ballHitCurrentSection) {
                if(player.movingDown){
                    if(player.left) {
                        ball.angle = PLAYERUP_SECTION_ANGLES[i] * (-1);
                    }
                    else{
                        ball.angle = PLAYERUP_SECTION_ANGLES[i];
                    }
                }
                else if(player.movingUp){
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


        if(ball.x < player.x + player.width && ball.y > player.y + player.height){
            ball.y = player.y + player.height + ball.radius + 1;
        }
        else if(ball.x < player.x + player.width && ball.y < player.y){
            ball.y = player.y - ball.radius - 1;
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
        gameThread.setRunning(false);
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }
    }


    public void resume() {
        gameThread.setRunning(false);
        gameThread = new GameThread(surfaceHolder, this);
        gameThread.start();
    }

    protected void update(long currentTime){

        //System.out.println(currentTime);

        if(paused){
            pause();
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
            resetBall();
        }

        else if (ball.x >= canvasWidth) {
            //oppScore.play();
            player1.score++;
            resetBall();
        }


        //-------------------- kontrola kolizie micku s vrchnym a spodnym okrajom ------------------
        if (ball.y + ball.radius >= canvasHeight) {
            //wall.play();
            ball.angle = ball.angle * -1;
            ball.y = canvasHeight - ball.radius - 1;
        }
        else if (ball.y - ball.radius <= 0) {
            //wall.play();
            ball.angle = ball.angle * -1;
            ball.y = ball.radius + 1;
        }

        //---------------------------- kontrola skore -----------------------------------------------

        if (player1.score > MAX_SCORE || player2.score > MAX_SCORE) {

            //clearInterval(loop);
            if(player1.score > MAX_SCORE){
                //document.getElementById('state').innerHTML = "You Won !!!";
            }
            else{
                //document.getElementById('state').innerHTML = "You lost !!!";
            }
            //document.getElementById("push-button11").disabled = true;
            //document.getElementById('dialog-2').show();
        }


        if(ball.x < canvasWidth / 2){
            if(collision(ball, player1)){
                //hit.play();
                playerBallCollision(player1);
            }

        }
        else{
            if(collision(ball, player2)){
                //hit.play();
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
            player1.move();
        }
        else{
            player1.move();
            player2.move();
        }


        double secondsElapsed = (currentTime - previousTime) / 1_000_000_000.0;


        //System.out.println(secondsElapsed);

        if (secondsElapsed > 0.0333) {
            secondsElapsed = 0.0333;
        }

        ball.move(secondsElapsed);

        //System.out.println(ball.x);
        //System.out.println(ball.y);

        player1.upAccel = false;
        player1.downAccel = false;

        player2.upAccel = false;
        player2.downAccel = false;

        previousTime = currentTime;
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



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean left = event.getX() < canvasWidth/2;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                if(!left && gameMode == 2)
                    y1right = event.getY();
                else{
                    y1left = event.getY();
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
                if(!left && gameMode == 2){
                    y2right = event.getY();
                    yDiff = y1right - y2right;
                    y1right = y2right;
                    if (yDiff > 1) {
                        player2.upAccel = true;
                        player2.downAccel = false;
                    } else if (yDiff < -1) {
                        player2.upAccel = false;
                        player2.downAccel = true;
                    }
                }
                else{
                    y2left = event.getY();
                    yDiff = y1left - y2left;
                    y1left = y2left;
                    if (yDiff > 1) {
                        player1.upAccel = true;
                        player1.downAccel = false;
                    } else if (yDiff < -1) {
                        player1.upAccel = false;
                        player1.downAccel = true;
                    }
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                if(!left && gameMode == 2){
                    player2.upAccel = false;
                    player2.downAccel = false;
                }
                else {
                    player1.upAccel = false;
                    player1.downAccel = false;
                }
                break;
            }
        }
        return true;
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

        this.gameThread = new GameThread(holder,this);
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
            }
            //retry= true;
        }

    }

    public int getGameMode(){
        return this.gameMode;
    }

    public long getPreviousTime() {
        return this.previousTime;
    }


    public void setAvgFPS(double fps){
        this.avgFPS = fps;
    }
}

