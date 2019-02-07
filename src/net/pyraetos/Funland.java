package net.pyraetos;

import net.pyraetos.objects.BasicMesh;
import net.pyraetos.objects.DirectionalLight;
import net.pyraetos.objects.MeshIO;
import net.pyraetos.objects.Model;
import net.pyraetos.objects.RegionMesh;
import net.pyraetos.objects.Scene;
import net.pyraetos.objects.ShadowMapQuadMesh;
import net.pyraetos.objects.Terrain;
import net.pyraetos.objects.TestCube;
import static net.pyraetos.shaders.Shader.*;
import static net.pyraetos.Options.*;
import net.pyraetos.shaders.Shader;
import net.pyraetos.util.Sys;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Funland {

	//State
	private ShadowMap shadowMap;
	private Terrain terrain;
	private Model testCube;
	private Model cylinder;
	private Model house;
	private Scene scene;
	private Model smq;
	private DirectionalLight light;
	private int rX;
	private int rZ;
	private float x;
	private float y;
	private float z;
	
	public static void main(String[] args) {
		new Funland();
	}
	
	public Funland() {
		init();
		loop();
		cleanup();
	}
	
	private void loop(){
		while (!Window.isClosing()) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			update();
			render();
			Window.swapBuffers();
		}
	}
	
	private void init() {
		Statistics.init();
		Window.init();
		initEnvironment();
	}
	
	private void initEnvironment() {
		//Load shader classes so uniforms can be set
		Shader.enable(null);
		
		//Create the scene
		BasicMesh cubeMesh = new TestCube();
		testCube = cubeMesh.spawnModel();
		
		BasicMesh cylMesh = MeshIO.loadOBJ("cylinder");
		cylinder = cylMesh.spawnModel();
		
		BasicMesh houseMesh = MeshIO.loadOBJ("bigcubemany");
		house = houseMesh.spawnModel();
		house.translate(5f, 5f, -15f);
		
		ShadowMapQuadMesh smqMesh = new ShadowMapQuadMesh();
		smq = smqMesh.spawnModel();
		
		scene = new Scene();
		scene.addModels(testCube, cylinder, house);
		
		terrain = new Terrain(scene, 0, 0);
		
		light = new DirectionalLight(LIGHT_DIR);
		shadowMap = new ShadowMap(light);
		
		x = y = z = 0f;
	}

	private void update() {
		//Statistics
		Statistics.update();
		
		//Input
		Window.pollEvents();
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
		
		//Update player location
		if(Camera.translated()) {
			x = Camera.x;
			y = Camera.y;
			z = Camera.z;
			light.update(x, y, z);
		}
		
		//Window Title
		Window.setTitle("Funland - FPS: " + Statistics.getFPS() +
				" | x = " + Sys.round1(Camera.x) + " y = " + Sys.round1(Camera.y) + " z = " + Sys.round1(Camera.z) +
				" | rX = " + rX + " rZ = " + rZ);
		
		//Work on new region queue
		terrain.tryGenRegion();
		
		//Update regions
		int newrX = ((int)Math.floor(Camera.x) / (RegionMesh.SIDE-1));
		int newrZ = ((int)Math.floor(Camera.z) / (RegionMesh.SIDE-1)); 
		if(newrX != rX || newrZ != rZ) {
			rX = newrX;
			rZ = newrZ;
			terrain.reorderNewRegions(rX, rZ);
		}
		terrain.updateRegions();
		
		//Logic
		testCube.translate(0.001f, -0.001f, -.01f);
		testCube.rotate(1f, 1f, 0f);
		cylinder.translate(-0.001f, 0.001f, -.01f);
		cylinder.rotate(.2f, .4f, .6f);
	}
 
	private void render() {
		renderShadowMap();
		renderSMQ();
		renderScene();
	}
	
	private void renderShadowMap() {
		shadowMap.render(scene);
	}
	
	private void renderSMQ() {
		Shader.enable(SMQ);
		shadowMap.bind();
		Camera.view();
		smq.render();
		shadowMap.unbind();
	}
	
	private void renderScene() {
		Shader.enable(BASIC);
		shadowMap.bind();
		Camera.view();
		for(Model m : scene.models()) {
			if(Camera.isInFrontOfCamera(m)) m.render();
		}
		shadowMap.unbind();
	}
	
	private void cleanup() {
		Window.cleanup();
	}
	
}