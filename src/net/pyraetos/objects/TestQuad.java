package net.pyraetos.objects;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

public class TestQuad extends Mesh{

	public TestQuad() {
		super();
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
	}
	
}
