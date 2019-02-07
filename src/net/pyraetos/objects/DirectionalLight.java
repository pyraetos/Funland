package net.pyraetos.objects;

import static net.pyraetos.shaders.Shader.ACTIVE_SHADER;
import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import net.pyraetos.Matrices;
import net.pyraetos.Vectors;
import net.pyraetos.shaders.Shader;
import net.pyraetos.util.Sys;

public class DirectionalLight{

	private Matrix4f lightTranslationMatrix;
	private Matrix4f lightRotationMatrix;
	private Matrix4f lightViewMatrix;
	private FloatBuffer lightView;
	private Vector3f dirTo;
	
	public DirectionalLight(Vector3f dirTo) {
		this.dirTo = dirTo;
		lightTranslationMatrix = new Matrix4f(Matrices.IDENTITY_MATRIX);
		initRotationMatrix();
		lightViewMatrix = new Matrix4f(Matrices.IDENTITY_MATRIX);
		updateLightViewMatrix();
		Shader.BASIC.setUniform("lightDir", dirTo);
	}
	
	private void initRotationMatrix() {
		Vector3f orthogonal = new Vector3f();
		orthogonal.orthogonalizeUnit(dirTo);
		orthogonal = Vectors.cross(orthogonal, dirTo);
		lightRotationMatrix = new Matrix4f(Matrices.IDENTITY_MATRIX);
		lightRotationMatrix.rotate(Sys.PI/2, orthogonal);
	}
	
	public DirectionalLight(float x, float y, float z) {
		this(new Vector3f(x, y, z));
	}
	
	public void update(float x, float y, float z) {
		lightTranslationMatrix.setTranslation(-x, -y, -z);
		updateLightViewMatrix();
	}
	
	public void useLightView() {
		ACTIVE_SHADER.setUniform("lightView", lightView);
	}
	
	private void updateLightViewMatrix() {
		lightRotationMatrix.mulAffine(lightTranslationMatrix, lightViewMatrix);
		lightView = Matrices.toBuffer(lightViewMatrix);
	}
}
