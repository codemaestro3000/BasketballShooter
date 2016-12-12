/**
 * Created on Oct 4, 2016 by ethan
 */
package com.world;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.lwjgl.opengl.GL11;
import org.xml.sax.SAXException;

import com.GameSystems;
import com.input.PlayerControlsState;
import com.models.Basketball;
import com.models.BasketballHoop;
import com.models.Goofy;
import com.player.MainPlayer;
import com.viduus.charon.global.audio.Sound;
import com.viduus.charon.global.error.ErrorHandler;
import com.viduus.charon.global.graphics.opengl.OpenGLGraphics;
import com.viduus.charon.global.graphics.opengl.shaders.exceptions.ShaderException;
import com.viduus.charon.global.graphics.opengl.textures.ImageTexture;
import com.viduus.charon.global.output.OutputHandler;
import com.viduus.charon.global.util.math.Mat4;
import com.viduus.charon.global.util.math.Vec3;
import com.viduus.util.models.loader.DaeLoader;

/**
 * 
 * 
 * @author ethan
 */
public class WorldEngine extends com.viduus.charon.global.world.WorldEngine {

	private Goofy goofy;
	private Basketball basketball;
	private BasketballHoop basketball_hoop;
	
	private ImageTexture skybox_left;
	private ImageTexture skybox_right;
	private ImageTexture skybox_top;
	private ImageTexture skybox_bottom;
	private ImageTexture skybox_front;
	private ImageTexture skybox_back;
	private ImageTexture basketball_court;
	
	private MainPlayer player;
	
	private Vec3 bcourt_1, bcourt_2, bcourt_3, bcourt_4;
	
	private long basketball_shot_start_time = 0;
	private boolean basketball_shot = false;
	public float basketball_shot_percent;
	public int times_scored = 0;
	private long last_score_time = 0;
	
	private long bball_locs_captured_time = 0;
	private boolean capture_bball_locs = false;
	private int bball_location_index = 0;
	ArrayList<Vec3> bball_locations;
	public boolean replay_enabled = false;
	private int speed = -1;
	private long speed_count = 0;
	
	private long replay_focus_toggled = 0;
	private boolean replay_focus_on_hoop = true;
	
	Sound theme, win_sound, lose_sound;
	
	private final GameSystems game_systems;
	
	/**
	 * TODO
	 * @param gameSystems
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 */
	public WorldEngine(GameSystems game_systems) {
		this.game_systems = game_systems;
		this.bball_locations = new ArrayList<Vec3>();
		
		try
		{
		    DaeLoader DAELoader = new DaeLoader();
		    goofy = new Goofy(DAELoader.loadModel("./models/Goofy.dae"));
		    basketball = new Basketball(DAELoader.loadModel("./models/Basketball.dae"));
		    basketball_hoop = new BasketballHoop(DAELoader.loadModel("./models/BasketballHoop.dae"));
		    player = new MainPlayer(game_systems);
		    
		    bcourt_1 = new Vec3(130f, -.7f, -69f);
		    bcourt_2 = new Vec3(-130f, -.7f, -69f);
		    bcourt_3 = new Vec3(-130f, -.7f, 69f);
		    bcourt_4 = new Vec3(130f, -.7f, 69f);
		    
		    theme = game_systems.audio_engine.createSound("./sounds/theme.ogg");
		    theme.setToLoop(true);
		    theme.setVolume(0.2f);
		    game_systems.audio_engine.playSound(theme);
		    
		    win_sound = game_systems.audio_engine.createSound("./sounds/bingo.ogg");
		    lose_sound = game_systems.audio_engine.createSound("./sounds/fooled_you.ogg");
		}
		catch(ParserConfigurationException | SAXException | IOException e)
		{
			ErrorHandler.catchError(e);
		}
	}

	@Override
	public void saveState() {
		//Loop through the state of each region and save it
	}
	
