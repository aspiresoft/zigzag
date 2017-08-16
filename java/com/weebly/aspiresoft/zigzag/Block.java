package com.weebly.aspiresoft.zigzag;
import android.graphics.Canvas;
import android.graphics.RectF;

public class Block
{
    private static final int DELAY = 5000;

    public float x, y, width, height;
    public int id, number;
    public boolean direction;
    public boolean alive, dead, checked, gavePoint;
    public  long time;
    private int  index;

    public Block (float x, float y, int id, int number, boolean direction)
    {
        this.x = x;
        this.y = y;
        this.id = id;
        this.number = number;

        width = Game.WIDTH;
        height = Game.WIDTH;
        alive = true;
        this.direction = direction;
        gavePoint = false;
        checked = false;
        time = 0;
        index = Engine.blockIndexFinder(number);
    }

    public void update ()
    {
        if (!alive && !dead)
        {
            if (System.currentTimeMillis() - time > DELAY)
            {
                dead = true;
            }
            if (!(x + GamePanel.InvertX < -Game.WIDTH || x + GamePanel.InvertX > Game.SCREEN_HEIGHT ||
                    (y + GamePanel.InvertY < -Game.WIDTH || (y + GamePanel.InvertY > Game.SCREEN_WIDTH))))
            {
                y -= 6 * Game.DENSTIY;
                if (direction)
                {
                    x += 4 * Game.DENSTIY;
                }
                else
                {
                    x -= 4 * Game.DENSTIY;
                }

            }
            else
            {
                dead = true;
            }
        }
    }

    public void render (Canvas canvas)
    {
        canvas.drawBitmap(Game.BLOCK_IMAGES[index], null, new RectF(x + GamePanel.InvertX, y + GamePanel.InvertY, x + width + GamePanel.InvertX, y + height + GamePanel.InvertY), null);
    }
}
