package com.DarkEG.Core.Entity.Component;

import com.DarkEG.Core.Entity.Entity;

public abstract class Component{
	
	Entity parent;
	public Component(Entity e){
		parent = e;
	}
	public abstract void update();
}
