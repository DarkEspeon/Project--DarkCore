package com.DarkEG.Core.Event.Object;

import java.util.EventObject;

public class MouseScrollEvent extends EventObject {
	private static final long serialVersionUID = 1L;
	
	public float mx, my;
	public float dx, dy;
	public float dscroll;
	
	public MouseScrollEvent(Object source) {
		super(source);
	}
	public MouseScrollEvent(Object source, float mx, float my, float dx, float dy, float dscroll) {
		super(source);
		this.mx = mx;
		this.my = my;
		this.dx = dx;
		this.dy = dy;
		this.dscroll = dscroll;
	}
	public float getMouseX() { return mx; }
	public float getMouseY() { return my; }
	public float getMouseDX() { return dx; }
	public float getMouseDY() { return dy; }
	public float getDeltaScroll() { return dscroll; }
}
