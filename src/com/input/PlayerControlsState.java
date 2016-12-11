package com.input;

import com.viduus.charon.global.util.math.Vec2;

public class PlayerControlsState {
	public Vec2 mouse_location = new Vec2();
	public float left = 0;
	public float right = 0;
	public float up = 0;
	public float down = 0;
	public float strafe = 0;
	public float jmp = 0;
	public float zoom = 0;
	public boolean select;
	public boolean interact;
	
	@Override
	public PlayerControlsState clone() {
		PlayerControlsState s = new PlayerControlsState();
		
		s.mouse_location = new Vec2(this.mouse_location);
		s.left = this.left;
		s.right = this.right;
		s.up = this.up;
		s.down = this.down;
		s.strafe = this.strafe;
		s.jmp = this.jmp;
		s.zoom = this.zoom;
		s.select = this.select;
		s.interact = this.interact;
		
		return s;
	}
}
