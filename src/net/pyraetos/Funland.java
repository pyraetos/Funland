package net.pyraetos;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import net.pyraetos.objects.Mesh;
import net.pyraetos.objects.Model;
import net.pyraetos.objects.Region;
import net.pyraetos.objects.RegionModel;
import net.pyraetos.objects.TestCube;
import net.pyraetos.shaders.BasicShader;
import net.pyraetos.shaders.Shader;
import net.pyraetos.shaders.TerrainShader;
import net.pyraetos.util.Sys;

import java.nio.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
	private Model testCube;
	private Model cylinder;
	private Region regionMesh;
	private Map<Integer, Map<Integer, Model>> regions;
	private Set<Model> activeRegions;
	private int rX;
	private int rZ;
	
	//Shaders
	private Shader basic;
	private Shader terrain;
	
	//Framerate statistics
	long lastPrintTS;
	long previousTS;
	long currentTS;
	int nextIndex;
	double average;
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
		//TODO MOUSE SCROLL
		/*if(Mouse.scrolled())
			Camera.rotate(Mouse.getAngle(), 1f, 0f, 0f);*/
		
		//Window Title and Generate New Regions
		int newrX = ((int)Math.floor(Camera.x) / (Region.SIDE-1));
		int newrZ = ((int)Math.floor(Camera.z) / (Region.SIDE-1)); 
		if(newrX != rX || newrZ != rZ) {
			rX = newrX;
			rZ = newrZ;
			updateRegions();
		}
		glfwSetWindowTitle(window, "Funland - FPS: " + average +
				" | x = " + Sys.round1(Camera.x) + " y = " + Sys.round1(Camera.y) + " z = " + Sys.round1(Camera.z) +
				" | rX = " + rX + " rZ = " + rZ);
		
		//Logic
		testCube.translate(0.001f, -0.001f, -.005f);
		testCube.rotate(1f, 1f, 0f);
		cylinder.translate(-0.001f, 0.001f, -.005f);
		cylinder.rotate(.2f, .4f, .6f);
	}
	
	//Not super ideal to generate as a square.. should be circular around camera
	//Also updates active regions that ought to be rendered
	private void updateRegions() {
		activeRegions.clear();
		for(int i = rX-5; i < rX+6; i++) {
			for(int j = rZ-5; j < rZ+6; j++) {
				if(!regions.containsKey(i))
					regions.put(i, new HashMap<Integer, Model>());
				Map<Integer, Model> internalMap = regions.get(i);
				if(!internalMap.containsKey(j)) {
					RegionModel r = regionMesh.spawnModel(Region.SIDE * i - i, Region.SIDE * j - j);
					internalMap.put(j, r);
				}
				activeRegions.add(internalMap.get(j));
			}
		}
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
	
	private void handleScrollInput(double yoffset) {
		Mouse.scroll(yoffset);
	}
	
	private void render() {
		terrain.setEnabled(true);
		Camera.view();
		for(Model region : activeRegions)
			region.render();
		basic.setEnabled(true);
		Camera.view();//Simply don't call this to do a HUD
		testCube.render();
		cylinder.render();
		basic.setEnabled(false);
	}

	private void initEnvironment() {
		Mesh cubeMesh = new TestCube();
		testCube = cubeMesh.spawnModel();
		
		regionMesh = new Region();
		regions = new HashMap<Integer, Map<Integer, Model>>();
		activeRegions = new HashSet<Model>();
		
		Mesh cylMesh = ObjLoader.load("bcylinder.obj");
		cylinder = cylMesh.spawnModel();
		updateRegions();
	}
	
	private void initShaders() {
		basic = new BasicShader();
		terrain = new TerrainShader();
	}
	
	private void updateStats() {
		previousTS = currentTS;
		currentTS = Sys.time();
		double seconds = ((double)(currentTS - previousTS)) / 1000d;
		double framerate = 1d / seconds;
		last30[nextIndex] = framerate;
		nextIndex = (nextIndex + 1) % 30;
		if(currentTS - lastPrintTS > 1000) {
			average = Sys.round(Sys.average(last30));
			lastPrintTS = currentTS;
		}
	}

	private void initGL() {
		GLFWErrorCallback.createPrint(System.err).set();
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
		window = glfwCreateWindow(1200,900, "Funland", NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			handleKeyInput(key, action);
		});
		glfwSetScrollCallback(window, (window, xoffset, yoffset)->{
			handleScrollInput(yoffset);
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
		glClearColor(0.5f, 0.7f, 1.0f, 0.0f);
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
		initShaders();
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