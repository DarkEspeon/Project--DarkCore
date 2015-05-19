package com.DarkEG.Core.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import com.DarkEG.Core.Core;
import com.DarkEG.Core.Entity.Component.Component;
import com.DarkEG.Core.Entity.Component.HudComponent;
import com.DarkEG.Core.Entity.Component.LightComponent;
import com.DarkEG.Core.Render.RenderCore;
import com.DarkEG.Core.Util.Maths;

public class Entity {
	private List<Component> components = new ArrayList<>();
	private List<HudComponent> huds = new ArrayList<>();
	private LightComponent light = null;
	
	private Vector3f eye;
	private Vector3f up;
	private Vector3f right;
	private Vector3f forward;
	
	private Quaternion rot = new Quaternion();
	private float scale = 1;
	
	private UUID id;
	
	public Entity(){
		id = UUID.randomUUID();
		
		eye = new Vector3f(0, 0, 0);
		up = new Vector3f(0, 1, 0);
		right = new Vector3f(1, 0, 0);
		forward = new Vector3f(0, 0, 1);
	}
	
	public Entity addComponent(Component comp){
		if(comp instanceof HudComponent) huds.add((HudComponent)comp);
		else if(comp instanceof LightComponent) light = ((LightComponent)comp);
		else components.add(comp);
		
		return this;
	}
	public void update(){
		for(Component c : components){
			c.update();
		}
	}
	
	public boolean hasLight(){ return (light != null); }
	public LightComponent getLight(){ return light; }
	
	public void renderHUD(){
		Core.core.disableDepth();
		Core.core.enableBlend();
		Core.core.blendFunc(Core.SRCALPHA, Core.ONEMINUSSRCALPHA);
		RenderCore.gui.start();
		for(HudComponent c : huds){
			c.update();
		}
		RenderCore.gui.stop();
		Core.core.disableBlend();
		Core.core.enableDepth();
	}
	public UUID getUUID() { return id; }
	public Vector3f getPosition(){ return eye; }
	public Matrix4f getModelMatrix(){
		return Maths.fromQuat(rot);
	}
	public Matrix4f getViewMatrix(){
		return Maths.fromQuat(rot, eye, true);
	}
	public float getScale() {return scale;}
	public void Move(float dx, float dy, float dz, float dp, float dya, float dr){
		Rotate(forward, dr);
		Rotate(right, dp);
		Rotate(up, dya);
		
		forward = Maths.getForwardAxis(rot);
		up = Maths.getUpAxis(rot);
		right = Maths.getRightAxis(rot);
		
		float ndx = dx * right.x + dy * up.x + dz * forward.x;
		float ndy = dx * right.y + dy * up.y + dz * forward.y;
		float ndz = dx * right.z + dy * up.z + dz * forward.z;
		Move(ndx, ndy, ndz);
	}
	public void Rotate(Vector3f axis, float angle){
		Rotate(Maths.fromAxisAngle(angle, axis));
	}
	public void Rotate(Quaternion q){
		//q.normalise();
		rot = Quaternion.mul(q, rot, null);
		rot.normalise();
	}
	public void Move(float dx, float dy, float dz){
		eye.x += dx;
		eye.y += dy;
		eye.z += dz;
	}
	public void setPos(float x, float y, float z){
		eye.x = x;
		eye.y = y;
		eye.z = z;
	}
}
