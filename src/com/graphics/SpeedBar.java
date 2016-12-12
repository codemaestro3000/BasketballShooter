package com.graphics;

import com.viduus.charon.global.error.ErrorHandler;
import com.viduus.charon.global.graphics.opengl.OpenGLGraphics;
import com.viduus.charon.global.graphics.opengl.shaders.ShaderProgram;
import com.viduus.charon.global.graphics.opengl.shaders.exceptions.ShaderException;
import com.viduus.charon.global.graphics.opengl.shaders.variables.ShaderAttribute;
import com.viduus.charon.global.graphics.opengl.shapes.Rectangle;

public class SpeedBar {
	
	private float height, max_width, curr_width;
	private float[] inner_color = new float[] { 0, 1, 0, 1};
	private float[] outline_color = new float[] { 1, 1, 1, 1};
	
	public SpeedBar(float height, float width) {
		this.height = height;
		this.max_width = width;
	}
	
	public void render(OpenGLGraphics graphics, float percent_filled, float window_height, float window_width) {
//		drawOutline(graphics, window_height, window_width);
		fillInner(graphics, percent_filled, window_height, window_width);
	}
	
	private void drawOutline(OpenGLGraphics graphics, float window_height, float window_width) {
		try{
			// Grab the current active shader
			String prev_shader = graphics.shader_manager.getActiveShaderName();
			graphics.shader_manager.useShader("do_nothing");
			
			graphics.pushMatricies();

			ShaderProgram active_program = graphics.shader_manager.getActiveShader();
			active_program.getUniformVariable("set_Color").setValue(outline_color);

			// Draw the background
			ShaderAttribute position = active_program.getAttributeVariable("in_position");
			
			Rectangle.fillRectangle(graphics, position, (window_width / 2.0f) - (max_width / 2.0f), 0, max_width, height);
			
			graphics.shader_manager.useShader(prev_shader);
			
		}catch( ShaderException e ){
			ErrorHandler.catchError(e);
		}
	}
	
	private void fillInner(OpenGLGraphics graphics, float percent_filled, float window_height, float window_width) {
		
		curr_width = max_width * percent_filled;
		
		try{
			// Grab the current active shader
			String prev_shader = graphics.shader_manager.getActiveShaderName();
			graphics.shader_manager.useShader("opengl_frame");
			
			graphics.pushMatricies();

			ShaderProgram active_program = graphics.shader_manager.getActiveShader();
			active_program.getUniformVariable("set_Color").setValue(inner_color);

			// Draw the background
			ShaderAttribute position = active_program.getAttributeVariable("in_Position");
			
			Rectangle.fillRectangle(graphics, position, (window_width / 2.0f) - (curr_width / 2.0f), 0, curr_width, height);
			
			graphics.shader_manager.useShader(prev_shader);
			
		}catch( ShaderException e ){
			ErrorHandler.catchError(e);
		}
	}
}
