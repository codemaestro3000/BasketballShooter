/**
 * Created on Oct 4, 2016 by ethan
 */
package com.world;

import com.GameSystems;
import com.viduus.charon.global.graphics.opengl.OpenGLGraphics;

/**
 * 
 * 
 * @author ethan
 */
public class WorldEngine extends com.viduus.charon.global.world.WorldEngine {

	/**
	 * TODO
	 * @param gameSystems
	 */
	public WorldEngine(GameSystems gameSystems) {
		// TODO Implement constructor for WorldEngine
	}

	@Override
	public void saveState() {
		//Loop through the state of each region and save it
	}
	
	@Override
	public void render(OpenGLGraphics graphics) {
		super.render(graphics);
	}
}
