/**
 * Created on Oct 4, 2016 by ethan
 */
package com.graphics;

import javax.xml.parsers.ParserConfigurationException;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import com.GameSystems;
import com.viduus.charon.global.error.ErrorHandler;
import com.viduus.charon.global.graphics.Dimension;
import com.viduus.charon.global.graphics.opengl.OpenGLPanel;
import com.viduus.charon.global.graphics.opengl.models.loader.DaeLoader;
import com.viduus.charon.global.input.controller.device.KeyboardMouseController;
import com.viduus.charon.global.output.OutputHandler;

/**
 * 
 * 
 * @author ethan
 */
public class GraphicsEngine {

	public static final int LOADING_SCREEN = 100;

	private GLFWErrorCallback errorCallback;

	private FrameManager frame_manager;
	private GraphicsFrame graphics_frame;
	private GameSystems game_systems;
	private OpenGLPanel current_frame;

	private int frame_focus;
	private boolean running;
	
	private boolean is2D;
	
	/**
	 * 
	 * TODO
	 * @param game_systems
	 */
	public GraphicsEngine(GameSystems game_systems, boolean is2D) {
		OutputHandler out = new OutputHandler();
		out.startTimedPrintln("Starting up Graphics Engine...");
		OutputHandler.addTab();
		
		this.game_systems = game_systems;
		
		this.is2D = is2D;
		
		init();
		
		OutputHandler.removeTab();
		out.endTimedPrintln("Finished setting up the Graphics Engine");
	}

	/**
	 * Starts the graphics thread
	 */
	public void start() {
		if( !running ){
			running = true;
		}
	}
	
	
	/**
	 * Tries to stop the graphics thread
	 */
	public void stop() {
		if( running ){
			graphics_frame.close();
			
			// Release the GLFWErrorCallback
			errorCallback.free();
			running = false;
		}
	}
	
	
	/**
	 * Shows a given frame based off of the int selector.
	 * @param frame_id - Id of the desired frame
	 * @param remove_curr_frame - If true removes the previous frame
	 */
	public void showFrame( int frame_id, boolean remove_curr_frames ){
		// Check to make sure that this is a new frame
		if( frame_focus != frame_id ){
			frame_focus = frame_id;

			OpenGLPanel new_frame = null;
			switch( frame_id ){
			case(LOADING_SCREEN): 
				new_frame = frame_manager.retrieveInitialGameLoadingFrame( graphics_frame );
				break;
			}
			
			if( new_frame != null ){
				current_frame = new_frame;
				// Remove previous frame if necessary
				if( remove_curr_frames ){
					graphics_frame.removeAll();
				}
				// Add this frame
				graphics_frame.add( current_frame );
			}
		}
	}
	
	
	/**
	 * Shows a given frame based off of the int selector.<br>
	 * <b>Short hand for showFrame( frame_id, True )</b>
	 * @param frame_id - Id of the desired frame
	 * @see #showFrame(int, boolean)
	 */
	public void showFrame( int frame_id ){
		showFrame( frame_id, true );
	}
	

	/**
	 * Initialize everything that the Graphics Thread will need like the
	 * windows and OpenGL context.
	 */
	private void init(){
		OutputHandler out = new OutputHandler();
		
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFW.glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));

		out.startTimedPrintln("Creating GraphicsFrame...");
		OutputHandler.addTab();
		graphics_frame = new GraphicsFrame( game_systems, this.is2D );
		OutputHandler.removeTab();
		out.endTimedPrintln("Finished creating GraphicsFrame");

		out.startTimedPrintln("Creating FrameManager...");
		OutputHandler.addTab();
		frame_manager = new FrameManager( game_systems );
		OutputHandler.removeTab();
		out.endTimedPrintln("Finished creating FrameManager");
	}

	public void startEventQueue() {
		graphics_frame.runEventQueue(game_systems.input_engine);
	}

	public void bindController(KeyboardMouseController controller) {
		controller.setControllerOwnership(graphics_frame);
	}

	public Dimension getCurrentFrameSize() {
		return this.current_frame.getDimension();
	}
}
