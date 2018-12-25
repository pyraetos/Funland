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
import net.pyraetos.util.Sys;

import static org.lwjgl.opengl.GL30.*;

public class Region extends Mesh{

	protected static PGenerate pg;
	public static final int SIDE = 9;
	public static final float ENTROPY = 4f;
	public static final long SEED = Sys.randomSeed();
	
	static {
		pg = new PGenerate(1024, 1024, SEED);//Don't like hard size, .generate should return if already generated
		pg.setEntropy(ENTROPY);
	}
	
	//On construction, create basic 5x5 region complete with indices
	public Region() {
		super();
		numVertices = SIDE * SIDE;
        numIndices = 6 * (SIDE - 1) * (SIDE - 1);
        
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
		float off = -((float)SIDE / 2f) - 0.5f;
		for(int xi = 0; xi < SIDE; xi++) {
			float curX = ((float)xi + off);
			for(int zi = 0; zi < SIDE; zi++) {
				float curZ = ((float)zi + off);
				fbuf.put(curX).put(0f).put(curZ);
			}
		}
		fbuf.flip();
	}

	private void initIndices(IntBuffer ibuf) {
		int arr[] = {1,0,SIDE,1,SIDE,SIDE+1};
		for(int i = 0; i < SIDE * (SIDE - 1) - 1; i++) {
			for(int j = 0; j < 6; j++) {
				if((i+1) % SIDE != 0) {
					ibuf.put(arr[j]);
				}
				arr[j]++;
			}
		}
		ibuf.flip();
	}
	
	//See RegionModel notes
	public RegionModel spawnModel(float x, float z) {
		RegionModel rm = new RegionModel(x, z, this);
		return rm;
	}
	
	//Attrib ptr 1 at this point holds Y data unique to the model
	@Override
	protected void specialRender(){
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
	}
	
}
