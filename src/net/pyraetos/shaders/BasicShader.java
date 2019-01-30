package net.pyraetos.shaders;

import static org.lwjgl.opengl.GL20.*;

import net.pyraetos.Matrices;

public class BasicShader extends Shader{
	
	public int lightViewUniform;
	public int lightProjUniform;
	
	public BasicShader() {
		initShader("basicv.txt", "basicf.txt");
	}
	
	@Override
	protected void initShader(String vertexPath, String fragmentPath) {
		super.initShader(vertexPath, fragmentPath);
        glUseProgram(program);
		projectionUniform = glGetUniformLocation(program, "proj");
		viewUniform = glGetUniformLocation(program, "view");
		modelUniform = glGetUniformLocation(program, "model");
		lightViewUniform = glGetUniformLocation(program, "lightView");
		lightProjUniform = glGetUniformLocation(program, "lightProj");
		glUniformMatrix4fv(projectionUniform, false, Matrices.PERSPECTIVE);
		glUniformMatrix4fv(modelUniform, false, Matrices.IDENTITY);
		glUniformMatrix4fv(viewUniform, false, Matrices.IDENTITY);
		glUniformMatrix4fv(lightViewUniform, false, Matrices.IDENTITY);
		glUniformMatrix4fv(lightProjUniform, false, Matrices.SHADOW);
        glUseProgram(0);
	}
	
}
