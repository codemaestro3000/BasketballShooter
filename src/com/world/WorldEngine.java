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
import com.viduus.charon.global.error.ErrorHandler;
import com.viduus.charon.global.graphics.opengl.OpenGLGraphics;
import com.viduus.charon.global.graphics.opengl.shaders.ShaderProgram;
import com.viduus.charon.global.graphics.opengl.shaders.exceptions.ShaderException;
import com.viduus.charon.global.graphics.opengl.shaders.variables.ShaderAttribute;
import com.viduus.charon.global.graphics.opengl.shapes.Rectangle;
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

	private Model goofy;
	
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
		    DaeLoader loader = new DaeLoader();
		    goofy = new Model(loader.loadModel("./models/Goofy.dae"));
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
		
		graphics.setDisplay3D();
		
		Mat4 transformer = new Mat4();
		transformer.translate(0, -0.05f, -0.1f);
//		transformer.rotate(0, 3.14159f / 2.0f, 0);
		
		graphics.view_matrix.multiply(transformer);
		
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		
		try {
			graphics.shader_manager.useShader("greenisgood");
		} catch (ShaderException e) {
			ErrorHandler.catchError(e);
		}
		
		goofy.render(graphics);
	}
}
