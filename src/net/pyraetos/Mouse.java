package net.pyraetos;

import net.pyraetos.util.Sys;

public abstract class Mouse{

	private static double movement;
	
	static {
		movement = 0d;
	}
	
	public static boolean scrolled() {
		return movement != 0d;
	}
	
	public static float getAngle() {
		if(movement > 0) {
			movement = Sys.round(movement - 0.2d);
			return 2f;
		}else {
			movement = Sys.round(movement + 0.2d);
			return -2f;
		}
	}
	
	public static void scroll(double yoffset) {
		movement += yoffset;
	}
}
