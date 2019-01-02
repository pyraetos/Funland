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

public class TestCube extends Mesh{

	private int nbo;
	
	public TestCube() {
		super();
		
		numVertices = 8;
        numIndices = 36;
        
		FloatBuffer fbuf = BufferUtils.createFloatBuffer(3 * numVertices);
		fbuf.put(-.5f).put(-.5f).put(.5f);
		fbuf.put(-.5f).put(.5f).put(.5f);
		fbuf.put(.5f).put(.5f).put(.5f);
		fbuf.put(.5f).put(-.5f).put(.5f);
		
		fbuf.put(-.5f).put(-.5f).put(-.5f);
		fbuf.put(-.5f).put(.5f).put(-.5f);
		fbuf.put(.5f).put(.5f).put(-.5f);
		fbuf.put(.5f).put(-.5f).put(-.5f);
		fbuf.flip();
		
		IntBuffer indices = BufferUtils.createIntBuffer(numIndices);
		indices.put(new int[] {0,1,2,0,2,3,
								4,5,1,4,1,0,
								7,6,5,7,5,4,
								3,2,6,3,6,7,
								1,5,6,1,6,2,
								4,0,3,4,3,7});
		indices.flip();
		
		FloatBuffer normals = BufferUtils.createFloatBuffer(3 * numVertices);
		Vector3f[] normalVecs = new Vector3f[8];
		normalVecs[0] = new Vector3f(-1f, -1f, 1f).normalize();
		normalVecs[1] = new Vector3f(-1f, 1f, 1f).normalize();
		normalVecs[2] = new Vector3f(1f, 1f, 1f).normalize();
		normalVecs[3] = new Vector3f(1f, -1f, 1f).normalize();
		normalVecs[4] = new Vector3f(-1f, -1f, -1f).normalize();
		normalVecs[5] = new Vector3f(-1f, 1f, -1f).normalize();
		normalVecs[6] = new Vector3f(1f, 1f, -1f).normalize();
		normalVecs[7] = new Vector3f(1f, -1f, -1f).normalize();
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
