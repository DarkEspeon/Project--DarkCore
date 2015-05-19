package com.DarkEG.Core.Entity.Component;

import java.util.EventObject;

import org.lwjgl.input.Keyboard;

import com.DarkEG.Core.Core;
import com.DarkEG.Core.Entity.Entity;
import com.DarkEG.Core.Event.EventListener;
import com.DarkEG.Core.Event.Object.KeyEvent;
import com.DarkEG.Core.Event.Object.MouseEvent;

public class PlayerMoveComponent extends MoveComponent{
	private static EventListener key;
	private static EventListener mouse;
	private boolean keysDown[] = new boolean[Keyboard.KEYBOARD_SIZE];
	private float dx, dy;
	public PlayerMoveComponent(Entity e) {
		super(e);
		
		key = new EventListener(KeyEvent.class, this, "input");
		mouse = new EventListener(MouseEvent.class, this, "input");
		key.registerListener();
		mouse.registerListener();
		
	}

	public void update() {
		float dx = 0, dy = 0, dz = 0, dp = 0, dya = 0, dr = 0;
		if(keysDown[Keyboard.KEY_W] || keysDown[Keyboard.KEY_UP]) dz += RUN_SPEED;
		if(keysDown[Keyboard.KEY_S] || keysDown[Keyboard.KEY_DOWN]) dz -= RUN_SPEED;
		if(keysDown[Keyboard.KEY_A] || keysDown[Keyboard.KEY_LEFT]) dx += RUN_SPEED;
		if(keysDown[Keyboard.KEY_D] || keysDown[Keyboard.KEY_RIGHT]) dx -= RUN_SPEED;
		if(keysDown[Keyboard.KEY_SPACE]) dy -= RUN_SPEED;
		if(keysDown[Keyboard.KEY_LSHIFT]) dy += RUN_SPEED;
		if(keysDown[Keyboard.KEY_Q]) dr += RUN_SPEED;
		if(keysDown[Keyboard.KEY_E]) dr -= RUN_SPEED;
		dx *= Core.getDelta();
		dy *= Core.getDelta();
		dz *= Core.getDelta();
		dr *= Core.getDelta() * 2;
		dp += this.dy* Core.getDelta() * 2;
		dya -= this.dx * Core.getDelta() * 2;
		this.dx = 0;
		this.dy = 0;
		parent.Move(dx, dy, dz, dp, dya, dr);
	}
	public void input(EventObject e){
		if(e instanceof KeyEvent){ keysDown[((KeyEvent)e).getKeyCode()] = ((KeyEvent)e).wasPressed();}
		else if(e instanceof MouseEvent){
			this.dx += ((MouseEvent) e).getMouseDX();
			this.dy +=((MouseEvent) e).getMouseDY();
		}
	}
}
