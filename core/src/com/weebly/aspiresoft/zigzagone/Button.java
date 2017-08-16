package com.weebly.aspiresoft.zigzagone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by Juggernaut on 17.08.2016.
 */
public class Button {
    private float x;
    private float y;
    private float width;
    private float height;
    private Texture texture, texture2;
    private boolean clicked;
    private boolean touched;
    private Program program;

    public Button(Program program, float x, float y, float width, float height, String path, String path2) {
        this.program = program;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        texture = new Texture(path);
        texture2 = new Texture(path2);
        setClicked(false);
        setTouched(false);
    }

    public void update(float deltaTime) {
        if (InputHandler.TOUCH) {
            if (contains(InputHandler.orgX, InputHandler.orgY)) {
                setTouched(true);
            } else {
                setTouched(false);
            }
        } else {
            if (contains(InputHandler.orgX, InputHandler.orgY)) {
                if (isTouched()) {
                    setClicked(true);
                    setTouched(false);
                }
            } else {
                setTouched(false);
            }
        }
    }

    public void render(SpriteBatch sb) {
        if (isTouched()) {
            sb.draw(texture2, getX(), getY(), getWidth(), getHeight());
        } else {
            sb.draw(texture, getX(), getY(), getWidth(), getHeight());
        }
    }

    public void dispose() {
        texture.dispose();
        texture2.dispose();
    }

    public boolean contains(float tX, float tY) {
        if (new Rectangle(getX(), getY(), getWidth(), getHeight()).contains(tX, tY)) {
            return true;
        }
        return false;
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

    public boolean isClicked() {
        return clicked;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    public boolean isTouched() {
        return touched;
    }

    public void setTouched(boolean touched) {
        this.touched = touched;
    }
}
