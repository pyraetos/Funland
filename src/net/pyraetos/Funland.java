package net.pyraetos;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import net.pyraetos.util.Sys;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Funland {

	//Options
	public static final boolean CULL_BACK = false;
	
	//State
	private long window;
	private Model quad1;
	private Model quad2;
	private Shader shader;
	
	//Framerate statistics
	long lastPrintTS;
	long previousTS;
	long currentTS;
	int nextIndex;
	double[] last30;
	
	public static void main(String[] args) {
		new Funland();
	}
	
	public Funland() {
		init();
		loop();
		cleanup();
	}
	
	private void update() {
		updateStats();
		quad1.translate(0.001f, 0.001f, -.01f);
		quad2.translate(-0.001f, -0.001f, -.01f);
		quad1.rotate(1, 1, 0, 1);
		quad2.rotate(1, 1, 1, 0);
		glfwPollEvents();
	}
	
	private void render() {
		shader.setEnabled(true);
		quad1.render();
		quad2.render();
		shader.setEnabled(false);
	}

	private void initEnvironment() {
		Mesh mesh = new TestQuad();
		quad1 = new Model(mesh);
		quad2 = new Model(mesh);
	}
	
	private void initShader() {
		shader = new BasicShader();
	}
	
	private void updateStats() {
		previousTS = currentTS;
		currentTS = Sys.time();
		double seconds = ((double)(currentTS - previousTS)) / 1000d;
		double framerate = 1d / seconds;
		last30[nextIndex] = framerate;
		nextIndex = (nextIndex + 1) % 30;
		if(currentTS - lastPrintTS > 1000) {
			double average = Sys.round(Sys.average(last30));
			glfwSetWindowTitle(window, "Funland - FPS: " + average);
			lastPrintTS = currentTS;
		}
	}

	private void initGL() {
		GLFWErrorCallback.createPrint(System.err).set();
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		window = glfwCreateWindow(800,600, "Funland", NULL, NULL);
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
		glEnable(GL_DEPTH_TEST);
		if(CULL_BACK) {
			glEnable(GL_CULL_FACE);
			glCullFace(GL_BACK);
		}
		
		
		previousTS = currentTS = lastPrintTS = Sys.time();
		nextIndex = 0;
		last30 = new double[30];
		
		glfwShowWindow(window);
	}
	
	private void init() {
		initGL();
		initEnvironment();
		initShader();
	}

	private void loop(){
		while (!glfwWindowShouldClose(window)) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
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