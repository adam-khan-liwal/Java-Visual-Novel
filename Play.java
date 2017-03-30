package eclipse;

import org.newdawn.slick.*;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * VN engine implementation
 *
 * @author Adam Khan Liwal
 * @author Jegan Puroshothaman
 * @version 1.0
 */
public class Play extends BasicGameState{
	
	HashMap<Integer, String> action_types;
	
	// Alternative way to get the files
	/*
	static final String directory = System.getProperty("user.dir");
	static final String DB_URL = "jdbc:sqlite:"+directory+"\\mysqlitedb.db";
	static final String images_path = directory + "\\Assets\\Images\\";
	*/
	static final String DB_URL = "jdbc:sqlite:/Library/WebServer/Documents/csci213/mysqlitedb.db";
	static final String images_path = "/Library/WebServer/Documents/csci213/images/";
	
	private Image background;
	private String dialogue = "";
	private Sound sound;
	private Music bg_music;
	
	int a = 0;
	
	private boolean show_choices = false;
	ArrayList<Choice> choices = new ArrayList<Choice>();
	ArrayList<Rectangle> choice_boxes = new ArrayList<Rectangle>();

	boolean if_is_true = false;
	
	private int current_action_id = 0;
	
	public Play() {
		
		// define types of actions
		action_types = new HashMap<Integer, String>();
		action_types.put(1, "ACTION_DIALOGUE");
		action_types.put(2, "ACTION_ADD_CHOICE");
		action_types.put(3, "ACTION_IMAGE");
		action_types.put(4, "ACTION_SOUND");
		action_types.put(5, "ACTION_MUSIC");
		action_types.put(6, "ACTION_NEXTSCENE");
		action_types.put(7, "ACTION_IF");
		action_types.put(8, "ACTION_ELSE");
		action_types.put(9, "ACTION_ENDIF");
		action_types.put(10, "ACTION_SHOW_CHOICES");
		action_types.put(11, "ACTION_END");
		
	}
 
