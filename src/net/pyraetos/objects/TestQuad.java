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

public class TestQuad extends Mesh{

	private int nbo;
	
	public TestQuad() {
		super();
		
		numVertices = 4;
        numIndices = 6;
		
		FloatBuffer fbuf = BufferUtils.createFloatBuffer(3 * numVertices);
		fbuf.put(-.5f).put(-.5f).put(0f);
		fbuf.put(-.5f).put(.5f).put(0f);
		fbuf.put(.5f).put(.5f).put(0f);
		fbuf.put(.5f).put(-.5f).put(0f);
		fbuf.flip();
		
		IntBuffer indices = BufferUtils.createIntBuffer(numIndices);
		indices.put(new int[] {0,1,2,0,2,3});
		indices.flip();
		
		FloatBuffer normals = BufferUtils.createFloatBuffer(3 * numVertices);
		Vector3f[] normalVecs = new Vector3f[4];
		normalVecs[0] = new Vector3f(-1f, -1f, 0f).normalize();
		normalVecs[1] = new Vector3f(-1f, 1f, 0f).normalize();
		normalVecs[2] = new Vector3f(1f, 1f, 0f).normalize();
		normalVecs[3] = new Vector3f(1f, -1f, 0f).normalize();
		for(Vector3f normalVec : normalVecs)
			normals.put(normalVec.x).put(normalVec.y).put(normalVec.z);
		normals.flip();
		
		vao = glGenVertexArrays();
		glBindVertexArray(vao);
		
		vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, fbuf, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3 ,GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
        ibo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        
        nbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, nbo);
        glBufferData(GL_ARRAY_BUFFER, normals, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 3 ,GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
        glBindVertexArray(0);
	}

	@Override
	protected void specialRender(){
		glEnableVertexAttribArray(1);
	}
	
}
