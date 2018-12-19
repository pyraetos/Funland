package net.pyraetos;

import static org.lwjgl.opengl.GL20.*;

public abstract class Shader{

	public int program;
	public int projectionUniform;
	public int modelViewUniform;
	public static Shader ACTIVE_SHADER;
	
	public void setEnabled(boolean enabled) {
		if(enabled) {
			glUseProgram(program);
			ACTIVE_SHADER = this;
		}else {
			glUseProgram(0);
			ACTIVE_SHADER = null;
		}
	}
	
}
