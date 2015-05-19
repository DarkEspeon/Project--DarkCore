package com.DarkEG.Core.Event.Object;

import java.util.EventObject;

public class MouseClickEvent extends EventObject {
	private static final long serialVersionUID = 1L;

	private float mx, my;
	private float dx, dy;
	private int button;
	private boolean clicked;
	
	public MouseClickEvent(Object source) {
		super(source);
	}
	public MouseClickEvent(Object source, float mx, float my, float dx, float dy, int button, boolean clicked){
		super(source);
		this.mx = mx;
		this.my = my;
		this.dx = dx;
		this.dy = dy;
		this.button = button;
		this.clicked = clicked;
	}
	
	public float getMouseX(){ return mx; }
	public float getMouseY(){ return my; }
	public float getMouseDX(){ return dx; }
	public float getMouseDY(){ return dy; }
	public int getButton(){ return button; }
	public boolean wasClicked(){ return clicked; }
	
}
