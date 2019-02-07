package net.pyraetos.shaders;

import static org.lwjgl.opengl.GL20.*;

import net.pyraetos.Matrices;

public class ShadowShader extends Shader{
	
	public ShadowShader() {
		initShader("shadowv.txt", "shadowf.txt");
	}
	
	@Override
	protected void initShader(String vertexPath, String fragmentPath) {
		super.initShader(vertexPath, fragmentPath);
        glUseProgram(program);
		uniforms.put("proj", glGetUniformLocation(program, "proj"));
		uniforms.put("model", glGetUniformLocation(program, "model"));
		uniforms.put("lightView", glGetUniformLocation(program, "lightView"));
		setUniform("model", Matrices.IDENTITY);
		setUniform("lightView", Matrices.IDENTITY);
		setUniform("proj", Matrices.SHADOW);
        glUseProgram(0);
	}
	
}
