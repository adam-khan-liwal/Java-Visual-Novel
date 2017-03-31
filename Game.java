import org.newdawn.slick.*;
import org.newdawn.slick.state.*;

public class Game extends StateBasedGame{
   
   public static final String gamename = "MyGameName";
   public static final int menu = 0;
   public static final int play = 1;
   
   // A better screen size to work with
   public static final int xSize = 1120;
   public static final int ySize = 700;
   
   public Game(String gamename){
      super(gamename);
      this.addState(new Menu());
      this.addState(new Play());
   }
   
   public void initStatesList(GameContainer gc) throws SlickException{
      this.enterState(menu);
   }
   
   public static void main(String[] args) {
	  System.out.println("Working Directory = " + System.getProperty("user.dir") + "/images" );
      AppGameContainer appgc;
      try{
         appgc = new AppGameContainer(new Game(gamename));
      //   appgc.setDisplayMode(appgc.getWidth(), appgc.getHeight(), false); (Trying to make it scale to window size)
         appgc.setDisplayMode(xSize, ySize, false);
         appgc.setTargetFrameRate(60);
         appgc.start();
      }catch(SlickException e){
         e.printStackTrace();
      }
   }
}
