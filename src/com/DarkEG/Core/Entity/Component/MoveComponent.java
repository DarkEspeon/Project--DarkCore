package com.DarkEG.Core.Entity.Component;

import com.DarkEG.Core.Entity.Entity;

public abstract class MoveComponent extends Component{
	public MoveComponent(Entity e) {
		super(e);
	}
	protected final int RUN_SPEED = 20;
	protected final int TURN_SPEED = 160;
	protected final float GRAVITY = -50;
	protected final float JUMP_POWER = 30;
}
