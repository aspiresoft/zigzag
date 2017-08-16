package com.weebly.aspiresoft.zigzag;

import android.graphics.Canvas;
import android.graphics.RectF;

public class Ball
{
    public static final long DELAY = 1500;
    public float x, y, width, height;
    public boolean alive;
    public boolean shouldRender;
    public boolean direction;
    public long timer;
    public int currentBlockNumber;

    public Ball (float x, float y)
    {
        this.x = x;
        this.y = y;
        this.width = Game.WIDTH / 4;
        this.height = Game.WIDTH / 4;

        init();

    }

    public void update ()
    {
        if (alive)
        {
            timer = System.currentTimeMillis();
        }
        else
        {
            if (System.currentTimeMillis() - timer <= DELAY)
            {
                y += GamePanel.BALL_SPEED * GamePanel.GAME_SPEED * 6;
            }
            else
            {
                shouldRender = false;
            }
        }
    }

    public void render (Canvas canvas)
    {
        if (shouldRender)
        {
            canvas.drawBitmap(Game.BALL_IMAGE, null, new RectF(x, y, x + width, y + height), null);
        }
    }

    public void init ()
    {
        alive = true;
        shouldRender = true;
        direction = true;
        currentBlockNumber = 0;
    }
}
