package net.pyraetos.objects;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static net.pyraetos.objects.RegionMesh.*;

import java.nio.FloatBuffer;
import java.util.HashSet;
import java.util.Set;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import net.pyraetos.Camera;
import net.pyraetos.shaders.Shader;
import net.pyraetos.util.Sys;

public class RegionModel extends Model{

	//Stores generated y values. Sent to shader in attrib ptr 1
	private int ybo;
	private int nbo;
	
	//Used to calculate normals on the fringe and for trees
	private Vector3f[][] vertices = new Vector3f[SIDE + 2][SIDE + 2];
	
	//Used if useTrees is true
	private Set<Model> trees;
	
	//The buffers for this model
	private FloatBuffer ybuf;
	private FloatBuffer nbuf;
	
	//Indicates whether model was created async and needs initialization
	private boolean glInitialized = false;
	
	//Once mesh is made, specific model constructed here with x and z
	//ybuf is populated based on PGenerate, local translate occurs at end
	public RegionModel(float x, float z, RegionMesh mesh, boolean async){
		super(mesh);
		
		ybuf = BufferUtils.createFloatBuffer(NUM_VERTICES);
		initYbuf(x, z, ybuf);
		nbuf = computeNormals();
		
        if(useTrees) createTrees();

        translate(x, 0f, z);
        
        if(!async) {
        	bufferData();
        	glInitialized = true;
        }
	}
	
	//GL methods for async initialization
	public void bufferData() {
		ybo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, ybo);
        glBufferData(GL_ARRAY_BUFFER, ybuf, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
        nbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, nbo);
        glBufferData(GL_ARRAY_BUFFER, nbuf, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
	
	//Creates trees and translates to vertex
	private void createTrees() {
		trees = new HashSet<Model>();
		for(int i = 1; i <= SIDE; i++) {
			for(int j = 1; j <= SIDE; j++) {
				if(Sys.chance(0.01, rand)) {
					Model tree = treeMesh.spawnModel();
					tree.translate(vertices[i][j].x, vertices[i][j].y + 0.5f, vertices[i][j].z);
					trees.add(tree);
				}
			}
		}
	}
	
	//Build ybuf with global PGenerate instance
	private void initYbuf(float x, float z, FloatBuffer ybuf) {
		float off = -((float)SIDE / 2f) - 0.5f;
		for(int xi = -1; xi < SIDE + 1; xi++) {
			float curX = x + ((float)xi + off);
			int icurX = (int)Math.round(curX) + 512;
			
			for(int zi = -1; zi < SIDE + 1; zi++) {
				float curZ = z + ((float)zi + off);
				int icurZ = (int)Math.round(curZ) + 512;
				pg.generate(icurX, icurZ);
				float y = pg.getValue(icurX, icurZ);
				vertices[xi + 1][zi + 1] = new Vector3f(curX, y, curZ);
				if(xi >= 0 && xi < SIDE)
					if(zi >= 0 && zi < SIDE) {
						ybuf.put(y);
					}
			}
		}
		ybuf.flip();
	}
	
	//Scans through region, computes adjacent 6 face normals,
	//averages to vertex normal
	private FloatBuffer computeNormals() {
		FloatBuffer nbuf = BufferUtils.createFloatBuffer(3 * NUM_VERTICES);
		for(int i = 1; i <= SIDE; i++) {
			for(int j = 1; j <= SIDE; j++) {
				Vector3f n0 = cross(to(i, j, i, j + 1), to(i, j, i + 1, j));
				Vector3f n1 = cross(to(i, j, i, j - 1), to(i, j, i - 1, j));
				
				Vector3f vn = average(n0, n1);
				nbuf.put(vn.x).put(vn.y).put(vn.z);
			}
		}
		nbuf.flip();
		return nbuf;
	}
	
	//3D Cross Product
	private static Vector3f cross(Vector3f a, Vector3f b) {
		float x = a.y * b.z - a.z * b.y;
		float y = a.z * b.x - a.x * b.z;
		float z = a.x * b.y - a.y * b.x;
		return new Vector3f(x, y, z);
	}
	
	//Returns the vector from the vertex specified by the first indices to the second
	private Vector3f to(int i0, int j0, int i1, int j1) {
		Vector3f dest = new Vector3f(0, 0, 0);
		vertices[i1][j1].sub(vertices[i0][j0], dest);
		return dest;
	}
	
	//Normalized vector average
	private Vector3f average(Vector3f... vecs) {
		Vector3f dest = new Vector3f(0, 0, 0);
		for(Vector3f vec : vecs) {
			dest.add(vec, dest);
		}
		dest.div(vecs.length, dest);
		return dest.normalize();
	}
	
	//Bind the mesh-level vao, point attrib 1 to the model's
	//local ybuf
	//Call super to do any other transformations (super
	//calls mesh render)
	@Override
	public void render() {
		if(!glInitialized) {
			bufferData();
			glInitialized = true;
		}
		
		glBindVertexArray(((RegionMesh)mesh).vao);
		glBindBuffer(GL_ARRAY_BUFFER, ybo);
		glVertexAttribPointer(1, 1, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
        glBindBuffer(GL_ARRAY_BUFFER, nbo);
		glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
		super.render();
		
		if(useTrees) {
			Shader.enable(Shader.BASIC);
			Camera.view();
			for(Model tree : trees) tree.render();
			Shader.enable(Shader.TERRAIN);
		}
	}

}
