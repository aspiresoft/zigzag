package com.weebly.aspiresoft.zigzagone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Juggernaut on 24.08.2016.
 */
public class Animation {
    public long duration;
    public boolean shouldAnimate;
    public float x, y, width, height;
    public float currentX, currentY;
    public long lastTime;
    public long interval;
    public float frameCount;
    public int currentFrame;
    public boolean loop;
    private Texture[] textures;

    public Animation(float x, float y, float width, float height, String[] images, long duration, boolean loop) {
        this.duration = duration;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.loop = loop;
        textures = new Texture[images.length];
        for (int i = 0; i < images.length; i++) {
            textures[i] = new Texture(Gdx.files.internal(images[i]));
        }
        frameCount = images.length;
        shouldAnimate = true;
        lastTime = System.currentTimeMillis();
        interval = (long) (duration / frameCount);
        currentFrame = 0;
    }


    public void update() {
        if (shouldAnimate) {
            if (System.currentTimeMillis() - lastTime >= interval) {
                lastTime = System.currentTimeMillis();
                currentFrame++;
                if (currentFrame >= frameCount) {
                    if (loop) {
                        currentFrame = 0;
                    } else {
                        shouldAnimate = false;
                    }
                }
            }
        }
    }

    public void render(SpriteBatch batch) {
        if (shouldAnimate) {
            batch.draw(textures[currentFrame], x, y, width, height);
        }
    }
}
