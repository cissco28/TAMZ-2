package com.example.pong_app;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class GameThread extends Thread{

    private int fps;
    private double avgFps;
    private GameView gameView;
    private SurfaceHolder surfaceHolder;
    private boolean running;
    private static Canvas canvas;
    private boolean limitFPS;
    long targetTime;

    public GameThread(SurfaceHolder sh, GameView gv, int FPS, boolean lFPS){
        super();
        this.surfaceHolder = sh;
        this.gameView = gv;
        this.fps = FPS;
        this. limitFPS = lFPS;
    }

    @Override
    public void run() {
        long startTime;
        long timeMillis;
        long waitTime;
        long totalTime = 0;
        int frameCount = 0;
        if(limitFPS) {
            targetTime = 1000 / fps;
        }

        while (running) {
            startTime = System.nanoTime();
            canvas = null;

            try {
                this.gameView.update(startTime);
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (canvas) {
                    //this.gameView.draw(canvas);
                    this.gameView.redraw(canvas);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (canvas != null) {
                    try {
                        this.surfaceHolder.unlockCanvasAndPost(canvas);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            //------------- FPS -------------------------------
            totalTime += System.nanoTime() - startTime;
            frameCount++;

            if(totalTime >= 1000_000_000){
            //if (limitFPS){
                //if (frameCount == fps) {
                    avgFps = 1000 / (( totalTime / frameCount) / 1000000);
                    frameCount = 0;
                    totalTime = 0;
                    gameView.setAvgFPS(avgFps);
                    System.out.println(avgFps);
                //}
            //}
            }



            //--------- sleep ----------------------------------------------------
            if(limitFPS) {
                timeMillis = (System.nanoTime() - startTime) / 1000000;
                //System.out.println(timeMillis);
                //waitTime =  timeMillis - targetTime;
                waitTime = targetTime - timeMillis;

                /*
                if(waitTime < 10){
                    waitTime = 10;
                }*/

                if (waitTime > 0) {
                    try {
                        this.sleep(waitTime);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            //super.run();
        }
    }

    public void setRunning(boolean r){
        this.running = r;
    }

}
