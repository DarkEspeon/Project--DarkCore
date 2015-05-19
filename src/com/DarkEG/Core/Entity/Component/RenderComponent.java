package com.DarkEG.Core.Entity.Component;

import com.DarkEG.Core.Entity.Entity;
import com.DarkEG.Core.Render.RenderCore;

public class RenderComponent extends Component{
	private int MeshID;
	
	public RenderComponent(Entity e, int MeshID) {
		super(e);
		this.MeshID = MeshID;
	}

	public void update() {
		RenderCore.processEntity(MeshID, parent);
	}

}
