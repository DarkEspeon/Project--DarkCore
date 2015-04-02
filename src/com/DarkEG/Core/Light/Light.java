package com.DarkEG.Core.Light;

import org.lwjgl.util.vector.Vector3f;

public class Light {
	private Vector3f color;
	private Vector3f attenuation = new Vector3f(1, 0, 0);
	public Light(Vector3f color){
		this.color = color;
	}
	public Light(Vector3f color, Vector3f attenuation){
		this.color = color;
		this.attenuation = attenuation;
	}
	public Vector3f getAttenuation(){ return attenuation; }
	public Vector3f getColor(){ return color; }
}
