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
import com.viduus.charon.global.output.OutputHandler;

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
	private final String REPLAY_STRING = "Replay Enabled: Full Speed (1), Half Speed (2), Tenth Speed (3)";

	public WorldFrame(GameSystems game_systems, GraphicsFrame graphics_frame) {
		super();
		this.game_systems = game_systems;
		this.graphics_frame = graphics_frame;
		
		// Create the actual frame
	
		graphics_frame.setTitle("Spaceballs 2");
		graphics_frame.setLocationRelativeTo(null);
		graphics_frame.setDesiredFPS(60);
		this.speed_bar = new SpeedBar(50, 800);
	}

	@Override
	public void render( OpenGLGraphics graphics ){
		super.render(graphics);

		game_systems.world_engine.render(graphics);
		graphics.setDisplay2D();
		
		if( start_time == 0 )
			start_time = System.currentTimeMillis();
		
//		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		speed_bar.render(graphics, game_systems.world_engine.basketball_shot_percent, getHeight(), getWidth());
	
		String score = String.format("%02d", game_systems.world_engine.times_scored);
		OpenGLFont.setFont(graphics, new Font("comic sans", Font.PLAIN, 100));
		OpenGLFont.setFontColor(253.0f / 255.0f, 95.0f / 255.0f, 0.0f, 1);
		OpenGLFont.drawString2D(graphics, score, (int) (getWidth() - OpenGLFont.getStringWidth(score)), 0);
		
		if(game_systems.world_engine.replay_enabled) {
			OpenGLFont.setFont(graphics, new Font("comic sans", Font.PLAIN, 30));
			OpenGLFont.setFontColor(1, 1, 1, 1);
			OpenGLFont.drawString2D(graphics, REPLAY_STRING, (int) ((getWidth()-OpenGLFont.getStringWidth(REPLAY_STRING))/2), 5);
		}
	}
}
