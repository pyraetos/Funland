package net.pyraetos;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import net.pyraetos.util.Sys;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Funland {

	private long window;
	
	private FloatBuffer quad;
	private IntBuffer indices;
	private int vbo;
	private int ibo;
	
	private int shader;
	private Matrix4f modelView;
	private Matrix4f proj;
	
	public static void main(String[] args) {
		new Funland();
	}
	
	public Funland() {
		init();
		loop();
		cleanup();
	}
	
	private void update() {
		
	}
	
	private void render() {
	     glUseProgram(shader);
		 glEnableVertexAttribArray(0);
		 glBindBuffer(GL_ARRAY_BUFFER, vbo);
		 glVertexAttribPointer(0, 3, GL_FLOAT, false, 3*4, 0);
	     glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
	     glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
	     glBindBuffer(GL_ARRAY_BUFFER, 0);
	     glDisableVertexAttribArray(0);
	     glUseProgram(0);
	}

	private void initEnvironment() {
		//Create matrices
		proj = new Matrix4f();
		modelView = new Matrix4f();
		proj.perspective(Sys.toRadians(45), 1.3333333f, 0.1f, 1000f);
		modelView.identity();
		modelView.rotate(Sys.toRadians(20), 0, 0, 1);
		//modelView.rotate(Sys.toRadians(20), 0, 0, 1);
		
		//Create quad
		quad = BufferUtils.createFloatBuffer(3 * 4);
		quad.put(0f).put(0f).put(0f);
		quad.put(0f).put(1.0f).put(0f);
		quad.put(1.0f).put( 1.0f).put(0f);
		quad.put(1.0f).put( 0f).put(0f);
		quad.flip();
		
		indices = BufferUtils.createIntBuffer(6);
		indices.put(new int[] {0,1,2,0,2,3});
		indices.flip();
		
		
		vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, quad, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
        ibo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	private void initGL() {
		GLFWErrorCallback.createPrint(System.err).set();
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		window = glfwCreateWindow(800,600, "Hello World!", NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true);
		});

		// Get the thread stack and push a new frame
		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*
			glfwGetWindowSize(window, pWidth, pHeight);
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
			glfwSetWindowPos(
					window,
					(vidmode.width() - pWidth.get(0)) / 2,
					(vidmode.height() - pHeight.get(0)) / 2
					);
		}
		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);
		GL.createCapabilities();
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		glFrontFace(GL_CW);
		glCullFace(GL_BACK);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		glfwShowWindow(window);
	}

	private void initShaders() {
		int vs = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(vs, Sys.load("vertex.txt"));
		glCompileShader(vs);
		int cvs = glGetShaderi(vs, GL_COMPILE_STATUS);
		if(cvs == 0) {
			Sys.error("Shader compile error!\n" + glGetShaderInfoLog(vs));
		}
		
		int fs = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(fs, Sys.load("fragment.txt"));
		glCompileShader(fs);
		int cfs = glGetShaderi(fs, GL_COMPILE_STATUS);
		if(cfs == 0) {
			Sys.error("Shader compile error!\n" + glGetShaderInfoLog(vs));
		}
		
		int program = glCreateProgram();
        glAttachShader(program, vs);
        glAttachShader(program, fs);
        glLinkProgram(program);
        int linked = glGetProgrami(program, GL_LINK_STATUS);
        if(linked == 0) {
			Sys.error("Shader linking error!\n" + glGetShaderInfoLog(vs));
		}
        
		glUseProgram(program);
		int u = glGetUniformLocation(program, "proj");
		FloatBuffer projBuf = BufferUtils.createFloatBuffer(16);
		glUniformMatrix4fv(u, false, proj.get(projBuf));
		
		u = glGetUniformLocation(program, "modelView");
		FloatBuffer mvBuf = BufferUtils.createFloatBuffer(16);
		glUniformMatrix4fv(u, false, modelView.get(mvBuf));
        glUseProgram(0);
        
        this.shader = program;
	}
	
	private void init() {
		initGL();
		initEnvironment();
		initShaders();
	}

	private void loop(){
		while (!glfwWindowShouldClose(window)) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			glfwPollEvents();
			update();
			render();
			glfwSwapBuffers(window);
		}
	}
	
	private void cleanup() {
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

}