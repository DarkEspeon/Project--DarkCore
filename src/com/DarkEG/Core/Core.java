package com.DarkEG.Core;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.Sys;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import com.DarkEG.Core.Entity.Entity;
import com.DarkEG.Core.Entity.Component.HudComponent;
import com.DarkEG.Core.Entity.Component.PlayerMoveComponent;
import com.DarkEG.Core.Entity.Component.RenderComponent;
import com.DarkEG.Core.Input.KBInput;
import com.DarkEG.Core.Input.MouseInput;
import com.DarkEG.Core.Light.Light;
import com.DarkEG.Core.Model.Mesh;
import com.DarkEG.Core.Render.RenderCore;
import com.DarkEG.Core.Resources.ResourceManager;
import com.DarkEG.Core.Resources.TextureManager;
import com.DarkEG.Core.Texture.Texture;
import com.DarkEG.Core.Util.Maths;
import com.DarkEG.Core.Util.OBJLoader;

public class Core implements Runnable {
	
	public static final int SRCALPHA = GL_SRC_ALPHA;
	public static final int ONEMINUSSRCALPHA = GL_ONE_MINUS_SRC_ALPHA;
	
	private static Thread gameThread;
	public static Core core;
	
	public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;
	public static final int FPS_CAP = 120;
	
	private static int frames = 0;
	private static int updates = 0;
	private static long lastFPSTime = System.currentTimeMillis();
	
	private static long lastFrameTime;
	private static float delta;
	
	public static boolean OGL30 = false;
	public static boolean ARBVAO = false;
	private static boolean running = true;
	
	public static boolean DepthDisabled = false;
	public static int quad;
	
	public KBInput kbi = new KBInput();
	public MouseInput mi = new MouseInput();
	
	public static ResourceManager rm = new ResourceManager();
	
	public Entity player = new Entity();
	public Entity camera = new Entity();
	public Light sun = new Light(new Vector3f(0.4f, 0.4f, 0.4f), new Vector3f(1, 0.1f, 0.01f), new Vector3f(0, 20, 0));
	
	public Mesh m = null;
	
	public Core(){
		Maths.setUp();
	}
	
	public void start(){
		gameThread = new Thread(this, "DarkEGGameEngine");
		gameThread.start();
	}
	public void init(){
		Maths.createProjectionMatrix();
		
		quad = rm.vm.createVAO();
		int tempV = rm.vm.createVBO();
		rm.vm.bindVAO(quad);
		
		glBindBuffer(GL_ARRAY_BUFFER, tempV);
		FloatBuffer buf = BufferUtils.createFloatBuffer(8);
		buf.put(-1.0f).put(1.0f).put(-1.0f).put(-1.0f).put(1.0f).put(1.0f).put(1.0f).put(-1.0f);
		buf.flip();
		glBufferData(GL_ARRAY_BUFFER, buf, GL_STATIC_DRAW);
		glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
		
		rm.vm.unbindVAO();
		
		Texture temp = rm.tm.loadTexture(TextureManager.TEX2D, "ShipOne");
		Texture temp2 = rm.tm.loadTexture(TextureManager.TEX2D, "Crosshair");
		m = OBJLoader.loadMeshOBJ("ShipOne").setTexture(temp).createVAO();
		camera.addComponent(new PlayerMoveComponent(camera));
		
		int mID = m.getID();
		
		player.addComponent(new RenderComponent(player, mID));
		player.addComponent(new HudComponent(player, temp2.getID(), new Vector2f(0f, 0f), new Vector2f(.01f, .02f)));
		
		player.Move(0, 0, 0);
		
		RenderCore.addLight(sun);
		RenderCore.setCamera(camera);
	}
	public static void renderQuad(){
		rm.vm.bindVAO(quad);
		glEnableVertexAttribArray(0);
		glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
		glDisableVertexAttribArray(0);
		rm.vm.unbindVAO();
	}
	public void run(){
		createDisplay();
		init();
		camera.setPos(0, 0, 0);
		while(!Display.isCloseRequested()){
			if(System.currentTimeMillis() - Core.lastFPSTime > 1000){
				Core.lastFPSTime += 1000;
				Display.setTitle("FPS: " + Core.frames + " | UPS: " + Core.updates);
				Core.frames = 0;
				Core.updates = 0;
			}
			Core.frames++;
			Core.updates++;
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			kbi.update();
			mi.update();
			
			camera.update();
			Maths.createViewMatrix(camera);
			player.update();
			
			RenderCore.render();
			player.renderHUD();
			updateDisplay();
		}
		running = false;
		rm.cleanUp();
		destoryDisplay();
	}
	public static void main(String[] args){
		core = new Core();
		core.start();
	}
	public static boolean isRunning(){
		return running;
	}
	public void createDisplay(){
		ContextAttribs attribs = new ContextAttribs(3, 0).withForwardCompatible(true);
		try {
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			Display.create(new PixelFormat(0, 8, 0, 0), attribs);
		} catch (Exception e){
			e.printStackTrace();
		}
		glViewport(0, 0, WIDTH, HEIGHT);
		glDisable(GL_MULTISAMPLE);
		enableDepth();
		glClearColor(0.5f, 0.5f, 0.5f, 1f);
		
		OGL30 = GLContext.getCapabilities().OpenGL30;
		ARBVAO = GLContext.getCapabilities().GL_ARB_vertex_array_object;
		
		lastFrameTime = getCurrentTime();
	}
	public void enableDepth(){
		glEnable(GL_DEPTH_TEST);
		DepthDisabled = false;
	}
	public void disableDepth(){
		glDisable(GL_DEPTH_TEST);
		DepthDisabled = true;
	}
	public void enableBlend(){
		glEnable(GL_BLEND);
	}
	public void disableBlend(){
		glDisable(GL_BLEND);
	}
	public void blendFunc(int src, int dst){
		glBlendFunc(src, dst);
	}
	public void updateDisplay(){
		Display.sync(FPS_CAP);
		Display.update();
		long currentFrameTime = getCurrentTime();
		delta = (currentFrameTime - lastFrameTime) / 1000f;
		lastFrameTime = currentFrameTime;
	}
	public void destoryDisplay(){
		Display.destroy();
	}
	public static float getDelta(){
		return delta;
	}
	public static long getCurrentTime(){
		return Sys.getTime() * 1000 / Sys.getTimerResolution();
	}
}
