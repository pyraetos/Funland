package net.pyraetos.objects;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.joml.GeometryUtils;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import net.pyraetos.util.Sys;

public class RegionModel extends Model{

	//Stores generated y values. Sent to shader in attrib ptr 1
	private int ybo;
	private int nbo;
	
	//Once mesh is made, specific model constructed here with x and z
	//ybuf is populated based on PGenerate, local translate occurs at end
	public RegionModel(float x, float z, Region mesh){
		super(mesh);
		
		FloatBuffer ybuf = BufferUtils.createFloatBuffer(mesh.numVertices);
		initYbuf(x, z, ybuf);
		
        ybo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, ybo);
        glBufferData(GL_ARRAY_BUFFER, ybuf, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
        FloatBuffer nbuf = computeNormals();
        
        nbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, nbo);
        glBufferData(GL_ARRAY_BUFFER, nbuf, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
        translate(x, 0f, z);
	}
	
	//Build ybuf with global PGenerate instance
	private void initYbuf(float x, float z, FloatBuffer ybuf) {
		float off = -((float)Region.SIDE / 2f) - 0.5f;
		for(int xi = 0; xi < Region.SIDE; xi++) {
			float curX = x + ((float)xi + off);
			int icurX = (int)Math.round(curX) + 512;
			for(int zi = 0; zi < Region.SIDE; zi++) {
				float curZ = z + ((float)zi + off);
				int icurZ = (int)Math.round(curZ) + 512;
				Region.pg.generate(icurX, icurZ);
				float y = Region.pg.getValue(icurX, icurZ);
				vertices[Region.SIDE * xi + zi] = new Vector3f(curX, y, curZ);
				ybuf.put(y);
			}
		}
		ybuf.flip();
	}
	
	private Vector3f[] vertices = new Vector3f[Region.SIDE * Region.SIDE];
	
	private FloatBuffer computeNormals() {
		int side = Region.SIDE;
		FloatBuffer nbuf = BufferUtils.createFloatBuffer(3 * mesh.numVertices);
		for(int i = 0; i < side; i++) {
			for(int j = 0; j < side; j++) {
				if(i == 0 || i == side-1 || j == 0 || j == side-1) {
					nbuf.put(0f).put(1f).put(0f);
					continue;
				}
				int index = side * i + j;
				Vector3f n0 = computeNormal(index, -side, 0, -1);
				Vector3f n1a = computeNormal(index, -side+1, 0, -side);
				Vector3f n1b = computeNormal(index, -side+1, 1, 0);
				Vector3f n1 = average(n1a, n1b);
				Vector3f n2 = computeNormal(index, 1, side, 0);
				Vector3f n3a = computeNormal(index, 0, side, side-1);
				Vector3f n3b = computeNormal(index, 0, side-1, -1);
				Vector3f n3 = average(n3a, n3b);
				Vector3f vn = average(n0, n1, n2, n3);
				Sys.debug(vn);
				nbuf.put(vn.x).put(vn.y).put(vn.z);
			}
		}
		nbuf.flip();
		return nbuf;
	}
	
	private Vector3f computeNormal(int index, int off0, int off1, int off2) {
		Vector3f dest = new Vector3f(0, 0, 0);
		Vector3f v0 = vertices[index + off0];
		Vector3f v1 = vertices[index + off1];
		Vector3f v2 = vertices[index + off2];
		GeometryUtils.normal(v0, v1, v2, dest);
		return dest;
	}
	
	private Vector3f average(Vector3f... vecs) {
		Vector3f dest = new Vector3f(0, 0, 0);
		for(Vector3f vec : vecs) {
			dest.add(vec, dest);
		}
		dest.div(6, dest);
		return dest.normalize();
	}
	
	//Bind the mesh-level vao, point attrib 1 to the model's
	//local ybuf
	//Call super to do any other transformations (super
	//calls mesh render)
	@Override
	public void render() {
		glBindVertexArray(mesh.vao);
		glBindBuffer(GL_ARRAY_BUFFER, ybo);
		glVertexAttribPointer(1, 1, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
        glBindBuffer(GL_ARRAY_BUFFER, nbo);
		glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
		super.render();
	}

}
