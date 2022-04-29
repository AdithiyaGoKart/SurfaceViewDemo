package com.example.surfaceviewdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    GameSurface gameSurface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        gameSurface = new GameSurface(this);
        setContentView(gameSurface);
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameSurface.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameSurface.resume();
    }

    public class GameSurface extends SurfaceView implements Runnable, SensorEventListener{

        Thread gameThread;
        SurfaceHolder holder;
        volatile boolean running = false;
        Bitmap ball;
        int ballX = 0;
        int flip = 1;
        Paint paintProperty;

        int screenWidth;
        int screenHeight;

        public GameSurface (Context context)
        {
            super(context);

            holder = getHolder();
            ball = BitmapFactory.decodeResource(getResources(), R.drawable.ball);

            Display screenDisplay = getWindowManager().getDefaultDisplay();
            Point sizeOfSceen = new Point();
            screenDisplay.getSize(sizeOfSceen);
            screenWidth = sizeOfSceen.x;
            screenHeight = sizeOfSceen.y;

            paintProperty = new Paint();
        }


        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }

        @Override
        public void run() {

            while (running)
            {
                if (!holder.getSurface().isValid())
                    continue;
                Canvas canvas = holder.lockCanvas();
                canvas.drawRGB(255, 0, 255);
                canvas.drawBitmap(ball, ((screenWidth/2))-ball.getWidth()/2+ballX, (screenHeight/2)-ball.getHeight(), null);

                if (ballX == screenWidth/2-ball.getWidth()/2 || ballX == -1 * screenWidth / 2 + ball.getWidth() /2){
                    flip *= -1;
                }
                ballX += flip;
                holder.unlockCanvasAndPost(canvas);
            }
        }

        public void resume()
        {
            running = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        public void pause()
        {
            running = false;
            while (true){
                try {
                    gameThread.join();
                }
                catch(InterruptedException e){

                }
            }
        }
    }
}