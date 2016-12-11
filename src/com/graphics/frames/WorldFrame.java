/**
 * Created on Oct 4, 2016 by ethan
 */
package com.graphics.frames;

import java.awt.Font;

import com.GameSystems;
import com.graphics.GraphicsFrame;
import com.graphics.SpeedBar;
import com.viduus.charon.global.graphics.opengl.OpenGLGraphics;
import com.viduus.charon.global.graphics.opengl.OpenGLPanel;
import com.viduus.charon.global.graphics.opengl.font.OpenGLFont;

/**
 * 
 * 
 * @author ethan
 */
public class WorldFrame extends OpenGLPanel {

	private GameSystems game_systems;
	private GraphicsFrame graphics_frame;
	private long start_time;
	private SpeedBar speed_bar;

	public WorldFrame(GameSystems game_systems, GraphicsFrame graphics_frame) {
		super();
		this.game_systems = game_systems;
		this.graphics_frame = graphics_frame;
		
		// Create the actual frame
	
		graphics_frame.setTitle("Spaceballs 2");
		graphics_frame.setLocationRelativeTo(null);
		graphics_frame.setDesiredFPS(60);
		this.speed_bar = new SpeedBar(50, 400);
	}

	@Override
	public void render( OpenGLGraphics graphics ){
		super.render(graphics);
		
		game_systems.world_engine.render(graphics);
		graphics.setDisplay2D();
		
		if( start_time == 0 )
			start_time = System.currentTimeMillis();
		
		long delta_time = System.currentTimeMillis() - start_time;
		
		float font_alpha = 0;
		if( delta_time < 3000 )
			font_alpha = delta_time / 3000.0f;
		else if( delta_time < 5000 )
			font_alpha = 1;
		else if( delta_time < 6000 ) {
			delta_time -= 5000;
			font_alpha = 1.0f - (delta_time / 1000.0f);
		}
		
//		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		speed_bar.render(graphics, game_systems.world_engine.basketball_shot_percent, getHeight(), getWidth());
		
		OpenGLFont.setFont(graphics, new Font("comic sans", Font.PLAIN, 150));
		OpenGLFont.setFontColor(1, 1, 1, font_alpha);
		OpenGLFont.drawString2D(graphics, "Spaceballs 2", (int) ((getWidth()-OpenGLFont.getStringWidth("Spaceballs 2"))/2), getHeight()/4);
	}
}
