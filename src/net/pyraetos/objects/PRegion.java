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

import net.pyraetos.pgenerate.PGenerate;
import net.pyraetos.util.Sys;

//PRegion is one to one mesh and model
//Should find better way to store mesh on GPU?
//Send generated value in VBO and let shader interpret?
public class PRegion extends Mesh{

	private static PGenerate pg;
	
	static {
		pg = new PGenerate(1024,1024);//Don't like hard size
	}
	
	public PRegion(float x, float z) {
		super();
		FloatBuffer fbuf = BufferUtils.createFloatBuffer(3 * 25);
		for(int xi = 0; xi < 5; xi++) {
			float curX = x + ((float)xi - 2f);
			int icurX = (int)Math.round(curX) + 512;
			for(int zi = 0; zi < 5; zi++) {
				float curZ = z + ((float)zi - 2f);
				int icurZ = (int)Math.round(curZ) + 512;
				pg.generate(icurX, icurZ);
				Sys.debug(curX + " " + 2f*pg.getValue(icurX, icurZ)+" " + curZ);
				fbuf.put(curX).put(2f*pg.getValue(icurX, icurZ)).put(curZ);
			}
		}
		fbuf.flip();
		
		IntBuffer indices = BufferUtils.createIntBuffer(6 * 16);
		int arr[] = {1,0,5,1,5,6};
		for(int i = 0; i < 19; i++) {
			for(int j = 0; j < 6; j++) {
				if(i != 4 && i != 9 && i != 14) {
					indices.put(arr[j]);
					System.out.print(arr[j] + " ");
				}
				arr[j]++;
			}
			Sys.debug();
		}
		indices.flip();
		
		
		vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, fbuf, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
        ibo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        
        numVertices = 25;
        numIndices = 16 * 6;
	}
	
}
