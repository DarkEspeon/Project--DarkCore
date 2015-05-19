package com.DarkEG.Core.Event.Object;

import java.util.EventObject;

public class MouseEvent extends EventObject{

	private static final long serialVersionUID = 1L;
	
	private float mx, my;
	private float dx, dy;
	
	public MouseEvent(Object sender) {
		super(sender);
	}
	public MouseEvent(Object sender, float nx, float ny, float ndx, float ndy){
		super(sender);
		this.mx = nx;
		this.my = ny;
		this.dx = ndx;
		this.dy = ndy;
	}
	public float getMouseX() { return mx; }
	public float getMouseY() { return my; }
	public float getMouseDX() { return dx; }
	public float getMouseDY() { return dy; }
}
