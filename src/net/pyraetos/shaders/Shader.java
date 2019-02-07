package net.pyraetos.shaders;

import static org.lwjgl.opengl.GL20.*;

import java.nio.FloatBuffer;
import java.util.HashMap;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import net.pyraetos.Window;
import net.pyraetos.util.Sys;

public abstract class Shader{

	public int program;
	public HashMap<String, Integer> uniforms = new HashMap<String, Integer>();
	public static Shader ACTIVE_SHADER;
	public static final Shader BASIC = new BasicShader();
	public static final Shader SHADOW = new ShadowShader();
	public static final Shader SMQ = new ShadowMapQuadShader();
	
	public static void enable(Shader s) {
		if(s == null) return;
		s.setEnabled(true);
	}
	
	public static void disable(Shader s) {
		if(s == null) return;
		s.setEnabled(false);
	}
	
	public void setUniform(String uniform, FloatBuffer val) {
		if(uniforms.containsKey(uniform))
			glUniformMatrix4fv(uniforms.get(uniform), false, val);
	}
	
	public void setUniform(String uniform, Vector3f v) {
		if(uniforms.containsKey(uniform)) {
			FloatBuffer buf = BufferUtils.createFloatBuffer(3);
			buf.put(v.x).put(v.y).put(v.z);
			buf.flip();
			Shader old = ACTIVE_SHADER;
			enable(this);
			glUniform3fv(uniforms.get(uniform), buf);
			enable(old);
		}
	}
	
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
		glShaderSource(vs, Sys.load("Shaders/" + vertexPath));
		glCompileShader(vs);
		int cvs = glGetShaderi(vs, GL_COMPILE_STATUS);
		if(cvs == 0) {
			Sys.error("Shader compile error!\n" + glGetShaderInfoLog(vs));
		}
		
		int fs = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(fs, Sys.load("Shaders/" + fragmentPath));
		glCompileShader(fs);
		int cfs = glGetShaderi(fs, GL_COMPILE_STATUS);
		if(cfs == 0) {
			Sys.error("Shader compile error!\n" + glGetShaderInfoLog(fs));
			Window.close();
		}
		
		program = glCreateProgram();
        glAttachShader(program, vs);
        glAttachShader(program, fs);
        glLinkProgram(program);
        int linked = glGetProgrami(program, GL_LINK_STATUS);
        if(linked == 0) {
			Sys.error("Shader linking error!\n" + glGetShaderInfoLog(vs) + "\n" + glGetShaderInfoLog(fs));
			Window.close();
		}
	}
	
}
