/**
 * Created on Oct 4, 2016 by ethan
 */
package com.graphics.frames;

import com.GameSystems;
import com.graphics.GraphicsFrame;
import com.viduus.charon.global.graphics.opengl.OpenGLPanel;

/**
 * 
 * 
 * @author ethan
 */
public class IntroFrame extends OpenGLPanel {

	private GameSystems game_systems;
	private GraphicsFrame graphics_frame;

	/**
	 * TODO
	 * @param game_systems
	 * @param graphics_frame
	 */
	public IntroFrame(GameSystems game_systems, GraphicsFrame graphics_frame) {
		super();
		this.game_systems = game_systems;
		this.graphics_frame = graphics_frame;
		
		// Create the actual frame
		
		graphics_frame.setTitle("Delta Station");
		graphics_frame.setLocationRelativeTo(null);
		graphics_frame.setDesiredFPS(50);
	}

}
