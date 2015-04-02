package com.DarkEG.Core;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.vector.Vector3f;

import com.DarkEG.Core.Entity.Entity;
import com.DarkEG.Core.Entity.Component.LightComponent;
import com.DarkEG.Core.Entity.Component.PlayerMoveComponent;
import com.DarkEG.Core.Entity.Component.RenderComponent;
import com.DarkEG.Core.Input.KBInput;
import com.DarkEG.Core.Input.MouseInput;
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
	
	private static final int WIDTH = 1280;
	private static final int HEIGHT = 720;
	private static final int FPS_CAP = 120;
	
	private static long lastFrameTime;
	private static float delta;
	
	public static boolean OGL30 = false;
	public static boolean ARBVAO = false;
	
	public static boolean DepthDisabled = false;
	
	public MsgHandler msg = new MsgHandler();
	public KBInput KBI;
	public MouseInput MI;
	
	public Entity player = new Entity();
	public Entity camera = new Entity();
	public Entity sun = new Entity();
	
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
		
		sun.addComponent(new LightComponent(sun, new Vector3f(0.4f, 0.4f, 0.4f)), null);
		sun.Move(0, 200, 0);
		
		int sID = s.getID();
		int mID = m.getID();
		
		player.addComponent(new RenderComponent(player, mID, sID), null);
		player.Move(0, 0, -40);
		
		RenderCore.setCamera(camera);
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
			
			//player.Rotate(0, 0, 1);
			
			RenderCore.render(sun);
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
			Display.create(new PixelFormat(), attribs);
		} catch (Exception e){
			e.printStackTrace();
		}
		glViewport(0, 0, WIDTH, HEIGHT);
		
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
