package net.pyraetos.objects;

import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;

import net.pyraetos.Matrices;
import net.pyraetos.Shader;
import net.pyraetos.util.Sys;

public class Model{

	protected Mesh mesh;
	protected Matrix4f modelMatrix;
	protected Matrix4f translationMatrix;
	protected Matrix4f rotationMatrix;
	protected FloatBuffer model;
	protected boolean transformed;
	
	public Model(Mesh mesh) {
		this.mesh = mesh;
		transformed = false;
		modelMatrix = new Matrix4f(Matrices.IDENTITY_MATRIX);
		translationMatrix = new Matrix4f(Matrices.IDENTITY_MATRIX);
		rotationMatrix = new Matrix4f(Matrices.IDENTITY_MATRIX);
		model = Matrices.toBuffer(modelMatrix);
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
			updateModelMatrix();
			glUniformMatrix4fv(Shader.ACTIVE_SHADER.modelUniform, false, model);
			transformed = false;
		}
		mesh.render();
	}
	
	private void updateModelMatrix() {
		translationMatrix.mulAffine(rotationMatrix, modelMatrix);
		model = Matrices.toBuffer(modelMatrix);
	}
	
}
