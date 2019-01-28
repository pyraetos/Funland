package net.pyraetos.objects;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

@SuppressWarnings("serial")
public class BasicMesh implements Mesh, Serializable{
	
	protected transient int vao;
	protected transient int vbo;
	protected transient int ibo;
	protected transient int nbo;
	protected transient int cbo;
	protected transient int numUAs;
	protected transient int numIndices;
	protected transient Vector3f centerMass;
	
	public UniqueAttribute[] uas;
	public int[] indices;
	
	public BasicMesh(UniqueAttribute[] uas, int[] indices) {
		this.uas = uas;
		this.indices = indices;
		init();
	}
	
	private void init() {
		numUAs = uas.length;
        numIndices = indices.length;
        
		FloatBuffer vbuf = BufferUtils.createFloatBuffer(3 * numUAs);
		FloatBuffer nbuf = BufferUtils.createFloatBuffer(3 * numUAs);
		FloatBuffer cbuf = BufferUtils.createFloatBuffer(3 * numUAs);
		for(UniqueAttribute ua : uas) {
			vbuf.put(ua.vertex.x).put(ua.vertex.y).put(ua.vertex.z);
			nbuf.put(ua.normal.x).put(ua.normal.y).put(ua.normal.z);
			cbuf.put(ua.color.r).put(ua.color.g).put(ua.color.b);
		}
		vbuf.flip();
		nbuf.flip();
		cbuf.flip();
		
		IntBuffer ibuf = BufferUtils.createIntBuffer(numIndices);
		for(int i : indices)
			ibuf.put(i);
		ibuf.flip();
		
		vao = glGenVertexArrays();
		glBindVertexArray(vao); 
		
		vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vbuf, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
        nbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, nbo);
        glBufferData(GL_ARRAY_BUFFER, nbuf, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 3 ,GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		cbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, cbo);
		glBufferData(GL_ARRAY_BUFFER, cbuf, GL_STATIC_DRAW);
		glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
        
		ibo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, ibuf, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        
        glBindVertexArray(0);
        
        centerMass = new Vector3f(0f, 0f, 0f);
	}
	
	public void render() {
		glBindVertexArray(vao);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
		glDrawElements(GL_TRIANGLES, numIndices, GL_UNSIGNED_INT, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glDisableVertexAttribArray(2);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(0);
		glBindVertexArray(0);
	}
	
	public Model spawnModel() {
		return new Model(this);
	}
	
	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException{
		stream.defaultReadObject();
		init();
	}

	@Override
	public Vector3f centerMass(){
		return centerMass;
	}
}
