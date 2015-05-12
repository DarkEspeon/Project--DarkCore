package com.DarkEG.Core.Entity.Component;

import com.DarkEG.Core.Core;
import com.DarkEG.Core.Entity.Entity;
import com.DarkEG.Core.Model.Mesh;
import com.DarkEG.Core.Msg.MessageFlags;
import com.DarkEG.Core.Render.RenderCore;
import com.DarkEG.Core.Resources.ResourceManager;
import com.DarkEG.Core.Shader.Shader;
import com.DarkEG.Core.Util.Maths;

public class RenderComponent extends Component{
	private int MeshID;
	private int ShaderID;
	
	public RenderComponent(Entity e, int MeshID, int ShaderID) {
		super(e);
		this.MeshID = MeshID;
		this.ShaderID = ShaderID;
	}

	public void update() {
		Shader s = Core.core.rm.sm.getShader(ShaderID);
		RenderCore.processEntity(MeshID, parent, s);
	}

	public void recieve(MessageFlags flag, String msg) {
		
	}

}
