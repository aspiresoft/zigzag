package com.weebly.aspiresoft.zigzagone.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.weebly.aspiresoft.zigzagone.Program;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = Program.WIDTH;
		config.height = Program.HEIGHT;
		config.title = Program.TITLE;
		new LwjglApplication(new Program(), config);
	}
}
