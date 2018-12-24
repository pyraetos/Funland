package net.pyraetos.shaders;

import static org.lwjgl.opengl.GL20.*;

import net.pyraetos.Funland;
import net.pyraetos.util.Sys;

public abstract class Shader{

	public int program;
	public int projectionUniform;
	public int viewUniform;
	public int modelUniform;
	public static Shader ACTIVE_SHADER;
	
	public void setEnabled(boolean enabled) {
		if(enabled) {
			if(ACTIVE_SHADER != null) glUseProgram(0);
			glUseProgram(program);
			ACTIVE_SHADER = this;
		}else {
			glUseProgram(0);
			ACTIVE_SHADER = null;
		}
	}
	
	protected void initShader(String vertexPath, String fragmentPath) {
		int vs = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(vs, Sys.load(vertexPath));
		glCompileShader(vs);
		int cvs = glGetShaderi(vs, GL_COMPILE_STATUS);
		if(cvs == 0) {
			Sys.error("Shader compile error!\n" + glGetShaderInfoLog(vs));
		}
		
		int fs = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(fs, Sys.load(fragmentPath));
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
	}
	
}
