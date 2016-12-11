package com.input;

import com.viduus.charon.global.input.controller.ControllerInputListener;
import com.viduus.charon.global.input.controller.ControllerState;
import com.viduus.charon.global.input.controller.device.joystick.JoystickController;
import com.viduus.charon.global.output.OutputHandler;
import com.viduus.charon.global.util.math.Vec2;

public class PlayerControls implements ControllerInputListener {

	private PlayerControlsState curr_state = new PlayerControlsState();
	
	private boolean joystick_active = false;
	private int joystick_active_count = 0;
	
	@Override
	public synchronized void onControllerState(ControllerState e) {
		
		//Check if a joystick controller has been active in the past 10 onControllerState calls
		//If a joystick controller is active then we don't want the keyboard controller to reset
		//the joystick controller state.
		if(!(e.getCaller() instanceof JoystickController) && joystick_active) {
			joystick_active_count++;
			if(joystick_active_count == 10) {
				joystick_active = false;
				joystick_active_count = 0;
			}
			return;
		}
		
		boolean state_changed = false;
		
		if(!(e.getCaller() instanceof JoystickController))
			curr_state.mouse_location = new Vec2(e.getMouseLocation());
		
		curr_state.left = 0;
		curr_state.right = 0;
		curr_state.up = 0;
		curr_state.down = 0;
		curr_state.strafe = 0;
		curr_state.jmp = 0;
		curr_state.zoom = 0;
		curr_state.select = false;
		curr_state.interact = false;

		if( e.getKeyState(ControllerState.FORWARD_GAME_KEY) == ControllerState.ON_STATE || 
			e.getKeyState(ControllerState.MOVE_UP) == ControllerState.ON_STATE ){
			curr_state.up = 1;
			state_changed = true;
		}
		if( e.getKeyState(ControllerState.BACKWARD_GAME_KEY) == ControllerState.ON_STATE || 
			e.getKeyState(ControllerState.MOVE_DOWN) == ControllerState.ON_STATE  ){
			curr_state.down = -1;
			state_changed = true;
		}
		if( e.getKeyState(ControllerState.LEFT_GAME_KEY) == ControllerState.ON_STATE || 
			e.getKeyState(ControllerState.MOVE_LEFT) == ControllerState.ON_STATE ){
			curr_state.left = 1;
			state_changed = true;
		}
		if( e.getKeyState(ControllerState.RIGHT_GAME_KEY) == ControllerState.ON_STATE || 
			e.getKeyState(ControllerState.MOVE_RIGHT) == ControllerState.ON_STATE ){
			curr_state.right = -1;
			state_changed = true;
		}
		if( e.getKeyState(ControllerState.STRAFE_LEFT_GAME_KEY) == ControllerState.ON_STATE){
			curr_state.strafe--;
			state_changed = true;
		}
		if( e.getKeyState(ControllerState.STRAFE_RIGHT_GAME_KEY) == ControllerState.ON_STATE){
			curr_state.strafe++;
			state_changed = true;
		}
		if( e.getKeyState(ControllerState.CAM_ZOOM_IN_GAME_KEY) == ControllerState.ON_STATE ){
			curr_state.zoom--;
			state_changed = true;
		}
		if( e.getKeyState(ControllerState.CAM_ZOOM_OUT_GAME_KEY) == ControllerState.ON_STATE ){
			curr_state.zoom++;
			state_changed = true;
		}
		if( e.getKeyState(ControllerState.JUMP_GAME_KEY) == ControllerState.ON_STATE ){
			curr_state.jmp++;
			state_changed = true;
		}
		
		if(e.getKeyState(ControllerState.SELECT_KEY) == ControllerState.ON_STATE) {
			curr_state.select = true;
			state_changed = true;
		}
		if(e.getKeyState(ControllerState.INTERACT_KEY) == ControllerState.ON_STATE) {
			curr_state.interact = true;	
			state_changed = true;
		}
		
		if(state_changed && e.getCaller() instanceof JoystickController) {
			joystick_active = true;
		}
	}
	
	public synchronized PlayerControlsState getState() {
		PlayerControlsState clone = this.curr_state.clone();
		
		return clone;
	}
}
