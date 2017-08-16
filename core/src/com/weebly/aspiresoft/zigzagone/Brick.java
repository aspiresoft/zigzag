package com.weebly.aspiresoft.zigzagone;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

/**
 * Created by Juggernaut on 18.08.2016.
 */
public class Brick {

    public float x, y, width, height;
    public Texture texture;
    public int number;
    public boolean visited;
    public boolean alive;
    public boolean direction;
    public boolean buried;
    public boolean gavePoint;
    private Program program;

    public Brick(Program program, float x, float y, float width, float height, String path, int number) {
        this.program = program;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.number = number;
        texture = new Texture(path);
        visited = false;
        alive = true;
        gavePoint = false;
        buried = false;
    }

    public void update(float deltaTime) {
        if (!alive && !buried) {
            if (direction) {
                x += (Program.BRICK_WIDTH / 100) * 2 * program.getPlayer().getSpeed();
                y += (Program.BRICK_HEIGHT / 100) * program.getPlayer().getSpeed();
            } else {
                x -= (Program.BRICK_WIDTH / 100) * 2 * program.getPlayer().getSpeed();
                y += (Program.BRICK_HEIGHT / 100) * program.getPlayer().getSpeed();
            }
            if (y + height > program.getPlayer().getY() + Program.HEIGHT / 3) {
                buried = true;
            }
        }
    }

    public void render(SpriteBatch sb) {
        sb.draw(texture, x, y, width, height);
    }

    public void kill() {
        if (alive) {
            alive = false;
            direction = new Random().nextBoolean();
        }
    }

    public void dispose() {
        texture.dispose();
    }

    public boolean contains(float tX, float tY) {
        if (new Rectangle(x, y, width, height).contains(tX, tY)) {
            return true;
        }
        return false;
    }
}
