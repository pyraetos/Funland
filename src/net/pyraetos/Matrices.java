package net.pyraetos;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import net.pyraetos.util.Sys;

public class Matrices{

	public static final Matrix4f PERSPECTIVE_MATRIX;
	public static final Matrix4f IDENTITY_MATRIX;
	
	public static final FloatBuffer PERSPECTIVE;
	public static final FloatBuffer IDENTITY;
	
	public static FloatBuffer toBuffer(Matrix4f matrix) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		matrix.get(buffer);
		return buffer;
	}
	
	static {
		PERSPECTIVE_MATRIX = new Matrix4f();
		PERSPECTIVE_MATRIX.perspective(Sys.toRadians(45), 1.3333333f, 0.1f, 1000f);
		PERSPECTIVE = BufferUtils.createFloatBuffer(16);
		PERSPECTIVE_MATRIX.get(PERSPECTIVE);
		
		IDENTITY_MATRIX = new Matrix4f();
		IDENTITY_MATRIX.identity();
		IDENTITY = BufferUtils.createFloatBuffer(16);
		IDENTITY_MATRIX.get(IDENTITY);
	}
	
}