	public void init(GameContainer gc, StateBasedGame sbg)
			throws SlickException {
		nextAction();
	}
 
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g)
			throws SlickException {
		
		
		background.draw(0, 0, gc.getWidth(), gc.getHeight(), new Color (255, 255, 255, a));
		
		// dialogue box
		g.setColor(Color.black);
		// 20% of the screen
		float dialogue_height = (20f/100f * gc.getHeight());
		float dialogue_y = gc.getHeight() - dialogue_height;
		g.fillRect(0, dialogue_y, gc.getWidth(), dialogue_height);
		
		g.setColor(Color.white);
		
		if(show_choices == true)
		{
			for(int i = 0; i< choices.size(); i++)
			{
				g.setColor(Color.white);
				float choice_y =  dialogue_y + (20 * i);
				choice_boxes.add(new Rectangle(0, choice_y, gc.getWidth(), 20));
				g.fill(choice_boxes.get(i));
				g.setColor(Color.black);
				g.drawString(choices.get(i).text, 0, choice_y);
			}
		}
		else
		{
			g.drawString(dialogue, 0, dialogue_y);
		}
				
	}
 
	public void update(GameContainer gc, StateBasedGame sbg, int delta)
			throws SlickException {
		if(a != 255)
		{
			a+=5;
		}
	}
	
	/**
	 * Selects a choice
	 *
	 * @author Adam Khan Liwal
	 * @since 1.0
	 */	
	@Override
	public void mouseReleased(int button, int x, int y)
	{
		if(button == Input.MOUSE_LEFT_BUTTON)
		{
			if(show_choices == true)
			{
				for(int i = 0; i< choices.size(); i++)
				{
					if(choice_boxes.get(i).contains(x, y))
					{
						// add the choice to player log and clear the choices and choice_boxex array
						System.out.println("Clicked "+ choices.get(i).text);
						insertChoiceMade(choices.get(i));
						show_choices = false;
						choices.clear();
						choice_boxes.clear();
						nextAction();
					}
				}
			}
			else
			{
				nextAction();
			}
		}
	}
	
	/**
	 * Get the next action from the database and depending on it's type carry out its purpose
	 *
	 * @author Adam Khan Liwal
	 * @since 1.0
	 */	
	public void nextAction()
	{
		
		Connection c = null;
		Statement stmt = null;
		
		try {
		Class.forName("org.sqlite.JDBC");
		c = DriverManager.getConnection(DB_URL);
		c.setAutoCommit(false);
		System.out.println("Opened database successfully");
		
		stmt = c.createStatement();
		
		// Select the starting scene
		int starting_scene_id = 0;
		ResultSet rs = stmt.executeQuery( "SELECT * FROM Scene WHERE start = 1;" );
		while ( rs.next() ) {
			starting_scene_id = rs.getInt("id");
		}
		
		if(starting_scene_id == 0)
		{
			System.exit(0);
		}
		
		// select the next action
		rs = stmt.executeQuery( "SELECT * FROM Action WHERE sid = " + starting_scene_id +" AND sort > " + current_action_id +" ORDER BY sort ASC;" );
		
		while ( rs.next() ) {
			int type = rs.getInt("type");
			
			Statement stmt2 = c.createStatement();
			
			current_action_id = rs.getInt("sort");
			
			System.out.println("Current action id:" + current_action_id);
			
			if(action_types.get(type) == "ACTION_IF")
			{
				// check player log to see if user made the choice in the if condition
				ResultSet log_rs = stmt2.executeQuery( "SELECT * FROM PlayerLog WHERE choice_id = " + rs.getInt("xid"));
				System.out.println("If");

				// if user made the choice
				if(log_rs.next())
				{
					System.out.println("Choice was made");
					if_is_true = true;
					rs.next();
					type = rs.getInt("type");
					current_action_id = rs.getInt("sort");
				}
				else
				{
					System.out.println("finding next else or endif");
					// else jump to next else or endif action
					while ( rs.next() ) {
						type = rs.getInt("type");
						
						if( action_types.get(type) == "ACTION_ELSE")
						{
							System.out.println("found next else");
							rs.next();
							current_action_id = rs.getInt("sort");
							type = rs.getInt("type");
							break;
						}
						if( action_types.get(type) == "ACTION_ENDIF")
						{
							System.out.println("found next endif");
							rs.next();
							current_action_id = rs.getInt("sort");
							type = rs.getInt("type");
							break;
						}
					}
					
				}
			}
			
			if(action_types.get(type) == "ACTION_ELSE")
			{
				if(if_is_true == true)
				{
					// jump to endif part
					while ( rs.next() ) {
						type = rs.getInt("type");
						
						if( action_types.get(type) == "ACTION_ENDIF")
						{
							rs.next();
							current_action_id = rs.getInt("sort");
							break;
						}
					}
				}
			}
			
			if(action_types.get(type) == "ACTION_ENDIF")
			{
				if_is_true = false;
			}
			
			if(action_types.get(type) == "ACTION_SHOW_CHOICES")
			{
				show_choices = true;
				break;
			}
			
			if(action_types.get(type) == "ACTION_IMAGE")
			{
				// select image action from Action_showImage to get image position and image id
				ResultSet action_showimage_rs = stmt2.executeQuery( "SELECT * FROM Action_ShowImage WHERE id = " + rs.getInt("xid"));
				while(action_showimage_rs.next())
				{
					ResultSet image_rs = stmt2.executeQuery( "SELECT * FROM Image WHERE id = " + action_showimage_rs.getInt("image_id"));
					while(image_rs.next())
					{
						a = 0;
						String filename = image_rs.getString("filename");
						background = new Image(images_path + filename);
						System.out.println("Changed Background image");
					}
				}
			}
			
			if(action_types.get(type) == "ACTION_DIALOGUE")
			{
				ResultSet dialogue_rs = stmt2.executeQuery( "SELECT * FROM Dialogue WHERE id = " + rs.getInt("xid"));
				while(dialogue_rs.next())
				{
					dialogue = dialogue_rs.getString("text");
					System.out.println("Changed Dialogue");
				}
				break;
			}
			
			if(action_types.get(type) == "ACTION_ADD_CHOICE")
			{				
				ResultSet choice_rs = stmt2.executeQuery( "SELECT * FROM Choice WHERE id = " + rs.getInt("xid"));
				while(choice_rs.next())
				{
					choices.add(new Choice(choice_rs.getInt("id"), choice_rs.getString("text")));
					System.out.println("Choice added");
				}
			}
		}
		
		rs.close();
		stmt.close();
		c.close();
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
	}
	
	/**
	 * Insert the choice made into the player log
	 *
	 * @author Adam Khan Liwal
	 */	
	public void insertChoiceMade(Choice choice)
	{
		Connection c = null;
		Statement stmt = null;
		
		try {
		Class.forName("org.sqlite.JDBC");
		c = DriverManager.getConnection(DB_URL);
		c.setAutoCommit(false);
		
		
		stmt = c.createStatement();
		
		String sql = "INSERT INTO PlayerLog (choice_id) " +
	            "VALUES ("+ choice.id +" );"; 
	    stmt.executeUpdate(sql);
	    System.out.println("Inserted choice *"+ choice.text +"* into player log");
		stmt.close();
		c.commit();
		c.close();
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
	}
 
	public int getID() {
		return 1;   // Menu state is 0 and Play state is 1
 	}

}
