package com.dvail.clodiku.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.dvail.clodiku.GameCore;

public class DesktopLauncher {
	public static void main (String[] arg) {
		new LwjglApplication(new GameCore(), "Clodiku", 400, 250);
	}
}
