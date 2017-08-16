package com.weebly.aspiresoft.zigzag;
import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

public class MainThread extends Thread
{
    private static final String TAG = MainThread.class.getSimpleName();

    private final static int MAX_FPS         = 40;
    private final static int MAX_FRAME_SKIPS = 5;
    private final static int FRAME_PERIOD    = 1000 / MAX_FPS;

    private SurfaceHolder surfaceHolder;
    private GamePanel     gamePanel;

    private boolean running;

    public void setRunning (boolean running)
    {
        this.running = running;
    }

    public boolean getRunning ()
    {
        return running;
    }

    public MainThread (SurfaceHolder surfaceHolder, GamePanel gamePanel)
    {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gamePanel = gamePanel;
    }

    @Override
    public void run ()
    {
        Canvas canvas;
        Log.d(TAG, "Starting game loop");

        long beginTime;
        long timeDiff;
        int  sleepTime;
        int  framesSkipped;

        sleepTime = 0;

        while (running)
        {
            canvas = null;
            try
            {
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder)
                {
                    beginTime = System.currentTimeMillis();
                    framesSkipped = 0;
                    this.gamePanel.update();
                    this.gamePanel.render(canvas);
                    timeDiff = System.currentTimeMillis() - beginTime;
                    sleepTime = (int) (FRAME_PERIOD - timeDiff);

                    if (sleepTime > 0)
                    {
                        try
                        {
                            Thread.sleep(sleepTime);
                        }
                        catch (InterruptedException e)
                        {
                        }
                    }

                    while (sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS)
                    {
                        this.gamePanel.update();
                        sleepTime += FRAME_PERIOD;
                        framesSkipped++;
                    }
                }
            }
            finally
            {
                if (canvas != null)
                {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }
}
