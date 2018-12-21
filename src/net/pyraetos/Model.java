package net.pyraetos;

import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;

import net.pyraetos.util.Sys;

public class Model{

	protected Mesh mesh;
	protected Matrix4f modelViewMatrix;
	protected Matrix4f translationMatrix;
	protected Matrix4f rotationMatrix;
	protected FloatBuffer modelView;
	protected boolean transformed;
	
	public Model(Mesh mesh) {
		this.mesh = mesh;
		transformed = false;
		modelViewMatrix = new Matrix4f(Matrices.IDENTITY_MATRIX);
		translationMatrix = new Matrix4f(Matrices.IDENTITY_MATRIX);
		rotationMatrix = new Matrix4f(Matrices.IDENTITY_MATRIX);
		modelView = Matrices.toBuffer(modelViewMatrix);
	}
	
	public void translate(float x, float y, float z) {
		translationMatrix.translate(x, y, z);
		transformed = true;
	}
	
	public void rotate(float ang, float x, float y, float z) {
		rotationMatrix.rotate(Sys.toRadians(ang), x, y, z);
		transformed = true;
	}

	public void render() {
		if(transformed) {
			updateModelViewMatrix();
			glUniformMatrix4fv(Shader.ACTIVE_SHADER.modelViewUniform, false, modelView);
			transformed = false;
		}
		mesh.render();
	}
	
	private void updateModelViewMatrix() {
		translationMatrix.mulAffine(rotationMatrix, modelViewMatrix);
		modelView = Matrices.toBuffer(modelViewMatrix);
	}
	
}
