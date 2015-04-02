package com.DarkEG.Core.Entity.Component;

import java.util.List;

import com.DarkEG.Core.Core;
import com.DarkEG.Core.Entity.Entity;
import com.DarkEG.Core.Input.KBHandler;
import com.DarkEG.Core.Input.KBInput;
import com.DarkEG.Core.Input.MouseHandler;
import com.DarkEG.Core.Input.MouseInput;
import com.DarkEG.Core.Msg.MessageFlags;

public class PlayerMoveComponent extends MoveComponent implements KBHandler, MouseHandler{
	private boolean keysDown[] = new boolean[30];
	private float dx, dy;
	public PlayerMoveComponent(Entity e, List<Integer> types, List<Integer> mTypes) {
		super(e);
		for(Integer k : types){
			Core.core.KBI.register(k, this);
		}
		for(Integer k : mTypes){
			Core.core.MI.register(k, this);
		}
	}

	public void update() {
		float dx = 0, dy = 0, dz = 0, dp = 0, dya = 0, dr = 0;
		if(keysDown[UP]) dz += RUN_SPEED;
		if(keysDown[DOWN]) dz -= RUN_SPEED;
		if(keysDown[LEFT]) dx += RUN_SPEED;
		if(keysDown[RIGHT]) dx -= RUN_SPEED;
		if(keysDown[SPACE]) dy -= RUN_SPEED;
		if(keysDown[SHIFT]) dy += RUN_SPEED;
		if(keysDown[Q]) dr += RUN_SPEED;
		if(keysDown[E]) dr -= RUN_SPEED;
		dx *= Core.getDelta();
		dy *= Core.getDelta();
		dz *= Core.getDelta();
		dr *= Core.getDelta() * 2;
		dp += this.dy* Core.getDelta() * 2;
		dya -= this.dx * Core.getDelta() * 2;
		parent.Move(dx, dy, dz, dp, dya, dr);
	}

	public void recieve(MessageFlags flag, String msg) {
		
	}

	public void kbrecieve(Integer type, String inputMsg) {
		//System.out.println(type + " | " + inputMsg);
		if(type == KBInput.keyTypes.getY("Movement")){
			String[] info = inputMsg.split(";");
			keysDown[UP] = (Integer.valueOf(info[0]) == 1);
			keysDown[LEFT] = (Integer.valueOf(info[1]) == 1);
			keysDown[DOWN] = (Integer.valueOf(info[2]) == 1);
			keysDown[RIGHT] = (Integer.valueOf(info[3]) == 1);
			keysDown[Q] = (Integer.valueOf(info[4]) == 1);
			keysDown[E] = (Integer.valueOf(info[5]) == 1);
			keysDown[SPACE] = (Integer.valueOf(info[6]) == 1);
			keysDown[SHIFT] = (Integer.valueOf(info[7]) == 1);
		}
	}

	public void mouserecieve(int type, String message) {
		//System.out.println(type + " | " + message);
		if(type == MouseInput.keyTypes.getY("Movement")){
			String[] info = message.split(";");
			for(int x = 0; x < info.length; x++){
				String i = info[x];
				if(i.equals(";")) continue;
				if(i.startsWith("DX")){
					String[] dx = i.split(":");
					this.dx = Float.valueOf(dx[1]);
				} else if(i.startsWith("DY")){
					String[] dy = i.split(":");
					this.dy = Float.valueOf(dy[1]);
				}
			}
		}
	}

}
