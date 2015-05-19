package com.DarkEG.Core.Entity.Component;

import org.lwjgl.util.vector.Vector2f;

import com.DarkEG.Core.Core;
import com.DarkEG.Core.Entity.Entity;
import com.DarkEG.Core.Render.RenderCore;
import com.DarkEG.Core.Resources.TextureManager;
import com.DarkEG.Core.Util.Maths;

public class HudComponent extends Component{
	private Vector2f position;
	private Vector2f scale;
	private int tID;
	
	public HudComponent(Entity e, int texID, Vector2f pos, Vector2f s) {
		super(e);
		this.tID = texID;
		this.position = pos;
		this.scale = s;
	}

	public void update() {
		Core.rm.tm.bindTexture(TextureManager.TEX2D, tID, 0);
		RenderCore.gui.loadUniform("transMat", Maths.createTransformationMatrix(position, scale));
		Core.renderQuad();
	}
	
}
