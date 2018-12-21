package net.pyraetos;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import net.pyraetos.objects.Mesh;
import net.pyraetos.objects.Model;
import net.pyraetos.objects.PRegion;
import net.pyraetos.objects.TestQuad;
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
	private static long window;
	private Model quad1;
	private Model quad2;
	private Model region;
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
		//Statistics
		updateStats();
		
		//Input
		glfwPollEvents();
		if(Keyboard.pressed(GLFW_KEY_W))
			Camera.translate(0f, 0f, -0.1f);
		if(Keyboard.pressed(GLFW_KEY_S))
			Camera.translate(0f, 0f, 0.1f);
		if(Keyboard.pressed(GLFW_KEY_A))
			Camera.rotate(1.5f, 0, 1, 0);
		if(Keyboard.pressed(GLFW_KEY_D))
			Camera.rotate(-1.5f, 0, 1, 0);
		if(Keyboard.pressed(GLFW_KEY_SPACE))
			Camera.translate(0f, 0.1f, 0f);
		if(Keyboard.pressed(GLFW_KEY_LEFT_SHIFT))
			Camera.translate(0f, -0.1f, 0f);
		
		
		//Logic
		quad1.translate(0.001f, 0.001f, -.01f);
		quad2.translate(-0.001f, -0.001f, -.01f);
		quad1.rotate(1, 1, 0, 1);
		quad2.rotate(1, 1, 1, 0);
	}
	
	private void handleKeyInput(int key, int action) {
		if(action == GLFW_PRESS) {
			Keyboard.press(key);
		}else
		if(action == GLFW_RELEASE) {
			Keyboard.release(key);
		}
		if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
			close();
		}
			
	}
	
	private void render() {
		shader.setEnabled(true);
		Camera.view();
		quad1.render();
		quad2.render();
		region.render();
		shader.setEnabled(false);
	}

	private void initEnvironment() {
		Mesh mesh = new TestQuad();
		quad1 = mesh.spawnModel();
		quad2 = mesh.spawnModel();
		Mesh mesh2 = new PRegion(0,0);
		region = mesh2.spawnModel();
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
			handleKeyInput(key, action);
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
	
	public static void close() {
		glfwSetWindowShouldClose(window, true);
	}
	
	private void cleanup() {
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

}