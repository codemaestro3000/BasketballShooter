package com.models;

import com.graphics.Model;
import com.viduus.charon.global.graphics.opengl.OpenGLGraphics;
import com.viduus.charon.global.util.math.Vec2;
import com.viduus.charon.global.util.math.Vec3;
import com.viduus.util.models.ModelData;

public class Goofy extends Model{
	public Vec3 initial_location, location, velocity;
	
	public Goofy(ModelData model_data) {
		super(model_data);
		
		initial_location = new Vec3(113f, 0f, 0f);
	}
	
	public void setLocation(float angle_around_hoop) {
		location = new Vec3((float) (initial_location.x + 61 * Math.cos(Math.toRadians(angle_around_hoop))),
							initial_location.y, 
							(float) (initial_location.z + 61 * Math.sin(Math.toRadians(angle_around_hoop))));
	}

	@Override
	public void render(OpenGLGraphics graphics) {
		graphics.model_matrix.translate(location.x, location.y, location.z);
		graphics.model_matrix.scale(150.0f, 150.0f, 150.0f);
		graphics.model_matrix.rotate(0, (float) (Math.toRadians(270) - Vec2.angleBetween(113f, 0f, location.x, location.z)), 0);
		super.render(graphics);
	}
}
