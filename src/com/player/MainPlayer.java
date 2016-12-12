package com.player;

import com.GameSystems;
import com.input.PlayerControls;
import com.input.PlayerControlsState;
import com.viduus.charon.global.graphics.Dimension;
import com.viduus.charon.global.input.controller.Controller;
import com.viduus.charon.global.output.OutputHandler;
import com.viduus.charon.global.util.math.Vec3;

public class MainPlayer {
	
	private final GameSystems game_systems;
	
	private Controller default_controller;
	private PlayerControls controls;
	private PlayerControlsState player_controls_state;
	
	private float x = 23, y = 10, z = 0;
	
	public float angle_around_hoop = -180;
	
	public Vec3 eye, center;
	
	public float zenith = 0, azimuth = 0;
	
	private boolean controls_enabled = true;
	
	public MainPlayer(GameSystems game_systems) {
		this.game_systems = game_systems;
		initialize();
	}
	
	private void initialize() {
		this.default_controller = this.game_systems.input_engine.getDefaultController();
		this.default_controller.registerInputListener("main-player-default-controls", controls = new PlayerControls());
		this.game_systems.input_engine.registerListenerForAutomaticJoystickBinding("main-player-default-controls-joystick", controls);
	}
	
	public PlayerControlsState getControlsState() {
		player_controls_state = controls.getState();
		
		return player_controls_state;
	}
	
	public void disableControls() {
		controls_enabled = false;
	}
	
	public void enableControls() {
		controls_enabled = true;
	}
	
	public boolean controlsEnabled() {
		return controls_enabled;
	}
	
	public void updateState() {
		PlayerControlsState controls_state = getControlsState();
		
		if(!controls_enabled) {
			controls_enabled = controls_state.select;
		}
		else {
			Dimension curr_frame_size = game_systems.graphics_engine.getCurrentFrameSize();
			
			angle_around_hoop += controls_state.left + controls_state.right;
			if(angle_around_hoop > -90)
				angle_around_hoop = -90;
			else if(angle_around_hoop < -270)
				angle_around_hoop = -270;
			
			if(!controls_state.joystick_active) {
				azimuth = 45.0f * ((controls_state.mouse_location.x - curr_frame_size.width / 2.0f) / (curr_frame_size.width / 2.0f));
				zenith = 45.0f * ((controls_state.mouse_location.y - curr_frame_size.height / 2.0f) / (curr_frame_size.height / 2.0f));
			}
			else {
				if(controls_state.left_joystick != null) {
//					OutputHandler.println(controls_state.left_joystick[0] + " " + controls_state.left_joystick[1]);
					azimuth = 45.0f * controls_state.left_joystick[0];
					zenith = 45.0f * -controls_state.left_joystick[1];
				}
			}
		}
		
		float eye_x = (float) (113 + 60 * Math.cos(Math.toRadians(angle_around_hoop)));
		float eye_z = (float) (0f + 60 * Math.sin(Math.toRadians(angle_around_hoop)));
		
		eye = new Vec3(eye_x, 11.f, eye_z);
		
		x = (float) (Math.sin(Math.toRadians(zenith + 90)) * Math.cos(Math.toRadians(azimuth + (180 + angle_around_hoop))));
		y = (float) (Math.cos(Math.toRadians(zenith + 90)));
		z = (float) (Math.sin(Math.toRadians(zenith + 90)) * Math.sin(Math.toRadians(azimuth + (180 + angle_around_hoop))));
		
		center = Vec3.add(eye, new Vec3(x, y, z));
	}
	
	/**
	 * Returns the angle between two points in radians
	 * @param x1 
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	public double getAngle(float x1, float y1, float x2, float y2)
	{
	    double dx = x2 - x1;
	    double dy = -(y2 - y1);

	    double rads = Math.atan2(dy, dx);

	    if (rads < 0)
	    	rads = Math.abs(rads);
	    else
	    	rads = 2 * Math.PI - rads;

	    return rads;
	}
}
