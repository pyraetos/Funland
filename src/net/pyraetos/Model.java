package net.pyraetos;

import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;

import net.pyraetos.util.Sys;

public class Model{

	protected Mesh mesh;
	protected Matrix4f modelViewMatrix;
	protected FloatBuffer modelView;
	protected boolean transformed;
	
	public Model(Mesh mesh) {
		this.mesh = mesh;
		transformed = false;
		modelViewMatrix = new Matrix4f(Matrices.IDENTITY_MATRIX);
		modelView = Matrices.toBuffer(modelViewMatrix);
	}
	
	//Transform all at once on render call/Make sure order is right
	public void translate(float x, float y, float z) {
		modelViewMatrix.translate(x, y, z);
		modelView = Matrices.toBuffer(modelViewMatrix);
		transformed = true;
	}
	
	public void rotate(float ang, float x, float y, float z) {
		modelViewMatrix.rotate(Sys.toRadians(ang), x, y, z);
		modelView = Matrices.toBuffer(modelViewMatrix);
		transformed = true;
	}

	public void render() {
		if(transformed) {
			glUniformMatrix4fv(Shader.ACTIVE_SHADER.modelViewUniform, false, modelView);
			transformed = false;
		}
		mesh.render();
	}
	
}
