package com.example.test3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

class TestSurfaceView extends SurfaceView implements SurfaceHolder.Callback{

    class DrawThread extends Thread {
        private SurfaceHolder surfaceHolder;
        Paint blue = new Paint();
        Paint red = new Paint();
        private volatile boolean running = true;

        public DrawThread(Context context, SurfaceHolder surfaceHolder) {
            this.surfaceHolder = surfaceHolder;
            blue.setColor(Color.BLUE);
            red.setColor(Color.RED);
        }

        public void requestStop() {
            running = false;
        }

        @Override
        public void run() {
            while(running) {
                if (movement == 200 || movement == -200) {
                    movement = 0;
                }
                Canvas canvas = surfaceHolder.lockCanvas();
                if (touched) {
                    if (x > getWidth() / 2 + movement)
                        movement += 20;
                    else
                        movement -= 20;
                    touched = false;
                }

                if (canvas != null) {
                    try {
                        canvas.drawRect(0,0,canvas.getWidth()/2 + movement,canvas.getHeight(),blue);
                        canvas.drawRect(canvas.getWidth()/2 + movement, 0, canvas.getWidth(), canvas.getHeight(),red);
                    } finally {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }

    DrawThread dt;
    float x, y;
    float movement = 0;
    boolean touched = false;


    public TestSurfaceView(Context context) {
        super(context);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        dt = new DrawThread(getContext(), getHolder());
        dt.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        dt.requestStop();
        boolean retry = true;
        while(retry) {
            try {
                dt.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.x = event.getX();
        this.y = event.getY();
        touched = true;
        return false;
    }
}
