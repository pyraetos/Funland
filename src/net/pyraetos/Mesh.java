package net.pyraetos;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL20.*;

import java.nio.FloatBuffer;
import org.joml.Matrix4f;

public abstract class Mesh{

	protected int vbo;
	protected int ibo;
	protected int shader;
	protected int numVertices;
	protected int numIndices;
	protected Matrix4f modelViewMatrix;
	protected FloatBuffer modelView;
	protected int projectionUniform;
	protected int modelViewUniform;
	protected boolean updateUniforms;
	
	public Mesh(int shader) {
		this.shader = shader;
		glUseProgram(shader);
		projectionUniform = glGetUniformLocation(shader, "proj");
		modelViewUniform = glGetUniformLocation(shader, "modelView");
		glUniformMatrix4fv(projectionUniform, false, Matrices.PERSPECTIVE);
        glUseProgram(0);
        updateUniforms = false;
	}
	
	//Ultimately, modelview needs to be at individual model level... not mesh level
	public void translate(float x, float y, float z) {
		modelViewMatrix.translate(x, y, z);
		modelView = Matrices.toBuffer(modelViewMatrix);
		updateUniforms = true;
	}

	public void render() {
		glUseProgram(shader);
		if(updateUniforms) {
			glUniformMatrix4fv(modelViewUniform, false, modelView);
			updateUniforms = false;
		}
		glEnableVertexAttribArray(0);
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * numVertices, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
		glDrawElements(GL_TRIANGLES, numIndices, GL_UNSIGNED_INT, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glDisableVertexAttribArray(0);
		glUseProgram(0);
	}
}
