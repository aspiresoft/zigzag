package com.weebly.aspiresoft.zigzagone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by Juggernaut on 18.08.2016.
 */
public class Player {
    private float speed;
    private int number;

    private float x;
    private float y;
    private float width;
    private float height;
    private Texture texture;
    private boolean direction;
    private boolean alive;
    private boolean alive2;
    private float fallSpeed;


    public Player(float x, float y, float width, float height, String path) {
        this.setX(x);
        this.setY(y);
        this.setWidth(width);
        this.setHeight(height);
        setTexture(new Texture(path));
        setDirection(true);
        setSpeed(1);
        setNumber(0);
        setAlive(true);
        setAlive2(true);
        fallSpeed = 3;
    }

    public void update(float deltaTime) {

        if (alive) {

            if (isDirection()) {
                setX(getX() + (Program.BRICK_WIDTH / 100) * 2 * getSpeed());
                setY(getY() - (Program.BRICK_HEIGHT / 100) * getSpeed());
            } else {
                setX(getX() - (Program.BRICK_WIDTH / 100) * 2 * getSpeed());
                setY(getY() - (Program.BRICK_HEIGHT / 100) * getSpeed());
            }
        } else {
            setY(getY() - (Program.BRICK_HEIGHT / 100 * 10 * getSpeed()));
            if (isDirection()) {
                setX(getX() + (Program.BRICK_WIDTH / 100 * getSpeed() * Math.max(1, fallSpeed)));
            } else {
                setX(getX() - (Program.BRICK_WIDTH / 100 * getSpeed() * Math.max(1, fallSpeed)));
            }
            fallSpeed -= 0.05;
        }

    }

    public void render(SpriteBatch sb) {
        sb.draw(getTexture(), getX(), getY(), getWidth(), getHeight());
    }

    public void dispose() {
        getTexture().dispose();
    }

    public boolean contains(float tX, float tY) {
        if (new Rectangle(getX(), getY(), getWidth(), getHeight()).contains(tX, tY)) {
            return true;
        }
        return false;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public boolean isDirection() {
        return direction;
    }

    public void setDirection(boolean direction) {
        this.direction = direction;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public boolean isAlive2() {
        return alive2;
    }

    public void setAlive2(boolean alive2) {
        this.alive2 = alive2;
    }

    public boolean getDirection() {
        return direction;
    }
}
