package net.pyraetos;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Color implements Serializable{

	public float r;
	public float g;
	public float b;
	
	public Color(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
}
