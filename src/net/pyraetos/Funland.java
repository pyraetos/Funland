package net.pyraetos;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import net.pyraetos.objects.BasicMesh;
import net.pyraetos.objects.MeshIO;
import net.pyraetos.objects.Model;
import net.pyraetos.objects.RegionMesh;
import net.pyraetos.objects.TestCube;
import net.pyraetos.objects.TestQuad;

import static net.pyraetos.shaders.Shader.*;
import net.pyraetos.shaders.Shader;
import net.pyraetos.util.Sys;

import java.nio.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Funland {

	//Options
	public static final boolean CULL_BACK = false;
	public static final int TERRAIN_DISTANCE = 20;
	public static final boolean SRGB = false;
	
	//State
	private static long window;
	private Model testCube;
	private Model testQuad;
	private Model cylinder;
	private Model house;
	private Map<Integer, Map<Integer, Model>> regions;
	private Set<Model> activeRegions;
	private int rX;
	private int rZ;
	
	//Terrain generation logic
	private Set<Model> activeRegionsWaiting;
	private Queue<Tuple> newRegions;
	private long lastGenTS;
	private boolean needRegionUpdate;
	
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
		
		//Window Title
		glfwSetWindowTitle(window, "Funland - FPS: " + average +
				" | x = " + Sys.round1(Camera.x) + " y = " + Sys.round1(Camera.y) + " z = " + Sys.round1(Camera.z) +
				" | rX = " + rX + " rZ = " + rZ);
		
		//Work on new region queue
		tryGenRegion();
		
		//Update regions
		int newrX = ((int)Math.floor(Camera.x) / (RegionMesh.SIDE-1));
		int newrZ = ((int)Math.floor(Camera.z) / (RegionMesh.SIDE-1)); 
		if(newrX != rX || newrZ != rZ) {
			rX = newrX;
			rZ = newrZ;
			reorderNewRegions();
			needRegionUpdate = true;
		}
		updateRegions();
		
		//Logic
		testCube.translate(0.001f, -0.001f, -.005f);
		testCube.rotate(1f, 1f, 0f);
		cylinder.translate(-0.001f, 0.001f, -.005f);
		cylinder.rotate(.2f, .4f, .6f);
	}

	private void tryGenRegion() {
		if(!newRegions.isEmpty()) {
			long currTS = Sys.time();
			if(currTS - lastGenTS > 100) {
				Tuple t = newRegions.poll();
				Model r = new RegionMesh(RegionMesh.SIDE * t.i - t.i, RegionMesh.SIDE * t.j - t.j, true).spawnModel();
				regions.get(t.i).put(t.j, r);
				lastGenTS = currTS;
				needRegionUpdate = true;
			}
		}
	}

	private void reorderNewRegions() {
		if(newRegions.isEmpty()) return;
		Queue<Tuple> newQueue = new PriorityQueue<Tuple>();
		for(Tuple t : newRegions) newQueue.offer(t);
		newRegions = newQueue;
	}
	
	//Not super ideal to generate as a square.. should be circular around camera
	//Also updates active regions that ought to be rendered
	private void updateRegions() {
		if(needRegionUpdate) {
			activeRegionsWaiting = new HashSet<Model>();
			for(int i = rX - TERRAIN_DISTANCE; i < rX + TERRAIN_DISTANCE + 1; i++) {
				for(int j = rZ - TERRAIN_DISTANCE; j < rZ + TERRAIN_DISTANCE + 1; j++) {
					if(!regions.containsKey(i))
						regions.put(i, new HashMap<Integer, Model>());
					Map<Integer, Model> internalMap = regions.get(i);
					if(!internalMap.containsKey(j)) {
						Tuple t = new Tuple(i, j);
						if(!newRegions.contains(t)) newRegions.offer(t);
					}else{
						activeRegionsWaiting.add(internalMap.get(j));
					}
				}
			}
			activeRegions.clear();
			activeRegions = activeRegionsWaiting;
			needRegionUpdate = false;
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
		Shader.enable(BASIC);
		Camera.view();
		for(Model region : activeRegions) {
			region.render();
		}
		testCube.render();
		cylinder.render();
		house.render();
		Shader.enable(TEST);
		Camera.view();
		testQuad.render();
		Shader.disable(ACTIVE_SHADER);
	}

	private void initEnvironment() {
		BasicMesh cubeMesh = new TestCube();
		testCube = cubeMesh.spawnModel();
		
		BasicMesh quadMesh = new TestQuad();
		testQuad = quadMesh.spawnModel();
		
		regions = new HashMap<Integer, Map<Integer, Model>>();
		activeRegions = new HashSet<Model>();
		activeRegionsWaiting = new HashSet<Model>();
		newRegions = new PriorityQueue<Tuple>();
		lastGenTS = 0;
		
		BasicMesh cylMesh = MeshIO.loadOBJ("cylinder");
		cylinder = cylMesh.spawnModel();
		
		BasicMesh houseMesh = MeshIO.loadOBJ("bigcubemany");
		house = houseMesh.spawnModel();
		house.translate(5f, 5f, -15f);
		
		needRegionUpdate = true;
		updateRegions();
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
		if(SRGB) glEnable(GL_FRAMEBUFFER_SRGB);
		if(CULL_BACK) {
			glEnable(GL_CULL_FACE);
			glCullFace(GL_BACK);
		}
		
		int texture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, texture);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, 1024, 1024, 0, GL_DEPTH_COMPONENT, GL_FLOAT, 0);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		int framebuffer = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, framebuffer);
		glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, texture, 0);
		glDrawBuffer(GL_NONE);
		glReadBuffer(GL_NONE);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		
		previousTS = currentTS = lastPrintTS = Sys.time();
		nextIndex = 0;
		last30 = new double[30];
		
		glfwShowWindow(window);
	}
	
	private void init() {
		initGL();
		initEnvironment();
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

	private class Tuple implements Comparable<Tuple>{
		
		int i;
		int j;
		
		Tuple(int i, int j){
			this.i = i;
			this.j = j;
		}
		
		@Override
		public boolean equals(Object other) {
			if(other instanceof Tuple) {
				Tuple t = (Tuple)other;
				return t.i == i && t.j == j;
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			return (i << 16) ^ j;
		}
		@Override
		public int compareTo(Tuple o){
			double thisDist = Math.pow(rX - i, 2) + Math.pow(rZ - j, 2);
			double otherDist = Math.pow(rX - o.i, 2) + Math.pow(rZ - o.j, 2);
			if(thisDist < otherDist) return -1;
			if(thisDist > otherDist) return 1;
			return 0;
		}
		
	}
	
}