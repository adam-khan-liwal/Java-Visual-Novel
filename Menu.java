import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.*;

import java.util.ArrayList;

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

	ArrayList<Rectangle> menu_boxes = new ArrayList<Rectangle>();

	// Buttons
	Image start;
	Image load;
	Image quit;
	// Background
	Image room;

	StateBasedGame sbg;

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
		this.sbg = sbg;
	}

	public void render (GameContainer gc, StateBasedGame sbg, Graphics g) throws SlickException
	{	
		float basex = (75f/100f) * gc.getWidth();  // Draws after 75% of the width is covered
		float basey = (30f/100f) * gc.getHeight(); // Draw after 30% of the height is covered
		// Background image
		room.draw(0, 0);

		// Mouse coordinates (Debug only)
		g.drawString(mouse, 50, 50);

		// Each button is drawn 40 pixels below another
		int x = (int)basex;
		int y = (int)basey;
		start.draw(x, y);
		menu_boxes.add(new Rectangle(x, y, start.getWidth(), start.getHeight()));
		y += start.getHeight() + 40;
		load.draw(x, y);
		menu_boxes.add(new Rectangle(x, y, load.getWidth(), load.getHeight()));
		y += load.getHeight() + 40;
		quit.draw(x, y);
		menu_boxes.add(new Rectangle(x, y, quit.getWidth(), quit.getHeight()));
	}

	public void update (GameContainer gc, StateBasedGame sbg, int delta) throws SlickException
	{
		Input input = gc.getInput();
		mouse = "X: " + Mouse.getX() + " Y: " + Mouse.getY();
		
		// Series of if/else statements works as intended
		if(menu_boxes.get(0).contains(Mouse.getX(), gc.getHeight() - Mouse.getY()))
		{	
			start = new Image (ui_path + "start_hover.png");
		}
		else
		{
			start = new Image (ui_path + "start.png");
		}
		
		if(menu_boxes.get(1).contains(Mouse.getX(), gc.getHeight() - Mouse.getY()))
		{	
			load = new Image (ui_path + "load_hover.png");
		}
		else
		{
			load = new Image (ui_path + "load.png");
		}
		
		if(menu_boxes.get(2).contains(Mouse.getX(), gc.getHeight() - Mouse.getY()))
		{	
			quit = new Image (ui_path + "quit_hover.png");
		}
		else
		{
			quit = new Image (ui_path + "quit.png");
		}
		
		// Ideal solution but does not work very well
		/*
		for (int i = 0; i<menu_boxes.size(); i++)
		{
			if(menu_boxes.get(i).contains(Mouse.getX(), gc.getHeight() - Mouse.getY()))
			{	
				if(i == 0 )
				{
					load = new Image (ui_path + "load.png");
					quit = new Image (ui_path + "quit.png");
					
					start = new Image (ui_path + "start_hover.png");
					System.out.println("Hoverstart");
				}
				else if(i == 1)
				{
					start = new Image (ui_path + "start.png");
					quit = new Image (ui_path + "quit.png");
					
					load = new Image (ui_path + "load_hover.png");
					System.out.println("Hoverload");
				}
				else if(i==2)
				{
					start = new Image (ui_path + "start.png");
					load = new Image (ui_path + "load.png");
					
					quit = new Image (ui_path + "quit_hover.png");
					System.out.println("Hoverquit");
				}
			}
		}
		*/
		
	}

	@Override
	public void mouseReleased(int button, int x, int y)
	{
		if(button == Input.MOUSE_LEFT_BUTTON)
		{
			for(int i = 0; i< menu_boxes.size(); i++)
			{
				if(menu_boxes.get(i).contains(x, y))
				{
					if(i == 0)
					{
						System.out.println("Start Game");
						sbg.enterState(1);
					}
					else if( i==1 )
					{
						System.out.println("Load Game");
						sbg.enterState(1); //Load screwwn??
					}
					else if( i==2 )
					{
						System.out.println("Program is ending");
						System.exit(0);
					}

				}
			}
		}

	}


		public int getID()
		{
			return 0;
		}

	}
