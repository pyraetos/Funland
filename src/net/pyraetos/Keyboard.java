package net.pyraetos;

import java.util.HashSet;
import java.util.Set;

public abstract class Keyboard{

	private static Set<Integer> down;
	
	static {
		down = new HashSet<Integer>();
	}
	
	public static void press(int key) {
		down.add(key);
	}
	
	public static void release(int key) {
		down.remove(key);
	}
	
	public static boolean pressed(int key) {
		return down.contains(key);
	}
	
}
