package net.pyraetos;

import net.pyraetos.util.Sys;

public abstract class Statistics{

	//Framerate statistics
	private static long lastPrintTS;
	private static long previousTS;
	private static long currentTS;
	private static int nextIndex;
	private static double average;
	private static double[] last30;
	
	public static void init() {
		previousTS = currentTS = lastPrintTS = Sys.time();
		nextIndex = 0;
		last30 = new double[30];
	}
	
	public static void update() {
		previousTS = currentTS;
		currentTS = Sys.time();
		double seconds = ((double)(currentTS - previousTS)) / 1000d;
		double framerate = 1d / seconds;
		last30[nextIndex] = framerate;
		nextIndex = (nextIndex + 1) % 30;
		if(currentTS - lastPrintTS > 1000) {
			double avg = Sys.average(last30);
			try {
				average = avg == Double.NaN ? 0.0 : Sys.round(avg);
			}catch(Exception e) {
				average = 0.0;
			}
			lastPrintTS = currentTS;
		}
	}
	
	public static double getFPS() {
		return average;
	}
	
}
