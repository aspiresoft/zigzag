package com.weebly.aspiresoft.zigzagone;

import com.badlogic.gdx.graphics.g2d.*;

/**
 * Created by Juggernaut on 24.08.2016.
 */
public class Coin {
    private static final int TYPE_COIN = 1;
    private static final int TYPE_SPEED = 2;
    private final int DURATION = 1500;
    private float x, y, width, height;
    private boolean picked;
    private boolean alive;
    private int itemLoc;
    private int type;

    private Animation anim;

    private int number;

    public Coin(float x, float y, float width, float height, int number, int itemLoc, int type) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.number = number;
        this.itemLoc = itemLoc;
        this.type = type;
        alive = true;
        picked = false;
        String imagePaths[] = null;
        if (type == TYPE_COIN) {
            imagePaths = new String[]
                    {
                            "coin/frame-1.png", "coin/frame-2.png", "coin/frame-3.png", "coin/frame-4.png", "coin/frame-5.png",
                            "coin/frame-6.png", "coin/frame-7.png", "coin/frame-8.png", "coin/frame-9.png", "coin/frame-10.png"
                    };

        } else if (type == TYPE_SPEED) {
            imagePaths = new String[]
                    {
                            "Speed/frame-1.png", "Speed/frame-2.png", "Speed/frame-3.png", "Speed/frame-4.png", "Speed/frame-5.png",
                            "Speed/frame-6.png", "Speed/frame-7.png", "Speed/frame-8.png", "Speed/frame-9.png", "Speed/frame-10.png"
                    };
        }

        anim = new Animation(x, y, width, height, imagePaths, DURATION, true);
    }

    public void update() {
        if (alive) {
            anim.update();
        }
    }

    public void render(SpriteBatch sb) {
        if (alive) {
            anim.render(sb);
        }
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public int getNumber() {
        return number;
    }

    public boolean isAlive() {
        return alive;
    }

    public boolean isPicked() {
        return picked;
    }

    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }

    public float getY() {
        return y;
    }

    public float getX() {
        return x;
    }

    public int getItemLoc() {
        return itemLoc;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public void setPicked(boolean picked) {
        this.picked = picked;
    }

}
