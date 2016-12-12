package com.models;

import com.graphics.Model;
import com.viduus.charon.global.graphics.opengl.OpenGLGraphics;
import com.viduus.charon.global.util.math.Vec2;
import com.viduus.charon.global.util.math.Vec3;
import com.viduus.charon.global.world.SphericalObject;
import com.viduus.charon.global.world.TriangularObject;
import com.viduus.util.debug.OutputHandler;
import com.viduus.util.models.ModelData;

public class Basketball extends Model{
	
	public Vec3 initial_location, previous_location, location, initial_velocity_when_shot;
	public boolean being_thrown = false;
	private long time_shot = 0;
	
	private float gravity = 0.3f; //units per second	
	private float angle_around_hoop_when_shot, zenith_when_shot, azimuth_when_shot = 0, speed_when_shot;
	
	private SphericalObject basketball_shell;
	
	private long last_collision = 0;
	private long last_made_it = 0;
	
	public Basketball(ModelData model_data) {
		super(model_data);
		
		location = initial_location = new Vec3(112f, 12f, 0f);
		basketball_shell = new SphericalObject(location, 1.2f);
	}
	
	public void updateLocation(float angle_around_hoop) {
		previous_location = new Vec3(location.x, location.y, location.z);
		
		if(being_thrown) {
//			location = new Vec3((float) (initial_location.x + 62 * Math.cos(Math.toRadians(angle_around_hoop_when_shot))),
//								initial_location.y, 
//								(float) (initial_location.z + 62 * Math.sin(Math.toRadians(angle_around_hoop_when_shot))));
			
			float time_dif = (System.currentTimeMillis() - time_shot) / 1000f;
			
			initial_velocity_when_shot.x *= 0.985f;
			initial_velocity_when_shot.y -= (gravity * time_dif);
			initial_velocity_when_shot.z *= 0.985f;
			location.x += (initial_velocity_when_shot.x * time_dif);
			location.y += (initial_velocity_when_shot.y * time_dif);
			location.z += (initial_velocity_when_shot.z * time_dif);
			
//				if(location.y < 0)
//					location.y = 0;
		}
		else {
			location = new Vec3((float) (initial_location.x + 62 * Math.cos(Math.toRadians(angle_around_hoop))),
								initial_location.y, 
								(float) (initial_location.z + 62 * Math.sin(Math.toRadians(angle_around_hoop))));
		}
		
		if((location.y - basketball_shell.radius) < -0.6f && location.x < 130f && location.z < 69f && location.z > -69f) {
			location.y = -0.6f;
		}
		
		basketball_shell.center = new Vec3(location.x, location.y, location.z);
	}
	
	public int isInside(SphericalObject rim) {
		if(System.currentTimeMillis() - last_made_it < 1000)
			return 0;
		
		Vec3 projected_point = new Vec3(basketball_shell.center.x, rim.center.y, basketball_shell.center.z);
		float temp1 = Vec3.distanceSquared(projected_point, basketball_shell.center);
		float temp2 = basketball_shell.radius * basketball_shell.radius;
		if(temp1 < temp2) {
			float circle_radius = temp2 - temp1;
			float distance = Vec3.distanceSquared(projected_point, rim.center);
			if(distance > rim.radius * rim.radius + circle_radius * circle_radius) {
				return 0;
			}
			else if(distance <= Math.abs(rim.radius * rim.radius - circle_radius * circle_radius)) {
				last_made_it = System.currentTimeMillis();
				return 1;
			}
			else {
				return -1;
			}
		}
		
		return 0;
	}
	
	public void checkForCollision(Model m) {
		long temp = System.currentTimeMillis();
		
		if(temp - last_collision < 500)
			return;
		
		for(TriangularObject face : m.faces) {
			if(face.collidesWith(basketball_shell)) {
				initial_velocity_when_shot = reflect(initial_velocity_when_shot, new Vec3(face.normal.y, face.normal.z, face.normal.x));
				last_collision = temp;
				break;
			}
		}
	}
	
	public void checkForCollision(float y) {
		if((basketball_shell.center.y - basketball_shell.radius < y) && location.x < 130f && location.z < 69f && location.z > -69f) {
			Vec3 normal = new Vec3(0, 1, 0);
			initial_velocity_when_shot = reflect(initial_velocity_when_shot, normal);
		}
	}
	
	public void shoot(float angle_around_hoop, float zenith, float azimuth, float speed) {
		being_thrown = true;
		time_shot = System.currentTimeMillis();
		
		angle_around_hoop_when_shot = angle_around_hoop;
		zenith_when_shot = zenith;
		azimuth_when_shot = azimuth;
		speed_when_shot = speed;
		
		location = new Vec3((float) (initial_location.x + 62 * Math.cos(Math.toRadians(angle_around_hoop))),
							initial_location.y, 
							(float) (initial_location.z + 62 * Math.sin(Math.toRadians(angle_around_hoop))));

		initial_velocity_when_shot = new Vec3((float) (speed * Math.cos(Math.toRadians((angle_around_hoop + 180) + azimuth))), 
											  (float) (speed * 1.5f * Math.sin(Math.toRadians(-zenith))), 
											  (float) (speed * Math.sin(Math.toRadians((angle_around_hoop + 180) + azimuth))));
	}
	
	public void setLocation(Vec3 location) {
		this.location = new Vec3(location.x, location.y, location.z);
	}
	
	@Override
	public void render(OpenGLGraphics graphics) {
		
		graphics.model_matrix.translate(location.x, location.y, location.z);
		graphics.model_matrix.scale(2.5f, 2.5f, 2.5f);
		graphics.model_matrix.rotate(0, (float) (Math.toRadians(270) - Vec2.angleBetween(113f, 0f, location.x, location.z)), 0);
		super.render(graphics);
	}
	
	private Vec3 reflect(Vec3 velocity, Vec3 normal) {
		Vec3 temp1 = Vec3.multiply(normal, 2.0f * Vec3.dotProduct(velocity, normal));
		Vec3 temp2 = Vec3.subtract(velocity, temp1);
		return new Vec3(temp2.x, temp2.y, temp2.z);
	}
}
