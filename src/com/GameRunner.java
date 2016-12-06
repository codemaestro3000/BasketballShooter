/**
 * Created on Sep 30, 2016 by Ethan Toney
 */
package com;

import com.graphics.GraphicsEngine;
import com.viduus.charon.global.input.controller.ControllerInputListener;
import com.viduus.charon.global.input.controller.ControllerState;
import com.viduus.charon.global.input.controller.device.KeyboardMouseController;

/**
 * 
 * 
 * @author ethan
 */
public class GameRunner {
	
	private GameSystems game_systems = new GameSystems();

	/**
	 * TODO
	 */
	public GameRunner() {
		// Initialize the minimum systems required
		game_systems.initMin();

		game_systems.graphics_engine.showFrame( GraphicsEngine.LOADING_SCREEN );
		game_systems.graphics_engine.start();
		
		game_systems.graphics_engine.bindController((KeyboardMouseController) game_systems.input_engine.getDefaultController());
		game_systems.input_engine.getDefaultController().registerInputListener("default-controls", new ControllerInputListener(){

			@Override
			public void onControllerState(ControllerState e) {

				if( e.getKeyState(ControllerState.EXIT_GAME_KEY) == ControllerState.ON_STATE ){
					game_systems.stopGame();
				}
			}
			
		});
		
		// Will enter a semi-infinite loop here until the main window is closed
		game_systems.graphics_engine.startEventQueue();
		
		game_systems.stopGame();
	}

	/**
	 * TODO
	 * @param args
	 */
	public static void main(String[] args) {
		System.setProperty("java.awt.headless", "true");
		
		new GameRunner();
	}

}
