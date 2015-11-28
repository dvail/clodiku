package com.dvail.klodiku.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.dvail.klodiku.GameCore;

public class DesktopLauncher {
	public static void main (String[] arg) {
		new LwjglApplication(new GameCore(), "Clodiku", 400, 250);
	}
}
