package net.pyraetos.objects;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public abstract class Mesh{

	protected int vao;
	protected int vbo;
	protected int ibo;
	protected int numVertices;
	protected int numIndices;
	
	public void render() {
		glBindVertexArray(vao);
		glEnableVertexAttribArray(0);
		specialRender();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
		glDrawElements(GL_TRIANGLES, numIndices, GL_UNSIGNED_INT, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glDisableVertexAttribArray(0);
		glBindVertexArray(0);
	}
	
	public Model spawnModel() {
		return new Model(this);
	}
	
	protected abstract void specialRender();
}
