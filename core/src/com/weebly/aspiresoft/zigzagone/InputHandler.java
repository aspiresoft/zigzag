package com.weebly.aspiresoft.zigzagone;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by Juggernaut on 17.08.2016.
 */
public class InputHandler implements InputProcessor {

    public static float X, Y;
    public static float orgX, orgY;
    public static boolean TOUCH;

    private Vector3 touch;

    private Program program;

    public InputHandler(Program program) {
        this.program = program;
        touch = new Vector3(0, 0, 0);
        X = Y = 0;
        TOUCH = false;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        TOUCH = true;

        program.getCamera().unproject(touch.set(screenX, screenY, 0));
        X = touch.x;
        Y = touch.y;

        program.getPivotCamera().unproject(touch.set(screenX, screenY, 0));
        orgX = touch.x;
        orgY = touch.y;

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        TOUCH = false;

        program.getCamera().unproject(touch.set(screenX, screenY, 0));
        X = touch.x;
        Y = touch.y;

        program.getPivotCamera().unproject(touch.set(screenX, screenY, 0));
        orgX = touch.x;
        orgY = touch.y;

        return true;

    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        TOUCH = true;

        program.getCamera().unproject(touch.set(screenX, screenY, 0));
        X = touch.x;
        Y = touch.y;

        program.getPivotCamera().unproject(touch.set(screenX, screenY, 0));
        orgX = touch.x;
        orgY = touch.y;

        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
