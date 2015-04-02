package com.DarkEG.Core.Render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import com.DarkEG.Core.ResourceManager;
import com.DarkEG.Core.Entity.Entity;
import com.DarkEG.Core.Model.Mesh;
import com.DarkEG.Core.Shader.Shader;
import com.DarkEG.Core.Util.Maths;

public class RenderCore {
	private static Entity camera;
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
	public static void setCamera(Entity cam){
		RenderCore.camera = cam;
	}
	public static void render(Entity sun){
		for(int x : entityRender.keySet()){
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
		}
		entityRender.clear();
	}
	
}
