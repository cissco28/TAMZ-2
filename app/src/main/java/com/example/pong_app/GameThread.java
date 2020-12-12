package com.example.pong_app;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class GameThread extends Thread{

    private int fps = 60;
    private double avgFps;
    private GameView gameView;
    private SurfaceHolder surfaceHolder;
    private boolean running;
    public static Canvas canvas;

    public GameThread(SurfaceHolder sh, GameView gv){
        super();
        this.surfaceHolder = sh;
        this.gameView = gv;
    }

    @Override
    public void run() {
        long startTime;
        long timeMillis;
        long waitTime;
        long totalTime = 0;
        int frameCount = 0;
        long targetTime = 1000 / fps;

        while (running) {
            startTime = System.nanoTime();
            canvas = null;

            try {
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (canvas) {
                    this.gameView.update(startTime);
                    this.gameView.draw(canvas);
                    //this.gameView.drawCanvas(canvas);
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

            timeMillis = (System.nanoTime() - startTime) / 1000000;
            waitTime =  timeMillis - targetTime;

            if(waitTime < 1){
                waitTime = 1;
            }

            /*
            try {
                this.sleep(waitTime);
            } catch (Exception e) {
                e.printStackTrace();
            }*/

            totalTime += System.nanoTime() - startTime;
            frameCount++;

            if (frameCount == fps) {
                avgFps = 1000 / ((totalTime / frameCount) / 1000000);
                frameCount = 0;
                totalTime = 0;
                gameView.setAvgFPS(avgFps);
                System.out.println(avgFps);
            }

            super.run();
        }
    }

    public void setRunning(boolean r){
        this.running = r;
    }

}
