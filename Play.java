package eclipse;

import org.lwjgl.input.Mouse;
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
 * @author Jegan Purushothaman
 * @version 1.0
 */
public class Play extends BasicGameState{
	
	HashMap<Integer, String> action_types;
	
	static final String directory = System.getProperty("user.dir");
	static final String DB_URL = "jdbc:sqlite:"+directory+ "\\mysqlitedb.db";
	static final String images_path = directory + "\\Assets\\Images\\";
	static final String ui_path = System.getProperty("user.dir") + "\\Assets\\UI\\";
	
	public String mouse = "No Input"; // For mouse pointer location
	
	private Image textbox;
	private Image background;
	private Image choiceBox;
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
		textbox = new Image (ui_path +"textbox.png");
		choiceBox = new Image (ui_path + "choice.png");
		nextAction();
	}
 
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g)
			throws SlickException {
		
		g.drawString(mouse, 100, 100);
		background.draw(0, 0, gc.getWidth(), gc.getHeight(), new Color (255, 255, 255, a));
		
		// Dialogue Box
		float textbox_x = gc.getWidth()*(15f/100f);
		float textbox_y = gc.getHeight()-150;
		float dialogue_y = gc.getHeight() - textbox_y;
		
		// Choice Box
		float choice_x = gc.getWidth()*(20f/100f);
		float choice_y = gc.getHeight()*(20f/100f);
		
		g.setColor(Color.black);
		
		if(show_choices == true)
		{
			for(int i = 0; i< choices.size(); i++)
			{
				// Draws the choices and adds the text
				g.setColor(Color.white);
				choiceBox.draw(choice_x, choice_y);
				g.drawString(choices.get(i).text, choice_x+100, choice_y+70);
				choice_boxes.add(new Rectangle(choice_x, choice_y, choiceBox.getWidth(), choiceBox.getHeight()));
				choice_y = choiceBox.getHeight()+ choice_y  + 20;
			}
		}
		else
		{
			// Draws the dialogue box and displays the dialogue
			textbox.draw(textbox_x, textbox_y);
			g.drawString(dialogue,textbox_x+50, textbox_y+30);
		}
				
	}
 
	public void update(GameContainer gc, StateBasedGame sbg, int delta) throws SlickException {
	
		int mousex = Mouse.getX();
		int mousey = gc.getHeight() - Mouse.getY();  
		mouse = "X: " + mousex + " Y: " + mousey;
		
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
		return 1;
	}

}
