package net.pyraetos.objects;

import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;

import net.pyraetos.Matrices;
import net.pyraetos.shaders.Shader;
import net.pyraetos.util.Sys;

public class Model{

	protected Mesh mesh;
	protected Matrix4f modelMatrix;
	protected Matrix4f translationMatrix;
	protected Matrix4f rotationMatrix;
	protected Matrix4f scaleMatrix;
	protected FloatBuffer model;
	protected boolean transformed;
	protected int modelID;
	
	public static int nextModelID = 0x0;
	
	public Model(Mesh mesh) {
		this.mesh = mesh;
		modelID = nextModelID++;
		transformed = false;
		modelMatrix = new Matrix4f(Matrices.IDENTITY_MATRIX);
		translationMatrix = new Matrix4f(Matrices.IDENTITY_MATRIX);
		rotationMatrix = new Matrix4f(Matrices.IDENTITY_MATRIX);
		scaleMatrix = new Matrix4f(Matrices.IDENTITY_MATRIX);
		model = Matrices.toBuffer(modelMatrix);
	}
	
	public void translate(float x, float y, float z) {
		translationMatrix.translate(x, y, z);
		transformed = true;
	}
	
	float rx = 0f;
	float ry = 0f;
	float rz = 0f;
	
	public void rotate(float xAng, float yAng, float zAng) {
		rx = Sys.simplifyAngler(rx + Sys.toRadians(xAng));
		ry = Sys.simplifyAngler(ry + Sys.toRadians(yAng));
		rz = Sys.simplifyAngler(rz + Sys.toRadians(zAng));
		rotationMatrix.setRotationXYZ(rx, ry, rz);
		transformed = true;
	}
	
	public void scale(float amt){
		scaleMatrix.scale(amt);
		transformed = true;
	}

	//Model-level rendering applies and sends to shader model matrix
	//then calls mesh-level render
	public void render() {
		if(transformed) {
			updateModelMatrix();
			transformed = false;
		}
		glUniformMatrix4fv(Shader.ACTIVE_SHADER.modelUniform, false, model);
		mesh.render();
	}
	
	private void updateModelMatrix() {
		rotationMatrix.mulAffine(scaleMatrix, modelMatrix);
		translationMatrix.mulAffine(modelMatrix, modelMatrix);
		model = Matrices.toBuffer(modelMatrix);
	}
	
	@Override
	public int hashCode(){
		return modelID;
	}

	@Override
	public boolean equals(Object obj){
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Model other = (Model) obj;
		if (modelID != other.modelID)
			return false;
		return true;
	}
	
}
