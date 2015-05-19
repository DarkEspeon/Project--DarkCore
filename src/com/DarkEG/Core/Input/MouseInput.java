package com.DarkEG.Core.Input;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;

import com.DarkEG.Core.Event.EventManager;
import com.DarkEG.Core.Event.Object.MouseClickEvent;
import com.DarkEG.Core.Event.Object.MouseEvent;
import com.DarkEG.Core.Event.Object.MouseScrollEvent;
import com.DarkEG.Core.Util.ReferenceList;

public class MouseInput {
	public static ReferenceList<String, Integer> keyTypes = new ReferenceList<>();
	private boolean[] ButtonDown;
	private static int numButtons;
	private float x, y, scroll, dx, dy;
	
	public MouseInput(){
		if(numButtons < 0) numButtons = Mouse.getButtonCount();
		Mouse.setGrabbed(true);
		ButtonDown = new boolean[numButtons];
	}
	public void update(){
		float nx = Mouse.getX();
		float ny = Mouse.getY();
		float ndx = Mouse.getDX();
		float ndy = Mouse.getDY();
		float nscroll = Mouse.getDWheel();
		if(nx != x || ny != y){
			if(nscroll != scroll){
				EventManager.getInstance().dispatchEvent(new MouseScrollEvent(this, nx, ny, ndx, ndy, nscroll));
				scroll = nscroll;
			} else {
				EventManager.getInstance().dispatchEvent(new MouseEvent(this, nx, ny, ndx, ndy));
			}
			x = nx;
			y = ny;
			dx = ndx;
			dy = ndy;
		}
		for(int i = 0; i < numButtons; i++){
			if(ButtonDown[i] != Mouse.isButtonDown(i)){
				ButtonDown[i] = Mouse.isButtonDown(i);
				EventManager.getInstance().dispatchEvent(new MouseClickEvent(this, nx, ny, ndx, ndy, i, ButtonDown[i]));
			}
		}
	}
}
