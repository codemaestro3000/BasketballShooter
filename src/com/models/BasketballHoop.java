package com.models;

import com.graphics.Model;
import com.viduus.charon.global.graphics.opengl.OpenGLGraphics;
import com.viduus.util.models.ModelData;

public class BasketballHoop extends Model{

	public BasketballHoop(ModelData model_data) {
		super(model_data);
	}

	@Override
	public void render(OpenGLGraphics graphics) {
		
		graphics.model_matrix.translate(120f, -0.6f, -7.0f);
		graphics.model_matrix.scale(10.0f, 10.0f, 10.0f);
		graphics.model_matrix.rotate((float) Math.toRadians(-90), (float) Math.toRadians(-90), 0);
		super.render(graphics);
	}
}
