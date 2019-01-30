package net.pyraetos;

import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static net.pyraetos.Vectors.*;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import net.pyraetos.objects.Model;
import net.pyraetos.shaders.BasicShader;
import net.pyraetos.shaders.Shader;

import static net.pyraetos.shaders.Shader.*;
import net.pyraetos.util.Sys;

public abstract class Camera{

	private static Matrix4f translationMatrix;
	private static Matrix4f rotationMatrix;
	private static Matrix4f viewMatrix;
	private static Matrix4f lightTranslationMatrix;
	private static Matrix4f lightRotationMatrix;
	private static Matrix4f lightViewMatrix;
	private static float rotY;
	private static Vector3f camDir;
	private static FloatBuffer view;
	private static FloatBuffer lightView;
	private static boolean transformed;
	
	//Camera position in world
	public static float x;
	public static float y;
	public static float z;
	
	static {
		translationMatrix = new Matrix4f(Matrices.IDENTITY_MATRIX);
		rotationMatrix = new Matrix4f(Matrices.IDENTITY_MATRIX);
		viewMatrix = new Matrix4f(Matrices.IDENTITY_MATRIX);
		lightTranslationMatrix = new Matrix4f(Matrices.IDENTITY_MATRIX);
		lightRotationMatrix = new Matrix4f(Matrices.IDENTITY_MATRIX).rotateX(-Sys.PI / 2);
		lightViewMatrix = new Matrix4f(Matrices.IDENTITY_MATRIX);
		rotY = 0f;
		camDir = new Vector3f(0f, 0f, -1f);
		x = y = z = 0.0f;
		transformed = true;
	}
	
	public static void translate(float x, float y, float z) {
		float dx = -z * Sys.sin(rotY);
		float dy = y;
		float dz = z * Sys.cos(rotY);
		Camera.x += dx;
		Camera.y += dy;
		Camera.z += dz;
		translationMatrix.translate(-dx, -dy, -dz);
		lightTranslationMatrix.translate(-dx, -dy, -dz);
		transformed = true;
	}

	public static void rotate(float ang, float x, float y, float z) {
		float rot = -Sys.toRadians(ang);
		if(y == 1) {
			rotY = Sys.simplifyAngler(rotY + rot);
			Vector3f temp = new Vector3f(x + Sys.sin(rotY), 0f, z - Sys.cos(rotY));
			temp.sub(new Vector3f(x, 0f, z), camDir);
			camDir.normalize();
		}
		rotationMatrix.rotate(rot, x, y, z);
		transformed = true;
	}

	public static void view() {
		if(transformed) {
			updateViewMatrix();
			updateLightViewMatrix();
			
			Shader old = ACTIVE_SHADER;
			enable(SHADOW);
			glUniformMatrix4fv(ACTIVE_SHADER.viewUniform, false, lightView);
			enable(old);
			
			transformed = false;
		}
		glUniformMatrix4fv(ACTIVE_SHADER.viewUniform, false, view);
		if(ACTIVE_SHADER instanceof BasicShader) {
			glUniformMatrix4fv(((BasicShader)ACTIVE_SHADER).lightViewUniform, false, lightView);
		}
	}
	
	private static void updateViewMatrix() {
		rotationMatrix.mulAffine(translationMatrix, viewMatrix);
		view = Matrices.toBuffer(viewMatrix);
	}
	

	private static void updateLightViewMatrix() {
		lightRotationMatrix.mulAffine(lightTranslationMatrix, lightViewMatrix);
		lightView = Matrices.toBuffer(lightViewMatrix);
	}
	
	//2D right now
	public static boolean isInFrontOfCamera(Model m) {
		//Accept if sufficiently close to camera
		Vector3f camPos = new Vector3f(x, 0f, z);
		Vector3f mPos = new Vector3f(m.getPos().x, 0f, m.getPos().z);
		Vector3f mDir = new Vector3f();
		float dist = mPos.sub(camPos, mDir).length();
		if(dist < 20) return true;
		
		//Otherwise use direction
		mDir.normalize();
		float dot = dot(mDir, camDir);
		return dot > 0.7f;
	}
	
}
