package com.models;

import com.graphics.Model;
import com.viduus.charon.global.graphics.opengl.OpenGLGraphics;
import com.viduus.charon.global.util.math.Vec2;
import com.viduus.charon.global.util.math.Vec3;
import com.viduus.charon.global.world.SphericalObject;
import com.viduus.util.debug.OutputHandler;
import com.viduus.util.models.ModelData;

public class Basketball extends Model{
	
	public Vec3 initial_location, previous_location, location, initial_velocity_when_shot;
	public boolean being_thrown = false;
	private long time_shot = 0;
	
	private float gravity = 9.8f * 10f; //units per second
	
	private float angle_around_hoop_when_shot, zenith_when_shot, azimuth_when_shot = 0, speed_when_shot;
	
	private SphericalObject basketball_shell;
	
	public Basketball(ModelData model_data) {
		super(model_data);
		
		location = initial_location = new Vec3(112f, 12f, 0f);
		basketball_shell = new SphericalObject(location, 10.0f);
	}
	
	public void updateLocation(float angle_around_hoop) {
		previous_location = new Vec3(location.x, location.y, location.z);
		
		if(being_thrown) {
			location = new Vec3((float) (initial_location.x + 62 * Math.cos(Math.toRadians(angle_around_hoop_when_shot))),
								initial_location.y, 
								(float) (initial_location.z + 62 * Math.sin(Math.toRadians(angle_around_hoop_when_shot))));
		}
		else {
			location = new Vec3((float) (initial_location.x + 62 * Math.cos(Math.toRadians(angle_around_hoop))),
								initial_location.y, 
								(float) (initial_location.z + 62 * Math.sin(Math.toRadians(angle_around_hoop))));
		}
		
		basketball_shell.center = new Vec3(location.x, location.y, location.z);
	}
	
	public void checkForCollision(Model m) {
		
	}
	
	public void checkForCollision(float y) {
		OutputHandler.println("" + basketball_shell.center.y);
		if(basketball_shell.center.y - basketball_shell.radius < y) {
			location.y = y;
		}
	}
	
	public void shoot(float angle_around_hoop, float zenith, float azimuth, float speed) {
		being_thrown = true;
		time_shot = System.currentTimeMillis();
		
		angle_around_hoop_when_shot = angle_around_hoop;
		zenith_when_shot = zenith;
		azimuth_when_shot = azimuth;
		speed_when_shot = speed;

		initial_velocity_when_shot = new Vec3((float) (speed * Math.cos(Math.toRadians((angle_around_hoop + 180) + azimuth))), 
											  (float) (speed * Math.sin(Math.toRadians(-zenith))), 
											  (float) (speed * Math.sin(Math.toRadians((angle_around_hoop + 180) + azimuth))));
	}
	
	private void calculateMovement() {
		if(being_thrown) {
			float time_dif = (System.currentTimeMillis() - time_shot) / 1000f;
			
			location.x += (initial_velocity_when_shot.x * time_dif);
			location.y += (initial_velocity_when_shot.y * time_dif) - (0.5f * gravity * time_dif * time_dif);
			location.z += (initial_velocity_when_shot.z * time_dif);
			
//			if(location.y < 0)
//				location.y = 0;
		} 
	}
 
	@Override
	public void render(OpenGLGraphics graphics) {
		calculateMovement();
		
		graphics.model_matrix.translate(location.x, location.y, location.z);
		graphics.model_matrix.scale(3.0f, 3.0f, 3.0f);
		graphics.model_matrix.rotate(0, (float) (Math.toRadians(270) - Vec2.angleBetween(113f, 0f, location.x, location.z)), 0);
		super.render(graphics);
	}
}
