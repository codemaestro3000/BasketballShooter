/**
 * Created on Oct 4, 2016 by ethan
 */
package com.world;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.lwjgl.opengl.GL11;
import org.xml.sax.SAXException;

import com.GameSystems;
import com.graphics.Model;
import com.input.PlayerControlsState;
import com.models.Basketball;
import com.models.BasketballHoop;
import com.models.Goofy;
import com.player.MainPlayer;
import com.viduus.charon.global.error.ErrorHandler;
import com.viduus.charon.global.graphics.opengl.OpenGLGraphics;
import com.viduus.charon.global.graphics.opengl.shaders.ShaderProgram;
import com.viduus.charon.global.graphics.opengl.shaders.exceptions.ShaderException;
import com.viduus.charon.global.graphics.opengl.shaders.variables.ShaderAttribute;
import com.viduus.charon.global.graphics.opengl.shapes.Rectangle;
import com.viduus.charon.global.graphics.opengl.textures.ImageTexture;
import com.viduus.charon.global.output.OutputHandler;
import com.viduus.charon.global.util.math.Mat4;
import com.viduus.charon.global.util.math.Vec3;
import com.viduus.util.models.loader.DaeLoader;
import com.viduus.util.models.util.Color;

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
	
	
	/**
	 * TODO
	 * @param gameSystems
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 */
	public WorldEngine(GameSystems gameSystems) {
		try
		{
		    DaeLoader DAELoader = new DaeLoader();
		    goofy = new Goofy(DAELoader.loadModel("./models/Goofy.dae"));
		    basketball = new Basketball(DAELoader.loadModel("./models/Basketball.dae"));
		    basketball_hoop = new BasketballHoop(DAELoader.loadModel("./models/BasketballHoop.dae"));
		    player = new MainPlayer(gameSystems);
		    
		    bcourt_1 = new Vec3(130f, -.7f, -69f);
		    bcourt_2 = new Vec3(-130f, -.7f, -69f);
		    bcourt_3 = new Vec3(-130f, -.7f, 69f);
		    bcourt_4 = new Vec3(130f, -.7f, 69f);
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
			
			Mat4 lookAt = Mat4.createLookAtMatrix(player.eye, player.center, new Vec3(0, 1, 0));
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
			p1.add(player.eye);    
			p2.add(player.eye);     
			p3.add(player.eye);
			p4.add(player.eye);  
			p5.add(player.eye);  
			p6.add(player.eye); 
			p7.add(player.eye); 
			p8.add(player.eye); 
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
			long temp = System.currentTimeMillis();
			if(temp - basketball_shot_start_time > 1000) {
				basketball_shot_start_time = 0;
				basketball_shot_percent = 0;
				basketball_shot = false;
			}
			else if (basketball_shot) {
				basketball_shot_percent = (temp - basketball_shot_start_time) / 1000f;
			}
			
			if(player.getControlsState().select) {
				float dif = temp - basketball_shot_start_time;
				if(basketball_shot_start_time == 0) {
					basketball_shot_start_time = temp;
					basketball_shot = true;
				}
				else if(dif > 100) {
					basketball_shot = false;
					float speed = basketball_shot_percent * 150f;
					basketball.shoot(player.angle_around_hoop, player.zenith, player.azimuth, speed);
				}
			}
			
			basketball.updateLocation(player.angle_around_hoop);
			basketball.checkForCollision(basketball_hoop);
			basketball.checkForCollision(bcourt_1.y);
			basketball.render(graphics);
		} catch (IOException | ShaderException e) {
			ErrorHandler.catchError(e);
		}
	}
}
