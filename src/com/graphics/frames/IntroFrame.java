/**
 * Created on Oct 4, 2016 by ethan
 */
package com.graphics.frames;

import java.awt.Font;

import com.GameSystems;
import com.graphics.GraphicsEngine;
import com.graphics.GraphicsFrame;
import com.viduus.charon.global.graphics.opengl.OpenGLButton;
import com.viduus.charon.global.graphics.opengl.OpenGLGraphics;
import com.viduus.charon.global.graphics.opengl.OpenGLPanel;
import com.viduus.charon.global.graphics.opengl.RenderFunction;
import com.viduus.charon.global.graphics.opengl.callback.OpenGLActionListener;
import com.viduus.charon.global.graphics.opengl.font.OpenGLFont;
import com.viduus.charon.global.graphics.opengl.shaders.ShaderProgram;
import com.viduus.charon.global.graphics.opengl.shaders.exceptions.ShaderException;
import com.viduus.charon.global.graphics.opengl.shaders.variables.ShaderAttribute;
import com.viduus.charon.global.graphics.opengl.shaders.variables.ShaderUniform;
import com.viduus.charon.global.graphics.opengl.shapes.Rectangle;

/**
 * 
 * 
 * @author ethan
 */
public class IntroFrame extends OpenGLPanel {
	
	private long start_time = 0;
	private GameSystems game_systems;
	private GraphicsFrame graphics_frame;
	private int text_pos;
	private String[] lines = {
		"It is a dark time for Goofy. Although the Walt Disney Star has been",
		"destroyed, Imperial Pete troops have driven Goofy and friends",
		"from their hidden base and pursued them across the galaxy.",
		"",
		"Evading the dreaded mini Pete Starfleet, a group of freedom",
		"fighters led by Goofy has established a new secret base on the",
		"remote ice world of Mickey Land.",
		"",
		"The evil lord Darth Pete, obsessed with finding young Goofy, has",
		"dispatched thousands of remote probes into the far reaches of",
		"space…"
	};
	
	/**
	 * TODO
	 * @param game_systems
	 * @param graphics_frame
	 */
	public IntroFrame(GameSystems game_systems, GraphicsFrame graphics_frame) {
		this.game_systems = game_systems;
		this.graphics_frame = graphics_frame;
		
		graphics_frame.setTitle("Delta Station");
		graphics_frame.setLocationRelativeTo(null);
		graphics_frame.setDesiredFPS(60);

		this.setBackgroundColor(0, 0, 0);
		this.setBorder(false);
	}
	
	@Override
	public void render( OpenGLGraphics graphics ){
		super.render(graphics);
		
		if( start_time == 0 )
			start_time = System.currentTimeMillis();
		
		long delta_time = System.currentTimeMillis() - start_time;
		

		float font_alpha_space_balls = 0;
		float font_alpha_2 = 0;
		if( delta_time < 3000 )
			font_alpha_space_balls = delta_time / 3000.0f;
		else if( delta_time < 6000 ) {
			font_alpha_space_balls = 1;
			font_alpha_2 = (delta_time - 3000.0f) / 3000.0f;
		}
		else if( delta_time < 7000 ) {
			delta_time -= 6000;
			font_alpha_space_balls = font_alpha_2 = 1.0f - (delta_time / 1000.0f);
			text_pos = getHeight();
			
		}else if( delta_time < 25500 ){
			text_pos -= 0.5f;
			OpenGLFont.setFont(graphics, new Font("comic sans", Font.PLAIN, 20));
			OpenGLFont.setFontColor(1, 1, 1, 1);
			for( int i=0 ; i<lines.length ; i++ ){
				OpenGLFont.drawString2D(graphics, lines[i], (int) ((getWidth() - OpenGLFont.getStringWidth(lines[i]))/2), (int) (text_pos + i*OpenGLFont.getFontMetrics().line_height));
			}
			
		}else{
			game_systems.graphics_engine.showFrame( GraphicsEngine.GAME_SCREEN );
		}
		
		OpenGLFont.setFont(graphics, new Font("comic sans", Font.PLAIN, 150));
		OpenGLFont.setFontColor(1, 1, 1, font_alpha_space_balls);
		OpenGLFont.drawString2D(graphics, "Spaceballs", (int) ((getWidth()-OpenGLFont.getStringWidth("Spaceballs"))/2 -OpenGLFont.getStringWidth("2")), getHeight()/4);

		OpenGLFont.setFont(graphics, new Font("comic sans", Font.PLAIN, 150));
		OpenGLFont.setFontColor(1, 1, 1, font_alpha_2);
		OpenGLFont.drawString2D(graphics, "2", (int) ((getWidth() - OpenGLFont.getStringWidth("2"))/2 + OpenGLFont.getStringWidth("Spaceballs") / 2), getHeight()/4);
		
	}

}
