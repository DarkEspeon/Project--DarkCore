package com.DarkEG.Core.Entity.Component;

import org.lwjgl.util.vector.Vector2f;

import com.DarkEG.Core.Core;
import com.DarkEG.Core.Entity.Entity;
import com.DarkEG.Core.Msg.MessageFlags;
import com.DarkEG.Core.Resources.TextureManager;

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
		Core.core.rm.tm.bindTexture(TextureManager.TEX2D, tID);
	}
	
}
