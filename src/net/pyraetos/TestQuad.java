package net.pyraetos;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

import net.pyraetos.util.Sys;

public class TestQuad extends Mesh{

	public TestQuad() {
		super(initShader());
		FloatBuffer fbuf = BufferUtils.createFloatBuffer(3 * 4);
		fbuf.put(-.5f).put(-.5f).put(0f);
		fbuf.put(-.5f).put(.5f).put(0f);
		fbuf.put(.5f).put(.5f).put(0f);
		fbuf.put(.5f).put(-.5f).put(0f);
		fbuf.flip();
		
		IntBuffer indices = BufferUtils.createIntBuffer(6);
		indices.put(new int[] {0,1,2,0,2,3});
		indices.flip();
		
		
		vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, fbuf, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
        ibo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        
        numVertices = 4;
        numIndices = 6;
        
        modelViewMatrix = Matrices.IDENTITY_MATRIX;
        modelView = Matrices.IDENTITY;
	}
	
	private static int initShader() {
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
		}
		
		int program = glCreateProgram();
        glAttachShader(program, vs);
        glAttachShader(program, fs);
        glLinkProgram(program);
        int linked = glGetProgrami(program, GL_LINK_STATUS);
        if(linked == 0) {
			Sys.error("Shader linking error!\n" + glGetShaderInfoLog(vs));
		}
        
        return program;
	}
	
}
