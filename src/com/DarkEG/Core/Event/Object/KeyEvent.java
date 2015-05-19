package com.DarkEG.Core.Event.Object;

import java.util.EventObject;

public class KeyEvent extends EventObject{
	private static final long serialVersionUID = 1L;
	
	private int key;
	private boolean pressed;
	public KeyEvent(Object source) {
		super(source);
	}
	
	public KeyEvent(Object source, int key, boolean pressed){
		super(source);
		this.key = key;
		this.pressed = pressed;
	}
	
	public int getKeyCode() { return key; }
	public String getKeyName() { return java.awt.event.KeyEvent.getKeyText(key); }
	public boolean wasPressed() { return pressed; }
	
}
