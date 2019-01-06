package net.pyraetos.objects;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

public class NormalMesh extends Mesh{

	private int nbo;
	
	public NormalMesh(Vector3f[] vertices, int[] indices, Vector3f[] normals) {
		super();
		
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
        
        glBindVertexArray(0);
        
        
	}

	@Override
	protected void specialRender(){
		glEnableVertexAttribArray(1);
	}
	
	public void saveObj(String path) {
		
	}
	
}
