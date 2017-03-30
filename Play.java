package eclipse;

import org.newdawn.slick.*;
import org.newdawn.slick.state.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;


/**
 * VN engine implementation
 *
 * @author Adam Khan Liwal
 * @author Jegan Puroshothaman
 */
public class Play extends BasicGameState {
	
	Map<Integer, String> action_types;
	
	static final String DB_URL = "jdbc:sqlite:mysqlitedb.db";
	private Connection conn = null;

	private Image background;
	private String dialogue = "";
	
	
	static final String images_path = "/Library/WebServer/Documents/csci213/images/";
	
	private int current_action_id = 0;
	
	public Play() {
		
		// define types of actions
		action_types = new HashMap<Integer, String>();
		action_types.put(1, "ACTION_DIALOGUE");
		action_types.put(2, "ACTION_CHOICE");
		action_types.put(3, "ACTION_IMAGE");
		action_types.put(4, "ACTION_SOUND");
		action_types.put(5, "ACTION_MUSIC");
		action_types.put(6, "ACTION_NEXTSCENE");
		action_types.put(7, "ACTION_IF");
		action_types.put(8, "ACTION_ELSE");
		action_types.put(9, "ACTION_ENDIF");
		action_types.put(10, "ACTION_END");
		
		
	}
 
	public void init(GameContainer gc, StateBasedGame sbg)
			throws SlickException {
		nextAction();
	}
 
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g)
			throws SlickException {
		background.draw(0, 0, gc.getWidth(), gc.getHeight());
		
		
		// dialogue box
		g.setColor(Color.black);
		// 20% of the screen
		float dialogue_height = (20f/100f * gc.getHeight());
		float dialogue_y = gc.getHeight() - dialogue_height;
		g.fillRect(0, dialogue_y, gc.getWidth(), dialogue_height);
		
		g.setColor(Color.white);
		g.drawString(dialogue, 0, dialogue_y);
	}
 
	public void update(GameContainer gc, StateBasedGame sbg, int delta)
			throws SlickException {
		if(gc.getInput().isKeyPressed(Input.KEY_SPACE))
		{
			nextAction();
			System.out.println("Pressed space");
		}
		
	}
	
	/**
	 * Get the next action from the database and depending on it's type carry out its purpose
	 *
	 * @author Adam Khan Liwal
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
			current_action_id = rs.getInt("sort");
			
			Statement stmt2 = c.createStatement();
			
			if(action_types.get(type) == "ACTION_IMAGE")
			{
				// select image action from Action_showImage to get image position and image id
				ResultSet action_showimage_rs = stmt2.executeQuery( "SELECT * FROM Action_ShowImage WHERE id = " + rs.getInt("xid"));
				while(action_showimage_rs.next())
				{
					ResultSet image_rs = stmt2.executeQuery( "SELECT * FROM Image WHERE id = " + action_showimage_rs.getInt("image_id"));
					while(image_rs.next())
					{
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

			System.out.println("Current action id:" + current_action_id);
			
		}
		
		rs.close();
		stmt.close();
		c.close();
		} catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
	}
 
	public int getID() {
		return 0;
	}
}
