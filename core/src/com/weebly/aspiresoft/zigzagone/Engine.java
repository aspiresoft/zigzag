package com.weebly.aspiresoft.zigzagone;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Juggernaut on 23.08.2016.
 */
public abstract class Engine {

    public static boolean isInRhombus(float x, float y, Vector2 a, Vector2 b, Vector2 c, Vector2 d) {
        Vector2 p = new Vector2(x, y);
        int topToRight = checkIfPointIsBetweenTwoPoints(a, b, p);
        int RightToBot = checkIfPointIsBetweenTwoPoints(b, c, p);
        int botToLeft = checkIfPointIsBetweenTwoPoints(c, d, p);
        int leftToTop = checkIfPointIsBetweenTwoPoints(d, a, p);
        p = null;
        if (topToRight >= 0 && RightToBot >= 0 && botToLeft >= 0 && leftToTop >= 0) {
            return true;
        }
        return false;
    }

    public static int checkIfPointIsBetweenTwoPoints(Vector2 a, Vector2 b, Vector2 c) {
        float term = ((b.x - a.x) * (c.y - a.y)) - ((b.y - a.y) * (c.x - a.x));

        if (term > 0) {
            return 1;
        }
        if (term < 0) {
            return -1;
        }
        return 0;
    }

    public static boolean contains(Vector2[] points,Vector2 test) {
        int i;
        int j;
        boolean result = false;
        for (i = 0, j = points.length - 1; i < points.length; j = i++) {
            if ((points[i].y > test.y) != (points[j].y > test.y) &&
                    (test.x < (points[j].x - points[i].x) * (test.y - points[i].y) / (points[j].y-points[i].y) + points[i].x)) {
                result = !result;
            }
        }
        return result;
    }

    public static String zeroFiller(int number) {
        if (number < 10) {
            return "000" + number;
        } else {
            if (number < 100) {
                return "00" + number;
            } else {
                if (number < 1000) {
                    return "0" + number;
                } else {
                    return "" + number;
                }
            }
        }
    }
}
