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

import org.lwjgl.BufferUtils;

public class RegionModel extends Model{

	//Stores generated y values. Sent to shader in attrib ptr 1
	private int ybo;
	
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
        
        translate(x, 0f, z);
	}
	
	//Build ybuf with global PGenerate instance
	private void initYbuf(float x, float z, FloatBuffer ybuf) {
		for(int xi = 0; xi < 5; xi++) {
			float curX = x + ((float)xi - 2f);
			int icurX = (int)Math.round(curX) + 512;
			for(int zi = 0; zi < 5; zi++) {
				float curZ = z + ((float)zi - 2f);
				int icurZ = (int)Math.round(curZ) + 512;
				Region.pg.generate(icurX, icurZ);
				ybuf.put(2f*Region.pg.getValue(icurX, icurZ));
			}
		}
		ybuf.flip();
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
        glBindVertexArray(0);
		super.render();
	}

}
