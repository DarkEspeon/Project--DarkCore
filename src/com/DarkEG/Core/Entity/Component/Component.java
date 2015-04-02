package com.DarkEG.Core.Entity.Component;

import com.DarkEG.Core.Entity.Entity;
import com.DarkEG.Core.Msg.MsgReceiver;

public abstract class Component implements MsgReceiver{
	
	protected static final int UP = 0;
	protected static final int DOWN = 1;
	protected static final int LEFT = 2;
	protected static final int RIGHT = 3;
	protected static final int SPACE = 4;
	protected static final int SHIFT = 5;
	protected static final int Q = 6;
	protected static final int E = 7;
	
	Entity parent;
	public Component(Entity e){
		parent = e;
	}
	public abstract void update();
}
