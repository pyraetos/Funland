package net.pyraetos.shaders;

import static org.lwjgl.opengl.GL20.*;

import net.pyraetos.Matrices;

public class ShadowMapQuadShader extends Shader{
	
	public ShadowMapQuadShader() {
		initShader("smqv.txt", "smqf.txt");
	}
	
	@Override
	protected void initShader(String vertexPath, String fragmentPath) {
		super.initShader(vertexPath, fragmentPath);
        glUseProgram(program);
		uniforms.put("proj", glGetUniformLocation(program, "proj"));
		uniforms.put("view", glGetUniformLocation(program, "view"));
		uniforms.put("model", glGetUniformLocation(program, "model"));
		setUniform("proj", Matrices.PERSPECTIVE);
		setUniform("view", Matrices.IDENTITY);
		setUniform("model", Matrices.IDENTITY);
        glUseProgram(0);
	}
	
}
