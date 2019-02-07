package net.pyraetos;

import static net.pyraetos.shaders.Shader.SHADOW;
import static net.pyraetos.Options.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.ByteBuffer;

import net.pyraetos.objects.DirectionalLight;
import net.pyraetos.objects.Model;
import net.pyraetos.objects.Scene;
import net.pyraetos.shaders.Shader;

public class ShadowMap{

	private int sTex;
	private int sFBO;
	private DirectionalLight light;
	
	public ShadowMap(DirectionalLight light) {
		this.light = light;
		sTex = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, sTex);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, SHADOW_MAP_DIM, SHADOW_MAP_DIM, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (ByteBuffer)null);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, LINEAR_INTERP ? GL_LINEAR : GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, LINEAR_INTERP ? GL_LINEAR : GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
		glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, new float[] {1f, 1f, 1f, 1f});
		sFBO = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, sFBO);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, sTex, 0);
		glDrawBuffer(GL_NONE);
		glReadBuffer(GL_NONE);
		glBindTexture(GL_TEXTURE_2D, 0);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}
	
	public void render(Scene scene) {
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
		glBindFramebuffer(GL_FRAMEBUFFER, sFBO);
		glViewport(0, 0, SHADOW_MAP_DIM, SHADOW_MAP_DIM);
		glClear(GL_DEPTH_BUFFER_BIT);
		Shader.enable(SHADOW);
		light.useLightView();
		glBindTexture(GL_TEXTURE_2D, sTex);
		for(Model m : scene.models()) {
			m.render();
		}
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glViewport(0, 0, 1200, 900);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glDisable(GL_CULL_FACE);
		if(CULL_BACK) {
			glEnable(GL_CULL_FACE);
			glCullFace(GL_BACK);
		}
	}
	
	public void bind() {
		glBindTexture(GL_TEXTURE_2D, sTex);
		light.useLightView();
	}
	
	public void unbind() {
		glBindTexture(GL_TEXTURE_2D, 0);
	}
}
