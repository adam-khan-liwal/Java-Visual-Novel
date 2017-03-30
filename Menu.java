import org.newdawn.slick.*;
import org.newdawn.slick.state.*;
import org.lwjgl.input.*;

/**
 * Main Menu Screen
 * @author Adam Khan Liwal
 * @author Jegan Purushothaman
 *
 */
public class Menu extends BasicGameState {
	
	// Directory for images
	String images_path = System.getProperty("user.dir") + "\\Assets\\Images\\";
	// Directory for UI elements
	String ui_path = System.getProperty("user.dir") + "\\Assets\\UI\\";
	
	// Buttons
	Image start;
	Image load;
	Image quit;
	// Background
	Image room;
	
	// Base coordinates of the first button
	int basex = 870; 
	int basey = 180;
	
	public String mouse = "No Input"; // For mouse pointer location
	
	public Menu()
	{
		
	}
	
	public void init(GameContainer gc, StateBasedGame sbg) throws SlickException 
	{
		// All images are set to files
		start = new Image (ui_path + "start.png");
		quit = new Image (ui_path + "quit.png");
		load = new Image (ui_path + "load.png");
		room = new Image (images_path + "room2.jpg");
	}
	
	public void render (GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException
	{	
		int rscale = basey;  // Stores the y coordinate
		
		// Background image
		room.draw(0, 0);
		
		// Mouse coordinates (Debug only)
		g.drawString(mouse, 50, 50);
		
		// Each button is drawn 40 pixels below another
		start.draw(basex, rscale);
		rscale += start.getHeight() + 40;
		load.draw(basex, rscale);
		rscale += load.getHeight() + 40;
		quit.draw(basex, rscale);
	}
	
	public void update (GameContainer gc, StateBasedGame sbg, int delta) throws SlickException
	{
		Input input = gc.getInput();
		
		// Java and Graphics coordinates work differently when it comes to the y coordinate
		int mousex = Mouse.getX();
		int mousey = gc.getHeight() - Mouse.getY();  
		int uscale = basey;
		mouse = "X: " + mousex + " Y: " + mousey;
		
		// Start button
		if(mousex > basex && mousex  < (basex+start.getWidth())  && mousey > uscale && mousey < (uscale+start.getHeight())) // If the mouse pointer is over the button
		{
			start = new Image (ui_path + "start_hover.png"); // Image updates
			
			if(input.isMouseButtonDown(0)) // If user clicks button
			{
				sbg.enterState(1); // Starts game (Play class)
			}
		}
		else
		{
	        start = new Image (ui_path + "start.png"); // Otherwise stays the same (If pointer goes out of direction)
		}
		
		uscale = uscale + start.getHeight() + 40; // Scalining system reduces the number of places to change when an edit is made
		
		// Load button
		if(mousex > basex && mousex  < (basex+load.getWidth())  && mousey > uscale && mousey < (uscale+load.getHeight()))
		{
			load = new Image (ui_path + "load_hover.png");
			
			if(input.isMouseButtonDown(0))
			{
				sbg.enterState(1);  // Needs load screen
			}
		}
		else
		{
			load = new Image (ui_path + "load.png");
		}
		
		uscale = uscale + load.getHeight() + 40;
		
		// Quit button
		if(mousex > basex && mousex  < (basex+quit.getWidth())  && mousey > uscale && mousey < (uscale+quit.getHeight()))
		{
			quit = new Image (ui_path + "quit_hover.png");
			
			if(input.isMouseButtonDown(0))
			{
				System.out.println("Program is Ending");
				System.exit(0);
			}
		}
		else
		{
			quit = new Image (ui_path + "quit.png");
		}
		
	}
	
	public int getID()
	{
		return 0;
	}

}
