/**
 * Created on Oct 4, 2016 by ethan
 */
package com.graphics.frames;

import java.awt.Font;

import com.GameSystems;
import com.graphics.GraphicsFrame;
import com.viduus.charon.global.graphics.opengl.OpenGLGraphics;
import com.viduus.charon.global.graphics.opengl.OpenGLPanel;
import com.viduus.charon.global.graphics.opengl.font.OpenGLFont;

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

	@Override
	public void render( OpenGLGraphics graphics ){
		super.render(graphics);
		
		OpenGLFont.setFont(graphics, new Font("arial", Font.PLAIN, 10));
		OpenGLFont.setFontColor(1, 1, 1, 1);
		OpenGLFont.drawString2D(graphics, "Press ESC to exit", (int) (getWidth()-OpenGLFont.getStringWidth("Press ESC to exit")-10), 5);
		
		game_systems.world_engine.render(graphics);
	}
}
