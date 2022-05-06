package com.example.surfaceviewdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

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

    public class GameSurface extends SurfaceView implements Runnable, SensorEventListener
    {
        Thread gameThread;
        SurfaceHolder holder;
        volatile boolean running = false;
        Bitmap ball;
        int ballX = 0;
        int ballY = 0;
        int flipX = 1;
        int flipY = 1;
        int screenWidth;
        int screenHeight;
        int red = 255;
        int green = 0;
        int blue = 0;
        //Paint paintProperty;

        public GameSurface(Context context)
        {
            super(context);

            holder = getHolder();
            ball = BitmapFactory.decodeResource(getResources(), R.drawable.ball);

            Display screenDisplay = getWindowManager().getDefaultDisplay();
            Point sizeOfScreen = new Point();
            screenDisplay.getSize(sizeOfScreen);
            screenWidth = sizeOfScreen.x;
            screenHeight = sizeOfScreen.y;

            SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(GameSurface.this, sensor, SensorManager.SENSOR_DELAY_NORMAL);

            //paintProperty = new Paint();
        }

        @Override
        public void run() {
            while (running)
            {
                if (holder.getSurface().isValid() == false)
                    continue;
                Canvas canvas = holder.lockCanvas();
                canvas.drawRGB(red, green, blue);
                canvas.drawBitmap(ball, (screenWidth/2)-ball.getWidth()/2 + ballX, (float) ((screenHeight/2)-ball.getHeight()/2 + ballY), null);
                increment();
                holder.unlockCanvasAndPost(canvas);

                /*
                if (ballX == screenWidth/2 - ball.getWidth()/2 || ballX == -1*screenWidth/2 + ball.getWidth()/2)
                    flipX *= -1;
                if (ballY == screenHeight/2 - ball.getHeight()/2 || ballY == -1*screenHeight/2 + ball.getHeight()/2)
                    flipY *= -1;
                ballX += flipX;
                ballY += flipY;
                 */
            }
        }


        public void increment()
        {
            if (ballX >= screenWidth/2 - ball.getWidth()/2)
            {
                ballX -= 1;
                //randomColorBackground();
            }
            else if (ballX <= -1*screenWidth/2 + ball.getWidth()/2)
            {
                ballX += 1;
                //randomColorBackground();
            }
            else
                ballX += flipX;
            if (ballY >= screenHeight/2 - ball.getHeight()/2)
            {
                ballY -= 1;
                //randomColorBackground();
            }
            else if (ballY <= -1*screenHeight/2 + ball.getHeight()/2)
            {
                ballY += 1;
                //randomColorBackground();
            }
            else
                ballY += flipY;
        }

//        public void randomColorBackground()
//        {
//            red = (int)(Math.random()*256);
//            green = (int)(Math.random()*256);
//            blue = (int)(Math.random()*256);
//        }

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            flipX = (int) (-1*sensorEvent.values[0]);
            flipY = (int) sensorEvent.values[1];
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

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
            while (true)
            {
                try
                {
                    gameThread.join();
                }
                catch (InterruptedException e)
                {

                }
            }
        }
    }
}
