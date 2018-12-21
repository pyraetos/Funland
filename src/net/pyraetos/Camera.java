package net.pyraetos;

import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;

import net.pyraetos.util.Sys;

public abstract class Camera{

	private static Matrix4f translationMatrix;
	private static Matrix4f rotationMatrix;
	private static Matrix4f viewMatrix;
	private static FloatBuffer view;
	private static boolean transformed;
	
	static {
		translationMatrix = new Matrix4f(Matrices.IDENTITY_MATRIX);
		rotationMatrix = new Matrix4f(Matrices.IDENTITY_MATRIX);
		viewMatrix = new Matrix4f(Matrices.IDENTITY_MATRIX);
		transformed = false;
	}
	
	public static void translate(float x, float y, float z) {
		translationMatrix.translate(-x, -y, -z);
		transformed = true;
	}
	
	public static void rotate(float ang, float x, float y, float z) {
		rotationMatrix.rotate(-Sys.toRadians(ang), x, y, z);
		transformed = true;
	}

	public static void view() {
		if(transformed) {
			updateViewMatrix();
			glUniformMatrix4fv(Shader.ACTIVE_SHADER.viewUniform, false, view);
			transformed = false;
		}
	}
	
	private static void updateViewMatrix() {
		rotationMatrix.mulAffine(translationMatrix, viewMatrix);
		view = Matrices.toBuffer(viewMatrix);
	}
	
}
