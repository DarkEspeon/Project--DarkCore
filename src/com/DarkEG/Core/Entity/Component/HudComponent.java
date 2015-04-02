package com.DarkEG.Core.Entity.Component;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.lwjgl.util.vector.Vector2f;

import com.DarkEG.Core.ResourceManager;
import com.DarkEG.Core.Entity.Entity;
import com.DarkEG.Core.Msg.MessageFlags;

public class HudComponent extends Component{
	private Vector2f position;
	private Vector2f scale;
	private int tID;
	
	public HudComponent(Entity e, int texID) {
		super(e);
		this.tID = texID;
	}

	public void recieve(MessageFlags flag, String msg) {
		
	}

	public void update() {
		glActiveTexture(GL_TEXTURE0);
		ResourceManager.getTexture(tID).bind();
	}
	
}
