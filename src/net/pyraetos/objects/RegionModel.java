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
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

public class RegionModel extends Model{

	//Stores generated y values. Sent to shader in attrib ptr 1
	private int ybo;
	private int nbo;
	
	//Used to calculate normals on the fringe
	private Vector3f[][] vertices = new Vector3f[Region.SIDE + 2][Region.SIDE + 2];
	
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
		for(int xi = -1; xi < Region.SIDE + 1; xi++) {
			float curX = x + ((float)xi + off);
			int icurX = (int)Math.round(curX) + 512;
			
			for(int zi = -1; zi < Region.SIDE + 1; zi++) {
				float curZ = z + ((float)zi + off);
				int icurZ = (int)Math.round(curZ) + 512;
				Region.pg.generate(icurX, icurZ);
				float y = Region.pg.getValue(icurX, icurZ);
				vertices[xi + 1][zi + 1] = new Vector3f(curX, y, curZ);
				if(xi >= 0 && xi < Region.SIDE)
					if(zi >= 0 && zi < Region.SIDE) {
						ybuf.put(y);
					}
			}
		}
		ybuf.flip();
	}
	
	//Scans through region, computes adjacent 6 face normals,
	//averages to vertex normal
	private FloatBuffer computeNormals() {
		int side = Region.SIDE;
		FloatBuffer nbuf = BufferUtils.createFloatBuffer(3 * mesh.numVertices);
		for(int i = 1; i <= side; i++) {
			for(int j = 1; j <= side; j++) {
				Vector3f n0 = cross(to(i, j, i, j + 1), to(i, j, i + 1, j));
				Vector3f n1 = cross(to(i, j, i, j - 1), to(i, j, i - 1, j));
				
				Vector3f vn = average(n0, n1);
				nbuf.put(vn.x).put(vn.y).put(vn.z);
			}
		}
		nbuf.flip();
		return nbuf;
	}
	
	private static Vector3f cross(Vector3f a, Vector3f b) {
		float x = a.y * b.z - a.z * b.y;
		float y = a.z * b.x - a.x * b.z;
		float z = a.x * b.y - a.y * b.x;
		return new Vector3f(x, y, z);
	}
	
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
