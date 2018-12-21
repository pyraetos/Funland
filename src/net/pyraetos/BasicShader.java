package net.pyraetos;

import static org.lwjgl.opengl.GL20.*;

import net.pyraetos.util.Sys;

public class BasicShader extends Shader{
	
	public BasicShader() {
		initShader();
	}
	
	private void initShader() {
		int vs = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(vs, Sys.load("vertex.txt"));
		glCompileShader(vs);
		int cvs = glGetShaderi(vs, GL_COMPILE_STATUS);
		if(cvs == 0) {
			Sys.error("Shader compile error!\n" + glGetShaderInfoLog(vs));
		}
		
		int fs = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(fs, Sys.load("fragment.txt"));
		glCompileShader(fs);
		int cfs = glGetShaderi(fs, GL_COMPILE_STATUS);
		if(cfs == 0) {
			Sys.error("Shader compile error!\n" + glGetShaderInfoLog(vs));
			Funland.close();
		}
		
		program = glCreateProgram();
        glAttachShader(program, vs);
        glAttachShader(program, fs);
        glLinkProgram(program);
        int linked = glGetProgrami(program, GL_LINK_STATUS);
        if(linked == 0) {
			Sys.error("Shader linking error!\n" + glGetShaderInfoLog(vs));
			Funland.close();
		}
        
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
