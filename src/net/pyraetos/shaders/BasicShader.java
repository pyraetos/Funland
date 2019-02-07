package net.pyraetos.shaders;

import static org.lwjgl.opengl.GL20.*;

import net.pyraetos.Matrices;
import net.pyraetos.Vectors;

public class BasicShader extends Shader{
	
	public BasicShader() {
		initShader("basicv.txt", "basicf.txt");
	}
	
	@Override
	protected void initShader(String vertexPath, String fragmentPath) {
		super.initShader(vertexPath, fragmentPath);
        glUseProgram(program);
		uniforms.put("proj", glGetUniformLocation(program, "proj"));
		uniforms.put("view", glGetUniformLocation(program, "view"));
		uniforms.put("model", glGetUniformLocation(program, "model"));
		uniforms.put("lightView", glGetUniformLocation(program, "lightView"));
		uniforms.put("lightProj", glGetUniformLocation(program, "lightProj"));
		uniforms.put("lightDir", glGetUniformLocation(program, "lightDir"));
		setUniform("proj", Matrices.PERSPECTIVE);
		setUniform("view", Matrices.IDENTITY);
		setUniform("model", Matrices.IDENTITY);
		setUniform("lightView", Matrices.IDENTITY);
		setUniform("lightProj", Matrices.SHADOW);
		setUniform("lightDir", Vectors.UP);
        glUseProgram(0);
	}
	
}
