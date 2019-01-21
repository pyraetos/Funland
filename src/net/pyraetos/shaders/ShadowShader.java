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
		projectionUniform = glGetUniformLocation(program, "proj");
		viewUniform = glGetUniformLocation(program, "view");
		modelUniform = glGetUniformLocation(program, "model");
		glUniformMatrix4fv(projectionUniform, false, Matrices.PERSPECTIVE);
		glUniformMatrix4fv(modelUniform, false, Matrices.IDENTITY);
		glUniformMatrix4fv(viewUniform, false, Matrices.IDENTITY);
        glUseProgram(0);
	}
	
}
