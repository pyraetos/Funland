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
import net.pyraetos.Color;

@SuppressWarnings("serial")
public class BasicMesh implements Mesh, Serializable{
	
	protected transient int vao;
	protected transient int vbo;
	protected transient int ibo;
	protected transient int nbo;
	protected transient int cbo;
	protected transient int numVertices;
	protected transient int numIndices;
	
	public Vector3f[] vertices;
	public int[] indices;
	public Vector3f[] normals;
	public Color[] colors;
	
	public BasicMesh(Vector3f[] vertices, int[] indices, Vector3f[] normals, Color[] colors) {
		this.vertices = vertices;
		this.indices = indices;
		this.normals = normals;
		this.colors = colors;
		init();
	}
	
	private void init() {
		numVertices = vertices.length;
        numIndices = indices.length;
        
		FloatBuffer vbuf = BufferUtils.createFloatBuffer(3 * numVertices);
		for(Vector3f vertex : vertices)
			vbuf.put(vertex.x).put(vertex.y).put(vertex.z);
		vbuf.flip();
		
		IntBuffer ibuf = BufferUtils.createIntBuffer(numIndices);
		for(int i : indices)
			ibuf.put(i);
		ibuf.flip();
		
		FloatBuffer nbuf = BufferUtils.createFloatBuffer(3 * numVertices);
		for(Vector3f normal : normals) {
			nbuf.put(normal.x).put(normal.y).put(normal.z);
		}
		nbuf.flip();
		
		FloatBuffer cbuf = BufferUtils.createFloatBuffer(numVertices * 3);
		for(Color c : colors)
			cbuf.put(c.r).put(c.g).put(c.b);
		cbuf.flip();
		
		vao = glGenVertexArrays();
		glBindVertexArray(vao);
		
		vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vbuf, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3 ,GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
        ibo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        
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
		
        glBindVertexArray(0);
	}
	
	public void setColors(int[] colorChanges, Color[] newColors) {
		int colorIndex = 0;
		Color curColor = newColors[0];
		for(int i = 0; i < colors.length; i++) {
			if(colorIndex < colorChanges.length && i == colorChanges[colorIndex]) {
				curColor = newColors[++colorIndex];
			}
			colors[i] = curColor;
		}
		init();
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
}
