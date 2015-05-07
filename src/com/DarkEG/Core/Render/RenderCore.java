package com.DarkEG.Core.Render;

import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2f;
import static org.lwjgl.opengl.GL14.GL_FUNC_ADD;
import static org.lwjgl.opengl.GL14.glBlendEquation;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL30.glBlitFramebuffer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.DarkEG.Core.Core;
import com.DarkEG.Core.ResourceManager;
import com.DarkEG.Core.Entity.Entity;
import com.DarkEG.Core.Light.Light;
import com.DarkEG.Core.Model.Mesh;
import com.DarkEG.Core.Shader.Shader;
import com.DarkEG.Core.Util.FBO;
import com.DarkEG.Core.Util.Maths;

public class RenderCore {
	private static Entity camera;
	private static FBO fbo = new FBO();
	private static Shader preprocess;
	private static Shader lighting;
	public static class Render{
		private Entity e;
		private Shader s;
		public Render(Entity e, Shader s){
			this.e = e;
			this.s = s;
		}
		public Entity getEntity(){return e;}
		public Shader getShader(){return s;}
	}
	private static Map<Integer, List<Render>> entityRender = new HashMap<>();
	private static List<Light> lights = new ArrayList<>();
	static{
		preprocess = new Shader()
			.addSubShader("src/com/DarkEG/Shaders/preprocesser.vs", GL_VERTEX_SHADER)
			.addSubShader("src/com/DarkEG/Shaders/preprocesser.fs", GL_FRAGMENT_SHADER)
			.createProgram()
			.bindAttribute(0, "pos")
			.bindAttribute(1, "texCoord")
			.bindAttribute(2, "norm")
			.finalizeProgram()
			.getUniform("transMat")
			.getUniform("viewMat")
			.getUniform("projMat");
		
		preprocess.start();
		preprocess.loadUniform("projMat", Maths.getProjectionMatrix());
		preprocess.stop();
		lighting = new Shader()
			.addSubShader("src/com/DarkEG/Shaders/lighting.vs", GL_VERTEX_SHADER)
			.addSubShader("src/com/DarkEG/Shaders/lighting.fs", GL_FRAGMENT_SHADER)
			.createProgram()
			.bindAttribute(0, "pos")
			.finalizeProgram()
			.getUniform("viewMat")
			.getUniform("colorBuff")
			.getUniform("normalBuff")
			.getUniform("posBuff")
			.getUniform("depthBuff")
			.getUniform("att")
			.getUniform("lightPos")
			.getUniform("lightCol")
			.getUniform("shineDamper")
			.getUniform("reflectivity")
			.getUniform("cameraPos");
		
		lighting.start();
		lighting.loadUniform("colorBuff", 0);
		lighting.loadUniform("normalBuff", 1);
		lighting.loadUniform("posBuff", 2);
		lighting.loadUniform("depthBuff", 3);
		lighting.stop();
		
		fbo.addColorAttachment();
		fbo.addColorAttachment();
		fbo.addColorAttachment();
		fbo.addColorAttachment();
		fbo.finalizeBuffer();
	}
	public static void processEntity(int mesh, Entity e, Shader s){
		if(entityRender.containsKey(mesh)){
			List<Render> entities = entityRender.get(mesh);
			entities.add(new Render(e, s));
		} else {
			List<Render> entities = new ArrayList<>();
			entities.add(new Render(e, s));
			entityRender.put(mesh, entities);
		}
	}
	public static void addLight(Light l){
		lights.add(l);
	}
	public static void setCamera(Entity cam){
		RenderCore.camera = cam;
	}
	public static void render(Entity sun){
		/*for(int x : entityRender.keySet()){
			List<Render> es = entityRender.get(x);
			Mesh m = ResourceManager.getMesh(x);
			m.preLoad();
			for(Render e : es){
				e.getShader().start();
				e.getShader().loadUniform("transMat", e.getEntity().getModelMatrix())
					.loadUniform("viewMat", camera.getViewMatrix())
					.loadUniform("lightPosition", sun.getPosition())
					.loadUniform("lightColor", sun.getLight().getLight().getColor())
					.loadUniform("attenuation", sun.getLight().getLight().getAttenuation())
					.loadUniform("shineDamper", 5)
					.loadUniform("reflectivity", 1)
					.loadUniform("skyColor", new Vector3f(0.5f, 0.5f, 0.5f));
				m.render();
				e.getShader().stop();
			}
			m.postRender();
		}*/
		rendertemp();
		entityRender.clear();
	}
	public static void rendertemp(){
		GeoPass();
		LightPass();
		FBO.bindDefaultBuffer();
		glClearColor(0f, 0f, 0f, 1f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glClearColor(0.5f, 0.5f, 0.5f, 1f);
		fbo.bindReadBuff();
		fbo.unbindDrawBuff();
		//fbo.setReadBuffer(0);
		//glBlitFramebuffer(0, 0, Core.WIDTH, Core.HEIGHT, 0, 0, Core.WIDTH, Core.HEIGHT, GL_COLOR_BUFFER_BIT, GL_LINEAR);
		//fbo.setReadBuffer(1);
		//glBlitFramebuffer(0, 0, Core.WIDTH, Core.HEIGHT, 0, 0, Core.WIDTH, Core.HEIGHT, GL_COLOR_BUFFER_BIT, GL_LINEAR);
		//fbo.setReadBuffer(2);
		//glBlitFramebuffer(0, 0, Core.WIDTH, Core.HEIGHT, 0, 0, Core.WIDTH, Core.HEIGHT, GL_COLOR_BUFFER_BIT, GL_LINEAR);
		fbo.setReadBuffer(3);
		glBlitFramebuffer(0, 0, Core.WIDTH, Core.HEIGHT, 0, 0, Core.WIDTH, Core.HEIGHT, GL_COLOR_BUFFER_BIT, GL_LINEAR);
	}
	private static void GeoPass(){
		fbo.bindDrawBuff();
		fbo.setDrawBuffers(new int[] {0, 1, 2});
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
		preprocess.start();
		preprocess.loadUniform("viewMat", camera.getViewMatrix());
		for(int x : entityRender.keySet()){
			List<Render> es = entityRender.get(x);
			Mesh m = ResourceManager.getMesh(x);
			m.preLoad();
			for(Render e : es){
				preprocess.loadUniform("transMat", e.getEntity().getModelMatrix());
				m.render();
			}
			m.postRender();
		}
		preprocess.stop();
		//render model with preprocess shader
		
		glDisable(GL_CULL_FACE);
		glDisable(GL_DEPTH_TEST);
	}
	private static void LightPass(){
		fbo.bindDrawBuff();
		fbo.bindReadBuff();
		fbo.setDrawBuffer(3);
		fbo.setReadBuffer(0);
		glBlitFramebuffer(0, 0, Core.WIDTH, Core.HEIGHT, 0, 0, Core.WIDTH, Core.HEIGHT, GL_COLOR_BUFFER_BIT, GL_LINEAR);
		
		fbo.unbindReadBuff();
		
		fbo.bindAttachTex(0, 0);
		fbo.bindAttachTex(1, 1);
		fbo.bindAttachTex(2, 2);
		fbo.bindDepthTex(3);
		
		glEnable(GL_BLEND);
		glBlendEquation(GL_FUNC_ADD);
		glBlendFunc(GL_ONE, GL_ONE);
		
		lighting.start();
		lighting.loadUniform("viewMat", camera.getViewMatrix());
		lighting.loadUniform("cameraPos", camera.getPosition());
		lighting.loadUniform("shineDamper", 5f);
		lighting.loadUniform("reflectivity", 1f);
		for(Light l : lights){
			lighting.loadUniform("lightCol", l.getColor());
			lighting.loadUniform("lightPos", l.getPos());
			lighting.loadUniform("att", l.getAttenuation());
			Core.renderQuad();
		}
		lighting.stop();
		
		glDisable(GL_BLEND);
	}
}
