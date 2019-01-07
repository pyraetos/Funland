package net.pyraetos.objects;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Random;

import org.lwjgl.BufferUtils;

import net.pyraetos.pgenerate.PGenerate;
import net.pyraetos.util.Sys;

import static org.lwjgl.opengl.GL30.*;

public class RegionMesh implements Mesh{

	protected int vao;
	protected int vbo;
	protected int ibo;
	
	protected static boolean useTrees;
	protected static BasicMesh treeMesh;
	protected static PGenerate pg;
	protected static Random rand;
	
	public static final int SIDE = 9;
	public static int NUM_VERTICES = SIDE * SIDE;
	public static int NUM_INDICES = 6 * (SIDE - 1) * (SIDE - 1);
	public static final float ENTROPY = 4f;
	public static final long SEED = Sys.randomSeed();
	
	static {
		pg = new PGenerate(1024, 1024, SEED);//Don't like hard size, .generate should return if already generated
		rand = new Random(SEED);
		pg.setEntropy(ENTROPY);
	}
	
	//On construction, create basic region complete with indices
	public RegionMesh(boolean useTrees) {
		RegionMesh.useTrees = useTrees;
		if(useTrees) { 
			treeMesh = MeshIO.loadDAT("tree");
			//treeMesh = MeshIO.loadOBJ("tree");
			//treeMesh.setColors(new int[]{514}, new net.pyraetos.Color[] {new net.pyraetos.Color(0.3f, 0.7f, 0.2f), new net.pyraetos.Color(0.8f, 0.4f, 0.2f)});
			//MeshIO.saveDAT(treeMesh,"tree");
		}
		
		FloatBuffer vbuf = BufferUtils.createFloatBuffer(3 * NUM_VERTICES);
		initVertices(vbuf);
		IntBuffer ibuf = BufferUtils.createIntBuffer(NUM_INDICES);
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
	public RegionModel spawnModel(float x, float z, boolean async) {
		RegionModel rm = new RegionModel(x, z, this, async);
		return rm;
	}
	
	public void render() {
		glBindVertexArray(vao);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);
		glEnableVertexAttribArray(2);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
		glDrawElements(GL_TRIANGLES, NUM_INDICES, GL_UNSIGNED_INT, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glDisableVertexAttribArray(2);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(0);
		glBindVertexArray(0);
	}
}
