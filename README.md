# Java-Visual-Novel

## Eclipse Project set up with Slick2D 
1. Download  [Slick2D](http://slick.ninjacave.com/slick.zip)
2. Download [LWJGL](https://sourceforge.net/projects/java-game-lib/files/Official%20Releases/LWJGL%202.9.3/)
4. Extract the two zips to a suitable location.
3. In Eclipse, go to project properties, in the list select Java Build path, in the tab select Libraries.
4. Click on Add External Jars and select these four files from where you extracted slick2d/lib/
    1. lwjgl.jar
    2. slick.jar
    3. jinput.jar
    4. lwjgl_util.jar
5. Expand slick.jar library, click on Native Library Location and then click on edit
6. Go to extracted lwjgl/native/ and select the folder with *your-OS* name



## Working on Github
- We don't upload the whole project because of difference in OS.
- We only upload class files and other souce files.
- When commiting a change, don't do it through eclipse. Simply replace the new files on github.

## Learning Slick2d
- The best way to learn slick is to go over the tests at extracted slick/src/org/newdawn/slick/tests/. Make sure to copy slick/testdata to project where you run the slick tests. Each test is a standalone application.
