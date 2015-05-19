package com.DarkEG.Core.Entity.Component;


import org.lwjgl.util.vector.Vector3f;

import com.DarkEG.Core.Entity.Entity;
import com.DarkEG.Core.Light.Light;

public class LightComponent extends Component {
	public LightComponent(Entity e, Vector3f color, Vector3f attenuation){
		this(e, new Light(color, attenuation));
	}
	public LightComponent(Entity e, Vector3f color){
		this(e, new Light(color));
	}
	public LightComponent(Entity e, Light l) {
		super(e);
		this.l = l;
	}

	private Light l;
	
	public Light getLight(){ return l; }

	public void update() {
		
	}
	
}
