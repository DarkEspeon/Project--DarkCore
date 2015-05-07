package com.DarkEG.Core;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
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
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.vector.Vector3f;

import com.DarkEG.Core.Entity.Entity;
import com.DarkEG.Core.Entity.Component.PlayerMoveComponent;
import com.DarkEG.Core.Entity.Component.RenderComponent;
import com.DarkEG.Core.Input.KBInput;
import com.DarkEG.Core.Input.MouseInput;
import com.DarkEG.Core.Light.Light;
import com.DarkEG.Core.Model.Mesh;
import com.DarkEG.Core.Msg.MessageFlags;
import com.DarkEG.Core.Msg.MsgHandler;
import com.DarkEG.Core.Msg.MsgReceiver;
import com.DarkEG.Core.Render.RenderCore;
import com.DarkEG.Core.Shader.Shader;
import com.DarkEG.Core.Texture.Texture;
import com.DarkEG.Core.Util.Maths;
import com.DarkEG.Core.Util.OBJLoader;

public class Core implements Runnable {
	private static Thread gameThread;
	public static Core core;
	
	public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;
	public static final int FPS_CAP = 120;
	
	private static long lastFrameTime;
	private static float delta;
	
	public static boolean OGL30 = false;
	public static boolean ARBVAO = false;
	
	public static boolean DepthDisabled = false;
	public static int quad;
	
	public MsgHandler msg = new MsgHandler();
	public KBInput KBI;
	public MouseInput MI;
	
	public Entity player = new Entity();
	public Entity camera = new Entity();
	public Light sun = new Light(new Vector3f(0.4f, 0.4f, 0.4f), new Vector3f(1, 0.01f, 0.002f), new Vector3f(0, 20, 0));
	public Light l2 = new Light(new Vector3f(1f, 0f, 0f), new Vector3f(1, 0.01f, 0.002f), new Vector3f(20, 0, 0));
	public Light l3 = new Light(new Vector3f(0f, 1f, 0f), new Vector3f(1, 0.01f, 0.002f), new Vector3f(0, -20, 0));
	public Light l4 = new Light(new Vector3f(0f, 0f, 1f), new Vector3f(1, 0.01f, 0.002f), new Vector3f(-20, 0, 0));
	
	public Mesh m = null;
	public Shader s = null;
	
	public Core(){
		
		KBInput.keyTypes.add("Movement", 0);
		MouseInput.keyTypes.add("Movement", 0);
		
		Maths.setUp();
		KBI = new KBInput();
		MI = new MouseInput();
		List<Integer> Keys = new ArrayList<>();
		
		Keys.add(Keyboard.KEY_W);
		Keys.add(Keyboard.KEY_A);
		Keys.add(Keyboard.KEY_S);
		Keys.add(Keyboard.KEY_D);
		Keys.add(Keyboard.KEY_Q);
		Keys.add(Keyboard.KEY_E);
		Keys.add(Keyboard.KEY_SPACE);
		Keys.add(Keyboard.KEY_LSHIFT);
		
		KBI.registerKeys(KBInput.keyTypes.getY("Movement"), Keys);
		
		Keys = new ArrayList<>();
		Keys.add(MouseInput.DX);
		Keys.add(MouseInput.DY);
		
		MI.registerData(MouseInput.keyTypes.getY("Movement"), Keys);
	}
	
	public void start(){
		gameThread = new Thread(this, "DarkEGGameEngine");
		gameThread.start();
	}
	public void init(){
		Maths.createProjectionMatrix();
		
		quad = ResourceManager.createVAO();
		int tempI = ResourceManager.createVBO();
		int tempV = ResourceManager.createVBO();
		ResourceManager.bindVAO(quad);
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, tempI);
		IntBuffer buffer = BufferUtils.createIntBuffer(6);
		buffer.put(0).put(1).put(2).put(2).put(1).put(3);
		buffer.flip();
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		
		glBindBuffer(GL_ARRAY_BUFFER, tempV);
		FloatBuffer buf = BufferUtils.createFloatBuffer(8);
		buf.put(-1.0f).put(1.0f).put(1.0f).put(1.0f).put(1.0f).put(-1.0f).put(-1.0f).put(-1.0f);
		buf.flip();
		glBufferData(GL_ARRAY_BUFFER, buf, GL_STATIC_DRAW);
		glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
		
		ResourceManager.unbindVAO();
		
		s = new Shader()
			.addSubShader("src/com/DarkEG/Shaders/Shader.vs", GL_VERTEX_SHADER)
			.addSubShader("src/com/DarkEG/Shaders/Shader.fs", GL_FRAGMENT_SHADER)
			.createProgram()
			.bindAttribute(0, "position")
			.bindAttribute(1, "uv")
			.bindAttribute(2, "norm")
			.finalizeProgram()
			.getUniform("projMat")
			.getUniform("transMat")
			.getUniform("viewMat")
			.getUniform("lightPosition")
			.getUniform("lightColor")
			.getUniform("attenuation")
			.getUniform("shineDamper")
			.getUniform("reflectivity")
			.getUniform("skyColor");
		s.start();
		s.loadUniform("projMat", Maths.getProjectionMatrix());
		s.stop();
		m = OBJLoader.loadMeshOBJ("ShipOne").setTexture(Texture.loadTexture(GL_TEXTURE_2D, "ShipOne")).createVAO();

		ArrayList<Integer> keys = new ArrayList<Integer>();
		ArrayList<Integer> mouse = new ArrayList<Integer>();
		keys.add(KBI.keyTypes.getY("Movement"));
		mouse.add(MouseInput.keyTypes.getY("Movement"));
		
		camera.addComponent(new PlayerMoveComponent(camera, keys, mouse), null);
		
		int sID = s.getID();
		int mID = m.getID();
		
		player.addComponent(new RenderComponent(player, mID, sID), null);
		player.Move(0, 0, 0);
		
		RenderCore.addLight(sun);
		RenderCore.setCamera(camera);
	}
	public static void renderQuad(){
		ResourceManager.bindVAO(quad);
		glEnableVertexAttribArray(0);
		glDrawArrays(GL_TRIANGLES, 0, 6);
		glDisableVertexAttribArray(0);
		ResourceManager.unbindVAO();
	}
	public void run(){
		createDisplay();
		init();
		camera.setPos(0, 0, 0);
		while(!Display.isCloseRequested()){
			Display.setTitle("" + Core.getDelta());
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			KBI.update();
			MI.update();
			
			camera.update();
			Maths.createViewMatrix(camera);
			player.update();
			
			player.Move(0, 0, 0, 0, 0, 1);
			
			RenderCore.render(null);
			updateDisplay();
		}
		msg.cleanUp();
		ResourceManager.cleanUp();
		destoryDisplay();
	}
	public static void sendMsg(MessageFlags flag, String msg){
		core.msg.sendMsg(flag, msg);
	}
	public static void register(MessageFlags flag, MsgReceiver rec){
		core.msg.register(flag, rec);
	}
	public static void main(String[] args){
		core = new Core();
		core.start();
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
