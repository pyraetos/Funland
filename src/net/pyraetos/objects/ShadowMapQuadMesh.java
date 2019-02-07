package net.pyraetos.objects;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

public class ShadowMapQuadMesh implements Mesh{
	
	protected int vbo;
	protected int tbo;
	protected int vao;
	protected int ibo;
	protected Vector3f centerMass;
	
	public ShadowMapQuadMesh() {
		init();
	}
	
	private void init() {
		FloatBuffer vbuf = BufferUtils.createFloatBuffer(3 * 4);
		vbuf.put(-1f).put(-1f).put(-1f);
		vbuf.put(-1f).put(1f).put(-1f);
		vbuf.put(1f).put(1f).put(-1f);
		vbuf.put(1f).put(-1f).put(-1f);
		vbuf.flip();
		
		FloatBuffer tbuf = BufferUtils.createFloatBuffer(2 * 4);
		tbuf.put(0f).put(0f);
		tbuf.put(0f).put(1f);
		tbuf.put(1f).put(1f);
		tbuf.put(1f).put(0f);
		tbuf.flip();
		
		IntBuffer ibuf = BufferUtils.createIntBuffer(4);
		ibuf.put(0).put(1).put(2).put(3);
		ibuf.flip();
		
		vao = glGenVertexArrays();
		glBindVertexArray(vao); 
		
		vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vbuf, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
        tbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, tbo);
        glBufferData(GL_ARRAY_BUFFER, tbuf, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 2 ,GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
		ibo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, ibuf, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        
        glBindVertexArray(0);
        
        centerMass = new Vector3f(0f, 0f, -1f);
	}
	
	public void render() {
		glBindVertexArray(vao);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
		glDrawElements(GL_QUADS, 4, GL_UNSIGNED_INT, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(0);
		glBindVertexArray(0);
	}
	
	public Model spawnModel() {
		return new Model(this);
	}
	
	@Override
	public Vector3f centerMass(){
		return centerMass;
	}
}
