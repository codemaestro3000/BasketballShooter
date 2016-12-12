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
	public boolean joystick_active;
	public float[] left_joystick;
	public float[] right_joystick;
	public boolean replay_enabled;
	public boolean replay_disabled;
	public boolean full_speed_selected;
	public boolean half_speed_selected;
	public boolean tenth_speed_selected;
	public boolean replay_focus_toggle;
	
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
		s.joystick_active = this.joystick_active;
		s.left_joystick = this.left_joystick;
		s.right_joystick = this.right_joystick;
		s.replay_enabled = this.replay_enabled;
		s.replay_disabled = this.replay_disabled;
		s.full_speed_selected = this.full_speed_selected;
		s.half_speed_selected = this.half_speed_selected;
		s.tenth_speed_selected = this.tenth_speed_selected;
		s.replay_focus_toggle = this.replay_focus_toggle;
		
		return s;
	}
}
