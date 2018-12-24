package net.pyraetos.objects;

import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

import net.pyraetos.pgenerate.PGenerate;
import static org.lwjgl.opengl.GL30.*;

public class Region extends Mesh{

	protected static PGenerate pg;
	
	static {
		pg = new PGenerate(1024,1024);//Don't like hard size
	}
	
	//On construction, create basic 5x5 region complete with indices
	public Region() {
		super();
		numVertices = 25;
        numIndices = 6 * 16;
        
		FloatBuffer vbuf = BufferUtils.createFloatBuffer(3 * numVertices);
		initVertices(vbuf);
		IntBuffer ibuf = BufferUtils.createIntBuffer(numIndices);
		initIndices(ibuf);
		
		vao = glGenVertexArrays();
		glBindVertexArray(vao);
		
		vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vbuf, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3 ,GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
        ibo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, ibuf, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        
        glBindVertexArray(0);
	}
	
	private void initVertices(FloatBuffer fbuf) {
		for(int xi = 0; xi < 5; xi++) {
			float curX = ((float)xi - 2f);
			for(int zi = 0; zi < 5; zi++) {
				float curZ = ((float)zi - 2f);
				fbuf.put(curX).put(0f).put(curZ);
			}
		}
		fbuf.flip();
	}

	private void initIndices(IntBuffer ibuf) {
		int arr[] = {1,0,5,1,5,6};
		for(int i = 0; i < 19; i++) {
			for(int j = 0; j < 6; j++) {
				if(i != 4 && i != 9 && i != 14) {
					ibuf.put(arr[j]);
				}
				arr[j]++;
			}
		}
		ibuf.flip();
	}
	
	//See RegionModel notes
	public RegionModel spawnModel(float x, float y) {
		RegionModel rm = new RegionModel(x, y, this);
		return rm;
	}
	
	//Attrib ptr 1 at this point holds Y data unique to the model
	@Override
	protected void specialRender(){
		glEnableVertexAttribArray(1);
	}
	
}
