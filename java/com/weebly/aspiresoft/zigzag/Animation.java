package com.weebly.aspiresoft.zigzag;
import android.graphics.Canvas;
import android.graphics.RectF;

public class Animation
{
    public long    duration;
    public boolean shouldAnimate;
    public float   x, y, width, height;
    public float currentX, currentY;
    public long    lastTime;
    public long    interval;
    public float   frameCount;
    public int     currentFrame;
    public boolean loop;

    public Animation (float x, float y, float width, float height, long duration, boolean loop)
    {
        this.duration = duration;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.loop = loop;
        currentX = x;
        currentY = y;
        shouldAnimate = true;
        lastTime = System.currentTimeMillis();
        frameCount = Game.EXP_1.length;
        interval = (long) (duration / frameCount);
        currentFrame = 0;
    }


    public void update ()
    {
        if (shouldAnimate)
        {
            if (System.currentTimeMillis() - lastTime >= interval)
            {
                lastTime = System.currentTimeMillis();
                currentFrame++;
                if (currentFrame >= frameCount)
                {
                    if (loop)
                    {
                        currentFrame = 0;
                    }
                    else
                    {
                        shouldAnimate = false;
                    }
                }
            }
        }
    }

    public void draw (Canvas canvas)
    {
        if (shouldAnimate)
        {
            canvas.drawBitmap(Game.EXP_1[currentFrame], null, new RectF(x, y, x + width, y + height), null);
        }
    }

}