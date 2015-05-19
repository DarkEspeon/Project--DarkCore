package com.DarkEG.Core.Render;

import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.GL_TEXTURE3;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL14.GL_FUNC_ADD;
import static org.lwjgl.opengl.GL14.glBlendEquation;
import static org.lwjgl.opengl.GL30.glBlitFramebuffer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import com.DarkEG.Core.Core;
import com.DarkEG.Core.Entity.Entity;
import com.DarkEG.Core.Light.Light;
import com.DarkEG.Core.Model.Mesh;
import com.DarkEG.Core.Resources.ResourceManager;
import com.DarkEG.Core.Resources.ShaderManager;
import com.DarkEG.Core.Shader.Shader;
import com.DarkEG.Core.Util.FBO;
import com.DarkEG.Core.Util.Maths;

public class RenderCore {
	private static Entity camera;
	private static FBO fbo = new FBO();
	private static Shader preprocess;
	private static Shader lighting;
	private static Shader dir;
	public static Shader gui;
	private static Map<Integer, List<Entity>> entityRender = new HashMap<>();
	private static List<Light> lights = new ArrayList<>();
	static{
		preprocess = new Shader();
		Core.rm.sm.addSubShader(preprocess, "src/com/DarkEG/Shaders/preprocesser.vs", ShaderManager.VERT);
		Core.rm.sm.addSubShader(preprocess, "src/com/DarkEG/Shaders/preprocesser.fs", ShaderManager.FRAG);
		preprocess.createProgram()
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
		lighting = new Shader();
		Core.rm.sm.addSubShader(lighting, "src/com/DarkEG/Shaders/lighting.vs", ShaderManager.VERT);
		Core.rm.sm.addSubShader(lighting, "src/com/DarkEG/Shaders/lighting.fs", ShaderManager.FRAG);
		lighting.createProgram()
			.bindAttribute(0, "pos")
			.finalizeProgram()
			.getUniform("viewMat")
			.getUniform("colorBuff")
			.getUniform("normalBuff")
			.getUniform("posBuff")
			.getUniform("depthBuff")
			.getUniform("lightRadius")
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
		
		dir = new Shader();
		Core.rm.sm.addSubShader(dir, "src/com/DarkEG/Shaders/lighting.vs", ShaderManager.VERT);
		Core.rm.sm.addSubShader(dir, "src/com/DarkEG/Shaders/dirlight.fs", ShaderManager.FRAG);
		dir.createProgram()
			.bindAttribute(0, "pos")
			.finalizeProgram()
			.getUniform("viewMat")
			.getUniform("colorBuff")
			.getUniform("normalBuff")
			.getUniform("posBuff")
			.getUniform("depthBuff")
			.getUniform("lightPos")
			.getUniform("lightCol")
			.getUniform("shineDamper")
			.getUniform("reflectivity")
			.getUniform("cameraPos");
		
		dir.start();
		dir.loadUniform("colorBuff", 0);
		dir.loadUniform("normalBuff", 1);
		dir.loadUniform("posBuff", 2);
		dir.loadUniform("depthBuff", 3);
		dir.stop();
		
		gui = new Shader();
		Core.rm.sm.addSubShader(gui, "src/com/DarkEG/Shaders/gui.vs", ShaderManager.VERT);
		Core.rm.sm.addSubShader(gui, "src/com/DarkEG/Shaders/gui.fs", ShaderManager.FRAG);
		gui.createProgram()
			.bindAttribute(0, "pos")
			.finalizeProgram()
			.getUniform("transMat");
		
		fbo.addColorAttachment();
		fbo.addColorAttachment();
		fbo.addColorAttachment();
		fbo.addColorAttachment();
		fbo.finalizeBuffer();
	}
	public static void processEntity(int mesh, Entity e){
		if(entityRender.containsKey(mesh)){
			List<Entity> entities = entityRender.get(mesh);
			entities.add(e);
		} else {
			List<Entity> entities = new ArrayList<>();
			entities.add(e);
			entityRender.put(mesh, entities);
		}
	}
	public static void addLight(Light l){
		lights.add(l);
	}
	public static void setCamera(Entity cam){
		RenderCore.camera = cam;
	}
	public static void render(){
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
		entityRender.clear();
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
			List<Entity> es = entityRender.get(x);
			Mesh m = ResourceManager.getMesh(x);
			m.preLoad();
			for(Entity e : es){
				preprocess.loadUniform("transMat", e.getModelMatrix());
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
			lighting.loadUniform("lightRadius", Maths.getLightDist(l.getColor(), l.getAttenuation()));
			lighting.loadUniform("lightCol", l.getColor());
			lighting.loadUniform("lightPos", l.getPos());
			lighting.loadUniform("att", l.getAttenuation());
			Core.renderQuad();
		}
		lighting.stop();
		
		dir.start();
		dir.loadUniform("viewMat", camera.getViewMatrix());
		dir.loadUniform("cameraPos", camera.getPosition());
		dir.loadUniform("shineDamper", 5f);
		dir.loadUniform("reflectivity", 0.1f);
		dir.loadUniform("lightCol", new Vector3f(0.3f, 0.3f, 0.3f));
		dir.loadUniform("lightPos", new Vector3f(0, 0, -1));
		Core.renderQuad();
		dir.stop();
		
		glActiveTexture(GL_TEXTURE0); glBindTexture(GL_TEXTURE_2D, 0);
		glActiveTexture(GL_TEXTURE1); glBindTexture(GL_TEXTURE_2D, 0);
		glActiveTexture(GL_TEXTURE2); glBindTexture(GL_TEXTURE_2D, 0);
		glActiveTexture(GL_TEXTURE3); glBindTexture(GL_TEXTURE_2D, 0);
		
		glDisable(GL_BLEND);
	}
}
