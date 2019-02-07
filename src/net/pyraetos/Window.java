package net.pyraetos;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;
import static net.pyraetos.Options.*;

import java.nio.IntBuffer;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

public abstract class Window{

	private static long window;
	
	public static void init(){
		initGLFW();
		initGL();
		glfwShowWindow(window);
	}
	
	private static void initGLFW() {
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
		if(V_SYNC) glfwSwapInterval(1);
	}
	
	private static void initGL() {
		GL.createCapabilities();
		glClearColor(0.5f, 0.7f, 1.0f, 0.0f);
		glFrontFace(GL_CW);
		glEnable(GL_DEPTH_TEST);
		if(SRGB) glEnable(GL_FRAMEBUFFER_SRGB);
	}
	
	private static void handleKeyInput(int key, int action) {
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
	
	private static void handleScrollInput(double yoffset) {
		Mouse.scroll(yoffset);
	}
	
	public static void setTitle(String title) {
		glfwSetWindowTitle(window, title);
	}
	
	public static void swapBuffers() {
		glfwSwapBuffers(window);
	}
	
	public static void pollEvents() {
		glfwPollEvents();
	}
	
	public static void close() {
		glfwSetWindowShouldClose(window, true);
	}
	
	public static boolean isClosing() {
		return glfwWindowShouldClose(window);
	}
	
	public static void cleanup() {
		glfwSetErrorCallback(null).free();
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
		glfwTerminate();
	}
	
}
