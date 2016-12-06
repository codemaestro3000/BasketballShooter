/**
 * Created on Oct 4, 2016 by ethan
 */
package com.graphics;

import com.GameSystems;
import com.viduus.charon.global.graphics.opengl.GraphicsFunction;
import com.viduus.charon.global.graphics.opengl.OpenGLFrame;
import com.viduus.charon.global.graphics.opengl.OpenGLGraphics;

/**
 * 
 * 
 * @author ethan
 */
public class GraphicsFrame extends OpenGLFrame {

	private GameSystems game_systems;

	/**
	 * TODO
	 * @param game_systems
	 */
	public GraphicsFrame(GameSystems game_systems, boolean is2D) {
		super("", true, true, is2D);
		
		this.game_systems = game_systems;
        
        queueGraphicsFunction( new GraphicsFunction(){

			@Override
			public void call() {
				game_systems.systems_engine.checkGraphicsSupport();
			}
        	
        });
		
//        setSize(800, 600);
        setLocationRelativeTo(null);
        enableVSync();
        setVisible(true);
        setFPSVisible(true);
        setDefaultCloseOperation( OpenGLFrame.KILL_ON_CLOSE );
	}

	/**
	 * Renders the content of the frame.<br><br>
	 * <b>Note</b> - This function does nothing when in OpenGL mode.
	 * @param g2 
	 */
	@Override
	public void render( OpenGLGraphics graphics ){
        super.render(graphics);
	}

}
