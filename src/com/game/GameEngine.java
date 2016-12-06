package com.game;

import com.GameSystems;
import com.viduus.charon.global.error.ErrorHandler;

public class GameEngine implements Runnable{
	
	private final GameSystems game_systems;
	private boolean running = false;
	private long desired_duration;
	private Thread tick_thread;
	
	public GameEngine(GameSystems game_systems, long fps) {
		this.game_systems = game_systems;
		this.desired_duration = fps;
		
		tick_thread = new Thread(this, "Game-Engine-Tick");
		tick_thread.start();
	}

	@Override
	public void run() {
		init();
		loop();
		shutdown();
	}
	
	public void init() {
		running = true;
	}
	
	public void loop() {
		
		long last_time = System.currentTimeMillis();
		long elapsed_time = 0;
		
		while(running) {
			try {
				game_systems.world_engine.tick(elapsed_time);
				
				// Wait so that this gets called at the specified fps
				long current_time = System.currentTimeMillis();
				elapsed_time = current_time - last_time;
				
				long required_break = desired_duration-elapsed_time;
				
				if( required_break > 0 ){
					elapsed_time += required_break;
					Thread.sleep(required_break-1);
				}
				last_time = System.currentTimeMillis();
			}
			catch(InterruptedException e) {
				ErrorHandler.catchError(e);
			}
		}
	}
	
	private void shutdown() {
		//Save world state
		game_systems.world_engine.saveState();
	}
	
	public void stop() {
		
		try {
			
			this.running = false;
			tick_thread.join();
			
		} catch (InterruptedException e) {
			ErrorHandler.catchError(e);
		}
	}
}