	@Override
	public void render(OpenGLGraphics graphics) {
		super.render(graphics);
		
		PlayerControlsState player_controls_state = player.getControlsState();
		
		if(player_controls_state.replay_enabled == true) { 
			replay_enabled = true;
		} 
		else if(player_controls_state.replay_disabled == true) { 
			replay_enabled = false;
		}
		
		if(player_controls_state.replay_focus_toggle && System.currentTimeMillis() - replay_focus_toggled > 500) {
			replay_focus_toggled = System.currentTimeMillis();
			replay_focus_on_hoop = !replay_focus_on_hoop;
		}
		
		if(replay_enabled) {
			if(player_controls_state.full_speed_selected) {
				speed = 1;
				speed_count = 0;
				bball_location_index = 0;
			}
			else if(player_controls_state.half_speed_selected) {
				speed = 5;
				speed_count = 0;
				bball_location_index = 0;
			}
			else if(player_controls_state.tenth_speed_selected) {
				speed = 10;
				speed_count = 0;
				bball_location_index = 0;
			}
			
			if(bball_locations.size() != 0) {
				if(speed != -1) {
					if(speed_count % speed == 0) {
						if(bball_location_index < bball_locations.size()) {
							basketball.setLocation(bball_locations.get(bball_location_index));
							bball_location_index++;
							speed_count++;
						}
						else {
							speed = -1;
							speed_count = 0;
							bball_location_index = 0;
						}
					}
					else {
						speed_count++;
					}
				}
			}
			else {
				replay_enabled = false;
			}
		}
		
		player.updateState();
		
		try {
			
			skybox_left = (ImageTexture) graphics.texture_manager.loadImage(graphics, "./skybox/rt.png");
			skybox_right = (ImageTexture) graphics.texture_manager.loadImage(graphics, "./skybox/lf.png");
			skybox_top = (ImageTexture) graphics.texture_manager.loadImage(graphics, "./skybox/dn.png");
			skybox_bottom = (ImageTexture) graphics.texture_manager.loadImage(graphics, "./skybox/up.png");
			skybox_front = (ImageTexture) graphics.texture_manager.loadImage(graphics, "./skybox/ft.png");
			skybox_back = (ImageTexture) graphics.texture_manager.loadImage(graphics, "./skybox/bk.png");
			basketball_court = (ImageTexture) graphics.texture_manager.loadImage(graphics, "./img/BasketBallCourt.png");
			
			graphics.setDisplay3D();
			GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
			
			
			Mat4 lookAt;
			if(!replay_enabled)
				lookAt = Mat4.createLookAtMatrix(player.eye, player.center, new Vec3(0, 1, 0));
			else {	
				lookAt = Mat4.createLookAtMatrix(new Vec3(basketball.location.x, basketball.location.y + 20, basketball.location.z), replay_focus_on_hoop ? basketball_hoop.location : player.eye, new Vec3(0, 1, 0));
			}
			
			graphics.view_matrix.reset();
			graphics.view_matrix.multiply(lookAt);
			
			float skybox_size = 10000.0f;
			Vec3 p1 = new Vec3(skybox_size, -skybox_size,  skybox_size);
			Vec3 p2 = new Vec3(skybox_size, skybox_size,  skybox_size);
			Vec3 p3 = new Vec3(-skybox_size, -skybox_size,  -skybox_size);
			Vec3 p4 = new Vec3(skybox_size, -skybox_size, -skybox_size);
			Vec3 p5 = new Vec3(skybox_size,  skybox_size, -skybox_size);
			Vec3 p6 = new Vec3(-skybox_size, -skybox_size,  skybox_size);
			Vec3 p7 = new Vec3(-skybox_size,  skybox_size,  skybox_size);
			Vec3 p8 = new Vec3(-skybox_size,  skybox_size, -skybox_size);
			if(!replay_enabled) p1.add(player.eye); else p1.add(basketball.location);    
			if(!replay_enabled) p2.add(player.eye); else p2.add(basketball.location);     
			if(!replay_enabled) p3.add(player.eye); else p3.add(basketball.location);
			if(!replay_enabled) p4.add(player.eye); else p4.add(basketball.location);
			if(!replay_enabled) p5.add(player.eye); else p5.add(basketball.location);  
			if(!replay_enabled) p6.add(player.eye); else p6.add(basketball.location); 
			if(!replay_enabled) p7.add(player.eye); else p7.add(basketball.location);
			if(!replay_enabled) p8.add(player.eye); else p8.add(basketball.location);
			skybox_left.drawTexture(graphics, p1, p4, p5, p2);
			skybox_right.drawTexture(graphics, p3, p6, p7, p8);
			skybox_top.drawTexture(graphics, p8, p7, p2, p5);
			skybox_bottom.drawTexture(graphics, p3, p6, p1, p4);
			skybox_front.drawTexture(graphics, p4, p3, p8, p5);
			skybox_back.drawTexture(graphics, p6, p1, p2, p7);

			basketball_court.drawTexture(graphics, bcourt_1, bcourt_2, bcourt_3, bcourt_4);
			
			graphics.shader_manager.useShader("greenisgood");

			//Render the Basketball Hoop
			basketball_hoop.render(graphics);
			
			//Render Goofy
			goofy.setLocation(player.angle_around_hoop);
			goofy.render(graphics);
			
			//Render the Basketball
			if(!replay_enabled) {
				long temp = System.currentTimeMillis();
				if(temp - basketball_shot_start_time > 1000) {
					basketball_shot_start_time = 0;
					basketball_shot_percent = 0;
					basketball_shot = false;
				}
				else if (basketball_shot) {
					basketball_shot_percent = (temp - basketball_shot_start_time) / 1000f;
				}
				
				if(player_controls_state.select) {
					float dif = temp - basketball_shot_start_time;
					if(basketball_shot_start_time == 0) {
						basketball_shot_start_time = temp;
						basketball_shot = true;
					}
					else if(dif > 100) {
						basketball_shot = false;
						capture_bball_locs = true;
						bball_locs_captured_time = temp;
						float speed = basketball_shot_percent * 8f;
						basketball.shoot(player.angle_around_hoop, player.zenith, player.azimuth, speed);
						bball_locations = new ArrayList<Vec3>();
						bball_location_index = 0;
						speed = -1;
						speed_count = 0;
					}
				}
				
				basketball.updateLocation(player.angle_around_hoop);
				basketball.checkForCollision(basketball_hoop);
				basketball.checkForCollision(bcourt_1.y);
				int score = basketball.isInside(basketball_hoop.rim);
				if(score == 1) {
					if(temp - last_score_time > 500) {
						times_scored++;
						game_systems.audio_engine.playSound(win_sound);
						last_score_time = temp;
					}
				}
				else if(score == -1) {
					game_systems.audio_engine.playSound(lose_sound);
				}
				
				if(capture_bball_locs) {
					if(temp - bball_locs_captured_time < 3000) {
						bball_locations.add(new Vec3(basketball.location.x, basketball.location.y, basketball.location.z));
					}
				}
			}
			
			basketball.render(graphics);
		} catch (IOException | ShaderException e) {
			ErrorHandler.catchError(e);
		}
	}
}
