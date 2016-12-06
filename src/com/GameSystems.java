/**
 * Created on Oct 4, 2016 by ethan
 */
package com;

import com.enemy.EnemyEngine;
import com.game.GameEngine;
import com.graphics.GraphicsEngine;
import com.item.ItemEngine;
import com.quest.QuestEngine;
import com.viduus.charon.global.audio.AudioEngine;
import com.viduus.charon.global.input.InputEngine;
import com.viduus.charon.global.output.OutputHandler;
import com.viduus.charon.global.systems.SystemsEngine;
import com.world.WorldEngine;

/**
 * 
 * 
 * @author ethan
 */
public class GameSystems {

	// State of GameSystem
	private boolean is_stopping = false;
	private boolean has_init_audio = false;
	private boolean has_init_graphics = false;
	private boolean has_init_enemy = false;
	private boolean has_init_item= false;
	private boolean has_init_quest = false;
	private boolean has_init_systems = false;
	private boolean has_init_world = false;
	private boolean has_init_input = false;
	private boolean has_init_game = false;
	
	// All engines that will be used through the game
	public AudioEngine audio_engine;
	public GameEngine game_engine;
	public GraphicsEngine graphics_engine;
	public EnemyEngine enemy_engine;
	public ItemEngine item_engine;
	public QuestEngine quest_engine;
	public SystemsEngine systems_engine;
	public WorldEngine world_engine;
	public InputEngine input_engine;
	
	public GameSystems(){
	}
	
	/**
	 * Ends the game process
	 */
	public void stopGame(){
		if( !is_stopping ){
			is_stopping = true;
			OutputHandler.println("");
			OutputHandler out = new OutputHandler();
			out.startTimedPrintln("Preparing to close game...");
			
			if (has_init_game) {
				game_engine.stop();
			}
			
			if( has_init_input ){
				input_engine.stop();
			}
			
			if( has_init_systems ){
				systems_engine.stop();
			}
			
			if( has_init_audio ){
				audio_engine.stop();
			}
	
			if( has_init_graphics ){
				graphics_engine.stop();
			}
			
			out.endTimedPrint("Closing game now");
			System.exit(0);
		}
	}
	
	/**
	 * Initialize only what is needed to log in
	 */
	public void initMin(){
		OutputHandler out = new OutputHandler();
		out.startTimedPrintln("Starting up the Game!");
		OutputHandler.addTab();
		
		// First get information about the user's system
		if( !has_init_systems ){
			systems_engine = new SystemsEngine();
			has_init_systems = true;
		}
		
		// Then create the login page
		if( !has_init_graphics ){
			graphics_engine = new GraphicsEngine( this, false );
			has_init_graphics = true;
		}
		
		// Then setup user input
		if( !has_init_input ){
			input_engine = new InputEngine();
			has_init_input = true;
		}
		
		if( !has_init_audio ){
			audio_engine = new AudioEngine();
			has_init_audio = true;
		}
		
		OutputHandler.removeTab();
		out.endTimedPrintln("Finished starting the game");
		OutputHandler.println("");
	}
	
	/**
	 * Initialize everything
	 */
	public void initGame(){
		// Just in case something was missed earlier
		initMin();
		
		// Initialize the enemy engine
		if( !has_init_enemy ){
			enemy_engine = new EnemyEngine( this );
			has_init_enemy = true;
		}
		
		// Initialize the item engine
		if( !has_init_item ){
			item_engine = new ItemEngine( this );
			has_init_item = true;
		}
		
		// Initialize the quest engine
		if( !has_init_quest ){
			quest_engine = new QuestEngine( this );
			has_init_quest = true;
		}
		
		// Initialize the world engine
		if( !has_init_world ){
			world_engine = new WorldEngine( this );
			has_init_world = true;
		}
		
		if (!has_init_game) {
			game_engine = new GameEngine(this, 60);
			has_init_game = true;
		}
	}
	
}
