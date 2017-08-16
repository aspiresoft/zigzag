package com.weebly.aspiresoft.zigzag;
import android.graphics.Point;
import android.graphics.PointF;

public abstract class Engine
{
    public static boolean isInRange (float x1, float y1, float x2, float y2, float radius)
    {
        if ((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1) <= radius * radius)
        {
            return true;
        }
        return false;
    }

    public static boolean isInRhombus (float x, float y, PointF a, PointF b, PointF c, PointF d)
    {
        PointF p          = new PointF(x, y);
        int    topToRight = checkIfPointIsBetweenTwoPoints(a, b, p);
        int    RightToBot = checkIfPointIsBetweenTwoPoints(b, c, p);
        int    botToLeft  = checkIfPointIsBetweenTwoPoints(c, d, p);
        int    leftToTop  = checkIfPointIsBetweenTwoPoints(d, a, p);
        p = null;
        if (topToRight >= 0 && RightToBot >= 0 && botToLeft >= 0 && leftToTop >= 0)
        {
            return true;
        }
        return false;
    }

    public static int checkIfPointIsBetweenTwoPoints (PointF a, PointF b, PointF c)
    {
        float term = ((b.x - a.x) * (c.y - a.y)) - ((b.y - a.y) * (c.x - a.x));

        if (term > 0)
        {
            return 1;
        }
        if (term < 0)
        {
            return -1;
        }
        return 0;
    }

    public static Point isoTwotwoD (Point p)
    {
        Point temp = new Point(0, 0);
        temp.x = (2 * p.y + p.x) / 2;
        temp.y = (2 * p.y - p.x) / 2;
        return temp;
    }

    public static Point twoDtoIso (Point p)
    {
        Point temp = new Point(0, 0);
        temp.x = p.x - p.y;
        temp.y = (p.x + p.y) / 2;
        return temp;
    }

    public static int blockIndexFinder (int number)
    {
        if (number < 25)
        {
            return 0;
        }
        else if (number < 50)
        {
            return 1;
        }
        else if (number < 75)
        {
            return 2;
        }
        else if (number < 100)
        {
            return 3;
        }
        else if (number < 150)
        {
            return 4;
        }
        else if (number < 200)
        {
            return 5;
        }
        else if (number < 250)
        {
            return 6;
        }
        else if (number < 300)
        {
            return 7;
        }
        else if (number < 400)
        {
            return 8;
        }
        else
        {
            return 9;
        }
    }

    public static String zeroFiller (int number)
    {
        if (number < 10)
        {
            return "000" + number;
        }
        else
        {
            if (number < 100)
            {
                return "00" + number;
            }
            else
            {
                if (number < 1000)
                {
                    return "0" + number;
                }
                else
                {
                    return "" + number;
                }
            }
        }
    }
}
