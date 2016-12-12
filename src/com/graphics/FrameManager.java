/**
 * Created on Oct 4, 2016 by ethan
 */
package com.graphics;

import com.GameSystems;
import com.graphics.frames.IntroFrame;
import com.graphics.frames.WorldFrame;
import com.viduus.charon.global.graphics.opengl.OpenGLPanel;

/**
 * 
 * 
 * @author ethan
 */
public class FrameManager {
	
	private WorldFrame initial_frame = null;
	private IntroFrame intro_frame = null;

	private GameSystems game_systems;

	/**
	 * TODO
	 * @param game_systems
	 */
	public FrameManager(GameSystems game_systems) {
		this.game_systems = game_systems;
	}

	/**
	 * TODO
	 * @param graphics_frame
	 * @return
	 */
	public OpenGLPanel retrieveInitialGameLoadingFrame(GraphicsFrame graphics_frame) {
		// Create it if it hasn't been initialized yet
		if( initial_frame == null )
			initial_frame  = new WorldFrame( game_systems, graphics_frame );
		return initial_frame;
	}

	/**
	 * @param graphics_frame
	 * @return
	 */
	public OpenGLPanel retrieveLoadingFrame(GraphicsFrame graphics_frame) {
		// Create it if it hasn't been initialized yet
		if( intro_frame == null )
			intro_frame  = new IntroFrame( game_systems, graphics_frame );
		return intro_frame;
	}

}
